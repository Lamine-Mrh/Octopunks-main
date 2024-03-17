import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.List;

public class App extends JFrame {

    private JTextArea codeTextArea;
    private JButton pasButton, stopButton, automatiqueButton;
    private GridPanel grid;
    private JLabel xLabel, tLabel;
    private Robot robot;
    private final Assembler assembler = new Assembler();
    private final List<File> musicFiles = new ArrayList<>();
    private Clip backgroundMusic;
    private final AtomicInteger currentMusicIndex = new AtomicInteger(0);
    private JFrame settingsFrame;
    private JTextArea settingsTextArea;

    public App() {
        initUI();
        initializeRobot();
        loadMusicFiles();
        playNextMusic();
    }

    private void initUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());
        JPanel buttonPanelL = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton musicButton = new JButton("M");
        musicButton.addActionListener(e -> handleMusicButton());
        buttonPanelL.add(musicButton);

        JButton settingsButton = new JButton("I");
        settingsButton.addActionListener(e -> handleSettingsButton());
        buttonPanelL.add(settingsButton);

        JButton levelButton = new JButton("L");
        levelButton.addActionListener(e -> handleLevelButton());
        buttonPanelL.add(levelButton);

        topPanel.add(buttonPanelL, BorderLayout.WEST);

        JLabel welcomeLabel = new JLabel("WELCOME TO OCTOPUNK", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 15));
        topPanel.add(welcomeLabel, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);

        codeTextArea = new JTextArea(20, 13);
        JScrollPane codeScrollPane = new JScrollPane(codeTextArea,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(codeScrollPane, BorderLayout.WEST);

        grid = new GridPanel();
        add(grid, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());

        pasButton = new JButton("Pas");
        pasButton.addActionListener(e -> handlePasButton());
        buttonPanel.add(pasButton);

        stopButton = new JButton("Stop");
        stopButton.addActionListener(e -> handleStopButton());
        buttonPanel.add(stopButton);

        automatiqueButton = new JButton("Auto");
        automatiqueButton.addActionListener(e -> handleAutomatiqueButton());
        buttonPanel.add(automatiqueButton);

        xLabel = new JLabel("X: ");
        tLabel = new JLabel("T: ");
        buttonPanel.add(xLabel);
        buttonPanel.add(tLabel);

        add(buttonPanel, BorderLayout.SOUTH);

        settingsFrame = new JFrame("Instructions");
        settingsFrame.setSize(700, 700);
        settingsFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        settingsFrame.setLayout(new BorderLayout());

        settingsTextArea = new JTextArea("Enter your settings here...");
        settingsTextArea.setEditable(false);
        JScrollPane settingsScrollPane = new JScrollPane(settingsTextArea);
        settingsFrame.add(settingsScrollPane, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private Object handleLevelButton() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleLevelButton'");
    }

    private Object handleSettingsButton() {
        SwingUtilities.invokeLater(() -> {
            try {
                String content = new String(Files.readAllBytes(Paths.get("tuto.txt")), StandardCharsets.UTF_8);
                settingsTextArea.setText(content);
                settingsFrame.setVisible(true);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Failed to load tutorial content.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        });
        return null;
    }

    private Object handleMusicButton() {
        SwingUtilities.invokeLater(() -> {
            skipSong();

        });
        return null;
    }

    private void skipSong() {
        if (backgroundMusic != null && backgroundMusic.isRunning()) {
            backgroundMusic.stop();
        }
    }

    private void initializeRobot() {
        Registre xRegister = new Registre("X", 0);
        Registre tRegister = new Registre("T", 0);
        Obstacle o1 = new Obstacle(2, 3);
        Obstacle o2 = new Obstacle(2, 2);
        Obstacle o3 = new Obstacle(4, 3);
        Obstacle o4 = new Obstacle(3, 4);
        ArrayList<Obstacle> obstacles = new ArrayList<Obstacle>();
        obstacles.add(o1);
        obstacles.add(o2);
        obstacles.add(o3);
        obstacles.add(o4);
        Grid grid = new Grid(5, obstacles);
        robot = new Robot("Robot", xRegister, tRegister, 0, 0, grid);
    }

    private void handlePasButton() {
        System.out.println(robot.getPointer());
        ArrayList<String> instructions = parseCode(codeTextArea.getText());
        if (robot != null && robot.getPointer() < instructions.size()) {
            System.out.println();
            try {
                assembler.executeOne(robot, instructions.get(robot.getPointer()), instructions);
                codeTextArea.setEditable(false);
                updateGrid();
                instructions.clear();
            } catch (Exception ex) {
                handleException(ex);
            }
        }
    }

    private void handleStopButton() {
        if (robot != null) {
            robot.reset();
            codeTextArea.setEditable(true);
            updateGrid();
        }
    }

    private void handleAutomatiqueButton() {
        ArrayList<String> instructions = parseCode(codeTextArea.getText());
        new Thread(() -> {
            int startPointer = robot.getPointer();
            while (robot != null && robot.getPointer() < instructions.size()) {
                try {
                    assembler.execute(robot, instructions);
                    robot.setPointer(robot.getPointer() + 1);
                    updateGrid();
                    Thread.sleep(500);
                } catch (Exception ex) {
                    handleException(ex);
                }
            }
            SwingUtilities.invokeLater(() -> codeTextArea.setText(""));
            robot.setPointer(startPointer);
        }).start();
    }

    private void handleException(Exception ex) {
        ex.printStackTrace();
    }

    private void updateGrid() {
        SwingUtilities.invokeLater(() -> grid.repaint());
        updateLabels();
    }

    private void updateLabels() {
        if (robot != null) {
            xLabel.setText("X: " + robot.getX().getValue());
            tLabel.setText("T: " + robot.getT().getValue());
        }
    }

    private ArrayList<String> parseCode(String code) {
        return assembler.parseAssembleurCode(code);
    }

    private void loadMusicFiles() {
        File musicFolder = new File("music");
        if (musicFolder.exists() && musicFolder.isDirectory()) {
            File[] files = musicFolder.listFiles((dir, name) -> name.toLowerCase().endsWith(".wav"));
            if (files != null) {
                Collections.addAll(musicFiles, files);
            }
        }
        Collections.shuffle(musicFiles);
    }

    private void playNextMusic() {
        if (!musicFiles.isEmpty()) {
            try {
                if (backgroundMusic != null && backgroundMusic.isOpen()) {
                    backgroundMusic.stop();
                    backgroundMusic.close();
                }

                File musicFile = musicFiles.get(currentMusicIndex.getAndIncrement() % musicFiles.size());
                System.out.println("Playing: " + musicFile.getName());
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(musicFile);
                backgroundMusic = AudioSystem.getClip();
                backgroundMusic.open(audioInputStream);
                backgroundMusic.addLineListener(event -> {
                    if (event.getType() == LineEvent.Type.STOP) {
                        playNextMusic();
                    }
                });
                backgroundMusic.start();
            } catch (LineUnavailableException | UnsupportedAudioFileException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            App app = new App();
            app.setSize(370, 300);
            app.setVisible(true);
        });
    }

    private class GridPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            int gridWidth = getWidth();
            int gridHeight = getHeight();
            int cellSize = Math.min(gridWidth, gridHeight) / 5;

            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 5; j++) {
                    g.drawRect(i * cellSize, j * cellSize, cellSize, cellSize);
                }
            }

            if (robot != null) {
                int robotPosX = robot.getPosX();
                int robotPosY = robot.getPosY();
                int robotX = robotPosX * cellSize + cellSize / 2;
                int robotY = robotPosY * cellSize + cellSize / 2;
                g.drawString("X", robotX, robotY);

                for (Obstacle o : robot.getGrid().getObstacles()) {
                    int obstacleX = o.getPosX() * cellSize + cellSize / 2;
                    int obstacleY = o.getPosY() * cellSize + cellSize / 2;
                    g.drawString("@", obstacleX, obstacleY);
                }
            }
        }
    }
}