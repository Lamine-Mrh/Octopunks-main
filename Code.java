public class Code {
    private final Instruction instruction;
    private final Robot robot;

    public Code(Instruction instruction, Robot robot) {
        this.instruction = instruction;
        this.robot = robot;
    }

    public Instruction getInstruction() {
        return instruction;
    }

    public Robot getRobot() {
        return robot;
    }

    // COPY
    public void copy(Registre source, Registre dest) {
        dest.setValue(source.getValue());
    }

    // ADDI
    public void add(Registre op1, Registre op2, Registre dest) {
        dest.setValue(op1.getValue() + op2.getValue());
    }

    // MULI
    public void mul(Registre op1, Registre op2, Registre dest) {
        dest.setValue(op1.getValue() * op2.getValue());
    }

    // MULI
    public void mod(Registre op1, Registre op2, Registre dest) {
        dest.setValue(op1.getValue() % op2.getValue());
    }

    // DIVI
    public void div(Registre op1, Registre op2, Registre dest) {
        if (op2.getValue() == 0) {
            throw new ArithmeticException("Cannot Devide by 0");
        }
        dest.setValue(op1.getValue() / op2.getValue());
    }

    // SWIZ
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

    // SUBI
    public void sub(Registre op1, Registre op2, Registre dest) {
        dest.setValue(op1.getValue() - op2.getValue());
    }

    // LINK
    public void link(Registre link) throws Exception {
        int newX = robot.getPosX();
        int newY = robot.getPosY();

        switch (link.getValue()) {
            case 0:
                newY--;
                break;
            case 1:
                newX++;
                break;
            case 2:
                newY++;
                break;
            case 3:
                newX--;
                break;
            default:
                throw new Exception("Invalid link argument, must be in range [0,3]");
        }

        if (newX < 0 || newX >= robot.getGrid().getSize() || newY < 0 || newY >= robot.getGrid().getSize() ||
                robot.getGrid().ThereIsAnObstacle(robot.getGrid().getObstacles(), newX, newY)) {
            throw new Exception("There is an obstacle there");
        }

        robot.setPosX(newX);
        robot.setPosY(newY);
        robot.setPos(newX * robot.getGrid().getSize() + newY);
    }

    // JUMP
    public void jump(int n) {
        robot.setPointer(robot.getPointer() + n);
    }

    // FJMP
    public void fjmp(int n) throws Exception {
        if (robot.getT().getValue() == 0) {
            jump(n);
        }
    }

    // TEST
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

    // NOOP
    public void noop() {

    }

    // HALT
    public void halt() {
        System.out.println("Programme Halted");
        System.exit(0);
    }

    public int[] convertToDigitArray(int number) {
        int[] digitArray = new int[4];
        for (int i = 3; i >= 0; i--) {
            digitArray[i] = number % 10;
            number /= 10;
        }
        return digitArray;
    }

    public int convertArraytoInt(int[] array) {
        int num = 0;
        for (int i : array) {
            num = num * 10 + i;
        }
        return num;
    }

}
