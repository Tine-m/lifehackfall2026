package app.persistence.teame;

import app.entities.teame.Template;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TemplateMapper {
    private final ConnectionPool connectionPool;

    public TemplateMapper(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    // Henter alle skabeloner til dropdown'en, sorteret efter navn
    public List<Template> getAllTemplates() throws DatabaseException {
        List<Template> templates = new ArrayList<>();
        String sql = "SELECT template_id, name, length, exclude_ambiguous FROM teame_template ORDER BY name";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int templateId = rs.getInt("template_id");
                templates.add(new Template(
                        templateId,
                        rs.getString("name"),
                        rs.getInt("length"),
                        rs.getBoolean("exclude_ambiguous"),
                        getCharacterTypeIds(connection, templateId)));
            }

        } catch (SQLException e) {
            throw new DatabaseException("Kunne ikke hente skabeloner", e.getMessage());
        }
        return templates;
    }

    // Henter én skabelon ud fra id
    public Template getTemplateById(int templateId) throws DatabaseException {
        String sql = "SELECT template_id, name, length, exclude_ambiguous FROM teame_template WHERE template_id = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, templateId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Template(
                        templateId,
                        rs.getString("name"),
                        rs.getInt("length"),
                        rs.getBoolean("exclude_ambiguous"),
                        getCharacterTypeIds(connection, templateId));
            }
            throw new DatabaseException("Skabelonen blev ikke fundet");

        } catch (SQLException e) {
            throw new DatabaseException("Kunne ikke hente skabelonen", e.getMessage());
        }
    }

    // Henter de tegntyper, en skabelon bruger, fra koblingstabellen
    private List<Integer> getCharacterTypeIds(Connection connection, int templateId) throws SQLException {
        List<Integer> characterTypeIds = new ArrayList<>();
        String sql = "SELECT character_type_id FROM teame_template_character_type " +
                "WHERE template_id = ? ORDER BY character_type_id";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, templateId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                characterTypeIds.add(rs.getInt("character_type_id"));
            }
        }
        return characterTypeIds;
    }
}
