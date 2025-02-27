package appevent.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing Activity entities.
 */
@Repository
public interface ActivityRepository extends JpaRepository<Activity, UUID> {

    /**
     * Finds all activities occurring after the specified date that are visible to the user.
     *
     * @param date the date to compare
     * @param userId the ID of the user
     * @return the list of visible activities after the specified date, ordered by date
     */
    List<Activity> findByDateTimeAfterOrderByDateTimeAsc(LocalDateTime date);



    /**
     * Finds all activities a user is participating in.
     *
     * @param user the user to search for
     * @return the list of activities the user is participating in
     */
    List<Activity> findByParticipantsContaining(User user);

    /**
     * Finds all activities owned by a specific user.
     *
     * @param ownerId the ID of the owner
     * @return list of activities owned by the user
     */
    List<Activity> findByOwnerId(UUID ownerId);

    /**
     * Finds all activities where the user is a participant.
     *
     * @param userId the ID of the participant
     * @return list of activities the user participates in
     */
    @Query("SELECT DISTINCT a FROM Activity a " +
           "JOIN a.participants p " +
           "WHERE p.id = :userId")
    List<Activity> findByParticipantId(@Param("userId") UUID userId);

}