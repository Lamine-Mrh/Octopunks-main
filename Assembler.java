import java.util.ArrayList;
import java.util.Arrays;

/**
 * La classe Assembler est responsable de l'analyse et de l'exécution du code assembleur.
 * Elle fournit des méthodes pour diviser le code assembleur en instructions individuelles
 * et pour exécuter ces instructions sur un robot.
 */
public class Assembler {
    
    /**
    * Analyse le code assembleur fourni et le divise en une liste d'instructions individuelles.
    *
    * @param code Le code assembleur à analyser, représenté sous forme de chaîne de caractères.
    * @return Une liste contenant chaque instruction du code assembleur, sans espaces inutiles.
    */
    public ArrayList<String> parseAssembleurCode(String code) {  
        ArrayList<String> instructions = new ArrayList<>();
        String[] lines = code.split("\n"); // retours lignes comme délimiteurs
        for (String line : lines) {
            instructions.add(line.trim());
        }
        return instructions;
    }

    /**
    * Analyse une ligne de code assembleur et extrait l'instruction.
    *
    * @param line La ligne de code à analyser.
    * @return L'instruction extraite de la ligne de code.
    */
    public Instruction parseInstruction(String line) {
        String[] parts = line.split("\\s+"); // espaces comme délimiteurs.
        return Instruction.valueOf(parts[0]);     // obtenir l'objet Instruction correspondant à cette chaîne.
    }

    /**
    * Analyse une ligne de code assembleur et extrait les arguments d'une instruction.
    *
    * @param line La ligne de code contenant l'instruction et ses arguments.
    * @return un tableau contenant les arguments de l'instruction, sans l'instruction elle-même.
    */
    public String[] parseArguments(String line) {
        String[] parts = line.split("\\s+");
        String[] arguments = new String[parts.length - 1]; 
        System.arraycopy(parts, 1, arguments, 0, arguments.length); // copier à partir de l'indice 1; part[0] est le nom de l'instruction.
        return arguments;
    }

    // Méthode pour vérifier si une valeur est dans la plage autorisée
    void verifyRange(int r) throws Exception {
        if (r > 9999 || r < -9999) {
            throw new Exception("Value out of range [-9999, 9999]");
        }
    }
    
    /**
    * Exécute une série d'instructions sur un robot jusqu'à ce que le pointeur du robot dépasse la taille des instructions.
    *
    * @param robot       Le robot sur lequel les instructions doivent être exécutées.
    * @param instructions La liste des instructions à exécuter.
    * @throws Exception Si une erreur survient pendant l'exécution des instructions.
    */
    public void execute(Robot robot, ArrayList<String> instructions) throws Exception {
        // Tant que le pointeur du robot est inférieur à la taille de la liste d'instructions.
        while (robot.getPointer() < instructions.size()) {
            this.executeOne(robot, instructions.get(robot.getPointer()), instructions);
        }
    }

    /**
    * Exécute une seule instruction sur un robot, en fonction de l'instruction fournie.
    *
    * @param robot       Le robot sur lequel l'instruction doit être exécutée.
    * @param instruction L'instruction à exécuter.
    * @param instructions La liste complète des instructions.
    * @throws Exception Si une erreur survient pendant l'exécution de l'instruction.
    */
    public void executeOne(Robot robot, String instruction, ArrayList<String> instructions) throws Exception {
        // Analyse les arguments de l'instruction et crée un objet Code représentant l'instruction.
        String[] args = this.parseArguments(instruction);
        Code c = new Code(this.parseInstruction(instruction), robot);
        // Affiche l'instruction en cours d'exécution.
        ArrayList<String> a = new ArrayList<String>(Arrays.asList(args));
        System.out.println("Executing:" + c.getInstruction() + a.toString());
        // Déclaration des registres utilisés dans les instructions
        Registre source, o1, o2, o3, dest;

        // Switch sur l'instruction pour exécuter le bon bloc de code en fonction de l'instruction.
        switch (c.getInstruction()) {
            case COPY:
                if (args.length != 2) {
                    throw new Exception("COPY requires 2 arguments");
                }
                // Le registre source
                try {
                    if (args[0].equals("X")) {
                        source = robot.getX();
                    } else if (args[0].equals("T")) {
                        source = robot.getT();
                    } else {
                        try {
                            int value = Integer.parseInt(args[0]);
                            source = new Registre("Temp", value);
                        } catch (NumberFormatException e) {
                            throw new Exception(
                                    "Invalid source register (standard register or numeric value in range [-9999, 9999] required for the source register) ");
                        }
                    }
                    this.verifyRange(source.getValue());

                    // Le registre destination
                    if (args[1].equals("X")) {
                        dest = robot.getX();
                    } else if (args[1].equals("T")) {
                        dest = robot.getT();
                    } else {
                        throw new Exception("Invalid destination register (standard register is required) ");
                    }

                    // Effectuer la copie
                    c.copy(source, dest);
                } catch (Exception e) {
                    System.err.println("Error executing COPY instruction: " + e.getMessage());
                }
                break;

            case ADDI:
                if (args.length != 3) {
                    throw new Exception("ADDI requires 3 arguments");
                }

                try {
                    int op1, op2;

                    // le premier opérande
                    if (args[0].equals("X")) {
                        op1 = robot.getX().getValue();
                    } else if (args[0].equals("T")) {
                        op1 = robot.getT().getValue();
                    } else {
                        try {
                            op1 = Integer.parseInt(args[0]);
                        } catch (NumberFormatException e) {
                            throw new Exception(
                                    "standard register or numeric value in range [-9999, 9999] required for the first operand");
                        }
                    }
                    o1 = new Registre("Temp", op1);
                    this.verifyRange(o1.getValue());

                    // le deuxième opérande
                    if (args[1].equals("X")) {
                        op2 = robot.getX().getValue();
                    } else if (args[1].equals("T")) {
                        op2 = robot.getT().getValue();
                    } else {
                        try {
                            op2 = Integer.parseInt(args[1]);
                        } catch (NumberFormatException e) {
                            throw new Exception(
                                    "standard register or numeric value in range [-9999, 9999] required for the second operand");
                        }
                    }
                    o2 = new Registre("Temp", op2);
                    this.verifyRange(o2.getValue());

                    // le registre de destination
                    if (args[2].equals("X")) {
                        o3 = robot.getX();
                    } else if (args[2].equals("T")) {
                        o3 = robot.getT();
                    } else {
                        throw new Exception("Invalid destination register (standard register is required)");
                    }

                    // Calcul de la somme et Vérification des limites
                    this.verifyRange(o1.getValue() + o2.getValue());
                    c.add(o1, o2, o3);
                } catch (Exception e) {
                    System.err.println("Error executing ADDI instruction: " + e.getMessage());
                }
                break;

            case MULI:
                if (args.length != 3) {
                    throw new Exception(" MULI requires 3 arguments");
                }

                try {
                    int op1, op2;

                    // le premier opérande
                    if (args[0].equals("X")) {
                        op1 = robot.getX().getValue();
                    } else if (args[0].equals("T")) {
                        op1 = robot.getT().getValue();
                    } else {
                        try {
                            op1 = Integer.parseInt(args[0]);
                        } catch (NumberFormatException e) {
                            throw new Exception(
                                    "standard register or numeric value in range [-9999, 9999] required for the first operand");
                        }
                    }
                    o1 = new Registre("Temp", op1);
                    this.verifyRange(o1.getValue());

                    // le deuxième opérande
                    if (args[1].equals("X")) {
                        op2 = robot.getX().getValue();
                    } else if (args[1].equals("T")) {
                        op2 = robot.getT().getValue();
                    } else {
                        try {
                            op2 = Integer.parseInt(args[1]);
                        } catch (NumberFormatException e) {
                            throw new Exception(
                                    "standard register or numeric value in range [-9999, 9999] required for the second operand");
                        }
                    }
                    o2 = new Registre("Temp", op2);
                    this.verifyRange(o2.getValue());

                    // le registre de destination
                    if (args[2].equals("X")) {
                        o3 = robot.getX();
                    } else if (args[2].equals("T")) {
                        o3 = robot.getT();
                    } else {
                        throw new Exception("Invalid destination register (standard register is required)");
                    }

                    // Calcul du produit et Vérification des limites
                    this.verifyRange(o1.getValue() * o2.getValue());
                    c.mul(o1, o2, o3);
                } catch (Exception e) {
                    System.err.println("Error executing MULI instruction: " + e.getMessage());
                }
                break;

            case SUBI:
                if (args.length != 3) {
                    throw new Exception("SUBI requires 3 arguments");
                }

                try {
                    int op1, op2;

                    // le premier opérande
                    if (args[0].equals("X")) {
                        op1 = robot.getX().getValue();
                    } else if (args[0].equals("T")) {
                        op1 = robot.getT().getValue();
                    } else {
                        try {
                            op1 = Integer.parseInt(args[0]);
                        } catch (NumberFormatException e) {
                            throw new Exception(
                                    "standard register or numeric value in range [-9999, 9999] required for the first operand");
                        }
                    }
                    o1 = new Registre("Temp", op1);
                    this.verifyRange(o1.getValue());

                    // le deuxième opérande
                    if (args[1].equals("X")) {
                        op2 = robot.getX().getValue();
                    } else if (args[1].equals("T")) {
                        op2 = robot.getT().getValue();
                    } else {
                        try {
                            op2 = Integer.parseInt(args[1]);
                        } catch (NumberFormatException e) {
                            throw new Exception(
                                    "standard register or numeric value in range [-9999, 9999] required for the second operand");
                        }
                    }
                    o2 = new Registre("Temp", op2);
                    this.verifyRange(o2.getValue());

                    // le registre de destination
                    if (args[2].equals("X")) {
                        o3 = robot.getX();
                    } else if (args[2].equals("T")) {
                        o3 = robot.getT();
                    } else {
                        throw new Exception("Invalid destination register (standard register is required)");
                    }

                    // Calcul de la soustraction et Vérification des limites
                    this.verifyRange(o1.getValue() - o2.getValue());
                    c.sub(o1, o2, o3);
                } catch (Exception e) {
                    System.err.println("Error executing SUBI instruction: " + e.getMessage());
                }
                break;

            case MODI:
                if (args.length != 3) {
                    throw new Exception("MODI requires 3 arguments");
                }

                try {
                    int op1, op2;

                    // le premier opérande
                    if (args[0].equals("X")) {
                        op1 = robot.getX().getValue();
                    } else if (args[0].equals("T")) {
                        op1 = robot.getT().getValue();
                    } else {
                        try {
                            op1 = Integer.parseInt(args[0]);
                        } catch (NumberFormatException e) {
                            throw new Exception(
                                    "standard register or numeric value in range [-9999, 9999] required for the first operand");
                        }
                    }
                    o1 = new Registre("Temp", op1);
                    this.verifyRange(o1.getValue());

                    // le deuxième opérande
                    if (args[1].equals("X")) {
                        op2 = robot.getX().getValue();
                    } else if (args[1].equals("T")) {
                        op2 = robot.getT().getValue();
                    } else {
                        try {
                            op2 = Integer.parseInt(args[1]);
                        } catch (NumberFormatException e) {
                            throw new Exception(
                                    "standard register or numeric value in range [-9999, 9999] required for the second operand");
                        }
                    }
                    o2 = new Registre("Temp", op2);
                    this.verifyRange(o2.getValue());

                    // le registre de destination
                    if (args[2].equals("X")) {
                        o3 = robot.getX();
                    } else if (args[2].equals("T")) {
                        o3 = robot.getT();
                    } else {
                        throw new Exception("Invalid destination register (standard register is required)");
                    }

                    // Calcul du modulo et Vérification des limites
                    this.verifyRange(o1.getValue() % o2.getValue());
                    c.mod(o1, o2, o3);
                } catch (Exception e) {
                    System.err.println("Error executing MODI instruction: " + e.getMessage());
                }
                break;

            case SWIZ:
                if (args.length != 3) {
                    throw new Exception("SWIZ requires 3 arguments");
                }

                try {
                    int op1, op2;

                    // le premier opérande
                    if (args[0].equals("X")) {
                        op1 = robot.getX().getValue();
                    } else if (args[0].equals("T")) {
                        op1 = robot.getT().getValue();
                    } else {
                        try {
                            op1 = Integer.parseInt(args[0]);
                        } catch (NumberFormatException e) {
                            throw new Exception(
                                    "standard register or numeric value in range [-9999, 9999] required for the first operand");
                        }
                    }
                    o1 = new Registre("Temp", op1);
                    this.verifyRange(o1.getValue());

                    // le deuxième opérande
                    if (args[1].equals("X")) {
                        op2 = robot.getX().getValue();
                    } else if (args[1].equals("T")) {
                        op2 = robot.getT().getValue();
                    } else {
                        try {
                            op2 = Integer.parseInt(args[1]);
                        } catch (NumberFormatException e) {
                            throw new Exception(
                                    "standard register or numeric value in range [-9999, 9999] required for the second operand");
                        }
                    }
                    o2 = new Registre("Temp", op2);
                    this.verifyRange(o2.getValue());

                    // le registre de destination
                    if (args[2].equals("X")) {
                        o3 = robot.getX();
                    } else if (args[2].equals("T")) {
                        o3 = robot.getT();
                    } else {
                        throw new Exception("Invalid destination register (standard register is required)");
                    }

                    c.swiz(o1, o2, o3);
                } catch (Exception e) {
                    System.err.println("Error executing SWIZ instruction: " + e.getMessage());
                }
                break;

            case DIVI:
                if (args.length != 3) {
                    throw new Exception("DIVI requires 3 arguments");
                }

                try {
                    int op1, op2;

                    // le premier opérande
                    if (args[0].equals("X")) {
                        op1 = robot.getX().getValue();
                    } else if (args[0].equals("T")) {
                        op1 = robot.getT().getValue();
                    } else {
                        try {
                            op1 = Integer.parseInt(args[0]);
                        } catch (NumberFormatException e) {
                            throw new Exception(
                                    "standard register or numeric value in range [-9999, 9999] required for the first operand");
                        }
                    }
                    o1 = new Registre("Temp", op1);
                    this.verifyRange(o1.getValue());

                    // le deuxième opérande
                    if (args[1].equals("X")) {
                        op2 = robot.getX().getValue();
                    } else if (args[1].equals("T")) {
                        op2 = robot.getT().getValue();
                    } else {
                        try {
                            op2 = Integer.parseInt(args[1]);
                        } catch (NumberFormatException e) {
                            throw new Exception(
                                    "standard register or numeric value in range [-9999, 9999] required for the second operand");
                        }
                    }
                    o2 = new Registre("Temp", op2);
                    this.verifyRange(o2.getValue());

                    // le registre de destination
                    if (args[2].equals("X")) {
                        o3 = robot.getX();
                    } else if (args[2].equals("T")) {
                        o3 = robot.getT();
                    } else {
                        throw new Exception("Invalid destination register (standard register is required)");
                    }

                    c.div(o1, o2, o3);
                } catch (Exception e) {
                    System.err.println("Error executing SWIZ instruction: " + e.getMessage());
                }
                break;

            case LINK:
                if (args.length != 1) {
                    throw new Exception("LINK requires 1 argument");
                }
                try {
                    robot.getGrid().set(robot.getPos(), 0); // only for Main
                    Registre registre = new Registre("Temp", Integer.parseInt(args[0]));
                    c.link(registre);
                    robot.getGrid().set(robot.getPos(), 1); // only for Main
                } catch (NumberFormatException e) {
                    throw new Exception("argmuent must be an int");
                }
                break;

            case JUMP:
                if (args.length != 1) {
                    throw new Exception("JUMP requires 1 argument");
                }
                try {
                    int n = Integer.parseInt(args[0]);
                    int newPointer = robot.getPointer() + n;
                    if (newPointer >= 0 && newPointer < instructions.size()) {
                        if (n < 0) {
                            c.jump(n - 1);

                        } else
                            c.jump(n);
                    } else {
                        throw new Exception("Jump out of range");
                    }
                } catch (NumberFormatException e) {
                    throw new Exception("Argument must be an int");
                }
                break;

            case FJMP:
                if (args.length != 1) {
                    throw new Exception("FJMP requires 1 argument");
                }
                try {
                    int n = Integer.parseInt(args[0]);
                    int newPointer = robot.getPointer() + n;
                    if (newPointer >= 0 && newPointer < instructions.size()) {
                        if (n < 0) {
                            c.fjmp(n - 1);
                        } else
                            c.fjmp(n);
                    } else {
                        throw new Exception("Jump out of range");
                    }
                } catch (NumberFormatException e) {
                    throw new Exception("Argument must be an int");
                }
                break;

            case TEST:
                if (args.length != 3) {
                    throw new Exception("TEST requires 3 arguments");
                }

                try {
                    int op1, op2;

                    // le premier opérande
                    if (args[0].equals("X")) {
                        op1 = robot.getX().getValue();
                    } else if (args[0].equals("T")) {
                        op1 = robot.getT().getValue();
                    } else {
                        try {
                            op1 = Integer.parseInt(args[0]);
                        } catch (NumberFormatException e) {
                            throw new Exception(
                                    "standard register or numeric value in range [-9999, 9999] required for the first operand");
                        }
                    }
                    o1 = new Registre("Temp", op1);
                    this.verifyRange(o1.getValue());

                    // le deuxième opérande
                    if (args[2].equals("X")) {
                        op2 = robot.getX().getValue();
                    } else if (args[2].equals("T")) {
                        op2 = robot.getT().getValue();
                    } else {
                        try {
                            op2 = Integer.parseInt(args[2]);
                        } catch (NumberFormatException e) {
                            throw new Exception(
                                    "standard register or numeric value in range [-9999, 9999] required for the second operand");
                        }
                    }
                    o2 = new Registre("Temp", op2);
                    this.verifyRange(o2.getValue());

                    c.test(o1, args[1], o2);
                } catch (Exception e) {
                    System.err.println("Error executing TEST instruction: " + e.getMessage());
                }
                break;

            case NOOP:
                if (args.length != 0) {
                    throw new Exception("NOOP requires 0 arguments");
                }
                c.noop();
                break;

            case HALT:
                if (args.length != 0) {
                    throw new Exception("NOOP requires 0 arguments");
                }
                c.halt();
                break;

            default:
                throw new Exception("Unrecognized Instruction");

        }
        // Incrémente le pointeur du robot pour passer à l'instruction suivante dans la séquence d'instructions.
        robot.setPointer(robot.getPointer() + 1);
    }
}

