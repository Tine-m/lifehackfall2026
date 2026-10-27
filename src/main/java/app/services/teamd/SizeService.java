package app.services.teamd;

import app.entities.teamd.SizeGuide;
import app.exceptions.DatabaseException;
import app.exceptions.teamd.ValidationException;
import app.persistence.teamd.SizeGuideMapper;

import java.util.ArrayList;
import java.util.List;

public class SizeService {

    private static final double MIN_CM = 55;
    private static final double MAX_CM = 150;

    private static final List<String> CUPS = List.of("AA", "A", "B", "C", "D", "E", "F", "G", "H", "J");

    private final SizeGuideMapper sizeGuideMapper;

    public SizeService(SizeGuideMapper sizeGuideMapper) {
        this.sizeGuideMapper = sizeGuideMapper;
    }

    // Tjekker brugerens input fra formularen og kaster en fejl med en besked, hvis noget er forkert
    public void validateInput(String brandId, String underbust, String bust) throws ValidationException {
        checkBrand(brandId);
        checkMeasurements(underbust, bust);
    }

    // Tjekker at der er valgt et gyldigt mærke
    private void checkBrand(String brandId) throws ValidationException {
        if (brandId == null || brandId.isEmpty()) {
            throw new ValidationException("Vælg et mærke");
        }
        try {
            Integer.parseInt(brandId);
        } catch (NumberFormatException e) {
            throw new ValidationException("Vælg et mærke");
        }
    }

    // Tjekker at målene er udfyldt, er tal, ligger mellem 55 og 150, og at brystmål er størst
    private void checkMeasurements(String underbust, String bust) throws ValidationException {
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

    // Henter mærkets størrelser og finder søsterstørrelserne
    public List<String> getSisterSizes(SizeGuide sizeGuide) throws DatabaseException {
        List<SizeGuide> brandSizes = sizeGuideMapper.getSizesByBrand(sizeGuide.getBrandId());
        return findSisterSizes(sizeGuide, brandSizes);
    }

    // Søsterstørrelser: bånd ned + skål op, bånd op + skål ned.
    public List<String> findSisterSizes(SizeGuide sizeGuide, List<SizeGuide> brandSizes) {
        List<String> sisterSizes = new ArrayList<>();
        if (sizeGuide == null) {
            return sisterSizes;
        }

        int step = sizeGuide.getSizeSystem().equals("UK") || sizeGuide.getSizeSystem().equals("US") ? 2 : 5;
        int band = sizeGuide.getBandSize();
        int cupIndex = CUPS.indexOf(sizeGuide.getCupSize());
        if (cupIndex < 0) {
            return sisterSizes;
        }

        if (cupIndex < CUPS.size() - 1) {
            addIfBrandHasSize(sisterSizes, brandSizes, band - step, CUPS.get(cupIndex + 1));
        }
        if (cupIndex > 0) {
            addIfBrandHasSize(sisterSizes, brandSizes, band + step, CUPS.get(cupIndex - 1));
        }
        return sisterSizes;
    }

    private void addIfBrandHasSize(List<String> result, List<SizeGuide> brandSizes, int band, String cup) {
        for (SizeGuide size : brandSizes) {
            if (size.getBandSize() == band && size.getCupSize().equals(cup)) {
                result.add(band + cup);
                return;
            }
        }
    }
}