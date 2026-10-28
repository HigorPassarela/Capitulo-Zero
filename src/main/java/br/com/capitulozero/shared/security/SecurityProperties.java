package br.com.capitulozero.shared.security;

import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/** {@code cz.security.*} properties: JWT validation and CORS origins. */
@Validated
@ConfigurationProperties("cz.security")
public record SecurityProperties(
        @Valid @NotNull Jwt jwt, @Valid Cors cors) {

    public SecurityProperties {
        if (cors == null) {
            cors = new Cors(List.of());
        }
    }

    public record Jwt(@NotBlank String issuer, @NotBlank String secret) {

        public static final int MINIMUM_SECRET_BYTES = 32;

        @AssertTrue(message = "the JWT secret must have at least 32 bytes")
        public boolean isSecretLongEnough() {
            return secret != null && secret.getBytes(StandardCharsets.UTF_8).length >= MINIMUM_SECRET_BYTES;
        }
    }

    public record Cors(List<String> origins) {

        public Cors {
            origins = origins == null
                    ? List.of()
                    : origins.stream().filter(origin -> !origin.isBlank()).toList();
        }
    }
}
