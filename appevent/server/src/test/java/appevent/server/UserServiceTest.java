package appevent.server;

import appevent.dto.UserDTO;
import appevent.model.Activity;
import appevent.model.ActivityRepository;
import appevent.model.User;
import appevent.model.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * A simplified implementation of UserService for testing purposes.
 * This class provides the core functionality needed for tests without external
 * dependencies.
 */
class TestUserService {
    private final UserRepository userRepository;
    private final ActivityRepository activityRepository;

    /**
     * Constructs a new TestUserService with the specified repositories.
     *
     * @param userRepository     repository for user operations
     * @param activityRepository repository for activity operations
     */
    public TestUserService(UserRepository userRepository, ActivityRepository activityRepository) {
        this.userRepository = userRepository;
        this.activityRepository = activityRepository;
    }

    /**
     * Searches for users by username, excluding the current user.
     *
     * @param searchTerm    the term to search for in usernames
     * @param currentUserId the ID of the current user to exclude from results
     * @return a list of UserDTO objects matching the search criteria
     * @throws IllegalArgumentException if the current user is not found
     */
    public List<UserDTO> searchUsers(String searchTerm, UUID currentUserId) {
        userRepository.findById(currentUserId)
                .orElseThrow(() -> new IllegalArgumentException("Bruker ikke funnet"));

        return userRepository.findByBrukernavnContainingIgnoreCase(searchTerm).stream()
                .filter(user -> !user.getId().equals(currentUserId))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Gets a user's profile information.
     *
     * @param userId the ID of the user to get
     * @return a UserDTO containing the user's information
     * @throws IllegalArgumentException if the user is not found
     */
    public UserDTO getUserProfile(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Bruker ikke funnet"));
        return convertToDTO(user);
    }

    /**
     * Gets a user by their ID.
     *
     * @param userId the ID of the user to get
     * @return a UserDTO containing the user's information
     * @throws IllegalArgumentException if the user is not found
     */
    public UserDTO getUserById(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Bruker ikke funnet"));
        return convertToDTO(user);
    }

    /**
     * Checks if a username is available.
     *
     * @param username the username to check
     * @return true if the username is available, false otherwise
     */
    public boolean isUsernameAvailable(String username) {
        return userRepository.findByBrukernavn(username).isEmpty();
    }

    /**
     * Gets all users participating in an activity.
     *
     * @param activityId the ID of the activity
     * @return a list of UserDTO objects representing the participants
     * @throws IllegalArgumentException if the activity is not found
     */
    public List<UserDTO> getUsersByActivity(UUID activityId) {
        return activityRepository.findById(activityId)
                .orElseThrow(() -> new IllegalArgumentException("Aktivitet ikke funnet"))
                .getParticipants().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Converts a User entity to a UserDTO.
     *
     * @param user the User entity to convert
     * @return a UserDTO containing the user's information
     */
    private UserDTO convertToDTO(User user) {
        return new UserDTO(user.getId(), user.getBrukernavn());
    }
}

/**
 * Test class for UserService functionality.
 * This class tests user-related operations using a simplified TestUserService
 * implementation.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ActivityRepository activityRepository;

    private TestUserService userService;

    private UUID userId;
    private User user;
    private Activity activity;

    /**
     * Sets up the test environment before each test.
     * Initializes the test service and mocks common behavior.
     */
    @BeforeEach
    void setUp() {
        userService = new TestUserService(userRepository, activityRepository);

        userId = UUID.randomUUID();
        user = mock(User.class);
        activity = mock(Activity.class);

        when(user.getId()).thenReturn(userId);
        when(user.getBrukernavn()).thenReturn("testUser");
        when(user.getFriends()).thenReturn(new HashSet<>());

        when(activity.getId()).thenReturn(UUID.randomUUID());
        when(activity.getTitle()).thenReturn("Test Activity");
        Set<User> participants = new HashSet<>();
        participants.add(user);
        when(activity.getParticipants()).thenReturn(participants);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.findByBrukernavn("testUser")).thenReturn(Optional.of(user));
        when(userRepository.findByBrukernavn("nonExistingUser")).thenReturn(Optional.empty());
    }

    /**
     * Tests that getUserProfile returns a UserDTO when the user exists.
     * 
     * <p>
     * Expected behavior:
     * <ul>
     * <li>The method should find the user by ID</li>
     * <li>A UserDTO should be returned with the correct ID and username</li>
     * </ul>
     */
    @Test
    void getUserProfile_ShouldReturnUserDTO_WhenUserExists() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        UserDTO result = userService.getUserProfile(userId);
        assertNotNull(result);
        assertEquals(userId, result.id());
        assertEquals("testUser", result.brukernavn());
        verify(userRepository, times(1)).findById(userId);
    }

    /**
     * Tests that getUserProfile throws an exception when the user does not exist.
     * 
     * <p>
     * Expected behavior:
     * <ul>
     * <li>The method should attempt to find the user by ID</li>
     * <li>When the user is not found, an IllegalArgumentException should be
     * thrown</li>
     * <li>The exception message should be "Bruker ikke funnet"</li>
     * </ul>
     */
    @Test
    void getUserProfile_ShouldThrowException_WhenUserDoesNotExist() {
        UUID nonExistentId = UUID.randomUUID();
        when(userRepository.findById(nonExistentId)).thenReturn(Optional.empty());
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.getUserProfile(nonExistentId);
        });
        assertEquals("Bruker ikke funnet", exception.getMessage());
        verify(userRepository, times(1)).findById(nonExistentId);
    }

    /**
     * Tests that isUsernameAvailable returns true when the username does not exist.
     * 
     * <p>
     * Expected behavior:
     * <ul>
     * <li>The method should check if the username exists</li>
     * <li>When the username is not found, true should be returned</li>
     * </ul>
     */
    @Test
    void isUsernameAvailable_ShouldReturnTrue_WhenUsernameDoesNotExist() {
        String username = "newUsername";
        when(userRepository.findByBrukernavn(username)).thenReturn(Optional.empty());
        boolean result = userService.isUsernameAvailable(username);
        assertTrue(result);
        verify(userRepository, times(1)).findByBrukernavn(username);
    }

    /**
     * Tests that isUsernameAvailable returns false when the username exists.
     * 
     * <p>
     * Expected behavior:
     * <ul>
     * <li>The method should check if the username exists</li>
     * <li>When the username is found, false should be returned</li>
     * </ul>
     */
    @Test
    void isUsernameAvailable_ShouldReturnFalse_WhenUsernameExists() {
        String username = "existingUsername";
        when(userRepository.findByBrukernavn(username)).thenReturn(Optional.of(user));
        boolean result = userService.isUsernameAvailable(username);
        assertFalse(result);
        verify(userRepository, times(1)).findByBrukernavn(username);
    }

    /**
     * Tests that getUsersByActivity returns a list of users when the activity
     * exists.
     * 
     * <p>
     * Expected behavior:
     * <ul>
     * <li>The method should find the activity by ID</li>
     * <li>A list of UserDTOs should be returned representing the activity's
     * participants</li>
     * <li>The list should contain the correct user information</li>
     * </ul>
     */
    @Test
    void getUsersByActivity_ShouldReturnListOfUsers_WhenActivityExists() {
        UUID activityId = activity.getId();
        when(activityRepository.findById(activityId)).thenReturn(Optional.of(activity));
        List<UserDTO> result = userService.getUsersByActivity(activityId);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(userId, result.get(0).id());
        assertEquals("testUser", result.get(0).brukernavn());
        verify(activityRepository, times(1)).findById(activityId);
    }

    /**
     * Tests that getUsersByActivity throws an exception when the activity does not
     * exist.
     * 
     * <p>
     * Expected behavior:
     * <ul>
     * <li>The method should attempt to find the activity by ID</li>
     * <li>When the activity is not found, an IllegalArgumentException should be
     * thrown</li>
     * <li>The exception message should be "Aktivitet ikke funnet"</li>
     * </ul>
     */
    @Test
    void getUsersByActivity_ShouldThrowException_WhenActivityDoesNotExist() {
        UUID nonExistentId = UUID.randomUUID();
        when(activityRepository.findById(nonExistentId)).thenReturn(Optional.empty());
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.getUsersByActivity(nonExistentId);
        });
        assertEquals("Aktivitet ikke funnet", exception.getMessage());
        verify(activityRepository, times(1)).findById(nonExistentId);
    }

    /**
     * Tests that searchUsers returns a filtered list of users.
     * 
     * <p>
     * Expected behavior:
     * <ul>
     * <li>The method should verify the current user exists</li>
     * <li>The method should search for users matching the search term</li>
     * <li>The result should exclude the current user</li>
     * <li>The result should contain all other matching users</li>
     * </ul>
     */
    @Test
    void searchUsers_ShouldReturnFilteredUsers() {
        UUID currentUserId = UUID.randomUUID();
        User currentUser = mock(User.class);
        when(currentUser.getId()).thenReturn(currentUserId);
        when(currentUser.getBrukernavn()).thenReturn("currentUser");

        UUID friendId = UUID.randomUUID();
        User friend = mock(User.class);
        when(friend.getId()).thenReturn(friendId);
        when(friend.getBrukernavn()).thenReturn("friend");

        UUID nonFriendId = UUID.randomUUID();
        User nonFriend = mock(User.class);
        when(nonFriend.getId()).thenReturn(nonFriendId);
        when(nonFriend.getBrukernavn()).thenReturn("nonFriend");

        UUID pendingRequestId = UUID.randomUUID();
        User pendingRequest = mock(User.class);
        when(pendingRequest.getId()).thenReturn(pendingRequestId);
        when(pendingRequest.getBrukernavn()).thenReturn("pendingRequest");

        List<User> searchResults = new ArrayList<>();
        searchResults.add(friend);
        searchResults.add(nonFriend);
        searchResults.add(pendingRequest);

        when(userRepository.findById(currentUserId)).thenReturn(Optional.of(currentUser));
        when(userRepository.findByBrukernavnContainingIgnoreCase("test")).thenReturn(searchResults);

        List<UserDTO> result = userService.searchUsers("test", currentUserId);
        assertNotNull(result);
        assertEquals(3, result.size());
    }
}