package demogame.view;
import java.awt.*;

import javax.swing.BorderFactory;


public class QuitButtonView {
    // Constants for button styling
    private static final int BUTTON_WIDTH = 120;
    private static final int BUTTON_HEIGHT = 40;
    private static final String BUTTON_TEXT = "Quit to Menu";
    private static final String FONT_FAMILY = "Comic Sans MS";
    private static final int FONT_SIZE = 14;
    private static final Color TEXT_COLOR = Color.RED;
    private static final Color BACKGROUND_COLOR = new Color(255, 255, 255, 200);
    private static final Color HOVER_COLOR = new Color(255, 200, 200, 200);
     public QuitButtonView() {
        initializeButton();
        setupStyle();
        addHoverEffect();
    }
     private void initializeButton() {
        setText(BUTTON_TEXT);
        setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        setFocusPainted(false);
        setBorderPainted(true);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
       
    private void setupStyle() {
        setFont(new Font(FONT_FAMILY, Font.BOLD, FONT_SIZE));
        setForeground(TEXT_COLOR);
        setBackground(BACKGROUND_COLOR);
        
        // Optional: Add border
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(TEXT_COLOR, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
    }


    



    
}
