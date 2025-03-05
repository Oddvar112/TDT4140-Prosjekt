package appevent.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

public class SearchDTOTest {

    private LocalDateTime time = LocalDateTime.of(1999, 1, 1, 1, 1, 1);
    private String type = "Test Type";
    private String search = "Test Search";

    @Test
    public void testSearchDTO() {
        SearchDTO searchDTO = new SearchDTO(time, type, search);
        assertEquals(time, searchDTO.date());
        assertEquals(type, searchDTO.type());
        assertEquals(search, searchDTO.searchString());
    }

    @Test
    public void testSearchDTOEquality() {
        SearchDTO searchDTO1 = new SearchDTO(time, type, search);
        SearchDTO searchDTO2 = new SearchDTO(time, type, search);
        assertEquals(searchDTO1, searchDTO2);
    }
    
}
