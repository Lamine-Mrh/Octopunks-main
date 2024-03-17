public class Registre {

    private String name; // Nom du registre
    private int value; // Valeur actuelle du registre
    private final int initV; // Valeur initiale du registre

    // Constructeur pour initialiser un registre avec un nom et une valeur spécifiés
    public Registre(String name, int value) {
        this.name = name;
        this.value = value;
        initV = value; // Initialisation de la valeur initiale avec la valeur spécifiée
    }

    // Méthode pour obtenir le nom du registre
    public String getName() {
        return this.name;
    }

    // Méthode pour obtenir la valeur actuelle du registre
    public int getValue() {
        return this.value;
    }

    // Méthode pour obtenir la valeur initiale du registre
    public int getInitV() {
        return initV;
    }

    // Méthode pour définir la valeur du registre
    public void setValue(int value) {
        this.value = value;
    }

    // Méthode pour définir le nom du registre
    public void setName(String n) {
        this.name = n;
    }

    // Méthode pour réinitialiser la valeur du registre à sa valeur initiale
    public void reset() {
        setValue(initV); // Réinitialisation de la valeur à la valeur initiale
    }
}