import javax.swing.*;
import java.awt.Graphics;
import java.awt.Font;
import java.awt.Color;

public class DisplayPanel extends JPanel {
    private JButton button;
    public DisplayPanel() {
        button = new JButton("Start");
        add(button);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Color green = new Color(0,153,0);
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setFont(new Font("Papyrus", Font.BOLD, 85));
        g.setColor(green);
        g.drawString("Welcome to Rags 2 Riches!", 350, 75);
        g.setFont(new Font("Papyrus", Font.BOLD, 45));
        g.drawString("An application for stock market enthusiasts and learners", 300, 150);
        g.setColor(Color.white);
        g.drawString("Click Start to begin your journey of investing!", 395,400 );
        button.setLocation(600, 480);
        button.setSize(500,50);
        button.setFont(new Font("Arial", Font.BOLD, 55 ));

    }
}
