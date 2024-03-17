import java.util.ArrayList;
import java.util.Arrays;

public class Assembler {
    public ArrayList<String> parseAssembleurCode(String code) {
        ArrayList<String> instructions = new ArrayList<>();
        String[] lines = code.split("\n");
        for (String line : lines) {
            instructions.add(line.trim());
        }
        return instructions;
    }

    void verifyRange(int r) throws Exception {
        if (r > 9999 || r < -9999) {
            throw new Exception("Value out of range [-9999, 9999]");
        }
    }

    public Instruction parseInstruction(String line) {
        String[] parts = line.split("\\s+");
        return Instruction.valueOf(parts[0]);
    }

    public String[] parseArguments(String line) {
        String[] parts = line.split("\\s+");
        String[] arguments = new String[parts.length - 1];
        System.arraycopy(parts, 1, arguments, 0, arguments.length);
        return arguments;
    }

    // Add exceptions for wrong arguments and wrong # of arguments
    public void execute(Robot robot, ArrayList<String> instructions) throws Exception {
        while (robot.getPointer() < instructions.size()) {
            executeOne(robot, instructions.get(robot.getPointer()), instructions);
        }
    }

    public void executeOne(Robot robot, String instruction, ArrayList<String> instructions) throws Exception {
        String[] args = this.parseArguments(instruction);
        Code c = new Code(this.parseInstruction(instruction), robot);
        ArrayList<String> a = new ArrayList<String>(Arrays.asList(args));
        System.out.println("Executing:" + c.getInstruction() + a.toString());
        Registre source, o1, o2, o3, dest;

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
                    int op1;
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
                    robot.getGrid().set(robot.getPos(), 0); // only for Main
                    c.link(o1);
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
        robot.setPointer(robot.getPointer() + 1);
    }
}
