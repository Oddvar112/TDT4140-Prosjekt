package appevent.server;

import appevent.dto.FriendRequestDTO;
import appevent.dto.UserDTO;
import appevent.model.Activity;
import appevent.model.ActivityRepository;
import appevent.model.FriendRequest;
import appevent.model.FriendRequestRepository;
import appevent.model.User;
import appevent.model.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service class for managing friend-related operations.
 */
@Service
public class FriendService {
    private final UserRepository userRepository;
    private final FriendRequestRepository friendRequestRepository;
    private final ActivityRepository activityRepository;

    public FriendService(final UserRepository userRepository, final FriendRequestRepository friendRequestRepository, final ActivityRepository activityRepository) {
        this.userRepository = userRepository;
        this.friendRequestRepository = friendRequestRepository;
        this.activityRepository = activityRepository;
    }

    /**
     * Retrieves the list of friends for a given user.
     *
     * @param userId the ID of the user
     * @return a list of UserDTO representing the user's friends
     */
    @Transactional(readOnly = true)
    public List<UserDTO> getFriends(final UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Bruker ikke funnet"));

        return user.getFriends().stream()
                .map(friend -> new UserDTO(friend.getId(), friend.getBrukernavn()))
                .collect(Collectors.toList());
    }

    /**
     * Sends a friend request from one user to another.
     *
     * @param senderId the ID of the sender
     * @param receiverId the ID of the receiver
     */
    @Transactional
    public void sendFriendRequest(final UUID senderId, final UUID receiverId) {
        if (senderId.equals(receiverId)) {
            throw new IllegalArgumentException("Du kan ikke sende venneforespørsel til deg selv");
        }

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("Bruker ikke funnet"));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new IllegalArgumentException("Mottaker ikke funnet"));

        if (sender.getFriends().contains(receiver)) {
            throw new IllegalArgumentException("Denne brukeren er allerede din venn");
        }

        if (friendRequestRepository.existsBySenderIdAndReceiverIdAndAcceptedFalse(senderId, receiverId)) {
            throw new IllegalArgumentException("Du har allerede sendt en venneforespørsel til denne brukeren");
        }

        FriendRequest request = new FriendRequest(sender, receiver);
        friendRequestRepository.save(request);
    }

    /**
     * Accepts a friend request.
     *
     * @param requestId the ID of the friend request
     * @param receiverId the ID of the receiver
     */
    @Transactional
    public void acceptFriendRequest(final UUID requestId, final UUID receiverId) {
        FriendRequest request = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Venneforespørsel ikke funnet"));

        if (!request.getReceiver().getId().equals(receiverId)) {
            throw new IllegalArgumentException("Ikke tilgang til denne forespørselen");
        }

        request.setAccepted(true);
        User sender = request.getSender();
        User receiver = request.getReceiver();

        sender.addFriend(receiver);

        userRepository.save(sender);
        userRepository.save(receiver);
        friendRequestRepository.save(request);
    }

    /**
     * Rejects a friend request.
     *
     * @param requestId the ID of the friend request
     * @param receiverId the ID of the receiver
     */
    @Transactional
    public void rejectFriendRequest(final UUID requestId, final UUID receiverId) {
        FriendRequest request = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Venneforespørsel ikke funnet"));

        if (!request.getReceiver().getId().equals(receiverId)) {
            throw new IllegalArgumentException("Ikke tilgang til denne forespørselen");
        }

        friendRequestRepository.delete(request);
    }

    /**
     * Retrieves the list of pending friend requests for a user.
     *
     * @param userId the ID of the user
     * @return a list of FriendRequestDTO representing the pending friend requests
     */
    @Transactional
    public List<FriendRequestDTO> getPendingRequests(final UUID userId) {
        return friendRequestRepository.findByReceiverIdAndAcceptedFalse(userId).stream()
            .map(request -> new FriendRequestDTO(
                request.getId(),
                new UserDTO(request.getSender().getId(), request.getSender().getBrukernavn())))
            .collect(Collectors.toList());
    }

    /**
     * Removes a friend from a user's friend list.
     *
     * @param userId the ID of the user
     * @param friendId the ID of the friend to remove
     */
    @Transactional
    public void removeFriend(final UUID userId, final UUID friendId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Bruker ikke funnet"));
        User friend = userRepository.findById(friendId)
                .orElseThrow(() -> new IllegalArgumentException("Vennen ble ikke funnet"));

        if (!user.getFriends().contains(friend)) {
            throw new IllegalArgumentException("Denne brukeren er ikke din venn");
        }

        user.removeFriend(friend);
        userRepository.save(user);
        userRepository.save(friend);
    }

    /**
     * Retrieves the list of friends who are not invited to a specific event.
     *
     * @param userId the ID of the user
     * @param eventId the ID of the event
     * @return a list of UserDTO representing the friends not invited to the event
     */
    @Transactional(readOnly = true)
    public List<UserDTO> getFriendsNotInvitedToEvent(final UUID userId, final UUID eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Bruker ikke funnet"));

        Activity activity = activityRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Activity not found"));

        return user.getFriends().stream()
            .filter(friend -> !activity.getInvitedUsers().contains(friend))
            .filter(friend -> !activity.getParticipants().contains(friend))
            .map(friend -> new UserDTO(friend.getId(), friend.getBrukernavn()))
            .collect(Collectors.toList());
    }
}
