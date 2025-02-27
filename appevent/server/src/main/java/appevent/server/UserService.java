package appevent.server;

import appevent.dto.UserDTO;
import appevent.model.Activity;
import appevent.model.ActivityRepository;
import appevent.model.FriendRequestRepository;
import appevent.model.User;
import appevent.model.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service class for handling user-related operations.
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final ActivityRepository activityRepository;
    private final FriendRequestRepository requestRepo;

    /**
     * Constructs a UserService with the specified repositories.
     *
     * @param userRepository repository for user operations
     * @param activityRepository repository for activity operations
     * @param friendRequestRepository repository for friend request operations
     */
    public UserService(final UserRepository userRepository, final ActivityRepository activityRepository, final FriendRequestRepository friendRequestRepository) {
        this.userRepository = userRepository;
        this.activityRepository = activityRepository;
        this.requestRepo = friendRequestRepository;
    }

    /**
     * Search for users by username.
     *
     * @param searchTerm term to search for
     * @param currentUserId ID of the current user (to exclude from results)
     * @return list of matching users
     */
    @Transactional
    public List<UserDTO> searchUsers(final String searchTerm, final UUID currentUserId) {
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new IllegalArgumentException("Bruker ikke funnet"));
        return userRepository.findByBrukernavnContainingIgnoreCase(searchTerm).stream()
                .filter(user -> !user.getId().equals(currentUserId))
                .filter(user -> !currentUser.getFriends().contains(user))
                .filter(user -> !requestRepo.existsBySenderIdAndReceiverIdAndAcceptedFalse(currentUserId, user.getId()))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    /**
     * Get user profile information.
     *
     * @param userId ID of the user
     * @return user profile information
     * @throws IllegalArgumentException if user is not found
     */
    @Transactional
    public UserDTO getUserProfile(final UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Bruker ikke funnet"));
        return convertToDTO(user);
    }

    /**
     * Get user information by ID.
     *
     * @param userId ID of the user
     * @return user information
     * @throws IllegalArgumentException if user is not found
     */
    @Transactional
    public UserDTO getUserById(final UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Bruker ikke funnet"));
        return convertToDTO(user);
    }

    /**
     * Check if a username is available.
     *
     * @param username username to check
     * @return true if username is available, false otherwise
     */
    @Transactional
    public boolean isUsernameAvailable(final String username) {
        return userRepository.findByBrukernavn(username).isEmpty();
    }

    /**
     * Get list of users registered for an activity.
     *
     * @param activityId ID of the activity
     * @return list of users registered for the activity
     * @throws IllegalArgumentException if activity is not found
     */
    @Transactional
    public List<UserDTO> getUsersByActivity(final UUID activityId) {
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new IllegalArgumentException("Aktivitet ikke funnet"));
        return activity.getParticipants().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Convert a User entity to a UserDTO.
     *
     * @param user the user entity to convert
     * @return the converted UserDTO
     */
    private UserDTO convertToDTO(final User user) {
        return new UserDTO(user.getId(), user.getBrukernavn());
    }
}
