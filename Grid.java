import java.util.ArrayList;

public class Grid {
    private final int size;
    private int[][] grid;
    private final ArrayList<Obstacle> obstacles;

    public Grid(int size) {
        this.size = size;
        grid = new int[size][size];
        obstacles = new ArrayList<Obstacle>();
    }

    public Grid(int size, ArrayList<Obstacle> obstacles) {
        this.size = size;
        grid = new int[size][size];
        this.obstacles = obstacles;
    }

    public int getSize() {
        return size;
    }

    public ArrayList<Obstacle> getObstacles() {
        return obstacles;
    }

    public int[][] getGrid() {
        return grid;
    }

    public int getElem(int x, int y) {
        return grid[x][y];
    }

    public int getElem(int x) {
        if (x < 0 || x >= size * size) {
            return -1;
        }
        return grid[x / size][x % size];
    }

    public void set(int x, int y, int value) {
        grid[x][y] = value;
    }

    public void set(int x, int value) {
        grid[x % size][x / size] = value;
    }

    public void print() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                System.out.print(grid[i][j] + "\t");
            }
            System.out.println();
        }
    }

    public boolean ThereIsAnObstacle(ArrayList<Obstacle> obstacles, int x, int y) {
        for (Obstacle o : obstacles) {
            if (o.getPosX() == x && o.getPosY() == y) {
                return true;
            }
        }
        return false;
    }

}
