package app.services.teamd;

import app.exceptions.teamd.ValidationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SizeServiceTest {

    @Test
    void gyldigeMaalGiverIngenFejl() {
        assertDoesNotThrow(() -> SizeService.validateInput("1", "75", "90"));
    }

    @Test
    void manglendeMaerkeGiverFejl() {
        ValidationException e = assertThrows(ValidationException.class,
                () -> SizeService.validateInput("", "75", "90"));
        assertEquals("Vælg et mærke", e.getMessage());
    }

    @Test
    void brystmaalMindreEndUndermaalGiverFejl() {
        ValidationException e = assertThrows(ValidationException.class,
                () -> SizeService.validateInput("1", "90", "75"));
        assertEquals("Brystmål skal være større end undermål", e.getMessage());
    }

    @Test
    void undermaalUnder55GiverFejl() {
        ValidationException e = assertThrows(ValidationException.class,
                () -> SizeService.validateInput("1", "40", "90"));
        assertEquals("Målene skal være mellem 55 og 150 cm", e.getMessage());
    }

    @Test
    void bogstaverGiverFejl() {
        ValidationException e = assertThrows(ValidationException.class,
                () -> SizeService.validateInput("1", "abc", "90"));
        assertEquals("Målene skal være tal", e.getMessage());
    }
}