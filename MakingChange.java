import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;

public class MakingChange extends Frame implements ActionListener {
    TextField coinsField, amountField;
    Button calculateButton;
    Label resultLabel;
    Label[][] dpLabels;

    public MakingChange() {
        setLayout(new GridLayout(6, 1));

        Panel inputPanel = new Panel(new FlowLayout());
        inputPanel.add(new Label("Enter coins separated by commas:"));
        coinsField = new TextField(20);
        inputPanel.add(coinsField);
        add(inputPanel);

        Panel amountPanel = new Panel(new FlowLayout());
        amountPanel.add(new Label("Enter amount:"));
        amountField = new TextField(10);
        amountPanel.add(amountField);
        add(amountPanel);

        Panel buttonPanel = new Panel(new FlowLayout());
        calculateButton = new Button("Calculate");
        calculateButton.addActionListener(this);
        buttonPanel.add(calculateButton);
        add(buttonPanel);

        resultLabel = new Label("Result will be displayed here");
        add(resultLabel);

        setSize(600, 400);
        setVisible(true);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });
    }

    public void actionPerformed(ActionEvent e) {
        try {
            String[] coinsStr = coinsField.getText().split(",");
            int[] coins = new int[coinsStr.length];
            for (int i = 0; i < coinsStr.length; i++) {
                coins[i] = Integer.parseInt(coinsStr[i].trim());
            }

            int amount = Integer.parseInt(amountField.getText());

            int[][] dpTable = new int[coins.length + 1][amount + 1];
            int[] coinCount = new int[coins.length];
            int result = minCoins(coins, amount, dpTable);

            boolean[][] inclusionUsed = new boolean[coins.length + 1][amount + 1];
            computeInclusion(dpTable, coins, amount, coinCount, inclusionUsed);

            if (result == -1) {
                resultLabel.setText("Change cannot be made with given coins.");
            } else {
                StringBuilder resText = new StringBuilder("Minimum coins required: " + result + " | Coins used: ");
                for (int i = 0; i < coins.length; i++) {
                    if (coinCount[i] > 0) {
                        resText.append(coins[i]).append("x").append(coinCount[i]).append(" ");
                    }
                }
                resultLabel.setText(resText.toString());
            }

            displayDpTable(dpTable, coins, amount, inclusionUsed);
        } catch (Exception ex) {
            resultLabel.setText("Invalid input. Please enter valid numbers.");
        }
    }

    public static int minCoins(int[] coins, int amount, int[][] C) {
        int n = coins.length;

        for (int i = 1; i <= n; i++) {
            C[i][0] = 0;
        }
        for (int j = 1; j <= amount; j++) {
            C[0][j] = Integer.MAX_VALUE - 1;
        }

        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= amount; j++) {
                if (j < coins[i - 1]) {
                    C[i][j] = C[i - 1][j];
                } else {
                    C[i][j] = Math.min(C[i - 1][j], 1 + C[i][j - coins[i - 1]]);
                }
            }
        }

        return C[n][amount] == Integer.MAX_VALUE - 1 ? -1 : C[n][amount];
    }

    public static void computeInclusion(int[][] C, int[] coins, int amount, int[] coinCount, boolean[][] inclusionUsed) {
        int i = coins.length, j = amount;
        while (i > 0 && j > 0) {
            if (C[i][j] == C[i - 1][j]) {
                i--;
            } else {
                coinCount[i - 1]++;
                 inclusionUsed[i][j] = true;
                j -= coins[i - 1];
            }
        }
    }

    public void displayDpTable(int[][] dpTable, int[] coins, int amount, boolean[][] inclusionUsed) {
        Frame tableFrame = new Frame("DP Table");
        tableFrame.setLayout(new GridLayout(coins.length + 2, amount + 2));

        tableFrame.add(new Label("Coin/Amount"));
        for (int j = 0; j <= amount; j++) {
            tableFrame.add(new Label(String.valueOf(j)));
        }

        for (int i = 0; i <= coins.length; i++) {
            if (i == 0) {
                tableFrame.add(new Label("0"));
            } else {
                tableFrame.add(new Label(String.valueOf(coins[i - 1])));
            }
            for (int j = 0; j <= amount; j++) {
                Label cell = new Label(dpTable[i][j] == Integer.MAX_VALUE - 1 ? "∞" : String.valueOf(dpTable[i][j]));
                if (inclusionUsed[i][j]) {
                    cell.setBackground(Color.YELLOW);
                }
                tableFrame.add(cell);
            }
        }

        tableFrame.setSize(600, 400);
        tableFrame.setVisible(true);
        tableFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                tableFrame.dispose();
            }
        });
    }

    public static void main(String[] args) {
        new MakingChange();
    }
}
