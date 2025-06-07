package demogame.controller;

import demogame.model.UserData;
import demogame.view.LoadingView;
import demogame.view.MenuView;


import javax.swing.*;

public class MenuController {
    private MenuView view;
    

    public MenuController(MenuView view, UserData currentUser) {
        this.view = view; //receives view and user data from loginPage
        
        setupButtonListeners();
    }
// button action
    private void setupButtonListeners() {
        // getting startbutton from view using getStartButton
        view.getStartButton().addActionListener(e -> {
       // Close the menu view
            view.dispose();
            
            // Create and show the loading view
            SwingUtilities.invokeLater(() -> {
                LoadingView loadingView = new LoadingView();
                LoadingController loadingController = new LoadingController(loadingView);
                loadingView.setVisible(true);
            });
        });

       

        view.getQuitButton().addActionListener(e -> {
            //this will show confirmation dialog , a JOptionPane feature
            int response = JOptionPane.showConfirmDialog(view, 
                "Are you sure you want to quit?", 
                "Confirm Exit", 
                JOptionPane.YES_NO_OPTION);
            // ehck for user response
            if (response == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });
    }
}
