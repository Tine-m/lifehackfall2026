package app.services.teamd;

import app.exceptions.teamd.ValidationException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import app.entities.teamd.SizeGuide;
import java.util.List;

class SizeServiceTest {
    private final SizeService sizeService = new SizeService(null);

    @Test
    void validInputGivesNoError() {
        assertDoesNotThrow(() -> sizeService.validateInput("1", "75", "90"));
    }

    @Test
    void missingBrandGivesError() {
        ValidationException e = assertThrows(ValidationException.class,
                () -> sizeService.validateInput("", "75", "90"));
        assertEquals("Vælg et mærke", e.getMessage());
    }

    @Test
    void bustSmallerThanUnderbustGivesError() {
        ValidationException e = assertThrows(ValidationException.class,
                () -> sizeService.validateInput("1", "90", "75"));
        assertEquals("Brystmål skal være større end undermål", e.getMessage());
    }

    @Test
    void underbustBelow55GivesError() {
        ValidationException e = assertThrows(ValidationException.class,
                () -> sizeService.validateInput("1", "40", "90"));
        assertEquals("Målene skal være mellem 55 og 150 cm", e.getMessage());
    }

    @Test
    void lettersGiveError() {
        ValidationException e = assertThrows(ValidationException.class,
                () -> sizeService.validateInput("1", "abc", "90"));
        assertEquals("Målene skal være tal", e.getMessage());
    }

    private SizeGuide size(int band, String cup) {
        return new SizeGuide(1, 1, band, cup, "EU", 0, 0, 0, 0);
    }

    @Test
    void sisterSizesFor75C() {
        List<SizeGuide> brand = List.of(size(70, "D"), size(75, "C"), size(80, "B"));

        List<String> result = sizeService.findSisterSizes(size(75, "C"), brand);

        assertEquals(List.of("70D", "80B"), result);
    }

    @Test
    void sisterSizeThatDoesNotExistIsNotShown() {
        // 70D skulle give 65E og 75C, men mærket har ikke 65E
        List<SizeGuide> brand = List.of(size(70, "D"), size(75, "C"));

        List<String> result = sizeService.findSisterSizes(size(70, "D"), brand);

        assertEquals(List.of("75C"), result);
    }

    @Test
    void noSizeGivesNoSisterSizes() {
        assertTrue(sizeService.findSisterSizes(null, List.of(size(75, "C"))).isEmpty());
    }

    @Test
    void smallestCupCannotGoDown() {
        List<SizeGuide> brand = List.of(size(75, "AA"), size(70, "A"), size(80, "AA"));

        List<String> result = sizeService.findSisterSizes(size(75, "AA"), brand);

        assertEquals(List.of("70A"), result);
    }

    @Test
    void largestCupCannotGoUp() {
        List<SizeGuide> brand = List.of(size(75, "H"), size(80, "G"), size(70, "H"));

        List<String> result = sizeService.findSisterSizes(size(75, "H"), brand);

        assertEquals(List.of("80G"), result);
    }

}