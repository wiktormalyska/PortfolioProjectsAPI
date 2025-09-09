package ovh.wiktormalyska.portfolioprojectsapi.configuration;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.cors.CorsConfigurationSource;

import static org.junit.jupiter.api.Assertions.*;

class CorsConfigurationTest {

    @Test
    void corsConfigurationSourceShouldBeProperlyConfigured() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        CorsConfigurationSource source = corsConfig.corsConfigurationSource();
        
        assertNotNull(source);
        
        // Create a mock request to get the configuration
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/test");
        
        // Get the configuration for the request
        org.springframework.web.cors.CorsConfiguration config = source.getCorsConfiguration(request);
        assertNotNull(config);
        
        // Check that origin patterns are set for subdomains
        assertNotNull(config.getAllowedOriginPatterns());
        assertTrue(config.getAllowedOriginPatterns().contains("http://*.wiktormalyska.ovh"));
        assertTrue(config.getAllowedOriginPatterns().contains("https://*.wiktormalyska.ovh"));
        
        // Check explicit localhost origins
        assertNotNull(config.getAllowedOrigins());
        assertTrue(config.getAllowedOrigins().contains("http://localhost:3000"));
        assertTrue(config.getAllowedOrigins().contains("https://localhost:3000"));
        assertTrue(config.getAllowedOrigins().contains("http://localhost:5173"));
        assertTrue(config.getAllowedOrigins().contains("https://localhost:5173"));
        
        // Check methods include OPTIONS for preflight
        assertNotNull(config.getAllowedMethods());
        assertTrue(config.getAllowedMethods().contains("OPTIONS"));
        assertTrue(config.getAllowedMethods().contains("GET"));
        assertTrue(config.getAllowedMethods().contains("POST"));
        assertTrue(config.getAllowedMethods().contains("PUT"));
        assertTrue(config.getAllowedMethods().contains("PATCH"));
        assertTrue(config.getAllowedMethods().contains("DELETE"));
        
        // Check credentials are allowed
        assertTrue(config.getAllowCredentials());
        
        // Check maxAge is set
        assertEquals(3600L, config.getMaxAge());
    }
}