package app.persistence.teamb;

import app.entities.teamb.Card;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CardMapper {

    public static List<Card> getRandomCards(int numberOfPairs, boolean lifehack, ConnectionPool connectionPool)
                throws DatabaseException {

            String sql = """
            SELECT c.card_id, c.pair_id, c.text_content, c.image_url, p.description
            FROM teamb_card c
                                JOIN teamb_pair p ON p.pair_id = c.pair_id
                     WHERE p.pair_id IN (
                         SELECT pair_id
                         FROM teamb_pair
                         WHERE is_lifehack = ?
                         ORDER BY random()
                         LIMIT ?
                         )
            """;

            List<Card> cards = new ArrayList<>();

            try (Connection connection = connectionPool.getConnection();
                 PreparedStatement ps = connection.prepareStatement(sql)) {

                ps.setBoolean(1, lifehack);
                ps.setInt(2, numberOfPairs);
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    cards.add(new Card(
                            rs.getInt("card_id"),
                            rs.getInt("pair_id"),
                            rs.getString("image_url"),
                            rs.getString("text_content"),
                            rs.getString("description")
                    ));
                }
            } catch (SQLException e) {
                throw new DatabaseException("Kunne ikke hente kort", e.getMessage());
            }

            Collections.shuffle(cards);
            return cards;
        }



}


