package demogame.view;
import java.awt.*;
import javax.swing.*;

public class LoadingView  extends JFrame{
    private JLabel logoLabel;
    private JProgressBar progressBar;
    private JLabel tipLabel;
    private JLabel loadingLabel;
    private Timer tipTimer;
    private int currentTipIndex = 0;

    // constructor
    public LoadingView(){
       
        setTitle("Loading");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Set background
        setContentPane(new JLabel(new ImageIcon(getClass().getResource("/resources/Loadingbg.jpg"))));
        getContentPane().setLayout(new GridBagLayout());

        initializeComponents();
        layoutComponents();
    }
    private void initializeComponents(){
        // logo setup
        ImageIcon logoIcon =  new ImageIcon(getClass().getResource("/resources/logo.png"));
        Image scaledLogo = logoIcon.getImage().getScaledInstance(300,300,Image.SCALE_SMOOTH);
        logoLabel = new JLabel(new ImageIcon(scaledLogo));

        // progress bar
        progressBar = new JProgressBar(0,100);
        progressBar.setStringPainted(true);
        progressBar.setForeground(Color.RED);
        progressBar.setBackground(Color.WHITE);
        progressBar.setPreferredSize(new Dimension(400,30));
    

    // Loading Text
    loadingLabel = new JLabel("Loading Game......🐒");
    loadingLabel.setFont(new Font("Comic Sans MS",Font.BOLD,24));
    loadingLabel.setForeground(Color.BLACK);

    // TIPS AND TRICK SECTION
    tipLabel = new JLabel();
    tipLabel.setFont(new Font("Comic Sans MS",Font.ITALIC,16));
    tipLabel.setForeground(Color.BLACK);
    tipLabel.setHorizontalAlignment(SwingConstants.CENTER);
    }

    // LAYOUT
    private void layoutComponents(){
        setLayout(new GridBagLayout());
        GridBagConstraints gbc =  new GridBagConstraints();

        // central panel 
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);

        // logo label
        gbc.gridx=0;
        gbc.gridy=0;
        gbc.insets = new Insets(0,0,20,0);
        centerPanel.add(logoLabel,gbc);

        // loading logic
        gbc.gridy=1;
        gbc.insets=  new Insets(0,0,10,0);
        centerPanel.add(loadingLabel,gbc);

        // progress bar
        gbc.gridy=2;
        centerPanel.add(progressBar,gbc);

        // center panel to frame
        gbc.gridy=0;
        add(centerPanel,gbc);

        // Tips and tricks at bottom
        gbc.gridy = 1;
        gbc.insets = new Insets(20, 0, 50, 0);
        add(tipLabel, gbc);
    }

     public void updateProgress(int progress) {
        progressBar.setValue(progress);
    }

    public void updateTip(String tip) {
        tipLabel.setText(tip);
    }

     public void dispose() {
        if (tipTimer != null) {
            tipTimer.stop();
        }
        super.dispose();
    }


    
    
}
 