package ovh.wiktormalyska.portfolioprojectsapi.meta_data.services;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import jakarta.transaction.Transactional;
import org.apache.catalina.core.ApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.CacheEvict;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class MetaFileService {
    GitHubService gitHubService;
    GitHubRepositoryRepository gitHubRepositoryRepository;
    Gson gson = new Gson();
    Logger logger = LoggerFactory.getLogger(MetaFileService.class);
    private final Environment env;

    @Autowired
    public MetaFileService(GitHubService gitHubService, GitHubRepositoryRepository gitHubRepositoryRepository, Environment env) {
        this.env = env;
        this.gitHubService = gitHubService;
        this.gitHubRepositoryRepository = gitHubRepositoryRepository;
    }

    @Cacheable("metaFiles")
    @Transactional
    public MetaFile getMetaFileContentFromUserRepo(String username, String repositoryName) throws FileNotFoundException {
        gitHubService.getUserRepos(username);

        GitHubRepository gitHubRepository = gitHubRepositoryRepository.findByName(repositoryName)
                .orElseThrow(() -> new RuntimeException("Repository not found"));

        GitHubFile gitHubFile = gitHubService.getMetaGitHubFileContentFromUserRepo(username, gitHubRepository.getName());
        if (gitHubFile == null || gitHubFile.getDecodedContent() == null) {
            return null;
        }

        try {
            MetaFile metaFile = gson.fromJson(gitHubFile.getDecodedContent(), MetaFile.class);
            metaFile.setGitHubRepositoryId(gitHubRepository.getId());
            metaFile.setForked(gitHubRepository.isForked());
            metaFile.setRepositoryUrl(gitHubRepository.getUrl());
            return metaFile;
        } catch (JsonSyntaxException e) {
            return null;
        }
    }

    @Cacheable(value = "metaFilesAll", key = "#username")
    @Transactional(dontRollbackOn = FileNotFoundException.class)
    public List<MetaFile> getAllMetaFilesFromUserRepos(String username) {
        final String finalUsername = (username == null || username.isBlank()) ? "wiktormalyska" : username;
        try {
            
            gitHubService.getUserRepos(finalUsername);
            List<GitHubRepository> repositories = gitHubRepositoryRepository.findByGithubUser_UserName(finalUsername);

            return repositories.stream()
                    .map(repository -> {
                        try {
                            return getMetaFileContentFromUserRepo(finalUsername, repository.getName());
                        } catch (FileNotFoundException e) {
                            logger.warn("Meta file not found for repository: {}", repository.getName(), e);
                            return null;
                        } catch (Exception e) {
                            logger.error("Error occurred while fetching meta file for repository: {}", repository.getName(), e);
                            throw new RuntimeException("Error fetching meta file", e);
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            logger.error("Transaction failed for user: {}", finalUsername, e);
            throw new RuntimeException("Transaction failed for user: " + finalUsername, e);
        }
    }

    @Scheduled(fixedRate = 5000)
    @CachePut(value = "metaFilesAll", key = "'default'")
    public List<MetaFile> updateAllMetaFiles() {
        String username = env.getProperty("DEFAULT_GITHUB_USERNAME");
        final String finalUsername = (username == null || username.isBlank()) ? "wiktormalyska" : username;
        logger.info("Scheduled update for all meta files for user: {}", finalUsername);
        try {
            return getAllMetaFilesFromUserRepos(finalUsername);
        } catch (Exception e) {
            logger.error("Scheduled update failed", e);
            return null;
        }
    }

}
