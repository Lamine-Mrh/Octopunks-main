/**
 * La classe Robot représente un robot dans un environnement de grille. Elle utilise la classe Grid pour accéder à la grille
 * et effectuer des opérations liées à la position du robot.
 */
public class Robot {
    private final String name;
    private final Registre X, T;   // Registres standards du robot.
    private int posX, posY, pos;   // la position en X (horizontale) , la position en Y (verticale), pos : position du robot dans la grille
    private final int initX, initY;
    private int pointer;          // position actuelle d'exécution dans le programme
    private Grid grid;

    public Robot(String name, Registre X, Registre T, int posX, int posY, Grid grid) {
        this.name = name;
        this.X = X;
        this.T = T;
        this.posX = posX;
        this.posY = posY;
        // Convertir la position horizontale (posX) en une valeur numérique en multipliant par la taille de la grille (supposée être 5),
        // puis ajouter la position verticale (posY); ceci prend en compte à la fois posX et posY dans la grille.
        this.pos = posX * 5 + posY;
        this.pointer = 0;
        this.grid = grid;
        this.initX = posX;
        this.initY = posY;
    }

    public String getName() {
        return this.name;
    }

    public Registre getX() {
        return this.X;
    }

    public Registre getT() {
        return this.T;
    }

    public int getPosX() {
        return this.posX;
    }

    public int getPosY() {
        return this.posY;
    }

    public int getPos() {
        return this.pos;
    }

    public Grid getGrid() {
        return this.grid;
    }

    public int getPointer() {
        return this.pointer;
    }

    public int getInitX() {
        return this.initX;
    }

    public int getInitY() {
        return this.initY;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public void setPosY(int posY) {
        this.posY = posY;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public void setPointer(int pointer) {
        this.pointer = pointer;
    }
    
    // réinitialisation des valeurs du robot.
    public void reset() {
        this.setPointer(0);
        this.setPosX(this.initX);
        this.setPosY(this.initY);
        this.setPos(this.initX * 5 + this.initY);
        this.getT().reset();
        this.getX().reset();
    }

    // afficher l'état actuel du robot.
    @Override
    public String toString() {
        return "Robot{" +
                "X:" + this.X.getValue() +
                ", T:" + this.T.getValue() +
                '}';
    }
}
