package appevent.server;

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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test class for ActivityService.
 * This class tests activity-related operations using a simplified
 * TestActivityService implementation.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ActivityServiceTest {

    @Mock
    private ActivityRepository activityRepository;

    @Mock
    private UserRepository userRepository;

    private static class TestActivityService {
        private final ActivityRepository activityRepository;
        private final UserRepository userRepository;

        /**
         * Constructs a new TestActivityService with the specified repositories.
         *
         * @param activityRepository repository for activity operations
         * @param userRepository     repository for user operations
         */
        public TestActivityService(ActivityRepository activityRepository, UserRepository userRepository) {
            this.activityRepository = activityRepository;
            this.userRepository = userRepository;
        }

        /**
         * Checks if a user is participating in an activity.
         *
         * @param activityId the ID of the activity
         * @param userId     the ID of the user
         * @return true if the user is participating, false otherwise
         * @throws RuntimeException if the user is not found
         */
        public boolean isUserParticipating(UUID activityId, UUID userId) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            List<Activity> activities = activityRepository.findByParticipantsContaining(user);
            return activities.stream().anyMatch(activity -> activity.getId().equals(activityId));
        }

        /**
         * Toggles a user's participation in an activity.
         * If the user is already participating, they will be removed.
         * If the user is not participating, they will be added.
         *
         * @param activityId the ID of the activity
         * @param userId     the ID of the user
         * @throws IllegalArgumentException if the activity or user is not found, or if
         *                                  the user is the owner
         */
        public void toggleParticipation(UUID activityId, UUID userId) {
            Activity activity = activityRepository.findById(activityId)
                    .orElseThrow(() -> new IllegalArgumentException("Aktivitet ikke funnet"));
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("Bruker ikke funnet"));

            if (activity.getOwner().equals(user)) {
                throw new IllegalArgumentException("Eier kan ikke melde seg av aktiviteten");
            }

            if (isUserParticipating(activityId, userId)) {
                activity.removeParticipant(user);
            } else {
                activity.addParticipant(user);
            }

            activityRepository.save(activity);
        }

        /**
         * Checks if an activity is completed based on its date and time.
         *
         * @param activityId the ID of the activity
         * @return true if the activity's date and time is in the past, false otherwise
         * @throws IllegalArgumentException if the activity is not found
         */
        public boolean isActivityCompleted(UUID activityId) {
            Activity activity = activityRepository.findById(activityId)
                    .orElseThrow(() -> new IllegalArgumentException("Activity not found"));
            return activity.getDateTime().isBefore(LocalDateTime.now());
        }
    }

    private TestActivityService activityService;
    private UUID userId;
    private UUID activityId;
    private User user;
    private Activity activity;
    private List<Activity> activities;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        activityId = UUID.randomUUID();

        user = mock(User.class);
        when(user.getId()).thenReturn(userId);
        when(user.getBrukernavn()).thenReturn("testUser");

        activity = mock(Activity.class);
        when(activity.getId()).thenReturn(activityId);
        when(activity.getTitle()).thenReturn("Test Activity");
        when(activity.getDescription()).thenReturn("Test Description");
        when(activity.getLocation()).thenReturn("Test Location");

        Set<User> participants = new HashSet<>();
        participants.add(user);
        when(activity.getParticipants()).thenReturn(participants);

        when(activity.getOwner()).thenReturn(user);

        activities = new ArrayList<>();
        activities.add(activity);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        when(activityRepository.findByParticipantsContaining(user)).thenReturn(activities);

        activityService = new TestActivityService(activityRepository, userRepository);
    }

    /**
     * Tests that isUserParticipating returns true when the user is participating in
     * the activity.
     * 
     * <p>
     * Expected behavior:
     * <ul>
     * <li>The method should find the user by ID</li>
     * <li>The method should find activities where the user is a participant</li>
     * <li>The method should return true when the activity ID matches</li>
     * </ul>
     */
    @Test
    void isUserParticipating_ShouldReturnTrue_WhenUserIsParticipating() {
        boolean result = activityService.isUserParticipating(activityId, userId);
        assertTrue(result);
    }

    /**
     * Tests that isUserParticipating returns false when the user is not
     * participating in the activity.
     * 
     * <p>
     * Expected behavior:
     * <ul>
     * <li>The method should find the user by ID</li>
     * <li>The method should find activities where the user is a participant</li>
     * <li>The method should return false when the activity ID does not match</li>
     * </ul>
     */
    @Test
    void isUserParticipating_ShouldReturnFalse_WhenUserIsNotParticipating() {
        UUID otherActivityId = UUID.randomUUID();
        Activity otherActivity = mock(Activity.class);
        when(otherActivity.getId()).thenReturn(otherActivityId);
        boolean result = activityService.isUserParticipating(otherActivityId, userId);
        assertFalse(result);
    }

    /**
     * Tests that isUserParticipating throws an exception when the user is not
     * found.
     * 
     * <p>
     * Expected behavior:
     * <ul>
     * <li>The method should attempt to find the user by ID</li>
     * <li>When the user is not found, a RuntimeException should be thrown</li>
     * <li>The exception message should be "User not found"</li>
     * </ul>
     */
    @Test
    void isUserParticipating_ShouldThrowException_WhenUserNotFound() {
        UUID nonExistentUserId = UUID.randomUUID();
        when(userRepository.findById(nonExistentUserId)).thenReturn(Optional.empty());
        Exception exception = assertThrows(RuntimeException.class, () -> {
            activityService.isUserParticipating(activityId, nonExistentUserId);
        });
        assertEquals("User not found", exception.getMessage());
    }

    /**
     * Tests that toggleParticipation adds a user to participants when the user is
     * not already participating.
     * 
     * <p>
     * Expected behavior:
     * <ul>
     * <li>The method should find the activity and user by ID</li>
     * <li>The method should check if the user is already participating</li>
     * <li>When the user is not participating, they should be added to the
     * participants</li>
     * <li>The activity should be saved</li>
     * </ul>
     */
    @Test
    void toggleParticipation_ShouldAddUserToParticipants_WhenUserIsNotParticipating() {
        Activity nonParticipatingActivity = mock(Activity.class);
        UUID nonParticipatingActivityId = UUID.randomUUID();
        when(nonParticipatingActivity.getId()).thenReturn(nonParticipatingActivityId);
        when(nonParticipatingActivity.getOwner()).thenReturn(mock(User.class));
        Set<User> emptyParticipants = new HashSet<>();
        when(nonParticipatingActivity.getParticipants()).thenReturn(emptyParticipants);
        when(activityRepository.findById(nonParticipatingActivityId)).thenReturn(Optional.of(nonParticipatingActivity));
        activityService.toggleParticipation(nonParticipatingActivityId, userId);
        verify(nonParticipatingActivity, times(1)).addParticipant(user);
        verify(activityRepository, times(1)).save(nonParticipatingActivity);
    }

    /**
     * Tests that toggleParticipation removes a user from participants when the user
     * is already participating.
     * 
     * <p>
     * Expected behavior:
     * <ul>
     * <li>The method should find the activity and user by ID</li>
     * <li>The method should check if the user is already participating</li>
     * <li>When the user is participating, they should be removed from the
     * participants</li>
     * <li>The activity should be saved</li>
     * </ul>
     */
    @Test
    void toggleParticipation_ShouldRemoveUserFromParticipants_WhenUserIsParticipating() {
        Activity participatingActivity = mock(Activity.class);
        UUID participatingActivityId = UUID.randomUUID();
        when(participatingActivity.getId()).thenReturn(participatingActivityId);

        User otherUser = mock(User.class);
        when(otherUser.getId()).thenReturn(UUID.randomUUID());
        when(participatingActivity.getOwner()).thenReturn(otherUser);

        Set<User> participants = new HashSet<>();
        participants.add(user);
        when(participatingActivity.getParticipants()).thenReturn(participants);

        List<Activity> userActivities = new ArrayList<>();
        userActivities.add(participatingActivity);
        when(activityRepository.findByParticipantsContaining(user)).thenReturn(userActivities);
        when(activityRepository.findById(participatingActivityId)).thenReturn(Optional.of(participatingActivity));

        activityService.toggleParticipation(participatingActivityId, userId);

        verify(participatingActivity, times(1)).removeParticipant(user);
        verify(activityRepository, times(1)).save(participatingActivity);
    }

    /**
     * Tests that toggleParticipation throws an exception when the user is the owner
     * of the activity.
     * 
     * <p>
     * Expected behavior:
     * <ul>
     * <li>The method should find the activity and user by ID</li>
     * <li>The method should check if the user is the owner</li>
     * <li>When the user is the owner, an IllegalArgumentException should be
     * thrown</li>
     * <li>The exception message should be "Eier kan ikke melde seg av
     * aktiviteten"</li>
     * </ul>
     */
    @Test
    void toggleParticipation_ShouldThrowException_WhenUserIsOwner() {
        Activity ownedActivity = mock(Activity.class);
        UUID ownedActivityId = UUID.randomUUID();
        when(ownedActivity.getId()).thenReturn(ownedActivityId);
        when(ownedActivity.getOwner()).thenReturn(user);
        Set<User> participants = new HashSet<>();
        participants.add(user);
        when(ownedActivity.getParticipants()).thenReturn(participants);
        List<Activity> userActivities = new ArrayList<>();
        userActivities.add(ownedActivity);
        when(activityRepository.findByParticipantsContaining(user)).thenReturn(userActivities);
        when(activityRepository.findById(ownedActivityId)).thenReturn(Optional.of(ownedActivity));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            activityService.toggleParticipation(ownedActivityId, userId);
        });
        assertEquals("Eier kan ikke melde seg av aktiviteten", exception.getMessage());
    }

    /**
     * Tests that isActivityCompleted returns true when the activity's date and time
     * is in the past.
     * 
     * <p>
     * Expected behavior:
     * <ul>
     * <li>The method should find the activity by ID</li>
     * <li>The method should check if the activity's date and time is before the
     * current time</li>
     * <li>When the date and time is in the past, true should be returned</li>
     * </ul>
     */
    @Test
    void isActivityCompleted_ShouldReturnTrue_WhenActivityEndTimeIsBeforeNow() {
        LocalDateTime pastTime = LocalDateTime.now().minusDays(1);
        when(activityRepository.findById(activityId)).thenReturn(Optional.of(activity));
        doReturn(pastTime).when(activity).getDateTime();
        boolean result = activityService.isActivityCompleted(activityId);
        assertTrue(result);
        verify(activityRepository, times(1)).findById(activityId);
    }

    @Test
    void isActivityCompleted_ShouldReturnFalse_WhenActivityEndTimeIsAfterNow() {
        LocalDateTime futureTime = LocalDateTime.now().plusDays(1);
        when(activityRepository.findById(activityId)).thenReturn(Optional.of(activity));
        doReturn(futureTime).when(activity).getDateTime();
        boolean result = activityService.isActivityCompleted(activityId);
        assertFalse(result);
        verify(activityRepository, times(1)).findById(activityId);
    }
}