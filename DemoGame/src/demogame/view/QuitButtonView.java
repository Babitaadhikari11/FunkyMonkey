// package demogame.view;
// import java.awt.*;

// import javax.swing.BorderFactory;


// public class QuitButtonView {
//     // Constants for button styling
//     private static final int BUTTON_WIDTH = 120;
//     private static final int BUTTON_HEIGHT = 40;
//     private static final String BUTTON_TEXT = "Quit to Menu";
//     private static final String FONT_FAMILY = "Comic Sans MS";
//     private static final int FONT_SIZE = 14;
//     private static final Color TEXT_COLOR = Color.RED;
//     private static final Color BACKGROUND_COLOR = new Color(255, 255, 255, 200);
//     private static final Color HOVER_COLOR = new Color(255, 200, 200, 200);
//      public QuitButtonView() {
//         initializeButton();
//         setupStyle();
//         addHoverEffect();
//     }
//      private void initializeButton() {
//         setText(BUTTON_TEXT);
//         setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
//         setFocusPainted(false);
//         setBorderPainted(true);
//         setCursor(new Cursor(Cursor.HAND_CURSOR));
//     }
       
//     private void setupStyle() {
//         setFont(new Font(FONT_FAMILY, Font.BOLD, FONT_SIZE));
//         setForeground(TEXT_COLOR);
//         setBackground(BACKGROUND_COLOR);
        
//         // Optional: Add border
//         setBorder(BorderFactory.createCompoundBorder(
//             BorderFactory.createLineBorder(TEXT_COLOR, 1),
//             BorderFactory.createEmptyBorder(5, 10, 5, 10)
//         ));
//     }
//         private void addHoverEffect() {
//         addMouseListener(new java.awt.event.MouseAdapter() {
//             @Override
//             public void mouseEntered(java.awt.event.MouseEvent evt) {
//                 setBackground(HOVER_COLOR);
//                 setBorder(BorderFactory.createCompoundBorder(
//                     BorderFactory.createLineBorder(TEXT_COLOR, 2),
//                     BorderFactory.createEmptyBorder(4, 9, 4, 9)
//                 ));
//             }
//                     @Override
//             public void mousePressed(java.awt.event.MouseEvent evt) {
//                 setBackground(new Color(255, 150, 150, 200));
//             }
            
//             @Override
//             public void mouseReleased(java.awt.event.MouseEvent evt) {
//                 if (contains(evt.getPoint())) {
//                     setBackground(HOVER_COLOR);
//                 } else {
//                     setBackground(BACKGROUND_COLOR);
//                 }
//             }
//         });
//          @Override
//     protected void paintComponent(Graphics g) {
//         Graphics2D g2d = (Graphics2D) g.create();
//         g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
//                             RenderingHints.VALUE_ANTIALIAS_ON);
//         g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, 
//                             RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
//         // Draw background with slight transparency
//         g2d.setColor(getBackground());
//         g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
        
//         // Draw text
//         FontMetrics fm = g2d.getFontMetrics(getFont());
//         Rectangle stringBounds = fm.getStringBounds(getText(), g2d).getBounds();
        
//         int textX = (getWidth() - stringBounds.width) / 2;
//         int textY = (getHeight() - stringBounds.height) / 2 + fm.getAscent();
        
//         g2d.setColor(getForeground());
//         g2d.setFont(getFont());
//         g2d.drawString(getText(), textX, textY);
        
//         g2d.dispose();
//     }
//       // Optional: Add method to change text color
//     public void setTextColor(Color color) {
//         setForeground(color);
//         repaint();
//     }
    
//     // Optional: Add method to change background color
//     public void setButtonBackground(Color color) {
//         setBackground(color);
//         repaint();
//     }


//     }




    



    
// }
