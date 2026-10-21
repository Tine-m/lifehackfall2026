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
    public int getSizeGuideId() {
        return sizeGuideId;
    }

    public void setSizeGuideId(int sizeGuideId) {
        this.sizeGuideId = sizeGuideId;
    }

    public int getBrandId() {
        return brandId;
    }

    public void setBrandId(int brandId) {
        this.brandId = brandId;
    }

    public int getBandSize() {
        return bandSize;
    }

    public void setBandSize(int bandSize) {
        this.bandSize = bandSize;
    }

    public String getCupSize() {
        return cupSize;
    }

    public void setCupSize(String cupSize) {
        this.cupSize = cupSize;
    }

    public String getSizeSystem() {
        return sizeSystem;
    }

    public void setSizeSystem(String sizeSystem) {
        this.sizeSystem = sizeSystem;
    }

    public double getUnderbustMinCm() {
        return underbustMinCm;
    }

    public void setUnderbustMinCm(double underbustMinCm) {
        this.underbustMinCm = underbustMinCm;
    }

    public double getUnderbustMaxCm() {
        return underbustMaxCm;
    }

    public void setUnderbustMaxCm(double underbustMaxCm) {
        this.underbustMaxCm = underbustMaxCm;
    }

    public double getBustMinCm() {
        return bustMinCm;
    }

    public void setBustMinCm(double bustMinCm) {
        this.bustMinCm = bustMinCm;
    }

    public double getBustMaxCm() {
        return bustMaxCm;
    }

    public void setBustMaxCm(double bustMaxCm) {
        this.bustMaxCm = bustMaxCm;
    }
}

