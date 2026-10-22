package app.persistence.teamd;

import app.entities.teamd.Brand;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BrandMapper {
    private final ConnectionPool connectionPool;

    public BrandMapper(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    // Henter alle mærker fra databasen, sorteret efter navn
    public List<Brand> getAllBrands() throws DatabaseException {
        List<Brand> brands = new ArrayList<>();
        String sql = "SELECT brand_id, name FROM teamd_brand ORDER BY name";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int brandId = rs.getInt("brand_id");
                String name = rs.getString("name");
                brands.add(new Brand(brandId, name));
            }

        } catch (SQLException e) {
            throw new DatabaseException("Kunne ikke hente mærker: " + e.getMessage());
        }
        return brands;
    }
    // Henter ét mærke ud fra id
    public Brand getBrandById(int brandId) throws DatabaseException {
        String sql = "SELECT brand_id, name FROM teamd_brand WHERE brand_id = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, brandId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Brand(rs.getInt("brand_id"), rs.getString("name"));
            }
            throw new DatabaseException("Mærket blev ikke fundet");

        } catch (SQLException e) {
            throw new DatabaseException("Kunne ikke hente mærket: " + e.getMessage());
        }
    }
}