//Carl Dahlén cada7128

import javafx.scene.control.Alert;


public class ShowDescribedPlaceInfoDialog extends Alert {
    public ShowDescribedPlaceInfoDialog(String name, Position position, String description) {
        super(AlertType.INFORMATION);
        setHeaderText("Name: " + name + " " + position + "\n" + "Description: " + description);
        setTitle("Info");
    }
}
