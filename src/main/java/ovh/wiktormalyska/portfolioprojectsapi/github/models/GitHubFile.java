package ovh.wiktormalyska.portfolioprojectsapi.github.models;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
public class GitHubFile {
    String name;
    String encoding;
    String content;
    @Setter
    String decodedContent;
}
