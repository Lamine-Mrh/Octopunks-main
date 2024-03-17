// Classe représentant un obstacle sur la grille
public class Obstacle {
    private final int posX, posY; // Position de l'obstacle sur la grille

    // Constructeur pour initialiser un obstacle avec une position spécifiée
    public Obstacle(int posX, int posY) {
        this.posX = posX;
        this.posY = posY;
    }

    // Méthode pour obtenir la position X de l'obstacle
    public int getPosX() {
        return posX;
    }

    // Méthode pour obtenir la position Y de l'obstacle
    public int getPosY() {
        return posY;
    }
}