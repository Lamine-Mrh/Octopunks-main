/**
 * L'énumération Instruction définit différentes instructions avec leur nombre d'arguments requis.
 * Chaque instruction est associée à un nombre spécifique d'arguments requis et à un nom.
 */
public enum Instruction {
    // Différentes instructions avec leur nombre d'arguments requis
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

    private int requiredArgs;  // Nombre d'arguments requis pour cette instruction
    private String name;      // Nom de l'instruction

    // Constructeur de l'énumération Instruction
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
