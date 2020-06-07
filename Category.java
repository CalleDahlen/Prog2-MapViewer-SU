//Carl Dahlén cada7128
import javafx.scene.paint.Color;

public enum Category {
   Bus("Bus",Color.RED),
   Train("Train",Color.GREEN),
    Underground("Underground", Color.BLUE),
    None("None", Color.BLACK);

    private final String name;
    private final Color color;


    Category(String name, Color color){
        this.name = name;
        this.color = color;
    }

    public String getName() {
       return name;
    }

    public Color getColor() {
        return color;
    }
    @Override
    public String toString(){
       return getName();
    }
}