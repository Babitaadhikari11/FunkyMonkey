package demogame.controller;

import demogame.view.UserUpdateView;
import demogame.view.MenuView;
import demogame.view.LoginView;
import demogame.dao.UserDao;
import demogame.model.UserData;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.Component;
import java.awt.Image;
import java.io.File;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.logging.Logger;
import java.util.logging.Level;
import demogame.dao.FeedbackDao;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import demogame.model.Feedback;
public class UserUpdateController {

    private static final Logger LOGGER = Logger.getLogger(UserUpdateController.class.getName());
    private UserUpdateView view;
    private UserDao userDAO;
    private UserData currentUser;
    private GameController gameController; // changed from menucontroller to gamecontroller
    private FeedbackDao feedbackDao;

 
    public UserUpdateController(UserUpdateView view, UserData currentUser) {
        this.view = view;
        this.userDAO = new UserDao();
        this.currentUser = currentUser;
        this.feedbackDao= new FeedbackDao();
        initializeController();
    }

    // loads user data, and handles window close
    private void initializeController() {
        initializeListeners();
        loadUserData();
        loadFeedbackList(); //this is for loading feedback list
        setupWindowListener();
    }

    // for profile update buttons
    private void initializeListeners() {
        view.getUploadPhotoButton().addActionListener(e -> handlePhotoUpload());
        view.getEditNameButton().addActionListener(e -> handleNameEdit());
        view.getDeleteAccountButton().addActionListener(e -> handleAccountDeletion());
        view.getBackButton().addActionListener(e -> handleBack());
        view.getViewHistoryButton().addActionListener(e -> showChangeHistory());
        view.getSubmitFeedbackButton().addActionListener(e -> handleSubmitFeedback()); //lsiten feedback submission
    }

    // profile picture and username from database show
    private void loadUserData() {
        try {
            Image profilePic = userDAO.getProfilePicture(currentUser.getId());
            if (profilePic != null) {
                view.setProfilePicture(profilePic);
            }
            view.updateUsername(currentUser.getUsername());
            LOGGER.info("Loaded user data for userId: " + currentUser.getId());
        } catch (Exception e) {
            handleError("Error loading user data", e);
        }
    }
    // load feedback
 private void loadFeedbackList() {
    try {
        // Get feedback list from database
        List<Feedback> feedbackList = feedbackDao.getUserFeedback(currentUser.getId());
        
        // Debug print
        System.out.println("Loading feedback list. Found: " + feedbackList.size() + " items");
        
        // Update the view
        view.updateFeedbackList(feedbackList);
        
        // Add listeners to buttons after the list is updated
        addFeedbackButtonListeners();
        
        LOGGER.info("Feedback list loaded successfully");
    } catch (Exception e) {
        LOGGER.severe("Error loading feedback list: " + e.getMessage());
        e.printStackTrace();  // Add this for debugging
        handleError("Error loading feedback list", e);
    }
}
// Separate method for adding button listeners
private void addFeedbackButtonListeners() {
    for (Component comp : view.feedbackListPanel.getComponents()) {
        if (comp instanceof JPanel) {
            traversePanel((JPanel) comp);
        }
    }
}
// Helper method to traverse panels and find buttons
private void traversePanel(JPanel panel) {
    for (Component comp : panel.getComponents()) {
        if (comp instanceof JButton) {
            JButton button = (JButton) comp;
            String actionCommand = button.getActionCommand();
            if (actionCommand != null) {
                if (actionCommand.startsWith("edit_")) {
                    button.addActionListener(this::handleEditFeedback);
                } else if (actionCommand.startsWith("delete_")) {
                    button.addActionListener(this::handleDeleteFeedback);
                }
            }
        } else if (comp instanceof JPanel) {
            traversePanel((JPanel) comp);
        }
    }
}
    // handle submission
   private void handleSubmitFeedback() {
    try {
        int rating = view.getFeedbackRating();
        String feedbackText = view.getFeedbackText();
        
        // Add validation
        if (feedbackText == null || feedbackText.trim().isEmpty()) {
            view.showError("Please enter feedback text");
            return;
        }
        
        if (rating < 1 || rating > 5) {
            view.showError("Rating must be between 1 and 5");
            return;
        }

        System.out.println("Submitting feedback - Rating: " + rating + ", Text: " + feedbackText);
        
        Feedback feedback = new Feedback(0, currentUser.getId(), rating, feedbackText.trim());
        
        if (feedbackDao.saveFeedback(feedback)) {
            view.showSuccess("Feedback submitted successfully!");
            view.clearFeedbackInput();
            loadFeedbackList();  // Reload the list
            LOGGER.info("Feedback submitted successfully");
        } else {
            view.showError("Failed to submit feedback");
            
        }
    } catch (Exception e) {
        LOGGER.severe("Error in handleSubmitFeedback: " + e.getMessage());
        
    }
}
    // handle feedback button for input field and updating on sbumit

    private void handleEditFeedback(ActionEvent e) {
    try {
        //extratc feedback id from action commant
        int feedbackId = Integer.parseInt(e.getActionCommand().split("_")[1]);
        List<Feedback> feedbackList = feedbackDao.getUserFeedback(currentUser.getId());
        //get feedback from database
        Feedback feedback = feedbackList.stream()
            .filter(f -> f.getId() == feedbackId)
            .findFirst()
            .orElse(null);
            
        if (feedback != null) {
            view.ratingSlider.setValue(feedback.getRating());
            view.feedbackTextArea.setText(feedback.getFeedbackText());  
            JButton submitButton = view.getSubmitFeedbackButton();
            submitButton.setText("Update Feedback");  
            // storing original text
            final String original = feedback.getFeedbackText();
            final int originalRating =feedback.getRating();
            // removing all and clear
            for(ActionListener listener : submitButton.getActionListeners()){
                 submitButton.removeActionListener(listener);
            }
            
            //this is for updating feedback 
            submitButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt){
                    updateFeedback(feedbackId, original, originalRating);
                }
            }); 
        }else{
            view.showError("FEEDBack not found");
        }
    }catch(Exception ex){
        handleError("error", ex);
    }
}
// method for updating feedback
private void updateFeedback(int feedbackId, String original, int originalRating){
    try{
        int newRating = view.getFeedbackRating();
        String newText = view.getFeedbackText().trim();
        // ensure valid input
        // Validate input
        if (newText.isEmpty()) {
            view.showError("Feedback text cannot be empty");
            return;
        }
        
        if (newRating < 1 || newRating > 5) {
            view.showError("Rating must be between 1 and 5");
            return;
        }
        
        // Check if anything changed
        if (newRating == originalRating && newText.equals(original)) {
            view.showError("No changes made to feedback");
            resetFeedbackForm();
            return;
        }
       // update feedback
       if(feedbackDao.updateFeedback(feedbackId, newRating, newText)){
        view.showSuccess("FEEDBACK UPDATED :)");
        resetFeedbackForm();
        loadFeedbackList();
       }else{
        view.showError("newText");
       }

    }catch(Exception e){
        handleError("error updating", e);
    }
}
// helping method to reset
private void resetFeedbackForm(){
    view.clearFeedbackInput();
    JButton submitButton = view.getSubmitFeedbackButton();
    submitButton.setText("Submit Feedback");

    // remove all lsitener
    for(ActionListener listener : submitButton.getActionListeners()){
        submitButton.removeActionListener(listener);
    }
}
                    // delete feedback
    private void handleDeleteFeedback(ActionEvent e) {
        int feedbackId = Integer.parseInt(e.getActionCommand().split("_")[1]);
        if (view.showConfirmDialog("Are you sure you want to delete this feedback?") == JOptionPane.YES_OPTION) {
            try {
                if (feedbackDao.deleteFeedback(feedbackId)) {
                    view.showSuccess("Feedback deleted!");
                    loadFeedbackList();
                    LOGGER.info("Feedback deleted for feedbackId: " + feedbackId + ", userId: " + currentUser.getId());
                } else {
                    view.showError("Failed to delete feedback.");
                    
                }
            } catch (Exception ex) {
                handleError("Error deleting feedback", ex);
            }
        }
    }


    private void setupWindowListener() {
        view.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                handleBack();
            }
        });
    }

    // adding profile from local system
    private void handlePhotoUpload() {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
            "Image files", "jpg", "jpeg", "png", "gif");
        fileChooser.setFileFilter(filter);

        // only proceed if file is selected
        if (fileChooser.showOpenDialog(view) == JFileChooser.APPROVE_OPTION) {
            try {
                File selectedFile = fileChooser.getSelectedFile();
                Image image = new ImageIcon(selectedFile.getPath()).getImage();
                if (userDAO.updateProfilePicture(currentUser.getId(), selectedFile)) {
                    view.setProfilePicture(image);
                    view.showSuccess("Profile picture updated successfully!");
                    LOGGER.info("Profile picture updated for userId: " + currentUser.getId());
                } else {
                    view.showError("Failed to update profile picture in database.");
                    LOGGER.warning("Failed to update profile picture for userId: " + currentUser.getId());
                }
            } catch (Exception e) {
                handleError("Error uploading image", e);
            }
        }
    }

    // updates username with user input
    private void handleNameEdit() {
        String currentUsername = currentUser.getUsername();
        String newUsername = JOptionPane.showInputDialog(view, 
            "Enter new username:", 
            currentUsername);

        // validate new username before updating
        if (newUsername != null && !newUsername.trim().isEmpty() && 
            !newUsername.equals(currentUsername)) {
            try {
                if (userDAO.updateUsername(currentUser.getId(), newUsername.trim())) {
                    currentUser.setUsername(newUsername.trim());
                    view.updateUsername(newUsername.trim());
                    view.showSuccess("Username updated successfully!");
                    showChangeHistory();
                    LOGGER.info("Username updated to " + newUsername + " for userId: " + currentUser.getId());
                } else {
                    view.showError(userDAO.getErrorMessage());
                    LOGGER.warning("Failed to update username for userId: " + currentUser.getId());
                }
            } catch (Exception e) {
                handleError("Error updating username", e);
            }
        }
    }

    // show change history from database
    private void showChangeHistory() {
        try {
            ResultSet history = userDAO.getUserChangeHistory(currentUser.getId());
            if (history != null) {
                StringBuilder changes = new StringBuilder("Recent changes:\n\n");
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                while (history.next()) {
                    changes.append("Type: ").append(history.getString("change_type"))
                           .append("\nOld Value: ").append(history.getString("old_value"))
                           .append("\nNew Value: ").append(history.getString("new_value"))
                           .append("\nDate: ").append(dateFormat.format(history.getTimestamp("changed_at")))
                           .append("\n\n");
                }
                showHistoryDialog(changes.toString());
                LOGGER.info("Displayed change history for userId: " + currentUser.getId());
            }
        } catch (Exception e) {
            handleError("Error showing change history", e);
        }
    }

   
    private void showHistoryDialog(String historyText) {
        JTextArea textArea = new JTextArea(historyText);
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new java.awt.Dimension(400, 300));
        JOptionPane.showMessageDialog(view, scrollPane, 
            "Change History", JOptionPane.INFORMATION_MESSAGE);
    }

    // handles account deletion 
    private void handleAccountDeletion() {
        int response = JOptionPane.showConfirmDialog(view,
            "Are you sure you want to delete your account? This action cannot be undone.",
            "Confirm Account Deletion",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        // delete only if confirmed
        if (response == JOptionPane.YES_OPTION) {
            try {
                if (userDAO.deleteUser(currentUser.getId())) {
                    view.showSuccess("Account deleted successfully!");
                    returnToLogin();
                    LOGGER.info("Account deleted for userId: " + currentUser.getId());
                } else {
                    view.showError(userDAO.getErrorMessage());
                    LOGGER.warning("Failed to delete account for userId: " + currentUser.getId());
                }
            } catch (Exception e) {
                handleError("Error deleting account", e);
            }
        }
    }

    
    private void handleBack() {
        SwingUtilities.invokeLater(() -> {
            try {
                LOGGER.info("Returning to MenuView from UserUpdateView for userId: " + currentUser.getId());
                view.dispose();
       
                gameController = new GameController(currentUser.getId());
                MenuView menuView = new MenuView(gameController);
                menuView.setVisible(true);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error returning to MenuView for userId: " + currentUser.getId(), e);
                JOptionPane.showMessageDialog(null,
                    "Error returning to menu: " + e.getMessage(),
                    "Menu Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    // navigates to login view after account deletion
    private void returnToLogin() {
        SwingUtilities.invokeLater(() -> {
            LOGGER.info("Returning to LoginView after account deletion for userId: " + currentUser.getId());
            view.dispose();
            LoginView loginView = new LoginView();
            new LoginController(loginView);
            loginView.setVisible(true);
        });
    }

    
    private void handleError(String message, Exception e) {
        LOGGER.log(Level.SEVERE, message + " for userId: " + currentUser.getId(), e);
        SwingUtilities.invokeLater(() -> {
            view.showError(message + ": " + e.getMessage());
        });
    }
    

    
    public void cleanup() {
        if (view != null) {
            view.dispose();
        }
    }

    
    public UserData getCurrentUser() {
        return currentUser;
    }
}