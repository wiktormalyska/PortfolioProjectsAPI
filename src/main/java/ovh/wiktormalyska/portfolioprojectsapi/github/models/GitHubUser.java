package ovh.wiktormalyska.portfolioprojectsapi.github.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Entity
@Table(name = "users")
public class GitHubUser {
    @Id
    @GeneratedValue(generator = "increment")
    private Long id;

    private String userName;

    @Setter
    private Long lastUpdate;

    @Setter
    @OneToMany(mappedBy = "githubUser", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<GitHubRepository> repositories = new ArrayList<>();

}
