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
        Long userId = 12345L;

        String token = jwtUtil.generate(userId);

        assertThat(token).isNotNull();
        assertThat(jwtUtil.verifyAndGetUserId(token)).isEqualTo(userId);
    }

    @Test
    @DisplayName("[verifyAndGetUserId] Верификация должна вернуть userId для корректного токена")
    public void shouldVerifyAndReturnUserId() {
        Long userId = 12345L;
        String token = jwtUtil.generate(userId);

        Long resultUserId = jwtUtil.verifyAndGetUserId(token);

        assertThat(resultUserId).isEqualTo(userId);
    }

    @Test
    @DisplayName("[verifyAndGetUserId] Верификация должна вернуть null для неверного токена")
    public void shouldReturnNullForInvalidToken() {
        String invalidToken = "invalid.token.here";

        Long resultUserId = jwtUtil.verifyAndGetUserId(invalidToken);

        assertNull(resultUserId, "Верификация неверного токена должна возвращать null");
    }

    @Test
    @DisplayName("[verifyAndGetUserId] Верификация должна вернуть null для истекшего токена")
    public void shouldReturnNullForExpiredToken() throws InterruptedException {
        Long userId = 12345L;

        JwtUtil jwtUtilShortExpiry = new JwtUtil(SECRET, 1);
        String token = jwtUtilShortExpiry.generate(userId);

        Thread.sleep(10);  // Ждем 10 мс для истечения срока

        Long resultUserId = jwtUtil.verifyAndGetUserId(token);

        assertNull(resultUserId, "Верификация истекшего токена должна возвращать null");
    }

    @Test
    @DisplayName("[verifyAndGetUserId] Верификация должна обрабатывать исключение JWTVerificationException")
    public void shouldHandleJWTVerificationException() {
        JwtUtil jwtUtilWithInvalidSecret = new JwtUtil("differentSecret", EXPIRATION_TIME);
        Long userId = 12345L;
        String token = jwtUtil.generate(userId);

        // Пытаемся проверить токен с неверным секретным ключом
        Long resultUserId = jwtUtilWithInvalidSecret.verifyAndGetUserId(token);

        assertNull(resultUserId, "Верификация токена с неверным секретом должна возвращать null");
    }
}
