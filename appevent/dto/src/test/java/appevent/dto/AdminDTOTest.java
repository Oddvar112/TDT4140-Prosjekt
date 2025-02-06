package appevent.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.UUID;

import org.junit.jupiter.api.Test;

public class AdminDTOTest {

    @Test
    public void testAdminDTO() {

        UUID id = UUID.randomUUID();
        String brukernavn = "testadmin";

        AdminDTO adminDTO = new AdminDTO(id, brukernavn);

        assertEquals(id, adminDTO.id());
        assertEquals(brukernavn, adminDTO.brukernavn());
        
    }
    
}
