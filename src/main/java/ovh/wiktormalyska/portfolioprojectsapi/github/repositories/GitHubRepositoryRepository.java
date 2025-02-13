package ovh.wiktormalyska.portfolioprojectsapi.github.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ovh.wiktormalyska.portfolioprojectsapi.github.models.GitHubRepository;
import ovh.wiktormalyska.portfolioprojectsapi.github.models.GitHubUser;

import java.util.List;
import java.util.Optional;

public interface GitHubRepositoryRepository extends JpaRepository<GitHubRepository, Long> {
    Optional<GitHubRepository> findByName(String name);

    List<GitHubRepository> findByGithubUser_UserName(String githubUserUserName);

    void deleteAllByGithubUser(GitHubUser user);
}
