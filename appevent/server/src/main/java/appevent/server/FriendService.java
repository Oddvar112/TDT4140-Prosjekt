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

@Service
public class FriendService {
    private final UserRepository userRepository;
    private final FriendRequestRepository friendRequestRepository;
    private final ActivityRepository activityRepository;

    public FriendService(UserRepository userRepository, FriendRequestRepository friendRequestRepository, ActivityRepository activityRepository) {
        this.userRepository = userRepository;
        this.friendRequestRepository = friendRequestRepository;
        this.activityRepository = activityRepository;
    }

    @Transactional(readOnly = true)
    public List<UserDTO> getFriends(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Bruker ikke funnet"));

        return user.getFriends().stream()
                .map(friend -> new UserDTO(friend.getId(), friend.getBrukernavn()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void sendFriendRequest(UUID senderId, UUID receiverId) {
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

    @Transactional
    public void acceptFriendRequest(UUID requestId, UUID receiverId) {
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

    @Transactional
    public void rejectFriendRequest(UUID requestId, UUID receiverId) {
        FriendRequest request = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Venneforespørsel ikke funnet"));

        if (!request.getReceiver().getId().equals(receiverId)) {
            throw new IllegalArgumentException("Ikke tilgang til denne forespørselen");
        }

        friendRequestRepository.delete(request);
    }

    @Transactional
    public List<FriendRequestDTO> getPendingRequests(UUID userId) {
        return friendRequestRepository.findByReceiverIdAndAcceptedFalse(userId).stream()
            .map(request -> new FriendRequestDTO(
                request.getId(),
                new UserDTO(request.getSender().getId(), request.getSender().getBrukernavn())))
            .collect(Collectors.toList());
    }

    @Transactional
    public void removeFriend(UUID userId, UUID friendId) {
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

    @Transactional(readOnly = true)
    public List<UserDTO> getFriendsNotInvitedToEvent(UUID userId, UUID eventId) {
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