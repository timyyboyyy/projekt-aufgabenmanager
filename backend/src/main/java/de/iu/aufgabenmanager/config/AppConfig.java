package de.iu.aufgabenmanager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Anwendungsweite Beans. Die vollstaendige Security-Konfiguration (Filter-Chain, JWT)
 * folgt in Schritt 3; hier bereits der PasswordEncoder, den der {@link DataSeeder} benoetigt.
 */
@Configuration
public class AppConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
