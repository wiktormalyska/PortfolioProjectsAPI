package ovh.wiktormalyska.portfolioprojectsapi.github.services;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ovh.wiktormalyska.portfolioprojectsapi.github.api.GitHubClient;
import ovh.wiktormalyska.portfolioprojectsapi.github.models.GitHubFile;
import ovh.wiktormalyska.portfolioprojectsapi.github.models.GitHubRepository;
import ovh.wiktormalyska.portfolioprojectsapi.github.models.GitHubUser;
import ovh.wiktormalyska.portfolioprojectsapi.github.repositories.GitHubRepositoryRepository;
import ovh.wiktormalyska.portfolioprojectsapi.github.repositories.GitHubUserRepositories;

import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
public class GitHubService {
    private final GitHubClient gitHubClient;
    private final GitHubUserRepositories gitHubUserRepositories;
    private final GitHubRepositoryRepository gitHubRepositoryRepository;

    @Autowired
    public GitHubService(GitHubClient gitHubClient, GitHubUserRepositories gitHubUserRepositories,
                         GitHubRepositoryRepository gitHubRepositoryRepository) {
        this.gitHubClient = gitHubClient;
        this.gitHubUserRepositories = gitHubUserRepositories;
        this.gitHubRepositoryRepository = gitHubRepositoryRepository;
    }


    @Transactional
    public List<GitHubRepository> getUserRepos(String username) {
        GitHubUser user = gitHubUserRepositories.findByUserName(username);

        if (user == null) {
            user = GitHubUser.builder()
                    .userName(username)
                    .lastUpdate(0L)
                    .build();
            gitHubUserRepositories.save(user);
        }

        long currentTime = System.currentTimeMillis();
        if (currentTime - user.getLastUpdate() > 60_000) {
            List<GitHubRepository> freshRepos = gitHubClient.getUserRepos(username);

            gitHubRepositoryRepository.deleteAllByGithubUser(user);

            GitHubUser finalUser = user;
            freshRepos.forEach(repo -> repo.setGithubUser(finalUser));

            gitHubRepositoryRepository.saveAll(freshRepos);
            user.setLastUpdate(currentTime);
            gitHubUserRepositories.save(user);

            return freshRepos;
        }

        return user.getRepositories();
    }

    @Transactional
    public GitHubFile getMetaFileContentFromUserRepo(String username, String repoName) {
        getUserRepos(username);

        GitHubUser user = gitHubUserRepositories.findByUserName(username);
        Optional<GitHubRepository> repository = gitHubRepositoryRepository.findByName(repoName);

        if (repository.isEmpty()) {
            getUserRepos(username);
            repository = gitHubRepositoryRepository.findByName(repoName);
        }

        if (user == null || repository.isEmpty()) {
            return null;
        }
        GitHubFile file = gitHubClient.getMetaFileContentFromUserRepo(user, repository.get());
        String content = file.getContent().replace("\n", "").replace("\r", "");
        byte [] decodedContentBytes = Base64.getDecoder().decode(content);
        file.setDecodedContent(new String(decodedContentBytes));
        return file;
    }
}

