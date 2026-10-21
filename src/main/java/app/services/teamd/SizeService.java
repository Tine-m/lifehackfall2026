package app.services.teamd;

import app.entities.teamd.SizeGuide;
import app.exceptions.DatabaseException;
import app.exceptions.teamd.ValidationException;
import app.persistence.teamd.SizeGuideMapper;

public class SizeService {

    private static final double MIN_CM = 55;
    private static final double MAX_CM = 150;

    private final SizeGuideMapper sizeGuideMapper;

    public SizeService(SizeGuideMapper sizeGuideMapper) {
        this.sizeGuideMapper = sizeGuideMapper;
    }

    // Tjekker brugerens input fra formularen og kaster en fejl med en besked, hvis noget er forkert
    public static void validateInput(String brandId, String underbust, String bust) throws ValidationException {
        if (brandId == null || brandId.isEmpty()) {
            throw new ValidationException("Vælg et mærke");
        }

        if (underbust == null || underbust.isEmpty() || bust == null || bust.isEmpty()) {
            throw new ValidationException("Udfyld både undermål og brystmål");
        }

        double underbustCm;
        double bustCm;
        try {
            underbustCm = Double.parseDouble(underbust);
            bustCm = Double.parseDouble(bust);
        } catch (NumberFormatException e) {
            throw new ValidationException("Målene skal være tal");
        }

        if (underbustCm < MIN_CM || underbustCm > MAX_CM || bustCm < MIN_CM || bustCm > MAX_CM) {
            throw new ValidationException("Målene skal være mellem 55 og 150 cm");
        }

        if (bustCm <= underbustCm) {
            throw new ValidationException("Brystmål skal være større end undermål");
        }
    }

    // Finder størrelsen i mærkets guide. Kaldes efter validateInput.
    public SizeGuide calculateSize(String brandId, String underbust, String bust) throws ValidationException, DatabaseException {
        SizeGuide size = sizeGuideMapper.findSize(
                Integer.parseInt(brandId),
                Double.parseDouble(underbust),
                Double.parseDouble(bust));

        if (size == null) {
            throw new ValidationException("Vi fandt ingen størrelse til dine mål hos dette mærke");
        }
        return size;
    }
}