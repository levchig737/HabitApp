package org.habitApp.auth;

import org.habitApp.utils.YamlPropertySourceFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
@TestPropertySource(value = "classpath:application.yml", factory = YamlPropertySourceFactory.class)
public class JwtUtilTest {

    private static final String SECRET = "testSecretKey";
    private static final int EXPIRATION_TIME = 60000; // 1 минута для тестирования

    private JwtUtil jwtUtil;

    @BeforeEach
    public void setUp() {
        jwtUtil = new JwtUtil(SECRET, EXPIRATION_TIME);
    }

    @Test
    @DisplayName("[generate] Генерация токена должна быть успешной")
    public void shouldGenerateTokenSuccessfully() {
        String email = "test@example.com";

        String token = jwtUtil.generate(email);

        assertThat(token).isNotNull();
        assertThat(jwtUtil.verifyAndGetUserId(token)).isEqualTo(email);
    }

    @Test
    @DisplayName("[verifyAndGetUserId] Верификация должна вернуть email для корректного токена")
    public void shouldVerifyAndReturnEmail() {
        String email = "test@example.com";
        String token = jwtUtil.generate(email);

        String resultEmail = jwtUtil.verifyAndGetUserId(token);

        assertThat(resultEmail).isEqualTo(email);
    }

    @Test
    @DisplayName("[verifyAndGetUserId] Верификация должна вернуть null для неверного токена")
    public void shouldReturnNullForInvalidToken() {
        String invalidToken = "invalid.token.here";

        String resultEmail = jwtUtil.verifyAndGetUserId(invalidToken);

        assertNull(resultEmail, "Верификация неверного токена должна возвращать null");
    }

    @Test
    @DisplayName("[verifyAndGetUserId] Верификация должна вернуть null для истекшего токена")
    public void shouldReturnNullForExpiredToken() throws InterruptedException {
        String email = "test@example.com";

        JwtUtil jwtUtilShortExpiry = new JwtUtil(SECRET, 1); // Очень короткий срок действия
        String token = jwtUtilShortExpiry.generate(email);

        Thread.sleep(10);  // Ждем 10 мс для истечения срока действия токена

        String resultEmail = jwtUtil.verifyAndGetUserId(token);

        assertNull(resultEmail, "Верификация истекшего токена должна возвращать null");
    }

    @Test
    @DisplayName("[verifyAndGetUserId] Верификация должна обрабатывать исключение JWTVerificationException")
    public void shouldHandleJWTVerificationException() {
        JwtUtil jwtUtilWithInvalidSecret = new JwtUtil("differentSecret", EXPIRATION_TIME);
        String email = "test@example.com";
        String token = jwtUtil.generate(email);

        // Пытаемся проверить токен с неверным секретным ключом
        String resultEmail = jwtUtilWithInvalidSecret.verifyAndGetUserId(token);

        assertNull(resultEmail, "Верификация токена с неверным секретом должна возвращать null");
    }
}
