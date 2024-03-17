import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws Exception {
        Obstacle o1 = new Obstacle(2, 3);
        Obstacle o2 = new Obstacle(1, 3);
        Obstacle o3 = new Obstacle(0, 1);
        ArrayList<Obstacle> obstacles = new ArrayList<Obstacle>();
        obstacles.add(o1);
        obstacles.add(o2);
        obstacles.add(o3);
        Grid grid = new Grid(5, obstacles); // grille comme statique
        for (Obstacle obstacle : obstacles) {
            grid.set(obstacle.getPosX(), obstacle.getPosY(), 2);
        }
        int niveau = 0;
        Registre registreX = new Registre("X", 8);
        Registre registreT = new Registre("T", 0);
        Robot robot = new Robot("Robot1", registreX, registreT, 0, 0, grid);

        grid.set(robot.getPosX(), robot.getPosY(), 1);

        boolean continuer = true;
        Scanner scanner = new Scanner(System.in);

        // Affichage du menu
        System.out.println(
                "*****************************************************************Menu*****************************************************************");
        System.out.println(
                "______________________________________________________________________________________________________________________________\n");
        System.out.println("                                                     Welcome to Exapunks game ! *.*\n \n");
        System.out.println("*** Agreements !!! *** \n");
        System.out.println("    ** There are two standard registers: X and T **\n");
        System.out.println("    ** Robot Instructions |> **\n");
        System.out.println("      * COPY source(R/N) dest(R)  :  Copies source to dest. *\n");
        System.out.println("      * ADDI a(R/N) b(R/N) dest(R):  Adds a + b and saves the result in dest. *\n");
        System.out.println("      * SUBI a(R/N) b(R/N) dest(R):  Subtracts a - b and saves the result in dest. *\n");
        System.out.println(
                "      * MULI a(R/N) b(R/N) dest(R):  Multiplies a * b and saves the result in dest. a, b. *\n");
        System.out.println("      * JUMP dest(L)              :  Jump execution to label dest. *\n");
        System.out.println("      * FJMP dest(L)              :  Jump execution to label dest if T is zero. *\n");
        System.out.println("      * HALT                      :  Halt the EXA. *\n");

        System.out.println("Niveau : 0 ");
        System.out.println("Etat Initial :");
        grid.print();

        while (continuer) {
            // robot avec des registres X et T

            // Demander à l'utilisateur d'ajouter des instructions ou d'exécuter
            System.out.println("Entrer votre code assembleur ou 'HALT' pour exécuter:");

            ArrayList<String> instructions = new ArrayList<String>();
            while (true) {
                String input = scanner.nextLine();
                if (input.equals("HALT")) {
                    // Exécuter les instructions
                    Assembler ass = new Assembler();
                    ass.execute(robot, instructions);
                    niveau++;
                    System.out.println("Niveau : " + niveau);
                    System.out.print("Nouvel Etat : " + robot);
                    System.out.println();
                    grid.print();
                    System.out.println("Voulez-vous continuer ? (O/N)");
                    String choix = scanner.nextLine();
                    if (continuer = choix.equalsIgnoreCase("O")) {
                        System.out.println("Entrer votre code assembleur ou 'HALT' pour exécuter:");
                    } else
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