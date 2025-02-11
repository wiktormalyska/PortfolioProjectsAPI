package ovh.wiktormalyska.portfolioprojectsapi.github;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ovh.wiktormalyska.portfolioprojectsapi.github.models.GitHubRepository;

import java.util.List;

@Service
public class GitHubService {
    private final GitHubClient gitHubClient;

    @Autowired
    public GitHubService(GitHubClient gitHubClient) {
        this.gitHubClient = gitHubClient;
    }

    public List<GitHubRepository> getUserRepos(String username) {
        gitHubClient.setUser(username);
        return gitHubClient.getUserRepos();
    }
}
