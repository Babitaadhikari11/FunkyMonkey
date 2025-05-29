package demogame;

import demogame.model.SignUpModel;
import demogame.view.SignUpView;
import demogame.controller.SignupController;

public class DemoGame {
    public static void main(String[] args) {
        SignUpView signUpView = new SignUpView();
        SignUpModel signUpModel = new SignUpModel();
        new SignupController(signUpModel, signUpView);
        signUpView.setVisible(true);
    }
}