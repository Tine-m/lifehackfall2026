package app.entities.teamd;

public class SizeGuide {
    private int sizeGuideId;
    private int brandId;
    private int bandSize;
    private String cupSize;
    private String sizeSystem;
    private double underbustMinCm;
    private double underbustMaxCm;
    private double bustMinCm;
    private double bustMaxCm;

    public SizeGuide(int sizeGuideId, int brandId, int bandSize, String cupSize, String sizeSystem, double underbustMinCm, double underbustMaxCm, double bustMinCm, double bustMaxCm) {
        this.sizeGuideId = sizeGuideId;
        this.brandId = brandId;
        this.bandSize = bandSize;
        this.cupSize = cupSize;
        this.sizeSystem = sizeSystem;
        this.underbustMinCm = underbustMinCm;
        this.underbustMaxCm = underbustMaxCm;
        this.bustMinCm = bustMinCm;
        this.bustMaxCm = bustMaxCm;
    }

    public int getBrandId() {
        return brandId;
    }

    public int getBandSize() {
        return bandSize;
    }

    public String getCupSize() {
        return cupSize;
    }

    public String getSizeSystem() {
        return sizeSystem;
    }

    public String getSize() {
        return bandSize + cupSize;
    }
}

