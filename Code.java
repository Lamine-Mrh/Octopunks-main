public class Code {
    private final Instruction instruction; // Instruction associée à ce code
    private final Robot robot; // Robot sur lequel le code s'applique

    // Constructeur de la classe Code
    public Code(Instruction instruction, Robot robot) {
        this.instruction = instruction;
        this.robot = robot;
    }

    // Méthode pour obtenir l'instruction associée à ce code
    public Instruction getInstruction() {
        return instruction;
    }

    // Méthode pour obtenir le robot associé à ce code
    public Robot getRobot() {
        return robot;
    }

    // Méthode pour copier la valeur d'un registre source dans un registre
    // destination
    public void copy(Registre source, Registre dest) {
        dest.setValue(source.getValue());
    }

    // Méthode pour ajouter les valeurs de deux registres et stocker le résultat
    // dans un troisième registre
    public void add(Registre op1, Registre op2, Registre dest) {
        dest.setValue(op1.getValue() + op2.getValue());
    }

    // Méthode pour multiplier les valeurs de deux registres et stocker le résultat
    // dans un troisième registre
    public void mul(Registre op1, Registre op2, Registre dest) {
        dest.setValue(op1.getValue() * op2.getValue());
    }

    // Méthode pour calculer le reste de la division de deux valeurs de registres et
    // le stocker dans un troisième registre
    public void mod(Registre op1, Registre op2, Registre dest) {
        dest.setValue(op1.getValue() % op2.getValue());
    }

    // Méthode pour diviser les valeurs de deux registres et stocker le résultat
    // dans un troisième registre
    public void div(Registre op1, Registre op2, Registre dest) {
        if (op2.getValue() == 0) {
            throw new ArithmeticException("Cannot Divide by 0");
        }
        dest.setValue(op1.getValue() / op2.getValue());
    }

    // Méthode pour effectuer une opération de swizzling sur une valeur de registre
    public void swiz(Registre op1, Registre op2, Registre dest) throws Exception {
        int neg = 1;
        int t = op2.getValue();
        if (t < 0) {
            t *= -1;
            neg *= -1;
        }
        String m = String.valueOf(t);
        int[] mask = convertToDigitArray(Integer.parseInt(m));
        int[] source = convertToDigitArray(op1.getValue());
        int[] swiz = new int[4];
        for (int i = 0; i < 4; i++) {
            if (mask[i] == 0) {
                swiz[i] = 0;
            } else {
                swiz[i] = source[mask[i] - 1];
            }
        }
        int n = convertArraytoInt(swiz) * neg;
        dest.setValue(n);
    }

    // Méthode pour soustraire la valeur d'un registre de la valeur d'un autre
    // registre et stocker le résultat dans un troisième registre
    public void sub(Registre op1, Registre op2, Registre dest) {
        dest.setValue(op1.getValue() - op2.getValue());
    }

    // Méthode pour déplacer le robot dans une direction spécifiée
    public void link(Registre link) throws Exception {
        int newX = robot.getPosX();
        int newY = robot.getPosY();

        switch (link.getValue()) {
            case 0:
                newY--; // Déplacement vers le haut
                break;
            case 1:
                newX++; // Déplacement vers la droite
                break;
            case 2:
                newY++; // Déplacement vers le bas
                break;
            case 3:
                newX--; // Déplacement vers la gauche
                break;
            default:
                throw new Exception("Invalid link argument, must be in range [0,3]");
        }

        // Vérification des limites et des obstacles
        if (newX < 0 || newX >= robot.getGrid().getSize() || newY < 0 || newY >= robot.getGrid().getSize() ||
                robot.getGrid().ThereIsAnObstacle(robot.getGrid().getObstacles(), newX, newY)) {
            throw new Exception("There is an obstacle there");
        }

        // Mise à jour de la position du robot
        robot.setPosX(newX);
        robot.setPosY(newY);
        robot.setPos(newX * robot.getGrid().getSize() + newY);
    }

    // Méthode pour déplacer le pointeur du robot vers une instruction spécifiée
    public void jump(int n) {
        robot.setPointer(robot.getPointer() + n);
    }

    // Méthode pour effectuer un saut conditionnel (Jump if False)
    public void fjmp(int n) throws Exception {
        if (robot.getT().getValue() == 0) {
            jump(n);
        }
    }

    // Méthode pour comparer les valeurs de deux registres en fonction d'un symbole
    // spécifié
    public void test(Registre op1, String symbol, Registre op2) throws Exception {
        switch (symbol) {
            case "=":
                robot.getT().setValue(op1.getValue() == op2.getValue() ? 1 : 0);
                break;

            case ">":
                robot.getT().setValue(op1.getValue() > op2.getValue() ? 1 : 0);
                break;

            case "<":
                robot.getT().setValue(op1.getValue() < op2.getValue() ? 1 : 0);
                break;
            default:
                throw new Exception("Unrecognized symbol: " + symbol);
        }
    }

    // Méthode pour exécuter une instruction no-op (aucune opération)
    public void noop() {
        try {
            Thread.sleep(1000);
            Random random = new Random();
            if (random.nextBoolean()) {
                System.out.println("Amimir");
            } else {
                System.out.println("Zzzzzz!");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
}
    }

    // Méthode pour arrêter l'exécution du programme
    public void halt() {
        System.out.println("Program Halted");
        System.exit(0);
    }

    // Méthode pour convertir un entier en un tableau d'entiers représentant ses
    // chiffres
    public int[] convertToDigitArray(int number) {
        int[] digitArray = new int[4];
        for (int i = 3; i >= 0; i--) {
            digitArray[i] = number % 10;
            number /= 10;
        }
        return digitArray;
    }

    // Méthode pour convertir un tableau d'entiers représentant des chiffres en un
    // entier
    public int convertArraytoInt(int[] array) {
        int num = 0;
        for (int i : array) {
            num = num * 10 + i;
        }
        return num;
    }
}
