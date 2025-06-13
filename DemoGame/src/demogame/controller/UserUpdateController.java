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

public class UserUpdateController {
    private UserUpdateView view;
    private UserDao userDAO;
    private UserData currentUser;

    public UserUpdateController(UserUpdateView view, UserData currentUser) {
        this.view = view;
        // initialize database
        this.userDAO = new UserDao();
        this.currentUser = currentUser;
        initializeListeners();
    }

    private void initializeListeners() {
        view.getUploadPhotoButton().addActionListener(e -> handlePhotoUpload());
        // view.getEditNameButton().addActionListener(e -> handleNameEdit());
        // view.getDeleteAccountButton().addActionListener(e -> handleAccountDeletion());
        view.getBackButton().addActionListener(e -> handleBack());
    }
// photo upload logic
    private void handlePhotoUpload() {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
            "Image files", "jpg", "jpeg", "png", "gif");
        fileChooser.setFileFilter(filter);

        if (fileChooser.showOpenDialog(view) == JFileChooser.APPROVE_OPTION) {
            try {
                File selectedFile = fileChooser.getSelectedFile();
                Image image = new ImageIcon(selectedFile.getPath()).getImage();
                view.setProfilePicture(image);
                view.showSuccess("Profile picture updated successfully!");
            } catch (Exception e) {
                view.showError("Error uploading image: " + e.getMessage());
            }
        }
    }

    // private void handleNameEdit() {
    //     String newUsername = JOptionPane.showInputDialog(view, 
    //         "Enter new username:", 
    //         view.getCurrentUsername()); //retrieve current username from db
        

    //     if (newUsername != null && !newUsername.trim().isEmpty()) {
    //         // database update block
    //         try {
    //             if (userDAO.updateUsername(currentUser.getId(), newUsername.trim())) {
    //                 // updte model, update username in local UserData
    //                 currentUser.setUsername(newUsername.trim());
    //                 // update UserUpdate view- update username display in UI and reflect immediately to user
    //                 view.updateUsername(newUsername.trim());
    //                 view.showSuccess("Username updated successfully!");
    //                 showChangeHistory();
    //             } else {
    //                 view.showError(userDAO.getErrorMessage());
    //             }
    //         } catch (Exception e) {
    //             view.showError("Error updating username: " + e.getMessage());
    //             e.printStackTrace();
    //         }
    //     }
    // }

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
                
                JTextArea textArea = new JTextArea(changes.toString());
                textArea.setEditable(false);
                JScrollPane scrollPane = new JScrollPane(textArea);
                scrollPane.setPreferredSize(new java.awt.Dimension(400, 300));
                
                JOptionPane.showMessageDialog(view, scrollPane, 
                    "Change History", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            System.err.println("Error showing change history: " + e.getMessage());
        }
    }

    // private void handleAccountDeletion() {
    //     int response = view.showConfirmDialog(
    //         "Are you sure you want to delete your account? This action cannot be undone.");

    //     if (response == JOptionPane.YES_OPTION) {
    //         try {
    //             if (userDAO.deleteUser(currentUser.getId())) {
    //                 view.showSuccess("Account deleted successfully!");
    //                 view.dispose();
    //                 LoginView loginView = new LoginView();
    //                 new LoginController(loginView);
    //                 loginView.setVisible(true);
    //             } else {
    //                 view.showError(userDAO.getErrorMessage());
    //             }
    //         } catch (Exception e) {
    //             view.showError("Error deleting account: " + e.getMessage());
    //         }
    //     }
    // }

    private void handleBack() {
        view.dispose();
        MenuView menuView = new MenuView(currentUser.getUsername());
        new MenuController(menuView, currentUser);
        menuView.setVisible(true);
    }
}

