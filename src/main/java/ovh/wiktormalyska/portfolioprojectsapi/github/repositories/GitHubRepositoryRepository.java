package ovh.wiktormalyska.portfolioprojectsapi.github.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ovh.wiktormalyska.portfolioprojectsapi.github.models.GitHubRepository;
import ovh.wiktormalyska.portfolioprojectsapi.github.models.GitHubUser;

import java.util.Optional;

public interface GitHubRepositoryRepository extends JpaRepository<GitHubRepository, Long> {
    Optional<GitHubRepository> findByName(String name);
    void deleteAllByGithubUser(GitHubUser user);
}
