package app.services.teamd;

import app.entities.teamd.Brand;
import app.exceptions.DatabaseException;
import app.persistence.teamd.BrandMapper;

import java.util.List;

// Business logic for brands. The controller uses this instead of calling BrandMapper directly
public class BrandService {
    private final BrandMapper brandMapper;

    public BrandService(BrandMapper brandMapper) {
        this.brandMapper = brandMapper;
    }

    // Gets all brands
    public List<Brand> getAllBrands() throws DatabaseException {
        return brandMapper.getAllBrands();
    }

    // Gets one brand by id
    public Brand getBrandById(int brandId) throws DatabaseException {
        return brandMapper.getBrandById(brandId);
    }
}

