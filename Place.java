//Carl Dahlén cada7128

import javafx.event.EventHandler;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

import java.util.*;

public abstract class Place extends Polygon {
    public static Set<Place> selectedPlace = new HashSet<>();
    private String name;
    private Category category;
    private Position position;
    private boolean marked = true;


    public Place(String name, Category category, Position position) {
        super(position.getXCoordinate(), position.getYCoordinate(), position.getXCoordinate() - 15, position.getYCoordinate() - 30, position.getXCoordinate() + 15, position.getYCoordinate() - 30);
        this.name = name;
        this.category = category;
        this.position = position;
        setOnMouseClicked(new SelectPlaceClickHandler());
    }

    public String getName() {
        return name;
    }

    public int getXcoordinate() {
        return position.getXCoordinate();
    }

    public int getYcoordinate() {
        return position.getYCoordinate();
    }

    public Position getPosition() {
        return position;
    }

    public Category getCategory() {
        return category;
    }

    @Override
    public String toString() {
        return String.format(name, category, position);
    }

    public void setMarked() {
        setFill(Color.YELLOW);
        setStroke(Color.BLACK);
    }

    public void isMarked(Place p, boolean on) {
        marked = on;
        if (marked)
            setMarked();
        else
            setUnMarked(p);

    }

    public void setUnMarked(Place p) {
        p.setFill(p.getCategory().getColor());
        setStroke(null);
    }


    public class SelectPlaceClickHandler implements EventHandler<MouseEvent> {
        @Override
        public void handle(MouseEvent event) {
            Place p = (Place) event.getSource();
            if (event.getButton() == MouseButton.PRIMARY) {
                if (selectedPlace.isEmpty()) {
                    isMarked(p, true);
                    selectedPlace.add(p);
                } else if (selectedPlace.contains(p)) {
                    isMarked(p, false);
                    selectedPlace.remove(p);
                } else {
                    isMarked(p, true);
                    selectedPlace.add(p);
                }
            }

            if (event.getButton() == MouseButton.SECONDARY) {
                isMarked(p, true);
                selectedPlace.add(p);

                if (p instanceof NamedPlace)
                    new ShowNamedPlaceInfoDialog(p.getName(), p.getPosition()).showAndWait();
                if (p instanceof DescribedPlace)
                    new ShowDescribedPlaceInfoDialog(p.getName(), p.getPosition(), ((DescribedPlace) p).getDescription()).showAndWait();
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Place place = (Place) o;
        return Objects.equals(name, place.name) &&
                category == place.category &&
                Objects.equals(position, place.position);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, category, position);
    }


}
