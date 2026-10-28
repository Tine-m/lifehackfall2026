package app.persistence.teame;

import app.entities.teame.CharacterType;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CharacterTypeMapper {
    private final ConnectionPool connectionPool;

    public CharacterTypeMapper(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    // Henter alle tegntyper, så afkrydsningsfelterne på forsiden ikke er skrevet fast i koden
    public List<CharacterType> getAllCharacterTypes() throws DatabaseException {
        List<CharacterType> characterTypes = new ArrayList<>();
        String sql = "SELECT character_type_id, name FROM teame_character_type ORDER BY character_type_id";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int characterTypeId = rs.getInt("character_type_id");
                String name = rs.getString("name");
                characterTypes.add(new CharacterType(characterTypeId, name));
            }

        } catch (SQLException e) {
            throw new DatabaseException("Kunne ikke hente tegntyper", e.getMessage());
        }
        return characterTypes;
    }
}
