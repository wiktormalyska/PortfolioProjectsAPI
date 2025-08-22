package ovh.wiktormalyska.portfolioprojectsapi.meta_data.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ovh.wiktormalyska.portfolioprojectsapi.meta_data.models.MetaFile;
import ovh.wiktormalyska.portfolioprojectsapi.meta_data.services.MetaFileService;

import java.io.FileNotFoundException;
import java.util.List;

@RestController
@RequestMapping("/meta")
public class MetaFileController {

    MetaFileService metaFileService;

    @Autowired
    public MetaFileController(MetaFileService metaFileService) {
        this.metaFileService = metaFileService;
    }

    @GetMapping("/{username}/{repositoryName}")
    public MetaFile getMetaFileContentFromUserRepo(@PathVariable String username, @PathVariable String repositoryName) throws FileNotFoundException {
        return metaFileService.getMetaFileContentFromUserRepo(username, repositoryName);
    }

    @GetMapping("/getAll/{username}")
    public List<MetaFile> getAllMetaFilesFromUser(@PathVariable String username) {
        return metaFileService.getMetaFilesForUser(username);
    }
}