package appevent.model;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Repository interface for managing FriendRequest entities.
 */
public interface FriendRequestRepository extends JpaRepository<FriendRequest, UUID> {

    /**
     * Finds all friend requests received by a user that are not yet accepted.
     *
     * @param receiverId the ID of the receiver
     * @return a list of friend requests
     */
    List<FriendRequest> findByReceiverIdAndAcceptedFalse(UUID receiverId);

    /**
     * Finds all friend requests sent by a user that are not yet accepted.
     *
     * @param senderId the ID of the sender
     * @return a list of friend requests
     */
    List<FriendRequest> findBySenderIdAndAcceptedFalse(UUID senderId);

    /**
     * Checks if a friend request exists between a sender and receiver that is not yet accepted.
     *
     * @param senderId the ID of the sender
     * @param receiverId the ID of the receiver
     * @return true if such a friend request exists, false otherwise
     */
    boolean existsBySenderIdAndReceiverIdAndAcceptedFalse(UUID senderId, UUID receiverId);
}
