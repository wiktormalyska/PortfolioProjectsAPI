package ovh.wiktormalyska.portfolioprojectsapi.github;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ovh.wiktormalyska.portfolioprojectsapi.github.models.GitHubFile;
import ovh.wiktormalyska.portfolioprojectsapi.github.models.GitHubRepository;

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

    @GetMapping("/meta/{username}/{repositoryName}/")
    public GitHubFile getMetaFileContent(@PathVariable String username, @PathVariable String repositoryName) {
        return gitHubService.getMetaFileContentFromUserRepo(username, repositoryName);
    }

}
