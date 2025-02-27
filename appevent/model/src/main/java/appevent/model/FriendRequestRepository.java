package appevent.model;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, UUID> {
    List<FriendRequest> findByReceiverIdAndAcceptedFalse(UUID receiverId);
    List<FriendRequest> findBySenderIdAndAcceptedFalse(UUID senderId);
    boolean existsBySenderIdAndReceiverIdAndAcceptedFalse(UUID senderId, UUID receiverId);
}