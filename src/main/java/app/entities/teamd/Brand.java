package app.entities.teamd;

public class Brand {
    private int brandId;
    private String name;

    public Brand(int brandId, String name) {
        this.brandId = brandId;
        this.name = name;
    }

    public int getBrandId() {
        return brandId;
    }

    public String getName() {
        return name;
    }
}