package ovh.wiktormalyska.portfolioprojectsapi.github.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ovh.wiktormalyska.portfolioprojectsapi.github.models.GitHubFile;
import ovh.wiktormalyska.portfolioprojectsapi.github.models.GitHubRepository;
import ovh.wiktormalyska.portfolioprojectsapi.github.services.GitHubService;

import java.util.List;

@RestController
@RequestMapping("/github")
public class GitHubController {
    private final GitHubService gitHubService;

    @Autowired
    public GitHubController(GitHubService gitHubService) {
        this.gitHubService = gitHubService;
    }

    @GetMapping("/repos/{username}")
    public List<GitHubRepository> getUserRepos(@PathVariable String username) {
        return gitHubService.getUserRepos(username);
    }

    @GetMapping("/meta/{username}/{repositoryName}")
    public GitHubFile getMetaGitHubFileContent(@PathVariable String username, @PathVariable String repositoryName) {
        return gitHubService.getMetaGitHubFileContentFromUserRepo(username, repositoryName);
    }

}
