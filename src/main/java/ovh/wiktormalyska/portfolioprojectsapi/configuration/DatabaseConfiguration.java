package ovh.wiktormalyska.portfolioprojectsapi.configuration;
import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.util.Objects;

@Configuration
public class DatabaseConfiguration {

    Environment env;

    @Autowired
    public DatabaseConfiguration(Environment env) {
        this.env = env;
    }

    @Bean
    public DataSource dataSource() {
        final DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(Objects.requireNonNull(env.getProperty("DATABASE_DRIVER_CLASS_NAME")));
        dataSource.setUrl(env.getProperty("DATABASE_URL"));
        dataSource.setUsername(env.getProperty("DATABASE_USERNAME"));
        dataSource.setPassword(env.getProperty("DATABASE_PASSWORD"));
        return dataSource;
    }
}
