package ovh.wiktormalyska.portfolioprojectsapi.meta_data.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.id.IncrementGenerator;

import java.util.List;

@Getter
@Setter
@Builder
@Entity
@Table(name = "meta_files")
@AllArgsConstructor
@NoArgsConstructor
public class MetaFile {
    @Id
    private Long gitHubRepositoryId;

    private String name;
    private String description;
    private boolean isForked;
    private String imageUrl;

    @ElementCollection
    @CollectionTable(name = "meta_file_technologies", joinColumns = @JoinColumn(name = "gitHubRepositoryId"))
    private List<String> technologies;
}
