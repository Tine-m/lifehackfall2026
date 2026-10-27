package app.services.teamd;

import app.entities.teamd.Brand;
import app.exceptions.DatabaseException;
import app.persistence.teamd.BrandMapper;

import java.util.List;

public class BrandService {
    private final BrandMapper brandMapper;

    public BrandService(BrandMapper brandMapper) {
        this.brandMapper = brandMapper;
    }

    public List<Brand> getAllBrands() throws DatabaseException {
        return brandMapper.getAllBrands();
    }

    public Brand getBrandById(int brandId) throws DatabaseException {
        return brandMapper.getBrandById(brandId);
    }
}

