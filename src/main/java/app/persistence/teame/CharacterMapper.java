package app.persistence.teame;

import app.entities.teame.PasswordCharacter;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;

import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CharacterMapper {
    private final ConnectionPool connectionPool;

    public CharacterMapper(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    // Henter alle tegn for de valgte tegntyper.
    // Hvis excludeAmbiguous er sand, udelades tegn der let forveksles (fx 0/O og l/1/I)
    public List<PasswordCharacter> getCharactersByTypes(List<Integer> characterTypeIds, boolean excludeAmbiguous)
            throws DatabaseException {
        List<PasswordCharacter> characters = new ArrayList<>();
        String sql = "SELECT value, character_type_id FROM teame_character " +
                "WHERE character_type_id = ANY (?) AND (is_ambiguous = FALSE OR ? = FALSE)";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            Array typeIds = connection.createArrayOf("integer", characterTypeIds.toArray());
            ps.setArray(1, typeIds);
            ps.setBoolean(2, excludeAmbiguous);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                char value = rs.getString("value").charAt(0);
                int characterTypeId = rs.getInt("character_type_id");
                characters.add(new PasswordCharacter(value, characterTypeId));
            }

        } catch (SQLException e) {
            throw new DatabaseException("Kunne ikke hente tegn", e.getMessage());
        }
        return characters;
    }
}
