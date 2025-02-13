package ovh.wiktormalyska.portfolioprojectsapi.github.services;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ovh.wiktormalyska.portfolioprojectsapi.github.api.GitHubApi;
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
    private final GitHubApi gitHubApi;
    private final GitHubUserRepositories gitHubUserRepositories;
    private final GitHubRepositoryRepository gitHubRepositoryRepository;

    @Autowired
    public GitHubService(GitHubApi gitHubApi, GitHubUserRepositories gitHubUserRepositories,
                         GitHubRepositoryRepository gitHubRepositoryRepository) {
        this.gitHubApi = gitHubApi;
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
        if (currentTime - user.getLastUpdate() >= 60_000) {
            List<GitHubRepository> freshRepos = gitHubApi.getUserRepos(username);

            gitHubRepositoryRepository.deleteAllByGithubUser(user);

            GitHubUser finalUser = user;
            freshRepos.forEach(repo -> repo.setGithubUser(finalUser));

            gitHubRepositoryRepository.saveAll(freshRepos);
            user.setLastUpdate(currentTime);
            gitHubUserRepositories.save(user);

            return freshRepos;
        }

        return gitHubRepositoryRepository.findByGithubUser_UserName(username);
    }

    @Transactional
    public GitHubFile getMetaGitHubFileContentFromUserRepo(String username, String repoName) {

        GitHubUser user = gitHubUserRepositories.findByUserName(username);
        Optional<GitHubRepository> repository = gitHubRepositoryRepository.findByName(repoName);

        if (repository.isEmpty()) {
            getUserRepos(username);
            repository = gitHubRepositoryRepository.findByName(repoName);
        }

        if (user == null || repository.isEmpty()) {
            return null;
        }
        GitHubFile file = gitHubApi.getMetaFileContentFromUserRepo(repository.get());
        String content = file.getContent().replace("\n", "").replace("\r", "");
        byte [] decodedContentBytes = Base64.getDecoder().decode(content);
        file.setDecodedContent(new String(decodedContentBytes));
        return file;
    }
}

