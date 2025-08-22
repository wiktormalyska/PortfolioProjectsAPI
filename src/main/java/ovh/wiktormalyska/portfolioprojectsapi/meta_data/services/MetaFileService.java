package ovh.wiktormalyska.portfolioprojectsapi.meta_data.services;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ovh.wiktormalyska.portfolioprojectsapi.github.models.GitHubFile;
import ovh.wiktormalyska.portfolioprojectsapi.github.models.GitHubRepository;
import ovh.wiktormalyska.portfolioprojectsapi.github.repositories.GitHubRepositoryRepository;
import ovh.wiktormalyska.portfolioprojectsapi.github.services.GitHubService;
import ovh.wiktormalyska.portfolioprojectsapi.meta_data.models.MetaFile;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class MetaFileService {
    private final GitHubService gitHubService;
    private final GitHubRepositoryRepository gitHubRepositoryRepository;
    private final Gson gson = new Gson();
    private final Logger logger = LoggerFactory.getLogger(MetaFileService.class);
    private final Environment env;

    @Autowired
    public MetaFileService(GitHubService gitHubService, GitHubRepositoryRepository gitHubRepositoryRepository, Environment env) {
        this.env = env;
        this.gitHubService = gitHubService;
        this.gitHubRepositoryRepository = gitHubRepositoryRepository;
    }

    @Cacheable(value = "userMetaFiles", key = "#username")
    @Transactional(dontRollbackOn = FileNotFoundException.class)
    public List<MetaFile> getMetaFilesForUser(String username) {
        logger.info("Fetching meta files from GitHub for user: {}. (Cache miss)", username);
        try {
            gitHubService.getUserRepos(username);
            List<GitHubRepository> repositories = gitHubRepositoryRepository.findByGithubUser_UserName(username);

            return repositories.stream()
                    .map(repository -> {
                        try {
                            return getMetaFileContentFromUserRepo(username, repository.getName());
                        } catch (FileNotFoundException e) {
                            logger.warn("Meta file not found for repository: {}", repository.getName());
                            return null;
                        } catch (Exception e) {
                            logger.error("Error occurred while fetching meta file for repository: {}", repository.getName(), e);
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            logger.error("Transaction failed while fetching all meta files for user: {}", username, e);
            throw new RuntimeException("Transaction failed for user: " + username, e);
        }
    }

    @Cacheable("metaFiles")
    public MetaFile getMetaFileContentFromUserRepo(String username, String repositoryName) throws FileNotFoundException {
        GitHubRepository gitHubRepository = gitHubRepositoryRepository.findByName(repositoryName)
                .orElseThrow(() -> new RuntimeException("Repository not found in DB: " + repositoryName));

        GitHubFile gitHubFile = gitHubService.getMetaGitHubFileContentFromUserRepo(username, gitHubRepository.getName());
        if (gitHubFile == null || gitHubFile.getDecodedContent() == null) {
            throw new FileNotFoundException("Meta file content is null or not found for " + repositoryName);
        }

        try {
            MetaFile metaFile = gson.fromJson(gitHubFile.getDecodedContent(), MetaFile.class);
            metaFile.setGitHubRepositoryId(gitHubRepository.getId());
            metaFile.setForked(gitHubRepository.isForked());
            metaFile.setRepositoryUrl(gitHubRepository.getUrl());
            return metaFile;
        } catch (JsonSyntaxException e) {
            logger.error("JSON syntax error in meta file for repo: {}", repositoryName, e);
            return null;
        }
    }

    @Scheduled(fixedRate = 5 * 60 * 1000)
    public void scheduledUpdateAllUsersMetaFiles() {
        String username = env.getProperty("DEFAULT_GITHUB_USERNAME");
        logger.info("Scheduled update for meta files for user: {}", username);
        try {
            updateAndCacheMetaFilesForUser(username);
        } catch (Exception e) {
            logger.error("Scheduled update failed for user: {}", username, e);
        }
    }
    @CachePut(value = "userMetaFiles", key = "#username")
    @Transactional(dontRollbackOn = FileNotFoundException.class)
    public List<MetaFile> updateAndCacheMetaFilesForUser(String username) {
        logger.info("Forcing cache update for user: {}", username);
        return this.getMetaFilesForUser(username);
    }
}