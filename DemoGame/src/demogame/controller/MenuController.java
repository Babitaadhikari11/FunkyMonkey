package demogame.controller;

import demogame.model.UserData;
import demogame.view.LoadingView;
import demogame.view.MenuView;
import demogame.view.UserUpdateView;
import javax.swing.*;

public class MenuController {
    private MenuView view;
    private UserData currentUser;

    public MenuController(MenuView view, UserData currentUser) {
        this.view = view;
        this.currentUser = currentUser;
        setupButtonListeners();
    }

    private void setupButtonListeners() {
        // Start Game button
        view.getStartButton().addActionListener(e -> {
            view.dispose();
            SwingUtilities.invokeLater(() -> {
                LoadingView loadingView = new LoadingView();
                LoadingController loadingController = new LoadingController(loadingView);
                loadingView.setVisible(true);
            });
        });

        // Update button - using getUpdateButton() to match your MenuView
        view.getUpdateButton().addActionListener(e -> {
            handleUpdateProfile();
        });

        // Quit button
        view.getQuitButton().addActionListener(e -> {
            int response = JOptionPane.showConfirmDialog(view, 
                "Are you sure you want to quit?", 
                "Confirm Exit", 
                JOptionPane.YES_NO_OPTION);
            
            if (response == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });
    }

    private void handleUpdateProfile() {
        SwingUtilities.invokeLater(() -> {
            try {
                // Hide current menu
                view.setVisible(false);
                
                // Create and show update profile view
                UserUpdateView updateView = new UserUpdateView(
                    currentUser.getUsername(),
                    currentUser.getEmail()
                );
                
                // Create controller for update view
                new UserUpdateController(updateView, currentUser);
                
                // Show the update view
                updateView.setVisible(true);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(
                    view,
                    "Error opening profile update: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
                view.setVisible(true); // Show menu again if there's an error
            }
        });
    }

    // Method to show menu again (called from UserUpdateController)
    public void showMenu() {
        SwingUtilities.invokeLater(() -> {
            view.setVisible(true);
        });
    }
}