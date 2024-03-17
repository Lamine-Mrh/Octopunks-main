import javax.swing.*;
import java.awt.*;

public class SplitPaneExample {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            createAndShowGUI();
        });
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("SplitPane Example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);

        // Create panels for left and right sides
        JPanel leftPanel = new JPanel(new BorderLayout());
        JPanel rightPanel = new JPanel(new BorderLayout());

        // Add components to left panel (levels)
        JTextArea levelsTextArea = new JTextArea("Levels");
        levelsTextArea.setEditable(false);
        leftPanel.add(levelsTextArea, BorderLayout.CENTER);

        // Add components to right panel (objectives)
        JTextArea objectivesTextArea = new JTextArea("Objectives");
        objectivesTextArea.setEditable(false);
        rightPanel.add(objectivesTextArea, BorderLayout.CENTER);

        // Create split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setDividerLocation(300); // Initial position of divider
        splitPane.setDividerSize(5); // Size of the divider

        // Add split pane to frame
        frame.getContentPane().add(splitPane, BorderLayout.CENTER);

        // Create button for loading levels
        JButton loadButton = new JButton("Load Level");
        frame.getContentPane().add(loadButton, BorderLayout.SOUTH);

        frame.setVisible(true);
    }
}