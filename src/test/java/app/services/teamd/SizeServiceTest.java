package app.services.teamd;

import app.exceptions.teamd.ValidationException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import app.entities.teamd.SizeGuide;
import java.util.List;

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

    private SizeGuide size(int band, String cup) {
        return new SizeGuide(1, 1, band, cup, "EU", 0, 0, 0, 0);
    }

    @Test
    void soesterstoerrelserTilHunkemoeller75C() {
        List<SizeGuide> brand = List.of(size(70, "D"), size(75, "C"), size(80, "B"));

        List<String> result = SizeService.findSisterSizes(size(75, "C"), brand);

        assertEquals(List.of("70D", "80B"), result);
    }

    @Test
    void soesterstoerrelseDerIkkeFindesVisesIkke() {
        // 70D skulle give 65E og 75C, men mærket har ikke 65E
        List<SizeGuide> brand = List.of(size(70, "D"), size(75, "C"));

        List<String> result = SizeService.findSisterSizes(size(70, "D"), brand);

        assertEquals(List.of("75C"), result);
    }

    @Test
    void ingenStoerrelseGiverIngenSoesterstoerrelser() {
        assertTrue(SizeService.findSisterSizes(null, List.of(size(75, "C"))).isEmpty());
    }

    @Test
    void mindsteSkaalKanIkkeGaaNed() {
        List<SizeGuide> brand = List.of(size(75, "AA"), size(70, "A"), size(80, "AA"));

        List<String> result = SizeService.findSisterSizes(size(75, "AA"), brand);

        assertEquals(List.of("70A"), result);
    }

    @Test
    void stoerksteSkaalKanIkkeGaaOp() {
        List<SizeGuide> brand = List.of(size(75, "H"), size(80, "G"), size(70, "H"));

        List<String> result = SizeService.findSisterSizes(size(75, "H"), brand);

        assertEquals(List.of("80G"), result);
    }

}