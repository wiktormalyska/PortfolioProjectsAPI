package ovh.wiktormalyska.portfolioprojectsapi.github;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ovh.wiktormalyska.portfolioprojectsapi.github.api.GitHubClient;
import ovh.wiktormalyska.portfolioprojectsapi.github.models.GitHubRepository;
import ovh.wiktormalyska.portfolioprojectsapi.github.models.GitHubUser;
import ovh.wiktormalyska.portfolioprojectsapi.github.repositories.GitHubUserRepositories;
import ovh.wiktormalyska.portfolioprojectsapi.github.services.GitHubService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GitHubTests {

    @Mock
    private GitHubUserRepositories gitHubUserRepositories;

    @Mock
    private GitHubClient gitHubClient;

    @InjectMocks
    private GitHubService gitHubService;

    @Test
    void shouldReturnMockedRepositories() {
        GitHubUser mockedGitHubUser = GitHubUser.builder()
                .id(1L)
                .userName("user")
                .lastUpdate(0L)
                .build();

        List<GitHubRepository> mockedGitHubRepositories = List.of(
                GitHubRepository.builder()
                        .id(1L)
                        .name("repo1")
                        .fullName("user/repo1")
                        .githubUser(mockedGitHubUser)
                        .build(),
                GitHubRepository.builder()
                        .id(2L)
                        .name("repo2")
                        .fullName("user/repo2")
                        .githubUser(mockedGitHubUser)
                        .build()
        );
        mockedGitHubUser.setRepositories(mockedGitHubRepositories);

        when(gitHubUserRepositories.findByUserName("user")).thenReturn(mockedGitHubUser);
        when(gitHubClient.getUserRepos("user")).thenReturn(mockedGitHubRepositories);

        List<GitHubRepository> result = gitHubService.getUserRepos("user");

        assertEquals(mockedGitHubRepositories.size(), result.size());
        assertEquals(mockedGitHubRepositories.get(0).getName(), result.get(0).getName());
        assertEquals(mockedGitHubRepositories.get(1).getName(), result.get(1).getName());
    }
}


