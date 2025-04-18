package ovh.wiktormalyska.portfolioprojectsapi;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:.env")
class PortfolioProjectsApiApplicationTests {
    @Autowired
    private Environment env;

    @Test
    void contextLoads() {
        assertEquals("meta.json", env.getProperty("META_FILE_NAME"));
    }

    @Test
    void mainRuns() {
        PortfolioProjectsApiApplication.main(new String[] {});
    }

}
