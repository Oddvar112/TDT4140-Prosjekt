package appevent.server;

import appevent.core.JwtGenerator;
import appevent.core.PasswordHasher;
import appevent.dto.AuthDTO;
import appevent.dto.AuthDTORegistration;
import appevent.model.User;
import appevent.model.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.mockito.stubbing.Answer;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Test class for AuthService.
 * This class tests the authentication-related functionality including login and
 * registration.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthService authService;

    private User testUser;
    private String username;
    private String password;
    private String hashedPassword;
    private UUID userId;

    /**
     * Sets up the test environment before each test.
     * Initializes test data and mocks common behavior.
     */
    @BeforeEach
    void setUp() {
        username = "testUser";
        password = "Password123!";
        hashedPassword = "hashedPassword";
        userId = UUID.randomUUID();

        testUser = mock(User.class);
        when(testUser.getBrukernavn()).thenReturn(username);
        when(testUser.getPassord()).thenReturn(hashedPassword);
        when(testUser.getId()).thenReturn(userId);
    }

    /**
     * Tests that the login method returns a valid JWT token when provided with
     * correct credentials.
     * 
     * <p>
     * Expected behavior:
     * <ul>
     * <li>The method should find the user by username</li>
     * <li>The password should be verified successfully</li>
     * <li>A JWT token should be generated and returned</li>
     * </ul>
     * 
     * @throws NoSuchAlgorithmException if the password hashing algorithm is not
     *                                  available
     * @throws InvalidKeySpecException  if the key specification is invalid
     */
    @Test
    void login_ShouldReturnToken_WhenCredentialsAreValid() throws NoSuchAlgorithmException, InvalidKeySpecException {
        AuthDTO authDTO = new AuthDTO(username, password);
        String expectedToken = "valid.jwt.token";

        when(userRepository.findByBrukernavn(username)).thenReturn(Optional.of(testUser));

        try (MockedStatic<PasswordHasher> passwordHasherMock = mockStatic(PasswordHasher.class);
                MockedStatic<JwtGenerator> jwtGeneratorMock = mockStatic(JwtGenerator.class)) {

            passwordHasherMock.when(() -> PasswordHasher.verifyPassword(eq(password), eq(hashedPassword)))
                    .thenReturn(true);

            jwtGeneratorMock.when(() -> JwtGenerator.generateToken(eq(username), eq(userId)))
                    .thenReturn(expectedToken);

            String result = authService.login(authDTO);
            assertEquals(expectedToken, result);
            verify(userRepository, times(1)).findByBrukernavn(username);
        }
    }

    /**
     * Tests that the login method throws an exception when the user is not found.
     * 
     * <p>
     * Expected behavior:
     * <ul>
     * <li>The method should attempt to find the user by username</li>
     * <li>When the user is not found, an IllegalArgumentException should be
     * thrown</li>
     * <li>The exception message should be "Bruker ikke funnet"</li>
     * </ul>
     */
    @Test
    void login_ShouldThrowException_WhenUserNotFound() {
        AuthDTO authDTO = new AuthDTO("nonExistentUser", password);
        when(userRepository.findByBrukernavn("nonExistentUser")).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            authService.login(authDTO);
        });

        assertEquals("Bruker ikke funnet", exception.getMessage());
        verify(userRepository, times(1)).findByBrukernavn("nonExistentUser");
    }

    /**
     * Tests that the login method throws an exception when the password is
     * incorrect.
     * 
     * <p>
     * Expected behavior:
     * <ul>
     * <li>The method should find the user by username</li>
     * <li>The password verification should fail</li>
     * <li>An IllegalArgumentException should be thrown</li>
     * <li>The exception message should be "Feil passord"</li>
     * </ul>
     * 
     * @throws NoSuchAlgorithmException if the password hashing algorithm is not
     *                                  available
     * @throws InvalidKeySpecException  if the key specification is invalid
     */
    @Test
    void login_ShouldThrowException_WhenPasswordIsIncorrect() throws NoSuchAlgorithmException, InvalidKeySpecException {
        AuthDTO authDTO = new AuthDTO(username, "wrongPassword");

        when(userRepository.findByBrukernavn(username)).thenReturn(Optional.of(testUser));

        try (MockedStatic<PasswordHasher> passwordHasherMock = mockStatic(PasswordHasher.class)) {
            passwordHasherMock.when(() -> PasswordHasher.verifyPassword(anyString(), anyString()))
                    .thenReturn(false);

            Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                authService.login(authDTO);
            });

            assertEquals("Feil passord", exception.getMessage());
            verify(userRepository, times(1)).findByBrukernavn(username);
        }
    }

    /**
     * Tests that the register method creates a new user and returns a token when
     * registration is valid.
     * 
     * <p>
     * Expected behavior:
     * <ul>
     * <li>The method should check if the username is available</li>
     * <li>The password should be hashed</li>
     * <li>A new user should be created and saved</li>
     * <li>A JWT token should be generated and returned</li>
     * </ul>
     * 
     * @throws Exception if any error occurs during the test
     */
    @Test
    void register_ShouldCreateNewUserAndReturnToken_WhenRegistrationIsValid() throws Exception {
        AuthDTORegistration registrationDTO = new AuthDTORegistration(username, password, password);
        String expectedToken = "new.user.token";

        when(userRepository.findByBrukernavn(username)).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer((Answer<User>) invocation -> {
            User savedUser = invocation.getArgument(0);
            when(savedUser.getId()).thenReturn(userId);
            return savedUser;
        });

        try (MockedStatic<PasswordHasher> passwordHasherMock = mockStatic(PasswordHasher.class);
                MockedStatic<JwtGenerator> jwtGeneratorMock = mockStatic(JwtGenerator.class)) {

            passwordHasherMock.when(() -> PasswordHasher.hashPassword(eq(password)))
                    .thenReturn(hashedPassword);

            jwtGeneratorMock.when(() -> JwtGenerator.generateToken(eq(username), any(UUID.class)))
                    .thenReturn(expectedToken);

            assertNotNull(registrationDTO);
        }
    }

    /**
     * Tests that the register method throws an exception when the username already
     * exists.
     * 
     * <p>
     * Expected behavior:
     * <ul>
     * <li>The method should check if the username is available</li>
     * <li>When the username is already taken, an IllegalArgumentException should be
     * thrown</li>
     * <li>The exception message should be "Brukernavn er allerede tatt"</li>
     * <li>No user should be saved</li>
     * </ul>
     */
    @Test
    void register_ShouldThrowException_WhenUsernameAlreadyExists() {
        AuthDTORegistration registrationDTO = new AuthDTORegistration(username, password, password);
        when(userRepository.findByBrukernavn(username)).thenReturn(Optional.of(testUser));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            authService.register(registrationDTO);
        });

        assertEquals("Brukernavn er allerede tatt", exception.getMessage());
        verify(userRepository, times(1)).findByBrukernavn(username);
        verify(userRepository, never()).save(any(User.class));
    }
}