public class Robot {
    private final String name;
    private final Registre X, T;
    private int posX, posY, pos;
    private final int initX, initY;
    private int pointer;
    private Grid grid;

    public Robot(String name, Registre X, Registre T, int posX, int posY, Grid grid) {
        this.name = name;
        this.X = X;
        this.T = T;
        this.posX = posX;
        this.posY = posY;
        this.pos = posX * 5 + posY;
        this.pointer = 0;
        this.grid = grid;
        initX = posX;
        initY = posY;
    }

    public String getName() {
        return name;
    }

    public Registre getX() {
        return X;
    }

    public Registre getT() {
        return T;
    }

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }

    public int getPos() {
        return pos;
    }

    public Grid getGrid() {
        return grid;
    }

    public int getPointer() {
        return pointer;
    }

    public int getInitX() {
        return initX;
    }

    public int getInitY() {
        return initY;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public void setPosY(int posY) {
        this.posY = posY;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public void setPointer(int pointer) {
        this.pointer = pointer;
    }

    public void reset() {
        setPointer(0);
        setPosX(initX);
        setPosY(initY);
        setPos(initX * 5 + initY);
        getT().reset();
        getX().reset();
    }

    @Override
    public String toString() {
        return "Robot{" +
                "X:" + X.getValue() +
                ", T:" + T.getValue() +
                '}';
    }
}
