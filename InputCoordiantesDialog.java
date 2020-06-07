//Carl Dahlén cada7128

import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class InputCoordiantesDialog extends Alert {
    private TextField xcoordinateField = new TextField();
    private TextField ycoordinateField = new TextField();

    public InputCoordiantesDialog() {
        super(AlertType.CONFIRMATION);
        setTitle("Input Coordinates:");
        setHeaderText("Input Coordinates");
        GridPane grid = new GridPane();
        grid.addRow(0, new Label("x: "), xcoordinateField);
        grid.addRow(1, new Label("y: "), ycoordinateField);
        getDialogPane().setContent(grid);

    }

    public String getYcoordinate() {
        return ycoordinateField.getText();
    }

    public String getXcoordinate() {
        return xcoordinateField.getText();
    }
}
