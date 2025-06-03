package demogame.controller;

import demogame.model.UserData;
import demogame.view.MenuView;
// import demogame.view.UserUpdateView;

import javax.swing.*;

public class MenuController {
    private MenuView view;
    // private UserData currentUser;

    public MenuController(MenuView view, UserData currentUser) {
        this.view = view; //receives view and user data from loginPage
        // this.currentUser = currentUser; //this contains userinformation from database
        setupButtonListeners();
    }
// button action
    private void setupButtonListeners() {
        // getting startbutton from view using getStartButton
        view.getStartButton().addActionListener(e -> {
            JOptionPane.showMessageDialog(view, "Starting game...");
        });

        // view.getUpdateButton().addActionListener(e -> {
        //     // prevent overlapping of window of updateuser and menu view
        //     view.setVisible(false);
        //     UserUpdateView updateView = new UserUpdateView(currentUser.getUsername(), currentUser.getEmail());
        //     new UserUpdateController(updateView, currentUser);
        //     updateView.setVisible(true);
        // });

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