package app.persistence.teamd;

import app.entities.teamd.SizeGuide;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SizeGuideMapper {

    private final ConnectionPool connectionPool;

    public SizeGuideMapper(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    // Finds the size for a brand from underbust and bust. Returns null if there is no match.
    public SizeGuide findSize(int brandId, double underbust, double bust) throws DatabaseException {
        String sql = "SELECT * FROM teamd_size_guide " +
                "WHERE brand_id = ? " +
                "AND ? >= underbust_min_cm AND ? < underbust_max_cm " +
                "AND ? >= bust_min_cm AND ? < bust_max_cm";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, brandId);
            ps.setDouble(2, underbust);
            ps.setDouble(3, underbust);
            ps.setDouble(4, bust);
            ps.setDouble(5, bust);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapSizeGuide(rs);
            }
            return null;

        } catch (SQLException e) {
            throw new DatabaseException("Kunne ikke finde størrelsen: " + e.getMessage());
        }
    }

    // Gets the whole size guide for one brand
    public List<SizeGuide> getSizesByBrand(int brandId) throws DatabaseException {
        List<SizeGuide> sizes = new ArrayList<>();
        String sql = "SELECT * FROM teamd_size_guide WHERE brand_id = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, brandId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                sizes.add(mapSizeGuide(rs));
            }

        } catch (SQLException e) {
            throw new DatabaseException("Kunne ikke hente størrelserne: " + e.getMessage());
        }
        return sizes;
    }

    // Creates one SizeGuide from the current row in the ResultSet
    private SizeGuide mapSizeGuide(ResultSet rs) throws SQLException {
        return new SizeGuide(
                rs.getInt("size_guide_id"),
                rs.getInt("brand_id"),
                rs.getInt("band_size"),
                rs.getString("cup_size"),
                rs.getString("size_system"),
                rs.getDouble("underbust_min_cm"),
                rs.getDouble("underbust_max_cm"),
                rs.getDouble("bust_min_cm"),
                rs.getDouble("bust_max_cm")
        );
    }
}