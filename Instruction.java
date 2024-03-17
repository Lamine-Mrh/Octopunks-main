public enum Instruction {
    COPY(2, "COPY"),
    ADDI(3, "ADDI"),
    MULI(3, "MULI"),
    SUBI(3, "SUBI"),
    JUMP(1, "JUMP"),
    FJMP(1, "FJMP"),
    MODI(3, "MODI"),
    DIVI(3, "DIVI"),
    SWIZ(3, "SWIZ"),
    TEST(3, "TEST"),
    NOOP(0, "NOOP"),
    LINK(1, "LINK"),
    HALT(0, "HALT");

    private int requiredArgs;
    private String name;

    Instruction(int requiredArgs, String name) {
        this.requiredArgs = requiredArgs;
        this.name = name;
    }

    public int getRequiredArgs() {
        return this.requiredArgs;
    }

    public String getName() {
        return this.name;
    }
}