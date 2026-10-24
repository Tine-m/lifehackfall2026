package app.entities.teamd;

public class Brand {
    private int brandId;
    private String name;
    private String website;

    public Brand(int brandId, String name, String website) {
        this.brandId = brandId;
        this.name = name;
        this.website = website;
    }

    public int getBrandId() {
        return brandId;
    }

    public String getName() {
        return name;
    }

    public String getWebsite(){
        return website;
    }
}