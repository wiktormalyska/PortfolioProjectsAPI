package ovh.wiktormalyska.portfolioprojectsapi.github.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Table(name = "repositories")
public class GitHubRepository {
    @Id
    private Long id;
    private String name;

    @JsonProperty("html_url")
    private String url;

    @JsonProperty("fork")
    private boolean forked;

    @JsonProperty("updated_at")
    private String lastUpdate;
}
