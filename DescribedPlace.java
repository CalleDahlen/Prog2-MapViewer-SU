//Carl Dahlén cada7128
public class DescribedPlace extends Place {
    private String description;

    public DescribedPlace(String name, Category category, Position position, String description) {
        super(name, category, position);
        this.description = description;
        setFill(category.getColor());
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "Described" + "," + getCategory() + "," + getXcoordinate() + "," + getYcoordinate() + "," + getName() + "," + getDescription();
    }
}
