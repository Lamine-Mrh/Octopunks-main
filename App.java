/** La Classe App représente l'interface graphique principale 
 * de l'application Octopunk. Elle fournit une zone de texte pour
 * éditer le code, une grille pour afficher le terrain de jeu,
 * des boutons pour exécuter le code pas à pas, arrêter l'exécution
 * ou l'exécuter automatiquement, ainsi que des étiquettes pour afficher
 * les valeurs des registres X et T du robot.
 */


// Importations pour la gestion des fichiers audio
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
// Importation pour la création d'interfaces graphiques avec Swing
import javax.swing.*;
import java.awt.*;
// Importations pour la manipulation de fichiers et de répertoires
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
//importation pour l'utilisation de listes
import java.util.ArrayList;
import java.util.Collections;
// Importation pour l'utilisation de la classe AtomicInteger
import java.util.concurrent.atomic.AtomicInteger;
import java.util.List;
// Importation pour l'utilisation de la classe Random
import java.util.Random;

public class App extends JFrame {
// Composants d'interface utilisateur
    private JTextArea codeTextArea;//zone de texte pour le code
    private JButton pasButton, stopButton, automatiqueButton;// Boutons pour l'exécution du code
    private GridPanel grid;// Panneau de la grille où se déplace le robot
    private JLabel xLabel, tLabel;// Labels pour afficher les valeurs des registres X et T
    private Robot robot;// Instance du robot
    private final Assembler assembler = new Assembler();// Instance de l'assembleur pour exécuter le code
    private final List<File> musicFiles = new ArrayList<>();// Liste des fichiers audio
    private Clip backgroundMusic;// Clip pour la musique de fond
    private final AtomicInteger currentMusicIndex = new AtomicInteger(0);// Index pour suivre la musique en cours
    private JFrame settingsFrame;// Fenêtre pour afficher les instructions
    private JTextArea settingsTextArea;// Zone de texte pour les instructions

    private JFrame levelsFrame;// Fenêtre pour afficher les niveaux disponibles
    private JList<String> levelsList;// Liste des niveaux
    private JTextArea objectivesTextArea;// Zone de texte pour afficher les objectifs des niveaux

    private int currentLevel;// Niveau actuellement sélectionné


    // Méthode pour redimensionner une icône
    private static ImageIcon resizeIcon(ImageIcon icon, int width, int height) {
        Image img = icon.getImage();
        Image resizedImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(resizedImg);
    }

    // Constructeur de la classe App
    public App() {
        initUI();// Initialise l'interface utilisateur
        initializeRobot();// Initialise le robot
        initializeRobot(robot); 
        loadMusicFiles(); // Charge les fichiers musicaux
        initializeLevelsFrame();// Initialise la fenêtre des niveaux
    }
 

    // Initialise l'interface utilisateur
    private void initUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        

        // Panneau supérieur contenant les boutons
        JPanel topPanel = new JPanel(new BorderLayout());
        JPanel buttonPanelL = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
         // Bouton pour la musique
        ImageIcon musicIcon = resizeIcon(new ImageIcon("images/music.png"), 20, 20);
        JButton musicButton = new JButton(musicIcon);
        musicButton.addActionListener(e -> handleMusicButton());
        buttonPanelL.add(musicButton);

         // Bouton pour les instructions
        ImageIcon tutoIcon = resizeIcon(new ImageIcon("images/tuto.png"), 20, 20);
        JButton settingsButton = new JButton(tutoIcon);
        settingsButton.addActionListener(e -> handleSettingsButton());
        buttonPanelL.add(settingsButton);
        
         // Bouton pour les niveaux
        ImageIcon levelsIcon = resizeIcon(new ImageIcon("images/levels.png"), 20, 20);
        JButton levelButton = new JButton(levelsIcon);
        levelButton.addActionListener(e -> handleLevelButton());
        buttonPanelL.add(levelButton);

        topPanel.add(buttonPanelL, BorderLayout.WEST);


        // Label de bienvenue
        JLabel welcomeLabel = new JLabel("WELCOME TO OCTOPUNK", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 15));
        topPanel.add(welcomeLabel, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);
        
        // Zone de texte pour le code
        codeTextArea = new JTextArea(20, 13);
        JScrollPane codeScrollPane = new JScrollPane(codeTextArea,
        JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(codeScrollPane, BorderLayout.WEST);
       
        
        // Panneau de la grille
        grid = new GridPanel();
        add(grid, BorderLayout.CENTER);
        

        // Panneau de boutons pour contrôler l'exécution du code
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
        
         // Fenêtre pour afficher les instructions
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
     
    // Gestionnaire d'événement pour le bouton de sélection de niveau
    private Object handleLevelButton() {
        SwingUtilities.invokeLater(() -> levelsFrame.setVisible(true));
        return null;
    }
    

    // Gestionnaire d'événement pour le bouton des instructions
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
    
    // Initialise la fenêtre des niveaux
    private void initializeLevelsFrame() {
        levelsFrame = new JFrame("Levels");
        levelsFrame.setSize(300, 250);
        levelsFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        levelsFrame.setLayout(new BorderLayout());

        JPanel levelsPanel = new JPanel(new BorderLayout());
        

        // Liste des niveaux
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
    
    //Affiche l'objectif du niveau sélectionné dans la zone de texte des objectifs.
    private void displayObjective() {
        // Récupère l'index du niveau sélectionné dans la liste
        int selectedLevelIndex = levelsList.getSelectedIndex();
         // Vérifie si un niveau est sélectionné
        if (selectedLevelIndex >= 0) {
             // Construit le chemin du fichier contenant l'objectif du niveau
            String levelFilePath = "levels/" + selectedLevelIndex + ".txt";
            try {
                // Lit le contenu du fichier d'objectif
                String objective = new String(Files.readAllBytes(Paths.get(levelFilePath)), StandardCharsets.UTF_8);
                // Affiche l'objectif dans la zone de texte des objectifs
                objectivesTextArea.setText(objective);
            } catch (IOException e) {
              // Affiche un message d'erreur si le chargement de l'objectif échoue
                JOptionPane.showMessageDialog(this, "Failed to load objective for Level " + selectedLevelIndex, "Error",
                        JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
    
    //Charge le niveau sélectionné à partir de la liste des niveaux.
    private void loadSelectedLevel() {
        // Récupère l'index du niveau sélectionné dans la liste
        int selectedLevelIndex = levelsList.getSelectedIndex();
        
        // Vérifie si un niveau est sélectionné
        if (selectedLevelIndex >= 0) {
            // Utilise une structure de commutation pour charger le niveau en fonction de son index
            switch (selectedLevelIndex) {
                case 0:
                    // Charge le niveau 0
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
                    // Charge le niveau 1
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
                    // Charge le niveau 2
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
                    // Charge le niveau 3
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
                    // Charge le niveau 4
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
                    // Charge le niveau 5
                    currentLevel = 5;
                    Grid grid5 = new Grid(5);
                    Registre X5 = new Registre("X5", 8762);
                    Registre T5 = new Registre("T5", 568);
                    Robot robot5 = new Robot("Robot5", X5, T5, 2, 3, grid5);
                    initializeRobot(robot5);
                    updateGrid();

                    break;

                case 6:
                    // Charge le niveau 6
                    currentLevel = 6;
                    Grid grid6 = new Grid(5);
                    Registre X6 = new Registre("X6", 6984);
                    Registre T6 = new Registre("T6", 2561);
                    Robot robot6 = new Robot("Robot6", X6, T6, 4, 3, grid6);
                    initializeRobot(robot6);
                    updateGrid();

                    break;

                case 7:
                    // Charge le niveau 7
                    currentLevel = 7;
                    Grid grid7 = new Grid(5);
                    Registre X7 = new Registre("X7", 6984);
                    Registre T7 = new Registre("T7", 2561);
                    Robot robot7 = new Robot("Robot7", X7, T7, 1, 3, grid7);
                    initializeRobot(robot7);
                    updateGrid();

                    break;

                case 8:
                    // Charge le niveau 8
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
                    // Charge le niveau 9
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
                    // Charge le niveau 10
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

    //Arrête la musique de fond en cours de lecture, si elle est en cours de lecture
    private void skipSong() {
        
        // Vérifie si la musique de fond est en cours de lecture et si le clip n'est pas nul
        if (backgroundMusic != null && backgroundMusic.isRunning()) {
            // Arrête la lecture de la musique de fond
            backgroundMusic.stop();
        }
    }
    

    //Initialise le robot avec des registres par défaut et des obstacles prédéfinis sur une grille
    private void initializeRobot() {
        // Crée un registre X avec une valeur initiale de 0
        Registre xRegister = new Registre("X", 0);
        // Crée un registre T avec une valeur initiale de 0
        Registre tRegister = new Registre("T", 0);
        // Crée des obstacles prédéfinis
        Obstacle o1 = new Obstacle(2, 3);
        Obstacle o2 = new Obstacle(2, 2);
        Obstacle o3 = new Obstacle(4, 3);
        Obstacle o4 = new Obstacle(3, 4);
        // Ajoute les obstacles à une liste
        ArrayList<Obstacle> obstacles = new ArrayList<Obstacle>();
        obstacles.add(o1);
        obstacles.add(o2);
        obstacles.add(o3);
        obstacles.add(o4);
        // Crée une grille avec les obstacles spécifiés
        Grid grid = new Grid(5, obstacles);
        // Initialise le robot avec un nom "Robot", les registres X et T créés, une position de départ (0,0) et la grille spécifiée
        robot = new Robot("Robot", xRegister, tRegister, 0, 0, grid);
        updateGrid();  // Mise à jour l'affichage de la grille
    }
    
    /**
 * Initialise le robot avec l'instance spécifiée de Robot.
 * 
 * @param r L'instance de Robot à initialiser.
 */
    public void initializeRobot(Robot r) {
        // Définit l'instance de Robot actuelle avec celle spécifiée
        this.robot = r;
    }
   
    /**
     * Gère l'action du bouton "Pas" qui exécute une seule instruction du code
     Imprime le pointeur actuel du robot dans la console.
    * Analyse le code de la zone de texte et exécute l'instruction à l'index du pointeur du robot.
    * Si le pointeur est valide et qu'il n'a pas atteint la fin du code, exécute l'instruction
     */
    private void handlePasButton() {
        // Affiche le pointeur actuel du robot dans la console
        System.out.println(robot.getPointer());
        // Analyse le code de la zone de texte pour obtenir les instructions
        ArrayList<String> instructions = parseCode(codeTextArea.getText());
        // Vérifie si le robot est initialisé et si le pointeur est dans la plage des instructions
        if (robot != null && robot.getPointer() < instructions.size()) {
            System.out.println();// Saut de ligne dans la console
            try {
                // Exécute l'instruction à l'index du pointeur du robot
                assembler.executeOne(robot, instructions.get(robot.getPointer()), instructions);
                // Désactive la modification de la zone de texte du code
                codeTextArea.setEditable(false);
                // Mise à jour de l'affichage de la grille après l'exécution de l'instruction
                updateGrid();
                 // Efface la liste des instructions
                instructions.clear();
            } catch (Exception ex) {
                // Gère toute exception survenue pendant l'exécution de l'instruction
                handleException(ex);
            }
        }
    }
    
    /**
    * Gère l'action du bouton "Stop" qui réinitialise l'état du robot et réactive la zone de texte du code.
    * Si le robot est initialisé, le réinitialise et réactive la zone de texte du code pour permettre une nouvelle entrée.
    * Met également à jour l'affichage de la grille après la réinitialisation du robot.
    */
    private void handleStopButton() {
        // Vérifie si le robot est initialisé
        if (robot != null) {
            // Réinitialise l'état du robot
            robot.reset();
            // Réactive la zone de texte du code pour permettre une nouvelle entrée
            codeTextArea.setEditable(true);
            // Mise à jour de l'affichage de la grille après la réinitialisation du robot
            updateGrid();
        }
    }
    

    /**
   * Gère l'action du bouton "Automatique" qui exécute les instructions du code de manière automatique.
   * Lorsque le bouton est cliqué, un nouveau thread est créé pour exécuter les instructions du code.
   * Les instructions sont exécutées une par une avec un délai de 500 millisecondes entre chaque instruction.
   * Une fois toutes les instructions exécutées, la zone de texte du code est effacée et le pointeur du robot est réinitialisé.
   */
    private void handleAutomatiqueButton() {
         // Analyse le code dans la zone de texte et obtient la liste des instructions
        ArrayList<String> instructions = parseCode(codeTextArea.getText());
        // Crée un nouveau thread pour exécuter les instructions automatiquement
        new Thread(() -> {
            // Sauvegarde le pointeur de départ du robot
            int startPointer = robot.getPointer();
            // Boucle tant que le robot est initialisé et que le pointeur est inférieur à la taille des instructions
            while (robot != null && robot.getPointer() < instructions.size()) {
                try {
                    // Exécute l'instruction suivante
                    assembler.execute(robot, instructions);
                    // Incrémente le pointeur du robot
                    robot.setPointer(robot.getPointer() + 1);
                    // Mise à jour de l'affichage de la grille
                    updateGrid();
                    // Pause de 500 millisecondes entre chaque instruction
                    Thread.sleep(500);
                } catch (Exception ex) {
                    // Gère toute exception survenue pendant l'exécution des instructions
                    handleException(ex);
                }
            }
            // Met à jour l'interface utilisateur pour effacer le contenu de la zone de texte du code
            SwingUtilities.invokeLater(() -> codeTextArea.setText(""));
            // Réinitialise le pointeur du robot à sa position de départ
            robot.setPointer(startPointer);
        }).start();// Démarre le thread
    }
    /**
    * Gère les exceptions survenues lors de l'exécution des instructions.
    * Affiche la trace de la pile d'exception dans la console.
    * 
    * @param ex L'exception à gérer
    */

    private void handleException(Exception ex) {
        // Affiche la trace de la pile d'exception dans la console
        ex.printStackTrace();
    }
   


    /**
     * Mise à jour de l'affichage de la grille.
     * Utilisation de SwingUtilities.invokeLater() pour mettre à jour l'affichage de manière asynchrone.
     * Mise à jour des étiquettes associées à la grille.
     */
    private void updateGrid() {
        // Mise à jour de l'affichage de la grille de manière asynchrone
        SwingUtilities.invokeLater(() -> grid.repaint());
        // Mise à jour des étiquettes associées à la grille
        updateLabels();
    }



  /**
   * Mise à jour des étiquettes d'affichage des registres X et T du robot.
   * Les étiquettes affichent les valeurs actuelles des registres X et T du robot.
   * Si le robot est null, les étiquettes restent vides.
   */
    private void updateLabels() {
        if (robot != null) {
        // Met à jour l'étiquette d'affichage du registre X avec la valeur actuelle du registre X du robot
            xLabel.setText("X: " + robot.getX().getValue());
        // Met à jour l'étiquette d'affichage du registre T avec la valeur actuelle du registre T du robot
          
            tLabel.setText("T: " + robot.getT().getValue());
        }
    }

   /**
   * Analyse le code d'assemblage pour le convertir en une liste d'instructions.
   * 
   * @param code Le code d'assemblage à analyser.
   * @return Une liste d'instructions obtenue après l'analyse du code d'assemblage.
   */

   private ArrayList<String> parseCode(String code) {
    // Appelle la méthode de l'assembleur pour analyser le code et le convertir en une liste d'instructions
        return assembler.parseAssembleurCode(code);
    }

    
   /**
   * Charge les fichiers musicaux à partir du dossier "music".
   * Les fichiers doivent avoir l'extension ".wav".
   */

    private void loadMusicFiles() {

         // Crée une instance File pour le dossier de musique
        File musicFolder = new File("music");
        // Vérifie si le dossier existe et est un répertoire
        if (musicFolder.exists() && musicFolder.isDirectory()) {
            // Liste tous les fichiers dans le dossier de musique avec l'extension ".wav"
            File[] files = musicFolder.listFiles((dir, name) -> name.toLowerCase().endsWith(".wav"));
            // Vérifie si des fichiers ont été trouvés
            if (files != null) {
                // Ajoute tous les fichiers trouvés à la liste de fichiers musicaux
                Collections.addAll(musicFiles, files);
            }
        }
        // Mélange la liste des fichiers musicaux pour une lecture aléatoire
        Collections.shuffle(musicFiles);
    }
   
    /**
    * Joue le prochain fichier musical dans la liste des fichiers musicaux.
    * Si aucun fichier musical n'est disponible, cette méthode ne fait rien.
    */


    private void playNextMusic() {
        // Vérifie si la liste des fichiers musicaux n'est pas vide
        if (!musicFiles.isEmpty()) {
            try {
                // Arrête et ferme le lecteur musical actuel s'il est ouvert
                if (backgroundMusic != null && backgroundMusic.isOpen()) {
                    backgroundMusic.stop();
                    backgroundMusic.close();
                }
                // Récupère le prochain fichier musical dans la liste (lecture circulaire)
                File musicFile = musicFiles.get(currentMusicIndex.getAndIncrement() % musicFiles.size());
                System.out.println("Playing: " + musicFile.getName());
                // Crée un flux audio à partir du fichier musical
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(musicFile);
                // Crée un lecteur musical et ouvre le flux audio
                backgroundMusic = AudioSystem.getClip();
                backgroundMusic.open(audioInputStream);
                // Ajoute un écouteur pour détecter la fin de la lecture et passer au prochain fichier
                backgroundMusic.addLineListener(event -> {
                    if (event.getType() == LineEvent.Type.STOP) {
                        playNextMusic();
                    }
                });
                // Lance la lecture du fichier musical
                backgroundMusic.start();
            } catch (LineUnavailableException | UnsupportedAudioFileException | IOException e) {
                e.printStackTrace();
            }
        }
    }

   /**
   * Méthode principale pour démarrer l'application.
   * Crée une instance de l'application et l'affiche.
   * La taille par défaut de l'application est définie sur 400x337 pixels.
    */

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            App app = new App();
            app.setSize(400, 337);
            app.setVisible(true);
        });
    }

    /**
   * Classe interne représentant le panneau de la grille.
   * Dessine la grille, le robot et les obstacles sur le panneau.
   */

    private class GridPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
        // Calcule la taille de la cellule en fonction de la taille du panneau et de la taille de la grille
            int gridWidth = getWidth();
            int gridHeight = getHeight();
            int cellSize = Math.min(gridWidth, gridHeight) / 5;
           // Dessine les lignes de la grille
            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 5; j++) {
                    g.drawRect(i * cellSize, j * cellSize, cellSize, cellSize);
                }
            }
            // Dessine le robot et les obstacles
            if (robot != null) {
                int robotPosX = robot.getPosX();
                int robotPosY = robot.getPosY();
                int robotX = robotPosX * cellSize + cellSize / 2;
                int robotY = robotPosY * cellSize + cellSize / 2;
                g.drawString("X", robotX, robotY);
                // Dessine des éléments spécifiques aux niveaux particuliers
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
                
                // Dessine les obstacles
                for (Obstacle o : robot.getGrid().getObstacles()) {
                    int obstacleX = o.getPosX() * cellSize + cellSize / 2;
                    int obstacleY = o.getPosY() * cellSize + cellSize / 2;
                    g.drawString("@", obstacleX, obstacleY);
                }
            }
        }
    }
}
