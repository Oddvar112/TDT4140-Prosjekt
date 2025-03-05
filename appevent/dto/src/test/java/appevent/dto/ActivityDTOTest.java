package appevent.dto;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

public class ActivityDTOTest {

    @Test
    public void testActivityDTO() {
        UUID id = UUID.randomUUID();
        String title = "Test Activity";
        LocalDateTime dateTime = LocalDateTime.now();
        String location = "Test Location";
        String description = "Test Description";
        Set<UserDTO> participants = Set.of(new UserDTO(UUID.randomUUID(), "testuser"));
        String type = "Test Type";

        ActivityDTO activityDTO = new ActivityDTO(id, title, dateTime, location, description, participants, type);

        assertEquals(id, activityDTO.id());
        assertEquals(title, activityDTO.title());
        assertEquals(dateTime, activityDTO.dateTime());
        assertEquals(location, activityDTO.location());
        assertEquals(description, activityDTO.description());
        assertEquals(participants, activityDTO.participants());
        assertEquals(type, activityDTO.type());
    }
}
