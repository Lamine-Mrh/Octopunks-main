import java.util.ArrayList;

public class Grid {
    private final int size; // Taille de la grille
    private int[][] grid; // Tableau 2D représentant la grille
    private final ArrayList<Obstacle> obstacles; // Liste des obstacles présents sur la grille

    // Constructeur pour initialiser une grille vide de la taille spécifiée
    public Grid(int size) {
        this.size = size;
        grid = new int[size][size]; // Initialisation de la grille avec la taille spécifiée
        obstacles = new ArrayList<Obstacle>(); // Initialisation de la liste des obstacles
    }

    // Constructeur pour initialiser une grille avec une liste d'obstacles et une
    // taille spécifiée
    public Grid(int size, ArrayList<Obstacle> obstacles) {
        this.size = size;
        grid = new int[size][size]; // Initialisation de la grille avec la taille spécifiée
        this.obstacles = obstacles; // Initialisation de la liste des obstacles avec la liste spécifiée
    }

    // Méthode pour obtenir la taille de la grille
    public int getSize() {
        return size;
    }

    // Méthode pour obtenir la liste des obstacles présents sur la grille
    public ArrayList<Obstacle> getObstacles() {
        return obstacles;
    }

    // Méthode pour obtenir la grille sous forme de tableau 2D d'entiers
    public int[][] getGrid() {
        return grid;
    }

    // Méthode pour obtenir l'élément de la grille aux coordonnées spécifiées
    public int getElem(int x, int y) {
        return grid[x][y];
    }

    // Méthode pour obtenir l'élément de la grille à partir d'un index unique
    public int getElem(int x) {
        if (x < 0 || x >= size * size) {
            return -1; // Retourne -1 si l'index est hors limites de la grille
        }
        return grid[x / size][x % size];
    }

    // Méthode pour définir la valeur de la grille aux coordonnées spécifiées
    public void set(int x, int y, int value) {
        grid[x][y] = value;
    }

    // Méthode pour définir la valeur de la grille à partir d'un index unique
    public void set(int x, int value) {
        grid[x % size][x / size] = value;
    }

    // Méthode pour afficher la grille dans la console
    public void print() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                System.out.print(grid[i][j] + "\t"); // Affichage de chaque élément de la grille
            }
            System.out.println(); // Saut de ligne après chaque ligne de la grille
        }
    }

    // Méthode pour vérifier s'il y a un obstacle aux coordonnées spécifiées
    public boolean ThereIsAnObstacle(ArrayList<Obstacle> obstacles, int x, int y) {
        for (Obstacle o : obstacles) {
            if (o.getPosX() == x && o.getPosY() == y) {
                return true; // Retourne vrai si un obstacle est trouvé aux coordonnées spécifiées
            }
        }
        return false; // Retourne faux si aucun obstacle n'est trouvé aux coordonnées spécifiées
    }
}
