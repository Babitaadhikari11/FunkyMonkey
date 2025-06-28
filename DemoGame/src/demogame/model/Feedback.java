package demogame.model;

public class Feedback {
    private int id; // this is for feedback table
    private int userID;
    private int rating;
    private String feedbackText;
    
    public Feedback(int id,int userID, int rating, String feedbackText) {
        this.id = id;
        this.userID = userID;
        this.rating = rating;
        this.feedbackText = feedbackText;
     
    }
    // getter and setters
    public int getId(){
        return id;
    }
    public void setId(int id){
        this.id=id;
    }
    public int getUserId(){
        return userID;
    }
    public void setUserId(int userID){
        this.userID=userID;
    }
    public int getRating(){
        return rating;
    }
    public void  setRating(int rating){
        this.rating=rating;
    }
    public String getFeedbackText(){
        return feedbackText;
    }
    public void setFeedbackText(String feedbackText){
        this.feedbackText=feedbackText;
    }
  
    
}
