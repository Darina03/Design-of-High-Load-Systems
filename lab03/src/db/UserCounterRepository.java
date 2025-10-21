package db;

import java.sql.*;

public class UserCounterRepository {

    public void resetCounter(int userId) throws SQLException {

        try (Connection conn = DBConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "UPDATE user_counter SET counter = 0, version = 0 WHERE user_id = ?")) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        }
    }

    public int getCounter(int userId) throws SQLException {

        try (Connection conn = DBConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT counter FROM user_counter WHERE user_id = ?")) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
                else throw new SQLException("User not found!");
            }
        }
    }
}
