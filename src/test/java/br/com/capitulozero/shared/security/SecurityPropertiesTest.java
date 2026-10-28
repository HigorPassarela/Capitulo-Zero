package br.com.capitulozero.shared.security;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Configuration;

class SecurityPropertiesTest {

    private static final String VALID_SECRET = "01234567890123456789012345678901";

    private final ApplicationContextRunner context = new ApplicationContextRunner()
            .withUserConfiguration(Config.class)
            .withPropertyValues("cz.security.jwt.issuer=capitulo-zero");

    @Test
    @DisplayName("Startup fails with a 10-byte JWT secret")
    void failsWithShortSecret() {
        context.withPropertyValues("cz.security.jwt.secret=0123456789")
                .run(ctx ->
                        assertThat(ctx).hasFailed().getFailure().rootCause().hasMessageContaining("at least 32 bytes"));
    }

    @Test
    @DisplayName("Startup succeeds with a 32-byte JWT secret")
    void startsWithMinimumSecret() {
        context.withPropertyValues("cz.security.jwt.secret=" + VALID_SECRET)
                .run(ctx -> assertThat(ctx).hasNotFailed().hasSingleBean(SecurityProperties.class));
    }

    @Test
    @DisplayName("Empty CORS origins result in an empty list")
    void emptyCors() {
        context.withPropertyValues("cz.security.jwt.secret=" + VALID_SECRET, "cz.security.cors.origins=")
                .run(ctx -> assertThat(
                                ctx.getBean(SecurityProperties.class).cors().origins())
                        .isEmpty());
    }

    @Configuration(proxyBeanMethods = false)
    @EnableConfigurationProperties(SecurityProperties.class)
    static class Config {}
}
