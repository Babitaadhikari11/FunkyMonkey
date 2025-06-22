package demogame.dao;

import demogame.model.UserData;
import demogame.util.DatabaseConnection;
import java.sql.Connection;
import java.sql.SQLException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ScoreDaoTest {

    private static UserDao userDao;
    private ScoreDao scoreDao;
    private Connection conn;
    private static UserData testUser;
    private static int testUserId;

    // This runs once before all tests to create our test user
    @BeforeAll
    public static void setUpClass() throws SQLException {
        userDao = new UserDao();
        testUser = new UserData("scoreTestUser", "scoretest@example.com", "password123");
        
        // Register the user and get their ID
        userDao.register(testUser);
        UserData registeredUser = userDao.authenticate(testUser.getUsername(), testUser.getPassword());
        assertNotNull(registeredUser, "Test user must be created before running score tests");
        testUserId = registeredUser.getId();
    }
    
    // This runs before each individual test
    @BeforeEach
    public void setUp() throws SQLException {
        conn = DatabaseConnection.getConnection(); // Connect to your TEST database
        conn.setAutoCommit(false); // Use transactions to isolate tests
        scoreDao = new ScoreDao();
    }

    // This runs after each individual test
    @AfterEach
    public void tearDown() throws SQLException {
        if (conn != null) {
            conn.rollback(); // Undo any changes made during the test
            conn.close();
        }
    }

    @Test
    public void testUpdateOrInsertScore_InsertNewScore() {
        System.out.println("Testing initial score insertion...");
        // Act: Insert a score of 100 for our test user
        boolean result = scoreDao.updateOrInsertScore(testUserId, 100);

        // Assert: The operation should be successful
        assertTrue(result, "Should successfully insert a new score.");

        // Assert: The best score for the user should now be 100
        assertEquals(100, scoreDao.getUserBestScore(testUserId), "Best score should be 100 after first insert.");
    }

    @Test
    public void testUpdateOrInsertScore_UpdateWithHigherScore() {
        System.out.println("Testing score update with a higher score...");
        // Arrange: First, insert an initial score of 100
        scoreDao.updateOrInsertScore(testUserId, 100);

        // Act: Now, insert a higher score of 150 on the same day
        boolean result = scoreDao.updateOrInsertScore(testUserId, 150);

        // Assert: The operation should be successful
        assertTrue(result, "Should successfully update with a higher score.");
        
        // Assert: The best score should be updated to 150
        assertEquals(150, scoreDao.getUserBestScore(testUserId), "Best score should be 150 after update.");
    }

    @Test
    public void testUpdateOrInsertScore_IgnoreLowerScore() {
         System.out.println("Testing score update with a lower score...");
        // Arrange: First, insert an initial score of 100
        scoreDao.updateOrInsertScore(testUserId, 100);

        // Act: Now, attempt to insert a lower score of 50
        boolean result = scoreDao.updateOrInsertScore(testUserId, 50);

        // Assert: The operation should still return true (as it's not an error)
        assertTrue(result, "Operation should be considered successful even if no update occurs.");
        
        // Assert: The best score should remain 100
        assertEquals(100, scoreDao.getUserBestScore(testUserId), "Best score should remain 100 when a lower score is submitted.");
    }
}