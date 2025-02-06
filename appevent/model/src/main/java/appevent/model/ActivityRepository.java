package appevent.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing Activity entities.
 */
@Repository
public interface ActivityRepository extends JpaRepository<Activity, UUID> {

    /**
     * Finds all activities occurring after the specified date, ordered by date.
     *
     * @param date the date to compare
     * @return the list of activities occurring after the specified date, ordered by date
     */
    List<Activity> findByDateTimeAfterOrderByDateTimeAsc(LocalDateTime date);

    /**
     * Finds all activities a user is participating in.
     *
     * @param user the user to search for
     * @return the list of activities the user is participating in
     */
    List<Activity> findByParticipantsContaining(User user);
}
