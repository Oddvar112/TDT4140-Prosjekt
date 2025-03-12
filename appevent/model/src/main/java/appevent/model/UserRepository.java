package appevent.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Finds a user by their username.
     * @param usernavn the username to search for
     * @return Optional containing the user if found
     */
    Optional<User> findByBrukernavn(String usernavn);

    /**
     * Finds a user by their ID.
     * @param id the ID to search for
     * @return Optional containing the user if found
     */
    Optional<User> findById(UUID id);

    List<User> findByBrukernavnContainingIgnoreCase(String searchTerm);


}
