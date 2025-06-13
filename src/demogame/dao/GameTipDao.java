package demogame.dao;
import demogame.model.GameTip;
import demogame.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GameTipDao {

    private Connection connection;

    public GameTipDao() {
        try {
            this.connection = DatabaseConnection.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException("Database connection error", e);
        }
    }

    public List<GameTip> getAllTips() {
        List<GameTip> tips = new ArrayList<>();
        String query = "SELECT * FROM game_tips";
        
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                tips.add(new GameTip(
                    rs.getInt("id"),
                    rs.getString("tip_text")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching tips: " + e.getMessage());
        }
        return tips;
    }
}


