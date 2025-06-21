package demogame.controller;

import demogame.view.UserUpdateView;
import demogame.view.MenuView;
import demogame.view.LoginView;
import demogame.dao.UserDao;
import demogame.model.UserData;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.Image;
import java.io.File;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.logging.Logger;
import java.util.logging.Level;

public class UserUpdateController {

    private static final Logger LOGGER = Logger.getLogger(UserUpdateController.class.getName());
    private UserUpdateView view;
    private UserDao userDAO;
    private UserData currentUser;
    private GameController gameController; // changed from menucontroller to gamecontroller

 
    public UserUpdateController(UserUpdateView view, UserData currentUser) {
        this.view = view;
        this.userDAO = new UserDao();
        this.currentUser = currentUser;
        initializeController();
    }

    // loads user data, and handles window close
    private void initializeController() {
        initializeListeners();
        loadUserData();
        setupWindowListener();
    }

    // for profile update buttons
    private void initializeListeners() {
        view.getUploadPhotoButton().addActionListener(e -> handlePhotoUpload());
        view.getEditNameButton().addActionListener(e -> handleNameEdit());
        view.getDeleteAccountButton().addActionListener(e -> handleAccountDeletion());
        view.getBackButton().addActionListener(e -> handleBack());
        view.getViewHistoryButton().addActionListener(e -> showChangeHistory());
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