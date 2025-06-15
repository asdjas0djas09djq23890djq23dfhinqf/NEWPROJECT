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
    private boolean askingForQuantity = false;
    private String currentStockSymbol = "";
    private String quantityInput = "";
    private JLabel statusLabel;
    private JButton updatePricesButton;
    private JButton resetPortfolioButton;
    private JProgressBar progressBar;

    private ProgramLogic logic;

    private Rectangle buttonOneRectangle = new Rectangle(550, 150, 600, 50);
    private Rectangle buttonTwoRectangle = new Rectangle(550, 200, 600, 50);

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
        startButton.setBounds(600, 480, 500, 60);
        startButton.setFont(new Font("Papyrus", Font.BOLD, 45));
        startButton.setBackground(Color.BLACK);
        startButton.setForeground(new Color(0, 200, 0));
        startButton.setFocusPainted(false);
        startButton.setBorder(BorderFactory.createLineBorder(new Color(0, 200, 0), 2));
        add(startButton);


        backButton = new JButton("Back");
        backButton.addActionListener(this);
        backButton.setBounds(50, 50, 150, 40);
        backButton.setFont(new Font("Papyrus", Font.BOLD, 20));
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
        statusLabel = new JLabel("Ready", SwingConstants.CENTER);
        statusLabel.setBounds(50, getHeight() - 100, getWidth() - 100, 20);
        add(statusLabel);

        progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setVisible(false);
        progressBar.setBounds(50, getHeight() - 70, getWidth() - 100, 20);
        add(progressBar);

        updatePricesButton = new JButton("Update Prices");
        updatePricesButton.setBounds(getWidth()/2 - 150, 400, 300, 40);
        updatePricesButton.addActionListener(e -> updateStockPrices());
        updatePricesButton.setVisible(false);
        add(updatePricesButton);

        resetPortfolioButton = new JButton("Reset Portfolio");
        resetPortfolioButton.setBounds(getWidth()/2 - 150, 450, 300, 40);
        resetPortfolioButton.setVisible(false);
        add(resetPortfolioButton);
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

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setFont(new Font("Papyrus", Font.BOLD, 85));
        g.setColor(green);
        g.drawString("Welcome to Rags 2 Riches!", 350, 75);

        g.setFont(new Font("Papyrus", Font.BOLD, 45));
        g.drawString("An application for stock market enthusiasts and learners", 300, 150);
        g.drawString("Click Start to begin your journey of investing!", 400, 400);

        startButton.setVisible(true);
        backButton.setVisible(false);
    }

    private void drawInvestmentScreen(Graphics g) {
        Color green = new Color(0, 153, 0);
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setFont(new Font("Papyrus", Font.BOLD, 90));
        g.setColor(green);
        g.drawString("Investment Dashboard", 410, 85);

        g.setFont(new Font("Papyrus", Font.BOLD, 50));
        g.drawString("1. Stock Market Simulator", 15, 200);
        g.drawString("2. Portfolio Builder", 15, 265);
        g.drawString("3. Investment Tutorials (COMING SOON)", 15, 330);
        g.drawString("4. Market Analysis Tools (COMING SOON)", 15, 395);

        buttonOneRectangle = new Rectangle(15, 150, 550, 50);
        buttonTwoRectangle = new Rectangle(15, 215, 400, 50);

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
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());

        Color green = new Color(0, 200, 0);
        g.setColor(green);
        g.setFont(new Font("Papyrus", Font.BOLD, 48));

        String title = "Stock Portfolio Builder";
        int titleWidth = g.getFontMetrics().stringWidth(title);
        g.drawString(title, getWidth()/2 - titleWidth/2, 100);

        inputBox.setLocation(getWidth()/2 - inputBox.width/2, 220);

        g.setFont(new Font("Papyrus", Font.PLAIN, 28));
        String promptText = askingForQuantity ? "Enter quantity:" : "Enter stock symbol:";
        int promptWidth = g.getFontMetrics().stringWidth(promptText);
        g.drawString(promptText, getWidth()/2 - promptWidth/2, 200);

        g.setColor(new Color(10, 30, 10));
        g.fillRoundRect(inputBox.x, inputBox.y, inputBox.width, inputBox.height, 15, 15);
        g.setColor(green);
        g.drawRoundRect(inputBox.x, inputBox.y, inputBox.width, inputBox.height, 15, 15);

        String displayText = "";
        if (typing) {
            String currentInput = askingForQuantity ? quantityInput : stockInput;
            displayText = currentInput + (System.currentTimeMillis() % 1000 < 500 ? "|" : "");
        }

        int textWidth = g.getFontMetrics().stringWidth(displayText);
        int textX = inputBox.x + (inputBox.width - textWidth)/2;
        g.drawString(displayText, textX, inputBox.y + 40);

        submitButton.setLocation(getWidth()/2 - submitButton.width/2, inputBox.y + inputBox.height + 20);
        g.setColor(submitButton.contains(mousePos) ? new Color(0, 100, 0) : new Color(0, 150, 0));
        g.fillRoundRect(submitButton.x, submitButton.y, submitButton.width, submitButton.height, 15, 15);
        g.setColor(green);
        g.setFont(new Font("Papyrus", Font.BOLD, 28));
        String submitText = askingForQuantity ? "Buy" : "Submit";
        int submitTextWidth = g.getFontMetrics().stringWidth(submitText);
        g.drawString(submitText, submitButton.x + (submitButton.width - submitTextWidth)/2, submitButton.y + 40);

        backButton.setVisible(true);
        backButton.setBounds(50, 50, 150, 40);
        backButton.setFont(new Font("Papyrus", Font.BOLD, 20));
        backButton.setForeground(green);
        backButton.setBackground(Color.BLACK);
        backButton.setBorder(BorderFactory.createLineBorder(green));
    }

    @Override
    public void keyTyped(KeyEvent e) {
        if (showPortfolioScreen && typing) {
            if (e.getKeyChar() == '\n') {
                if (askingForQuantity && !quantityInput.isEmpty()) {
                    try {
                        int quantity = Integer.parseInt(quantityInput);
                        double currentPrice = logic.getMap().get(currentStockSymbol).get(logic.getMap().get(currentStockSymbol).size() - 1);
                        logic.buyStock(currentStockSymbol, quantity);
                        askingForQuantity = false;
                        currentStockSymbol = "";
                        quantityInput = "";
                        typing = false;
                    } catch (Exception ex) {
                        statusLabel.setText(ex.getMessage());
                        quantityInput = "";
                    }
                } else if (!askingForQuantity && !stockInput.isEmpty()) {
                    currentStockSymbol = stockInput.toUpperCase();
                    askingForQuantity = true;
                    stockInput = "";
                    typing = true;
                }
                repaint();
            } else if (e.getKeyChar() == '\b') {
                if (askingForQuantity && !quantityInput.isEmpty()) {
                    quantityInput = quantityInput.substring(0, quantityInput.length() - 1);
                } else if (!askingForQuantity && !stockInput.isEmpty()) {
                    stockInput = stockInput.substring(0, stockInput.length() - 1);
                }
            } else if (Character.isLetterOrDigit(e.getKeyChar())) {
                if (askingForQuantity && Character.isDigit(e.getKeyChar())) {
                    quantityInput += e.getKeyChar();
                } else if (!askingForQuantity) {
                    stockInput += e.getKeyChar();
                }
            }
            repaint();
        }
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

    private void showLoading(boolean loading) {
        progressBar.setVisible(loading);
        progressBar.setIndeterminate(loading);
    }

    private void updateStockPrices() {
        showLoading(true);
        new Thread(() -> {
            SwingUtilities.invokeLater(() -> {
                showLoading(false);
                repaint();
            });
        }).start();
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
            // This is where the new code goes - replacing any existing portfolio screen handling
            if (inputBox.contains(clickPoint)) {
                typing = true;
                if (askingForQuantity) {
                    quantityInput = "";
                } else {
                    stockInput = "";
                }
                repaint();
            } else if (submitButton.contains(clickPoint)) {
                if (askingForQuantity && !quantityInput.isEmpty()) {
                    try {
                        int quantity = Integer.parseInt(quantityInput);
                        logic.buyStock(currentStockSymbol, quantity);
                        askingForQuantity = false;
                        currentStockSymbol = "";
                        quantityInput = "";
                        typing = false;
                    } catch (Exception ex) {
                        statusLabel.setText(ex.getMessage());
                        quantityInput = "";
                    }
                    repaint();
                } else if (!askingForQuantity && !stockInput.isEmpty()) {
                    currentStockSymbol = stockInput.toUpperCase();
                    askingForQuantity = true;
                    stockInput = "";
                    typing = true;
                    repaint();
                }
            }
        } else if (showSimulatorScreen) {
            Rectangle nextDayButton = new Rectangle(getWidth() - 180, getHeight() - 80, 150, 50);
            if (nextDayButton.contains(clickPoint)) {
                logic.nextDay();
                repaint();
            }
        }
    }
    @Override public void keyPressed(KeyEvent e) {}
    @Override public void keyReleased(KeyEvent e) {}
    @Override
    public void mousePressed(MouseEvent e) {
        Point clickPoint = e.getPoint();

        Rectangle nextDayButton = new Rectangle(getWidth() - 180, getHeight() - 80, 150, 50);
        if (nextDayButton.contains(clickPoint)) {
            logic.nextDay();
            repaint();
        }
    }
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
}