package ovh.wiktormalyska.portfolioprojectsapi.github.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ovh.wiktormalyska.portfolioprojectsapi.github.models.GitHubUser;

public interface GitHubUserRepositories extends JpaRepository<GitHubUser, Long> {
    GitHubUser findByUserName(String userName);
}
