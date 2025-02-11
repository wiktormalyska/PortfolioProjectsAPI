package ovh.wiktormalyska.portfolioprojectsapi.github.models;


import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GitHubFile {
    String name;
    String encoding;
    String content;
    String decodedContent;
}
