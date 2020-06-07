//Carl Dahlén cada7128

public class NamedPlace extends Place {

    public NamedPlace(String name, Category category, Position position) {
        super(name, category, position);
        setFill(category.getColor());

    }

    @Override
    public String toString() {
        return "Named" + "," + getCategory() + "," + getXcoordinate() + "," + getYcoordinate() + "," + getName();
    }

}
