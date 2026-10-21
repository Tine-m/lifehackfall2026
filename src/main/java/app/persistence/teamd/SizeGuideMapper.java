package app.persistence.teamd;

import app.entities.teamd.SizeGuide;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SizeGuideMapper {

    // Finder størrelsen hos et mærke ud fra undermål og brystmål. Returnerer null, hvis der ikke er et match.
    public static SizeGuide findSize(int brandId, double underbust, double bust, ConnectionPool connectionPool) throws DatabaseException {
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
            return null;

        } catch (SQLException e) {
            throw new DatabaseException("Kunne ikke finde størrelsen: " + e.getMessage());
        }
    }
}