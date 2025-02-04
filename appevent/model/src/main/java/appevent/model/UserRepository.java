package appevent.model;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    
    /**
     * Finds a user by their username
     * 
     * @param usernavn the username to search for
     * @return Optional containing the user if found
     */
    Optional<User> findByBrukernavn(String usernavn);

}