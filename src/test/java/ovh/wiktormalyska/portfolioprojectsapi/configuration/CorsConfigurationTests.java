package ovh.wiktormalyska.portfolioprojectsapi.configuration;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import static org.junit.jupiter.api.Assertions.*;

public class CorsConfigurationTests {

    @Test
    void shouldConfigureAllowedOriginPatterns() {
        // Given
        ovh.wiktormalyska.portfolioprojectsapi.configuration.CorsConfiguration corsConfig = 
            new ovh.wiktormalyska.portfolioprojectsapi.configuration.CorsConfiguration();
        
        // When
        CorsConfigurationSource source = corsConfig.corsConfigurationSource();
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/github/repos/testuser");
        CorsConfiguration configuration = source.getCorsConfiguration(request);
        
        // Then
        assertNotNull(configuration);
        assertEquals(2, configuration.getAllowedOriginPatterns().size());
        assertTrue(configuration.getAllowedOriginPatterns().contains("http://*.wiktormalyska.ovh"));
        assertTrue(configuration.getAllowedOriginPatterns().contains("https://*.wiktormalyska.ovh"));
    }

    @Test
    void shouldConfigureAllowedOrigins() {
        // Given
        ovh.wiktormalyska.portfolioprojectsapi.configuration.CorsConfiguration corsConfig = 
            new ovh.wiktormalyska.portfolioprojectsapi.configuration.CorsConfiguration();
        
        // When
        CorsConfigurationSource source = corsConfig.corsConfigurationSource();
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/github/repos/testuser");
        CorsConfiguration configuration = source.getCorsConfiguration(request);
        
        // Then
        assertNotNull(configuration);
        assertEquals(4, configuration.getAllowedOrigins().size());
        assertTrue(configuration.getAllowedOrigins().contains("http://localhost:3000"));
        assertTrue(configuration.getAllowedOrigins().contains("https://localhost:3000"));
        assertTrue(configuration.getAllowedOrigins().contains("http://localhost:5173"));
        assertTrue(configuration.getAllowedOrigins().contains("https://localhost:5173"));
    }

    @Test
    void shouldConfigureAllowedMethods() {
        // Given
        ovh.wiktormalyska.portfolioprojectsapi.configuration.CorsConfiguration corsConfig = 
            new ovh.wiktormalyska.portfolioprojectsapi.configuration.CorsConfiguration();
        
        // When
        CorsConfigurationSource source = corsConfig.corsConfigurationSource();
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/github/repos/testuser");
        CorsConfiguration configuration = source.getCorsConfiguration(request);
        
        // Then
        assertNotNull(configuration);
        assertEquals(6, configuration.getAllowedMethods().size());
        assertTrue(configuration.getAllowedMethods().contains("GET"));
        assertTrue(configuration.getAllowedMethods().contains("POST"));
        assertTrue(configuration.getAllowedMethods().contains("PUT"));
        assertTrue(configuration.getAllowedMethods().contains("PATCH"));
        assertTrue(configuration.getAllowedMethods().contains("DELETE"));
        assertTrue(configuration.getAllowedMethods().contains("OPTIONS"));
    }

    @Test
    void shouldConfigureCredentialsAndMaxAge() {
        // Given
        ovh.wiktormalyska.portfolioprojectsapi.configuration.CorsConfiguration corsConfig = 
            new ovh.wiktormalyska.portfolioprojectsapi.configuration.CorsConfiguration();
        
        // When
        CorsConfigurationSource source = corsConfig.corsConfigurationSource();
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/github/repos/testuser");
        CorsConfiguration configuration = source.getCorsConfiguration(request);
        
        // Then
        assertNotNull(configuration);
        assertTrue(configuration.getAllowCredentials());
        assertEquals(3600L, configuration.getMaxAge());
        assertEquals(1, configuration.getAllowedHeaders().size());
        assertTrue(configuration.getAllowedHeaders().contains("*"));
    }

    @Test
    void shouldCheckOriginMatching() {
        // Given
        ovh.wiktormalyska.portfolioprojectsapi.configuration.CorsConfiguration corsConfig = 
            new ovh.wiktormalyska.portfolioprojectsapi.configuration.CorsConfiguration();
        CorsConfigurationSource source = corsConfig.corsConfigurationSource();
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/github/repos/testuser");
        CorsConfiguration configuration = source.getCorsConfiguration(request);
        
        // Then - check that various origins would be allowed
        assertNotNull(configuration);
        
        // Test that the configuration would allow www subdomain
        String result1 = configuration.checkOrigin("https://www.wiktormalyska.ovh");
        assertEquals("https://www.wiktormalyska.ovh", result1);
        
        // Test that the configuration would allow localhost:3000
        String result2 = configuration.checkOrigin("http://localhost:3000");
        assertEquals("http://localhost:3000", result2);
        
        // Test that the configuration would allow localhost:5173
        String result3 = configuration.checkOrigin("http://localhost:5173");
        assertEquals("http://localhost:5173", result3);
        
        // Test that the configuration would reject unauthorized origin
        String result4 = configuration.checkOrigin("https://malicious-site.com");
        assertNull(result4);
    }
}