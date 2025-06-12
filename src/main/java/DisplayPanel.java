import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DisplayPanel extends JPanel implements ActionListener, MouseListener, KeyListener {
    private JButton startButton;
    private JButton backButton;
    private boolean showWelcomeScreen = true;
    private boolean showOptionsScreen = false;
    private boolean showSimulatorScreen = false;
    private boolean showPortfolioScreen = false;

    private ProgramLogic logic;

    private Rectangle buttonOneRectangle = new Rectangle(550, 150,600, 50);
    private Rectangle buttonTwoRectangle = new Rectangle(550, 200,600, 50);

    private String stockInput = "";
    private boolean typing = false;
    private final Rectangle inputBox = new Rectangle(100, 200, 400, 60);
    private final Rectangle submitButton = new Rectangle(520, 200, 150, 60);
    private Point mousePos = new Point(0, 0);

    public DisplayPanel(ProgramLogic logic) {
        this.logic = logic;
        setLayout(null);

        startButton = new JButton("Start");
        startButton.addActionListener(this);
        startButton.setBounds(600, 480, 500, 50);
        startButton.setFont(new Font("Arial", Font.BOLD, 55));
        add(startButton);

        backButton = new JButton("Back");
        backButton.addActionListener(this);
        backButton.setBounds(50, 50, 150, 40);
        backButton.setFont(new Font("Arial", Font.PLAIN, 20));
        backButton.setVisible(false);
        add(backButton);

        addMouseListener(this);
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                mousePos = e.getPoint();
                repaint();
            }
        });
        addKeyListener(this);
        setFocusable(true);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (showWelcomeScreen) {
            drawWelcomeScreen(g);
        } else if (showOptionsScreen) {
            drawInvestmentScreen(g);
        } else if (showSimulatorScreen) {
            drawSimulatorScreen((Graphics2D) g);
        } else if (showPortfolioScreen) {
            drawPortfolioScreen(g);
        }
    }

    private void drawWelcomeScreen(Graphics g) {
        Color green = new Color(0, 153, 0);

        g.setColor(Color.DARK_GRAY);
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setFont(new Font("Papyrus", Font.BOLD, 85));
        g.setColor(Color.white);
        g.drawString("Welcome to Rags 2 Riches!", 350, 75);

        g.setFont(new Font("Papyrus", Font.BOLD, 45));
        g.drawString("An application for stock market enthusiasts and learners", 300, 150);

        g.drawString("Click Start to begin your journey of investing!", 380, 400);

        startButton.setVisible(true);
        backButton.setVisible(false);
    }

    private void drawInvestmentScreen(Graphics g) {
        g.setColor(new Color(20, 20, 40)); // Dark blue background
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setFont(new Font("Arial", Font.BOLD, 90));
        g.setColor(Color.WHITE);
        g.drawString("Investment Dashboard", 400, 80);

        g.setFont(new Font("Arial", Font.PLAIN, 50));
        g.drawString("1. Stock Market Simulator", 550, 200);
        g.drawString("2. Portfolio Builder", 550, 250);
        g.drawString("3. Investment Tutorials (DO NOT CLICK)", 550, 300);
        g.drawString("4. Market Analysis Tools (DO NOT CLICK)", 550, 350);

        startButton.setVisible(false);
    }

    private void drawSimulatorScreen(Graphics2D g) {
        setBackground(Color.WHITE);

        int padding = 50;
        int rightMargin = 200;
        int graphX = padding;
        int graphY = padding;
        int graphWidth = getWidth() - rightMargin - 2 * padding;
        int graphHeight = getHeight() - 2 * padding;

        Rectangle nextDayButton = new Rectangle(getWidth() - 180, getHeight() - 80, 150, 50);

        g.setColor(nextDayButton.contains(mousePos) ? new Color(100, 200, 100) : new Color(50, 150, 50));
        g.fillRoundRect(nextDayButton.x, nextDayButton.y, nextDayButton.width, nextDayButton.height, 10, 10);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 18));
        g.drawString("Next Day", nextDayButton.x + 30, nextDayButton.y + 30);

        g.setColor(Color.LIGHT_GRAY);
        g.drawRect(graphX, graphY, graphWidth, graphHeight);

        int maxPoints = logic.getDay();
        if (maxPoints < 2) return;

        double max = logic.getHighestStockValue();
        double min = logic.getLowestStockValue();

        if (max == min) {
            max += 1;
            min -= 1;
        }

        double paddingAmount = (max - min) * 0.05;
        max += paddingAmount;
        min -= paddingAmount;
        double range = max - min;

        g.setColor(Color.GRAY);
        g.setFont(new Font("Arial", Font.PLAIN, 12));

        double yInterval = calculateNiceInterval(max - min);
        int numIntervals = (int)Math.ceil((max - min) / yInterval);

        for (int i = 0; i <= numIntervals; i++) {
            double value = min + (i * yInterval);
            if (value > max) continue;

            int yPos = graphY + graphHeight - (int)(((value - min) / range) * graphHeight);

            g.setColor(new Color(220, 220, 220));
            g.drawLine(graphX, yPos, graphX + graphWidth, yPos);

            g.setColor(Color.BLACK);
            String label = String.format("%.2f", value);
            g.drawString(label, graphX - 45, yPos + 5);
        }

        g.setColor(Color.GRAY);
        g.drawLine(graphX, graphY, graphX, graphY + graphHeight);

        int xInterval = Math.max(1, maxPoints / 10);
        for (int day = 0; day <= maxPoints; day += xInterval) {
            int xPos = graphX + (day * graphWidth / maxPoints);

            g.setColor(new Color(220, 220, 220));
            g.drawLine(xPos, graphY, xPos, graphY + graphHeight);

            g.setColor(Color.BLACK);
            g.drawString(Integer.toString(day), xPos - 5, graphY + graphHeight + 15);
        }

        g.setColor(Color.GRAY);
        g.drawLine(graphX, graphY + graphHeight, graphX + graphWidth, graphY + graphHeight);

        int legendX = graphX + graphWidth + 20;
        int legendY = graphY + 30;
        int boxWidth = 150;
        int boxHeight = 20;
        int boxSpacing = 30;

        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.drawString("Stock Performance", legendX, legendY);
        legendY += 30;

        for (int i = 0; i < logic.getListOfStockNames().size(); i++) {
            String currentStockName = logic.getListOfStockNames().get(i);
            ArrayList<Double> stockValues = logic.getMap().get(currentStockName);
            int pointsToGraph = stockValues.size() - 89;
            if (pointsToGraph < 2) continue;

            Color stockColor = logic.getColorFromSymbol(currentStockName);

            double currentValue = stockValues.get(stockValues.size() - 1);
            double previousValue = stockValues.get(stockValues.size() - 2);
            double percentChange = ((currentValue - previousValue) / previousValue) * 100;

            g.setColor(stockColor);
            g.fillRect(legendX, legendY, boxWidth, boxHeight);

            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.BOLD, 12));
            g.drawString(currentStockName, legendX + 5, legendY + 15);

            String changeText = String.format("%+.2f%%", percentChange);
            Color textColor = percentChange >= 0 ? Color.GREEN.darker() : Color.RED;
            g.setColor(textColor);
            g.drawString(changeText, legendX + boxWidth - g.getFontMetrics().stringWidth(changeText) - 5, legendY + 15);

            g.setColor(stockColor);
            int prevX = graphX;
            int prevY = graphY + graphHeight - (int)(((stockValues.get(89) - min) / range) * graphHeight);

            for (int j = 90; j < stockValues.size(); j++) {
                int x = graphX + ((j - 89) * graphWidth / (pointsToGraph - 1));
                int y = graphY + graphHeight - (int)(((stockValues.get(j) - min) / range) * graphHeight);
                g.drawLine(prevX, prevY, x, y);
                prevX = x;
                prevY = y;
            }

            legendY += boxSpacing;
        }
    }

    private double calculateNiceInterval(double range) {
        double exponent = Math.floor(Math.log10(range));
        double fraction = range / Math.pow(10, exponent);

        double niceFraction;
        if (fraction <= 1.5) {
            niceFraction = 1;
        } else if (fraction <= 3) {
            niceFraction = 2;
        } else if (fraction <= 7) {
            niceFraction = 5;
        } else {
            niceFraction = 10;
        }

        return niceFraction * Math.pow(10, exponent - 1);
    }

    private void drawPortfolioScreen(Graphics g) {
        g.setColor(new Color(240, 240, 240));
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 48)); // Larger font size
        g.drawString("Stock Portfolio Builder", getWidth()/2 - 200, 100);

        g.setColor(typing ? new Color(220, 240, 255) : Color.WHITE);
        g.fillRoundRect(inputBox.x, inputBox.y, inputBox.width, inputBox.height, 15, 15);
        g.setColor(Color.DARK_GRAY);
        g.drawRoundRect(inputBox.x, inputBox.y, inputBox.width, inputBox.height, 15, 15);

        g.setFont(new Font("Arial", Font.PLAIN, 28)); // Larger font size
        g.setColor(Color.BLACK);
        String displayText = typing ? stockInput + (System.currentTimeMillis() % 1000 < 500 ? "|" : "") :
                stockInput.isEmpty() ? "Enter stock symbol..." : stockInput;
        g.drawString(displayText, inputBox.x + 20, inputBox.y + 40);

        g.setColor(submitButton.contains(mousePos) ? new Color(70, 130, 180) : new Color(100, 149, 237));
        g.fillRoundRect(submitButton.x, submitButton.y, submitButton.width, submitButton.height, 15, 15);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 28)); // Larger font size
        g.drawString("Submit", submitButton.x + 30, submitButton.y + 40);

        backButton.setVisible(true);
        backButton.setBounds(50, 50, 150, 40);
    }

    public void changeScreen(String screen) {
        showWelcomeScreen = false;
        showOptionsScreen = false;
        showSimulatorScreen = false;
        showPortfolioScreen = false;
        if (screen.equals("Welcome")) {
            showWelcomeScreen = true;
        } else if (screen.equals("Options")) {
            showOptionsScreen = true;
            backButton.setVisible(false);
        } else if (screen.equals("Simulator")) {
            showSimulatorScreen = true;
            backButton.setVisible(true);
        } else if (screen.equals("Portfolio")) {
            showPortfolioScreen = true;
            backButton.setVisible(true);
            requestFocusInWindow();
        }
        repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == startButton) {
            changeScreen("Options");
        } else if (e.getSource() == backButton) {
            changeScreen("Options");
        }
        repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        Point clickPoint = e.getPoint();
        if (showOptionsScreen) {
            if (buttonOneRectangle.contains(clickPoint)) {
                changeScreen("Simulator");
            }
            if (buttonTwoRectangle.contains(clickPoint)) {
                changeScreen("Portfolio");
            }
            repaint();
        } else if (showPortfolioScreen) {
            if (inputBox.contains(clickPoint)) {
                typing = true;
                stockInput = "";
                repaint();
            } else if (submitButton.contains(clickPoint) && !stockInput.isEmpty()) {
                logic.initializeStock(stockInput);
                stockInput = "";
                typing = false;
                repaint();
            }
        } else if (showSimulatorScreen) {
            Rectangle nextDayButton = new Rectangle(getWidth() - 180, getHeight() - 80, 150, 50);
            if (nextDayButton.contains(clickPoint)) {
                logic.nextDay();
                repaint();
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        if (showPortfolioScreen && typing) {
            if (e.getKeyChar() == '\n') {
                if (!stockInput.isEmpty()) {
                    logic.initializeStock(stockInput);
                    stockInput = "";
                    typing = false;
                }
            } else if (e.getKeyChar() == '\b' && !stockInput.isEmpty()) {
                stockInput = stockInput.substring(0, stockInput.length() - 1);
            } else if (Character.isLetterOrDigit(e.getKeyChar())) {
                stockInput += e.getKeyChar();
            }
            repaint();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    // Other mouse listener methods
    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
}