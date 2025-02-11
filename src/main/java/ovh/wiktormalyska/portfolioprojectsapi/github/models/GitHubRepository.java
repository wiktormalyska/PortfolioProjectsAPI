package ovh.wiktormalyska.portfolioprojectsapi.github.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Table(name = "repositories")
@Builder
public class GitHubRepository {
    @Id
    private Long id;

    private String name;

    @JsonProperty("full_name")
    private String fullName;

    @JsonProperty("html_url")
    private String url;

    @JsonProperty("fork")
    private boolean forked;

    @JsonProperty("updated_at")
    private String lastUpdate;

    @Setter
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "github_user_id", nullable = false)
    private GitHubUser githubUser;
}
