package ovh.wiktormalyska.portfolioprojectsapi.github;

import lombok.Setter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ovh.wiktormalyska.portfolioprojectsapi.github.models.GitHubRepository;

import java.util.List;

@Component
@Setter
public class GitHubClient {
    private static final String GITHUB_API_URL = "https://api.github.com";
    String user;

    public GitHubClient() {
        this.user = "wiktormalyska";
    }

    public List<GitHubRepository> getUserRepos() {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        addHeaders(headers);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<List<GitHubRepository>> response = restTemplate.exchange(
                GITHUB_API_URL + "/users/" + user + "/repos",
                HttpMethod.GET, entity,
                new ParameterizedTypeReference<>() {});
        return response.getBody();
    }

    private static void addHeaders(HttpHeaders headers) {
        headers.set("Accept", "application/vnd.github+json");
        headers.set("User-Agent", "PortfolioProjectsAPI");
        headers.set("X-GitHub-Api-Version", "2022-11-28");
    }
}
