package br.com.capitulozero;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

// Generated context test; replaced by ApplicationIT and @IntegrationTest in task 2.1.
@Import(TestcontainersConfiguration.class)
@SpringBootTest(properties = "cz.security.jwt.secret=test-secret-capitulo-zero-with-more-than-32-bytes")
class CapituloZeroApplicationTest {

    @Test
    void contextLoads() {}
}
