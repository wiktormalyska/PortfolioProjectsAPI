package ovh.wiktormalyska.portfolioprojectsapi.github.api;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import ovh.wiktormalyska.portfolioprojectsapi.github.models.GitHubFile;
import ovh.wiktormalyska.portfolioprojectsapi.github.models.GitHubRepository;
import ovh.wiktormalyska.portfolioprojectsapi.github.models.GitHubUser;

import java.util.List;

@Component
@Setter
public class GitHubClient {
    private static final String GITHUB_API_URL = "https://api.github.com";

    private final Environment env;

    @Autowired
    public GitHubClient(Environment env) {
        this.env = env;
    }

    public List<GitHubRepository> getUserRepos(String user) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        addHeaders(headers);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<List<GitHubRepository>> response = restTemplate.exchange(
                GITHUB_API_URL + "/users/" + user + "/repos",
                HttpMethod.GET, entity,
                new ParameterizedTypeReference<>() {
                });
        return response.getBody();
    }

    public GitHubFile getMetaFileContentFromUserRepo(GitHubUser user, GitHubRepository repository) {
        String fileName = env.getProperty("META_FILE_NAME");

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        addHeaders(headers);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        try {
            ResponseEntity<GitHubFile> response = restTemplate.exchange(
                    GITHUB_API_URL + "/repos/" + repository.getFullName() + "/contents/" + fileName,
                    HttpMethod.GET, entity,
                    new ParameterizedTypeReference<>() {
                    });

            return response.getBody();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Meta file not found in repository.");
        }
    }

    private static void addHeaders(HttpHeaders headers) {
        headers.set("Accept", "application/vnd.github+json");
        headers.set("User-Agent", "PortfolioProjectsAPI");
        headers.set("X-GitHub-Api-Version", "2022-11-28");
    }
}
