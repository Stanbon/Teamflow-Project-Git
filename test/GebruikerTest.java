import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


public class GebruikerTest {

    Gebruiker gebruiker;

    @BeforeEach
    public void setup(){
         gebruiker = new Gebruiker(1, "Jan");
    }

    @Test
    public void testConstructorenGetters(){
        assertEquals("Jan", gebruiker.getNaam());
        assertEquals(1, gebruiker.getId());
    }

    @Test
    public void testSetId(){
        gebruiker.setId(3);
        assertEquals(3, gebruiker.getId());
    }

    @Test
    public void testSetNaam(){
        gebruiker.setNaam("Dennie");
        assertEquals("Dennie", gebruiker.getNaam());
    }
}
