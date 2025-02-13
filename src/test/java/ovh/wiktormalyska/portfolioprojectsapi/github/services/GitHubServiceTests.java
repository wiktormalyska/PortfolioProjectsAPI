package ovh.wiktormalyska.portfolioprojectsapi.github.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import ovh.wiktormalyska.portfolioprojectsapi.github.api.GitHubApi;
import ovh.wiktormalyska.portfolioprojectsapi.github.models.GitHubFile;
import ovh.wiktormalyska.portfolioprojectsapi.github.models.GitHubRepository;
import ovh.wiktormalyska.portfolioprojectsapi.github.models.GitHubUser;
import ovh.wiktormalyska.portfolioprojectsapi.github.repositories.GitHubRepositoryRepository;
import ovh.wiktormalyska.portfolioprojectsapi.github.repositories.GitHubUserRepositories;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class GitHubServiceTests {

    @Mock
    private GitHubRepositoryRepository gitHubRepositoryRepository;

    @Mock
    private GitHubUserRepositories gitHubUserRepositories;

    @Mock
    private GitHubApi gitHubApi;

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

        when(gitHubUserRepositories.findByUserName("user")).thenReturn(mockedGitHubUser);
        when(gitHubApi.getUserRepos("user")).thenReturn(mockedGitHubRepositories);

        List<GitHubRepository> result = gitHubService.getUserRepos("user");

        assertEquals(mockedGitHubRepositories.size(), result.size());
        assertEquals(mockedGitHubRepositories.get(0).getName(), result.get(0).getName());
        assertEquals(mockedGitHubRepositories.get(1).getName(), result.get(1).getName());
    }

    @Test
    void shouldCreateUserWhenNotFound() {
        String username = "user";

        when(gitHubUserRepositories.findByUserName(username)).thenReturn(null);

        GitHubUser newUser = GitHubUser.builder()
                .id(1L)
                .userName(username)
                .lastUpdate(0L)
                .build();

        when(gitHubUserRepositories.save(any(GitHubUser.class))).thenReturn(newUser);

        List<GitHubRepository> result = gitHubService.getUserRepos(username);

        verify(gitHubUserRepositories, times(2)).save(any(GitHubUser.class));

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldGiveCachedUserRepos() {
        String username = "user";
        long currentTime = System.currentTimeMillis();

        GitHubUser newUser = GitHubUser.builder()
                .id(1L)
                .userName(username)
                .lastUpdate(currentTime)
                .build();

        when(gitHubUserRepositories.findByUserName(username)).thenReturn(newUser);
        when(gitHubRepositoryRepository.findByGithubUser_UserName(username)).thenReturn(List.of());

        List<GitHubRepository> result = gitHubService.getUserRepos(username);

        verify(gitHubRepositoryRepository, times(1)).findByGithubUser_UserName(username);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnMetaFileContent() {
        String username = "user";
        String repoName = "repo";

        GitHubUser user = GitHubUser.builder()
                .id(1L)
                .userName(username)
                .lastUpdate(0L)
                .build();

        GitHubRepository repository = GitHubRepository.builder()
                .id(1L)
                .name(repoName)
                .fullName(username + "/" + repoName)
                .githubUser(user)
                .build();

        GitHubFile file = GitHubFile.builder()
                .name("meta.json")
                .content("c3VjY2VzcyE=")
                .build();

        when(gitHubUserRepositories.findByUserName(username)).thenReturn(user);
        when(gitHubRepositoryRepository.findByName(repoName)).thenReturn(Optional.of(repository));
        when(gitHubApi.getMetaFileContentFromUserRepo(repository)).thenReturn(file);

        gitHubService.getMetaGitHubFileContentFromUserRepo(username, repoName);

        verify(gitHubApi, times(1)).getMetaFileContentFromUserRepo(repository);

        assertTrue(file.getDecodedContent().contains("success!"));
    }

    @Test
    void shouldReturnMetaFileNull() {
        String username = "user";
        String repoName = "repo";

        GitHubUser user = GitHubUser.builder()
                .id(1L)
                .userName(username)
                .lastUpdate(0L)
                .build();

        GitHubRepository repository = GitHubRepository.builder()
                .id(1L)
                .name(repoName)
                .fullName(username + "/" + repoName)
                .githubUser(user)
                .build();

        when(gitHubUserRepositories.findByUserName(username)).thenReturn(user);
        gitHubRepositoryRepository.save(repository);
        when(gitHubRepositoryRepository.findByName(repoName)).thenReturn(Optional.of(repository));
        when(gitHubApi.getMetaFileContentFromUserRepo(repository))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Meta file not found in repository."));

        assertThrows(ResponseStatusException.class, () -> {
            gitHubService.getMetaGitHubFileContentFromUserRepo(username, repoName);
        });
        verify(gitHubApi, times(1)).getMetaFileContentFromUserRepo(repository);
    }

    @Test
    void shouldReturnMetaFileNullNoRepository() {
        String username = "user";
        String repoName = "repo";

        when(gitHubUserRepositories.findByUserName(username)).thenReturn(null);
        when(gitHubRepositoryRepository.findByName(repoName)).thenReturn(Optional.empty());

        GitHubFile result = gitHubService.getMetaGitHubFileContentFromUserRepo(username, repoName);

        verify(gitHubApi, times(0)).getMetaFileContentFromUserRepo(any(GitHubRepository.class));
        assertNull(result);
    }
}