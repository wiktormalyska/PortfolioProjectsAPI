package ovh.wiktormalyska.portfolioprojectsapi.meta_data.services;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ovh.wiktormalyska.portfolioprojectsapi.github.models.GitHubFile;
import ovh.wiktormalyska.portfolioprojectsapi.github.models.GitHubRepository;
import ovh.wiktormalyska.portfolioprojectsapi.github.models.GitHubUser;
import ovh.wiktormalyska.portfolioprojectsapi.github.repositories.GitHubRepositoryRepository;
import ovh.wiktormalyska.portfolioprojectsapi.github.services.GitHubService;
import ovh.wiktormalyska.portfolioprojectsapi.meta_data.models.MetaFile;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MetaFileServiceTests {

    @Mock
    private GitHubService gitHubService;

    @Mock
    private GitHubRepositoryRepository gitHubRepositoryRepository;

    @Mock
    private Gson gson;

    @InjectMocks
    private MetaFileService metaFileService;

    @Test
    public void shouldReturnMetaFileContentFromUserRepo() {
        String username = "username";
        String repositoryName = "repositoryName";

        GitHubRepository mockedGitHubRepository = GitHubRepository.builder()
                .id(1L)
                .name(repositoryName)
                .githubUser(GitHubUser.builder().userName(username).build())
                .forked(false)
                .url("url")
                .build();


        GitHubFile file = GitHubFile.builder()
                .decodedContent("{\"gitHubRepositoryId\":\"" + mockedGitHubRepository.getId() + "\"}")
                .build();
        when(gitHubService.getUserRepos(username)).thenReturn(List.of(mockedGitHubRepository));
        when(gitHubRepositoryRepository.findByName(repositoryName)).thenReturn(Optional.of(mockedGitHubRepository));
        when(gitHubService.getMetaGitHubFileContentFromUserRepo(username, repositoryName)).thenReturn(file);

        MetaFile metaFile = metaFileService.getMetaFileContentFromUserRepo(username, repositoryName);

        assertEquals(metaFile.getGitHubRepositoryId(), mockedGitHubRepository.getId());
        assertFalse(metaFile.isForked());
        assertEquals(metaFile.getRepositoryUrl(), mockedGitHubRepository.getUrl());

    }

    @Test
    public void shouldReturnMetaFileContentFromUserRepo_gitHubFileIsNull() {
        String username = "username";
        String repositoryName = "repositoryName";

        GitHubRepository mockedGitHubRepository = GitHubRepository.builder()
                .id(1L)
                .name(repositoryName)
                .githubUser(GitHubUser.builder().userName(username).build())
                .forked(false)
                .url("url")
                .build();

        when(gitHubService.getUserRepos(username)).thenReturn(List.of(mockedGitHubRepository));
        when(gitHubRepositoryRepository.findByName(repositoryName)).thenReturn(Optional.of(mockedGitHubRepository));
        when(gitHubService.getMetaGitHubFileContentFromUserRepo(username, repositoryName)).thenReturn(null);

        assertThrows(RuntimeException.class, () -> {
            metaFileService.getMetaFileContentFromUserRepo(username, repositoryName);
        });

    }

    @Test
    public void shouldReturnMetaFileContentFromUserRepo_invalidJsonGitHubFileContent() {
        String username = "username";
        String repositoryName = "repositoryName";

        GitHubRepository mockedGitHubRepository = GitHubRepository.builder()
                .id(1L)
                .name(repositoryName)
                .githubUser(GitHubUser.builder().userName(username).build())
                .forked(false)
                .url("url")
                .build();

        GitHubFile file = GitHubFile.builder()
                .decodedContent("{invalidJson}")
                .build();
        when(gitHubService.getUserRepos(username)).thenReturn(List.of(mockedGitHubRepository));
        when(gitHubRepositoryRepository.findByName(repositoryName)).thenReturn(Optional.of(mockedGitHubRepository));
        when(gitHubService.getMetaGitHubFileContentFromUserRepo(username, repositoryName)).thenReturn(file);
        assertThrows(RuntimeException.class, () -> {
            metaFileService.getMetaFileContentFromUserRepo(username, repositoryName);
        });

    }

}