package ovh.wiktormalyska.portfolioprojectsapi.meta_data.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ovh.wiktormalyska.portfolioprojectsapi.meta_data.models.MetaFile;
import ovh.wiktormalyska.portfolioprojectsapi.meta_data.services.MetaFileService;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class MetaFileControllerTests {

    MockMvc mockMvc;
    @Mock
    private MetaFileService metaFileService;

    @InjectMocks
    private MetaFileController metaFileController;

    @Test
    void shouldReturnMetaFileContent() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(new MetaFileController(metaFileService)).build();

        String username = "user";
        String repositoryName = "repo";

        MetaFile mockedMetaFile = MetaFile.builder()
                .gitHubRepositoryId(1L)
                .name("meta1")
                .build();

        when(metaFileService.getMetaFileContentFromUserRepo(username, repositoryName)).thenReturn(mockedMetaFile);

        mockMvc.perform(get("/meta/{username}/{repositoryName}", username, repositoryName)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gitHubRepositoryId").value(mockedMetaFile.getGitHubRepositoryId()))
                .andExpect(jsonPath("$.name").value(mockedMetaFile.getName()));
        verify(metaFileService).getMetaFileContentFromUserRepo(username, repositoryName);
    }
}
