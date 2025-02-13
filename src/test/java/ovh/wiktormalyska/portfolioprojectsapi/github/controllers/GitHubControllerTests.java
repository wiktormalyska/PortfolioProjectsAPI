package ovh.wiktormalyska.portfolioprojectsapi.github.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ovh.wiktormalyska.portfolioprojectsapi.github.api.GitHubApi;
import ovh.wiktormalyska.portfolioprojectsapi.github.models.GitHubFile;
import ovh.wiktormalyska.portfolioprojectsapi.github.models.GitHubRepository;
import ovh.wiktormalyska.portfolioprojectsapi.github.models.GitHubUser;
import ovh.wiktormalyska.portfolioprojectsapi.github.repositories.GitHubRepositoryRepository;
import ovh.wiktormalyska.portfolioprojectsapi.github.repositories.GitHubUserRepositories;
import ovh.wiktormalyska.portfolioprojectsapi.github.services.GitHubService;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class GitHubControllerTests {

    private MockMvc mockMvc;

    @Mock
    private GitHubRepositoryRepository gitHubRepositoryRepository;

    @Mock
    private GitHubUserRepositories gitHubUserRepositories;

    @Mock
    private GitHubApi gitHubApi;

    @Mock
    private GitHubService gitHubService;

    @InjectMocks
    private GitHubController gitHubController;

    @Test
    void shouldReturnUserRepositories() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(gitHubController).build();

        String username = "user";

        GitHubRepository mockedGitHubRepository = GitHubRepository.builder()
                .id(1L)
                .name("repo1")
                .fullName("user/repo1")
                .githubUser(GitHubUser.builder()
                        .id(1L)
                        .userName(username)
                        .lastUpdate(0L)
                        .build())
                .build();
        when(gitHubService.getUserRepos(username)).thenReturn(List.of(mockedGitHubRepository));

        mockMvc.perform(get("/github/repos/{username}", username)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(mockedGitHubRepository.getId()))
                .andExpect(jsonPath("$[0].name").value(mockedGitHubRepository.getName()))
                .andExpect(jsonPath("$[0].full_name").value(mockedGitHubRepository.getFullName()))
                .andExpect(jsonPath("$[0].fork").value(false));

        verify(gitHubService, times(1)).getUserRepos(username);
    }

    @Test
    void shouldReturnUserRepositoryMetaGitHubFile() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(gitHubController).build();

        String username = "user";
        String repositoryName = "repo1";

        GitHubFile mockedGitHubFile = GitHubFile.builder()
                .name("file1")
                .content("content1")
                .build();

        when(gitHubService.getMetaGitHubFileContentFromUserRepo(username, repositoryName)).thenReturn(mockedGitHubFile);


        mockMvc.perform(get("/github/meta/{username}/{repositoryName}", username, repositoryName)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(mockedGitHubFile.getName()))
                .andExpect(jsonPath("$.content").value(mockedGitHubFile.getContent()));

        verify(gitHubService, times(1)).getMetaGitHubFileContentFromUserRepo(username, repositoryName);
    }

}
