//Carl Dahlén cada7128

import javafx.scene.control.Alert;

public class ShowNamedPlaceInfoDialog extends Alert {
    public ShowNamedPlaceInfoDialog(String name, Position position) {
        super(AlertType.INFORMATION);
        setHeaderText("Name: " + name + " " + position);
        setTitle("Info");
    }
}
