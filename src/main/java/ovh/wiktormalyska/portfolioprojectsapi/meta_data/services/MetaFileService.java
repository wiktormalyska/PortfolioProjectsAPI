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

@Service
public class MetaFileService {
    GitHubService gitHubService;
    GitHubRepositoryRepository gitHubRepositoryRepository;
    Gson gson = new Gson();

    @Autowired
    public MetaFileService(GitHubService gitHubService, GitHubRepositoryRepository gitHubRepositoryRepository) {
        this.gitHubService = gitHubService;
        this.gitHubRepositoryRepository = gitHubRepositoryRepository;
    }

    @Transactional
    public MetaFile getMetaFileContentFromUserRepo(String username, String repositoryName) {
        gitHubService.getUserRepos(username);

        GitHubRepository gitHubRepository = gitHubRepositoryRepository.findByName(repositoryName)
                .orElseThrow(() -> new RuntimeException("Repository not found"));

        GitHubFile gitHubFile = gitHubService.getMetaGitHubFileContentFromUserRepo(username, gitHubRepository.getName());
        if (gitHubFile == null || gitHubFile.getDecodedContent() == null) {
            throw new RuntimeException("Meta file not found");
        }

        try {
            MetaFile metaFile = gson.fromJson(gitHubFile.getDecodedContent(), MetaFile.class);
            metaFile.setGitHubRepositoryId(gitHubRepository.getId());
            metaFile.setForked(gitHubRepository.isForked());
            metaFile.setRepositoryUrl(gitHubRepository.getUrl());
            return metaFile;
        } catch (JsonSyntaxException e) {
            throw new RuntimeException("Meta file is not valid JSON");
        }
    }
}
