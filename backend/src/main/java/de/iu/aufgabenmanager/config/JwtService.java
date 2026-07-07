package de.iu.aufgabenmanager.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Erstellt und prueft JWTs (HS256).
 * <p>Das Secret kommt aus {@code app.jwt.secret} (Umgebungsvariable / lokale, nicht eingecheckte
 * Properties). Fehlt es oder ist es zu kurz, wird ein fluechtiger Zufallsschluessel erzeugt –
 * dann laeuft die App ohne Einrichtung, Tokens werden aber bei jedem Neustart ungueltig.
 */
@Service
public class JwtService {

    private static final Logger log = LoggerFactory.getLogger(JwtService.class);
    private static final int MIN_SECRET_BYTES = 32; // HS256 benoetigt >= 256 Bit

    private final SecretKey key;
    private final long expirationMs;

    public JwtService(@Value("${app.jwt.secret:}") String secret,
                      @Value("${app.jwt.expiration-ms:3600000}") long expirationMs) {
        this.expirationMs = expirationMs;
        byte[] bytes = secret == null ? new byte[0] : secret.getBytes(StandardCharsets.UTF_8);
        if (bytes.length >= MIN_SECRET_BYTES) {
            this.key = Keys.hmacShaKeyFor(bytes);
        } else {
            this.key = Jwts.SIG.HS256.key().build();
            log.warn("app.jwt.secret fehlt oder ist kuerzer als {} Bytes. Es wird ein fluechtiger "
                    + "Zufallsschluessel verwendet - Tokens werden bei jedem Neustart ungueltig. "
                    + "Fuer stabile Tokens JWT_SECRET setzen (siehe application-local.properties.example).",
                    MIN_SECRET_BYTES);
        }
    }

    /** Erzeugt ein signiertes Token fuer den Benutzer inkl. Rolle als Claim. */
    public String generateToken(String username, String role) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(username)
                .claim("role", role)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(expirationMs)))
                .signWith(key)
                .compact();
    }

    /** Liefert den Benutzernamen (Subject) aus einem gueltigen Token. */
    public String extractUsername(String token) {
        return parse(token).getSubject();
    }

    /** Prueft Signatur und Ablauf. Bei ungueltigem Token {@code false}. */
    public boolean isValid(String token) {
        try {
            parse(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }

    private Claims parse(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
