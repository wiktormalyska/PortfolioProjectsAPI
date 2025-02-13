package ovh.wiktormalyska.portfolioprojectsapi.github.api;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import ovh.wiktormalyska.portfolioprojectsapi.github.models.GitHubFile;
import ovh.wiktormalyska.portfolioprojectsapi.github.models.GitHubRepository;
import ovh.wiktormalyska.portfolioprojectsapi.github.models.GitHubUser;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static ovh.wiktormalyska.portfolioprojectsapi.github.api.GitHubApi.addHeaders;

@ExtendWith(MockitoExtension.class)
public class GitHubApiTests {
    private static final String GITHUB_API_URL = "https://api.github.com";

    @Mock
    private RestTemplate restTemplate;

    @Mock
    Environment env;

    @InjectMocks
    private GitHubApi gitHubApi;


    @Test
    public void getUserRepos_ShouldReturnRepositories() {
        String username = "user";

        GitHubRepository mockedRepository = GitHubRepository.builder()
                .id(1L)
                .name("repo1")
                .fullName("user/repo1")
                .build();
        List<GitHubRepository> expected = List.of(mockedRepository);

        ResponseEntity<List<GitHubRepository>> mockResponse = new ResponseEntity<>(expected, HttpStatus.OK);

        when(restTemplate.exchange(
                eq(GITHUB_API_URL + "/users/" + username + "/repos"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(new ParameterizedTypeReference<List<GitHubRepository>>() {
                })
        )).thenReturn(mockResponse);

        List<GitHubRepository> repositories = gitHubApi.getUserRepos(username);

        assertNotNull(repositories);
        assertEquals(1, repositories.size());
        assertEquals("repo1", repositories.getFirst().getName());
    }

    @Test
    public void getMetaFileContentFromUserRepo_ShouldReturnGitHubFile() {
        String username = "user";

        GitHubRepository mockedRepository = GitHubRepository.builder()
                .id(1L)
                .name("repo1")
                .fullName("user/repo1")
                .githubUser(GitHubUser.builder()
                        .userName(username)
                        .id(1L)
                        .lastUpdate(0L)
                        .build())
                .build();
        GitHubFile expected = GitHubFile.builder()
                .name("file1")
                .content("content")
                .decodedContent("decodedContent")
                .build();

        ResponseEntity<GitHubFile> mockResponse = new ResponseEntity<>(expected, HttpStatus.OK);

        when(restTemplate.exchange(
                eq(GITHUB_API_URL + "/repos/" + mockedRepository.getFullName() + "/contents/" + env.getProperty("META_FILE_NAME")),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(new ParameterizedTypeReference<GitHubFile>() {
                })
        )).thenReturn(mockResponse);

        GitHubFile file = gitHubApi.getMetaFileContentFromUserRepo(mockedRepository);

        assertNotNull(file);
        assertEquals(file, expected);
    }

    @Test
    public void getMetaFileContentFromUserRepo_ShouldThrowFileNotFound() {
        String username = "user";

        GitHubRepository mockedRepository = GitHubRepository.builder()
                .id(1L)
                .name("repo1")
                .fullName("user/repo1")
                .githubUser(GitHubUser.builder()
                        .userName(username)
                        .id(1L)
                        .lastUpdate(0L)
                        .build())
                .build();
        GitHubFile expected = GitHubFile.builder()
                .name("file1")
                .content("content")
                .decodedContent("decodedContent")
                .build();



        when(restTemplate.exchange(
                eq(GITHUB_API_URL + "/repos/" + mockedRepository.getFullName() + "/contents/" + env.getProperty("META_FILE_NAME")),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(new ParameterizedTypeReference<GitHubFile>() {
                })
        )).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Meta file not found in repository."));

        assertThrows(ResponseStatusException.class, () -> {
            gitHubApi.getMetaFileContentFromUserRepo(mockedRepository);
        });
    }
}
