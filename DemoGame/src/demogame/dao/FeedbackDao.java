package demogame.dao;
import demogame.model.Feedback;
import demogame.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger; //this is for warning

public class FeedbackDao {
    private static final Logger LOGGER = Logger.getLogger(FeedbackDao.class.getName());
    // database connection object
    private Connection connection;
    public FeedbackDao(){
        try{
            // check for jdbc connection
            this.connection=DatabaseConnection.getConnection();
        }catch(SQLException e){
            LOGGER.severe("failed to initialize database connection: "+e.getMessage());
            throw new RuntimeException("Database connection error", e);
        
        }
    }
    // saving data to feedback table
    public boolean saveFeedback(Feedback feedback){ //the feedback class contain user details so taking params of it
        String query ="INSERT INTO feedback(user_id,rating, feedback_text) VALUES (?,?,?)";
        try(PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)){
            // setting parameters for userid, rating and feedback text
            stmt.setInt(1,feedback.getUserId());
            stmt.setInt(2,feedback.getRating());
            stmt.setString(3,feedback.getFeedbackText());
            // exeuting the query and getting how many rows has been affected
            int rowsAffected = stmt.executeUpdate();
        if(rowsAffected >0){
            ResultSet r=stmt.getGeneratedKeys();
            if(r.next()){
                // setting generated id to feedback object
                feedback.setId(r.getInt(1));

            }
            // successful
            LOGGER.info("feedbacksaved");
            return true;
        }   
        //if no rows affected
        return false;    
     }catch(SQLException e){
        LOGGER.severe("Failed to save feedback.");
        return false;
     }


    }
    //  retrieve feedback of user
  public List<Feedback> getUserFeedback(int userId) {
        // Initialize an empty list for feedback records
        List<Feedback> feedbackList = new ArrayList<>();
        // SQL query to select feedback for a user
        String query = "SELECT id, user_id, rating, feedback_text FROM feedback WHERE user_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            // Set user_id parameter
            stmt.setInt(1, userId);
            // Execute query and get result set
            ResultSet rs = stmt.executeQuery();
            // Iterate through results
            while (rs.next()) {
                // Create Feedback object for each row
                feedbackList.add(new Feedback(
                    rs.getInt("id"),
                    rs.getInt("user_id"),
                    rs.getInt("rating"),
                    rs.getString("feedback_text")
                ));
            }
            LOGGER.info("Retrieved ");
        } catch (SQLException e) {
            // Log detailed error with SQL state and message
            LOGGER.severe("Failed to retrieve user feedback: SQLState=" + e.getSQLState() + ", Error=" + e.getMessage());
        }
        return feedbackList;
    }

    // update logic
    public boolean updateFeedback(int feedbackId, int  rating, String feedbackText){
        // updation query
        String query ="UPDATE feedback SET rating=?,feedback_text=? WHERE id=?";
        try(PreparedStatement stmt = connection.prepareStatement(query)){
            // parameter rakhne
            stmt.setInt(1,rating);
            stmt.setString(2,feedbackText);
            stmt.setInt(3,feedbackId);
            // exceute 
            int rowsAffected = stmt.executeUpdate();
            LOGGER.info("feedback updated.");
            return rowsAffected > 0;
            }catch(SQLException e){
                LOGGER.severe("failed to update");
                return false;
            }

        }
        public boolean deleteFeedback(int feedbackId){
            String query ="DELETE FROM feedback WHERE id =?";
            try(PreparedStatement stmt = connection.prepareStatement(query)){
                stmt.setInt(1, feedbackId);
                int rowsAffected=stmt.executeUpdate();
                LOGGER.info("feedback deleted");
                return rowsAffected >0;
            }catch(SQLException e){

                LOGGER.severe("failed to delete");
                return false;
            }
        }
        

    }


    

