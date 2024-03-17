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
import java.util.Random;

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

    private JFrame levelsFrame;
    private JList<String> levelsList;
    private JTextArea objectivesTextArea;

    private int currentLevel;

    private static ImageIcon resizeIcon(ImageIcon icon, int width, int height) {
        Image img = icon.getImage();
        Image resizedImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(resizedImg);
    }

    public App() {
        initUI();
        initializeRobot();
        initializeRobot(robot);
        loadMusicFiles();
        playNextMusic();
        initializeLevelsFrame();
    }

    private void initUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());
        JPanel buttonPanelL = new JPanel(new FlowLayout(FlowLayout.LEFT));

        ImageIcon musicIcon = resizeIcon(new ImageIcon("images/music.png"), 20, 20);
        JButton musicButton = new JButton(musicIcon);
        musicButton.addActionListener(e -> handleMusicButton());
        buttonPanelL.add(musicButton);

        ImageIcon tutoIcon = resizeIcon(new ImageIcon("images/tuto.png"), 20, 20);
        JButton settingsButton = new JButton(tutoIcon);
        settingsButton.addActionListener(e -> handleSettingsButton());
        buttonPanelL.add(settingsButton);

        ImageIcon levelsIcon = resizeIcon(new ImageIcon("images/levels.png"), 20, 20);
        JButton levelButton = new JButton(levelsIcon);
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

        pasButton = new JButton("Step");
        pasButton.addActionListener(e -> handlePasButton());
        buttonPanel.add(pasButton);

        stopButton = new JButton("Reset");
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
        SwingUtilities.invokeLater(() -> levelsFrame.setVisible(true));
        return null;
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

    private void initializeLevelsFrame() {
        levelsFrame = new JFrame("Levels");
        levelsFrame.setSize(300, 250);
        levelsFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        levelsFrame.setLayout(new BorderLayout());

        JPanel levelsPanel = new JPanel(new BorderLayout());

        String[] levels = new String[11];
        for (int i = 0; i <= 10; i++) {
            levels[i] = "Level " + i;
        }
        levelsList = new JList<>(levels);
        levelsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        levelsList.addListSelectionListener(e -> displayObjective());
        levelsPanel.add(new JScrollPane(levelsList), BorderLayout.CENTER);

        JPanel objectivesPanel = new JPanel(new BorderLayout());
        objectivesTextArea = new JTextArea();
        objectivesTextArea.setEditable(false);
        objectivesPanel.add(new JScrollPane(objectivesTextArea), BorderLayout.CENTER);

        JButton loadLevelButton = new JButton("Load Level");
        loadLevelButton.addActionListener(e -> loadSelectedLevel());
        objectivesPanel.add(loadLevelButton, BorderLayout.SOUTH);

        levelsFrame.add(levelsPanel, BorderLayout.WEST);
        levelsFrame.add(objectivesPanel, BorderLayout.CENTER);
    }

    private void displayObjective() {
        int selectedLevelIndex = levelsList.getSelectedIndex();
        if (selectedLevelIndex >= 0) {
            String levelFilePath = "levels/" + selectedLevelIndex + ".txt";
            try {
                String objective = new String(Files.readAllBytes(Paths.get(levelFilePath)), StandardCharsets.UTF_8);
                objectivesTextArea.setText(objective);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Failed to load objective for Level " + selectedLevelIndex, "Error",
                        JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    private void loadSelectedLevel() {
        int selectedLevelIndex = levelsList.getSelectedIndex();
        if (selectedLevelIndex >= 0) {
            switch (selectedLevelIndex) {
                case 0:
                    currentLevel = 0;
                    Random random0 = new Random();
                    Grid grid0 = new Grid(5);
                    Registre X0 = new Registre("X0", random0.nextInt(4999));
                    Registre T0 = new Registre("T0", random0.nextInt(4999));
                    Robot robot0 = new Robot("Robot0", X0, T0, 3, 1, grid0);
                    initializeRobot(robot0);
                    updateGrid();

                    break;

                case 1:
                    currentLevel = 1;
                    Random random1 = new Random();
                    Grid grid1 = new Grid(5);
                    Registre X1 = new Registre("X1", 5);
                    Registre T1 = new Registre("T1", 3);
                    Robot robot1 = new Robot("Robot1", X1, T1, random1.nextInt(5), random1.nextInt(5), grid1);
                    initializeRobot(robot1);
                    updateGrid();

                    break;

                case 2:
                    currentLevel = 2;
                    Random random2 = new Random();
                    Grid grid2 = new Grid(5);
                    Registre X2 = new Registre("X2", random2.nextInt(9999));
                    Registre T2 = new Registre("T2", random2.nextInt(9999));
                    Robot robot2 = new Robot("Robot2", X2, T2, random2.nextInt(5), random2.nextInt(5), grid2);
                    initializeRobot(robot2);
                    updateGrid();

                    break;

                case 3:
                    currentLevel = 3;
                    Random random3 = new Random();
                    Grid grid3 = new Grid(5);
                    Registre X3 = new Registre("X3", random3.nextInt(9999));
                    Registre T3 = new Registre("T3", random3.nextInt(9999));
                    Robot robot3 = new Robot("Robot3", X3, T3, random3.nextInt(5), random3.nextInt(5), grid3);
                    initializeRobot(robot3);
                    updateGrid();

                    break;

                case 4:
                    ArrayList<Obstacle> obstacles = new ArrayList<Obstacle>();
                    obstacles.add(new Obstacle(0, 0));
                    obstacles.add(new Obstacle(4, 0));
                    obstacles.add(new Obstacle(2, 1));
                    obstacles.add(new Obstacle(4, 1));
                    obstacles.add(new Obstacle(0, 2));
                    obstacles.add(new Obstacle(1, 2));
                    obstacles.add(new Obstacle(2, 2));
                    obstacles.add(new Obstacle(4, 2));
                    obstacles.add(new Obstacle(0, 3));
                    obstacles.add(new Obstacle(4, 3));
                    obstacles.add(new Obstacle(0, 4));
                    obstacles.add(new Obstacle(2, 4));
                    obstacles.add(new Obstacle(3, 4));
                    obstacles.add(new Obstacle(4, 4));
                    currentLevel = 4;
                    Grid grid4 = new Grid(5, obstacles);
                    Registre X4 = new Registre("X4", 0);
                    Registre T4 = new Registre("T4", 0);
                    Robot robot4 = new Robot("Robot4", X4, T4, 0, 1, grid4);
                    initializeRobot(robot4);
                    updateGrid();

                    break;

                case 5:
                    currentLevel = 5;
                    Grid grid5 = new Grid(5);
                    Registre X5 = new Registre("X5", 8762);
                    Registre T5 = new Registre("T5", 568);
                    Robot robot5 = new Robot("Robot5", X5, T5, 2, 3, grid5);
                    initializeRobot(robot5);
                    updateGrid();

                    break;

                case 6:
                    currentLevel = 6;
                    Grid grid6 = new Grid(5);
                    Registre X6 = new Registre("X6", 6984);
                    Registre T6 = new Registre("T6", 2561);
                    Robot robot6 = new Robot("Robot6", X6, T6, 4, 3, grid6);
                    initializeRobot(robot6);
                    updateGrid();

                    break;

                case 7:
                    currentLevel = 7;
                    Grid grid7 = new Grid(5);
                    Registre X7 = new Registre("X7", 6984);
                    Registre T7 = new Registre("T7", 2561);
                    Robot robot7 = new Robot("Robot7", X7, T7, 1, 3, grid7);
                    initializeRobot(robot7);
                    updateGrid();

                    break;

                case 8:
                    currentLevel = 8;
                    ArrayList<Obstacle> obstacles0 = new ArrayList<Obstacle>();
                    obstacles0.add(new Obstacle(0, 2));
                    obstacles0.add(new Obstacle(0, 4));
                    obstacles0.add(new Obstacle(1, 1));
                    obstacles0.add(new Obstacle(1, 2));
                    obstacles0.add(new Obstacle(4, 3));
                    obstacles0.add(new Obstacle(2, 1));
                    Grid grid8 = new Grid(5, obstacles0);
                    Registre X8 = new Registre("X8", 6984);
                    Registre T8 = new Registre("T8", 2561);
                    Robot robot8 = new Robot("Robot8", X8, T8, 3, 3, grid8);
                    initializeRobot(robot8);
                    updateGrid();

                    break;

                case 9:
                    currentLevel = 9;
                    Random random9 = new Random();
                    Grid grid9 = new Grid(5);
                    Registre X9 = new Registre("X9", random9.nextInt(9999));
                    Registre T9 = new Registre("T9", random9.nextInt(9999));
                    Robot robot9 = new Robot("Robot2", X9, T9, random9.nextInt(5), random9.nextInt(5), grid9);
                    initializeRobot(robot9);
                    updateGrid();

                    break;

                case 10:
                    currentLevel = 10;
                    Random random10 = new Random();
                    Grid grid10 = new Grid(5);
                    Registre X10 = new Registre("X10", random10.nextInt(9999));
                    Registre T10 = new Registre("T10", random10.nextInt(9999));
                    Robot robot10 = new Robot("Robot2", X10, T10, random10.nextInt(5), random10.nextInt(5), grid10);
                    initializeRobot(robot10);
                    updateGrid();
                    break;

                default:
                    break;
            }
        }
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
        updateGrid();
    }

    public void initializeRobot(Robot r) {
        this.robot = r;
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
            app.setSize(400, 337);
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

                if (currentLevel == 1) {
                    int starX = 2 * cellSize + cellSize / 2;
                    int sratY = 4 * cellSize + cellSize / 2;
                    g.drawString("#", starX, sratY);
                }

                if (currentLevel == 4) {
                    int starX = 1 * cellSize + cellSize / 2;
                    int sratY = 4 * cellSize + cellSize / 2;
                    g.drawString("#", starX, sratY);
                }

                if (currentLevel == 8) {
                    int starX = 0 * cellSize + cellSize / 2;
                    int sratY = 0 * cellSize + cellSize / 2;
                    g.drawString("#", starX, sratY);
                }

                for (Obstacle o : robot.getGrid().getObstacles()) {
                    int obstacleX = o.getPosX() * cellSize + cellSize / 2;
                    int obstacleY = o.getPosY() * cellSize + cellSize / 2;
                    g.drawString("@", obstacleX, obstacleY);
                }
            }
        }
    }
}