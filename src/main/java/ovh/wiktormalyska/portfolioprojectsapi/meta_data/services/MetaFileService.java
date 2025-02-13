package ovh.wiktormalyska.portfolioprojectsapi.meta_data.services;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    public MetaFileService(GitHubService gitHubService, GitHubRepositoryRepository gitHubRepositoryRepository) {
        this.gitHubService = gitHubService;
        this.gitHubRepositoryRepository = gitHubRepositoryRepository;
    }

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

@Transactional(dontRollbackOn = FileNotFoundException.class)
public List<MetaFile> getAllMetaFilesFromUserRepos(String username) {
    try {
        // Fetching repositories from GitHub service
        gitHubService.getUserRepos(username);
        List<GitHubRepository> repositories = gitHubRepositoryRepository.findByGithubUser_UserName(username);

        // Stream through repositories, fetching meta files and returning results
        return repositories.stream()
                .map(repository -> {
                    try {
                        // Fetching meta file content for each repository
                        return getMetaFileContentFromUserRepo(username, repository.getName());
                    } catch (FileNotFoundException e) {
                        // Log file not found and return null to avoid breaking the flow
                        logger.warn("Meta file not found for repository: {}", repository.getName(), e);
                        return null; // Or return Optional.empty() for better handling
                    } catch (Exception e) {
                        // Log and rethrow exception for other unexpected issues
                        logger.error("Error occurred while fetching meta file for repository: {}", repository.getName(), e);
                        throw new RuntimeException("Error fetching meta file", e); // You may want to throw a custom exception
                    }
                })
                .filter(Objects::nonNull) // Filter out null results from failed meta file fetches
                .collect(Collectors.toList()); // Collect valid meta files

    } catch (Exception e) {
        // Log the overall exception and throw a more general exception if needed
        logger.error("Transaction failed for user: {}", username, e);
        throw new RuntimeException("Transaction failed for user: " + username, e);
    }
}

}
