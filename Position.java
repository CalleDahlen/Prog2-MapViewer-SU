//Carl Dahlén cada7128

import java.util.Objects;

public class Position implements Comparable<Position> {
    private int xcoordinate;
    private int ycoordinate;

    public Position(int xcoordinate, int ycoordinate) {
        this.xcoordinate = xcoordinate;
        this.ycoordinate = ycoordinate;
    }

    public int getXCoordinate() {
        return xcoordinate;
    }

    public int getYCoordinate() {
        return ycoordinate;
    }

    @Override
    public int compareTo(Position other) {
        if (xcoordinate < other.xcoordinate)
            return -1;
        else if (xcoordinate > other.xcoordinate)
            return 1;
        else if (ycoordinate < other.ycoordinate)
            return -1;
        else if (ycoordinate > other.ycoordinate)
            return 1;
        else
            return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return xcoordinate == position.xcoordinate &&
                ycoordinate == position.ycoordinate;
    }


    @Override
    public int hashCode() {
        return Objects.hash(xcoordinate, ycoordinate);
    }

    @Override
    public String toString() {
        return String.format("[ %d, %d]", getXCoordinate(), getYCoordinate());
    }

}
