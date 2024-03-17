import java.util.ArrayList;
import java.util.Scanner;

/**
 * La classe Main est le point d'entrée de l'application. Elle permet de démarrer le jeu,
 * de gérer les niveaux, les instructions du robot et l'affichage du menu.
 */
public class Main {

    public static void main(String[] args) throws Exception {
        // Création des obstacles et de la grille.
        Obstacle o1 = new Obstacle(2, 3);
        Obstacle o2 = new Obstacle(1, 3);
        Obstacle o3 = new Obstacle(0, 1);
        ArrayList<Obstacle> obstacles = new ArrayList<Obstacle>();
        obstacles.add(o1);
        obstacles.add(o2);
        obstacles.add(o3);

        // Initialisation de la grille avec les obstacles.
        Grid grid = new Grid(5, obstacles); // grille comme statique.
        for (Obstacle obstacle : obstacles) {
            grid.set(obstacle.getPosX(), obstacle.getPosY(), 2);
        }
        
        // Initialisation du robot et placement sur la grille
        Registre registreX = new Registre("X", 8);
        Registre registreT = new Registre("T", 0);
        Robot robot = new Robot("Robot1", registreX, registreT, 0, 0, grid);
        grid.set(robot.getPosX(), robot.getPosY(), 1);
        
        // Initialisation des variables.
        int niveau = 0;
        boolean continuer = true;
        // Création d'un objet Scanner pour la lecture des entrées utilisateur depuis la console.
        Scanner scanner = new Scanner(System.in);

        // Affichage du menu
        System.out.println("╔════════════════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                      Welcome to Octopunks                                              ║");
        System.out.println("║                 - Programming Puzzle Game -                                            ║");
        System.out.println("╠═══════════════════════════════════════════════════════════════════════════════════════ ╣");
        System.out.println("║                                                                                        ║");
        System.out.println("║                        -- Instructions --                                              ║");
        System.out.println("║  - You have two standard registers: X and T.                                           ║");
        System.out.println("║  - Available Robot Instructions:                                                       ║");
        System.out.println("║      * COPY source(R/N) dest(R)  :  Copy source to destination.                        ║");
        System.out.println("║      * ADDI a(R/N) b(R/N) dest(R):  Add a + b and save the result in destination.      ║");
        System.out.println("║      * SUBI a(R/N) b(R/N) dest(R):  Subtract a - b and save the result in destination. ║");
        System.out.println("║      * MULI a(R/N) b(R/N) dest(R):  Multiply a * b and save the result in destination. ║");
        System.out.println("║      * JUMP dest(L)              :  Jump execution to label destination.               ║");
        System.out.println("║      * FJMP dest(L)              :  Jump execution to label destination if T is zero.  ║");
        System.out.println("║      * MODI a(R/N) b(R/N) dest(R):  Calculate a mod b and saves the result in dest.    ║");
        System.out.println("║      * DIVI a(R/N) b(R/N) dest(R):  Divide a / b and saves the result in dest.         ║");
        System.out.println("║      * SWIZ a(R/N) b(R/N) dest(R):  Swizzle a by b and saves the result in dest.       ║");
        System.out.println("║      * TEST a(R/N) ? b(R/N)      :  Compares a and b and puts 1 in T if true, 0 if not.║");
        System.out.println("║      * NOOP                      :  Do nothing for a cycle (no operation).             ║");
        System.out.println("║      * LINK dest(L)              :  Traverse the link numbered dest.                   ║");
        System.out.println("║      * HALT                      :  Halt the EXA.                                      ║");
        System.out.println("║                                                                                        ║");
        System.out.println("╠════════════════════════════════════════════════════════════════════════════════════════╣");
        System.out.println("║                        Let's Begin!                                                    ║");
        System.out.println("╚════════════════════════════════════════════════════════════════════════════════════════╝");

        System.out.println("Niveau : 0 ");
        System.out.println("Etat Initial :");
        grid.print();

        // Boucle principale qui permet à l'utilisateur d'ajouter des instructions ou d'exécuter.
        while (continuer) {
            // Demander à l'utilisateur d'ajouter des instructions ou d'exécuter.
            System.out.println("Entrer votre code assembleur ou 'HALT' pour exécuter:");
            // Liste pour stocker les instructions entrées par l'utilisateur.
            ArrayList<String> instructions = new ArrayList<String>();
            while (true) {
                // Lire l'entrée de l'utilisateur
                String input = scanner.nextLine();
                if (input.equals("HALT")) {
                    // Exécuter les instructions.
                    Assembler ass = new Assembler();
                    // Passage au niveau suivant.
                    ass.execute(robot, instructions);
                    niveau++;
                    // Afficher le niveau et l'état du robot.
                    System.out.println("Niveau : " + niveau);
                    System.out.print("Nouvel Etat : " + robot);
                    System.out.println();
                    // Afficher la grille mise à jour
                    grid.print();
                    System.out.println("Voulez-vous continuer ? (O/N)");
                    String choix = scanner.nextLine();
                    if (continuer = choix.equalsIgnoreCase("O")) {
                        System.out.println("Entrer votre code assembleur ou 'HALT' pour exécuter:");
                    } else
                        // Sortir de la boucle principale si l'utilisateur ne souhaite pas continuer.
                        break;
                } else {
                    // Ajouter l'instruction à la liste
                    instructions.add(input);
                }
            }
        }
        // Fermer le scanner
        scanner.close();
        System.out.println("Au revoir !");
    }
}
