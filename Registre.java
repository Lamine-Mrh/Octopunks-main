public class Registre {

    private String name;
    private int value;
    private final int initV;

    public Registre(String name, int value) {
        this.name = name;
        this.value = value;
        initV = value;
    }

    public String getName() {
        return this.name;
    }

    public int getValue() {
        return this.value;
    }

    public int getInitV() {
        return initV;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void setName(String n) {
        this.name = n;
    }

    public void reset() {
        setValue(initV);
    }
}
