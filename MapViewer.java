//Carl Dahlén cada7128

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.*;
import java.util.*;


public class MapViewer extends Application {
    private boolean changed;

    private Map<Position, Place> placePositionMap = new HashMap<>();
    private Map<String, Set<Place>> placeByNameMap = new HashMap<>();
    private Map<Category, Set<Place>> categoryPlaceMap = new EnumMap<>(Category.class);
    private ObservableList<Category> categoriesColors = FXCollections.observableArrayList(Category.Bus, Category.Train, Category.Underground);
    private Set<Category> hiddenCategories = new HashSet<>();

    private Stage stage;
    private BorderPane root = new BorderPane();
    private Pane centerPane = new Pane();
    private VBox menuVBox = new VBox();
    private VBox radioButtonVBox = new VBox();

    private Menu newMenu = new Menu("File");
    private MenuBar menubar = new MenuBar();
    private MenuItem loadMapItem = new MenuItem("Load Map");
    private MenuItem loadPlacesItem = new MenuItem("Load Places");
    private MenuItem saveItem = new MenuItem("Save");
    private MenuItem exitItem = new MenuItem("Exit");

    private ImageView imageView = new ImageView();

    private Button coordinatesButton = new Button("Coordiantes");
    private Button removeButton = new Button("Remove");
    private Button addNewPlaceButton = new Button("New");
    private Button searchButton = new Button("Search");
    private Button hideButton = new Button("Hide");
    private Button hideCategoryButton = new Button("Hide Category");
    private RadioButton namedButton = new RadioButton("Named");
    private RadioButton describedButton = new RadioButton("Described");
    private FlowPane controlBoard = new FlowPane();
    private FlowPane categoriesBoard = new FlowPane(Orientation.VERTICAL);
    private TextField searchField = new TextField();
    private ListView categoriesView = new ListView(categoriesColors);
    private Label categoriesLabel = new Label("Categories");


    @Override
    public void start(Stage stage) {

        this.stage = stage;

        root.setCenter(centerPane);
        root.setTop(menuVBox);
        root.setRight(categoriesBoard);

        menuVBox.getChildren().add(menubar);
        radioButtonVBox.getChildren().addAll(namedButton, describedButton);
        ToggleGroup group = new ToggleGroup();
        group.getToggles().addAll(namedButton, describedButton);


        categoriesBoard.getChildren().addAll(categoriesLabel, categoriesView, hideCategoryButton);
        hideCategoryButton.setAlignment(Pos.CENTER);
        hideCategoryButton.setOnAction(new HideCategoryHandler());
        categoriesBoard.setAlignment(Pos.CENTER);
        categoriesView.setPrefSize(120, 100);

        menubar.getMenus().add(newMenu);
        newMenu.getItems().addAll(loadMapItem, loadPlacesItem, saveItem, exitItem);
        loadMapItem.setOnAction(new LoadMapHandler());
        loadPlacesItem.setOnAction(new LoadPlacesHandler());
        saveItem.setOnAction(new SaveHandler());
        exitItem.setOnAction(new ExitItemHandler());
        menuVBox.getChildren().add(controlBoard);
        controlBoard.setAlignment(Pos.CENTER);
        controlBoard.setHgap(5);
        centerPane.getChildren().add(imageView);
        controlBoard.getChildren().addAll(addNewPlaceButton, radioButtonVBox, searchField, searchButton, hideButton, removeButton, coordinatesButton);
        searchButton.setOnAction(new SearchHandler());
        hideButton.setOnAction(new HidePlaceHandler());
        addNewPlaceButton.setOnAction(new AddNewPlaceHandler());
        removeButton.setOnAction(new RemovePlaceHandler());
        coordinatesButton.setOnAction(new CoordinatesHandler());
        categoriesView.setOnMouseClicked(new SelectCategoryClickHandler());
        Scene scene = new Scene(root, 800, 800);
        stage.setTitle("MapViewer");
        stage.setScene(scene);
        stage.setOnCloseRequest(new ExitHandler());
        stage.show();

    }

    class SelectCategoryClickHandler implements EventHandler<MouseEvent> {
        @Override
        public void handle(MouseEvent event) {
            Category category = getCategoryFromListView();
            if (hiddenCategories.contains(category)) {
                Set<Place> places = categoryPlaceMap.get(category);
                Iterator<Place> iterator = places.iterator();
                while (iterator.hasNext()) {
                    Place place = iterator.next();
                    place.setVisible(true);
                    hiddenCategories.remove(category);
                }
            }
        }
    }

    public void clearCenterPane() {
        categoryPlaceMap.clear();
        placeByNameMap.clear();
        placePositionMap.clear();
        Place.selectedPlace.clear();
        centerPane.getChildren().clear();
        centerPane.getChildren().add(imageView);
        changed = false;
    }

    public void clearAllPlaces() {
        centerPane.getChildren().removeAll(placePositionMap.values());
        categoryPlaceMap.clear();
        placeByNameMap.clear();
        placePositionMap.clear();
        Place.selectedPlace.clear();
        changed = false;
    }

    private void clearMarkedPlaces() {
        for (Place place : Place.selectedPlace) {
            place.setUnMarked(place);
        }
    }

    class LoadMapHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            if (changed) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "There are unsaved changes, do you want to contiunue anyway?");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    clearCenterPane();

                }
                if (result.isPresent() && result.get() == ButtonType.CANCEL)
                    return;
            } else
                clearCenterPane();

            FileChooser fileChooser = new FileChooser();
            File file = fileChooser.showOpenDialog(stage);
            fileChooser.setTitle("Open map file");
            if (file == null)
                return;
            String fileName = file.getAbsolutePath();
            Image image = new Image("file:" + fileName);
            imageView.setImage(image);
            stage.sizeToScene();
        }

    }

    class LoadPlacesHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            try {
                if (changed) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "There are unsaved changes, do you want to contiunue anyway?");
                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.isPresent() && result.get() == ButtonType.OK) {
                        clearAllPlaces();
                    }
                    if (result.isPresent() && result.get() == ButtonType.CANCEL)
                        return;
                }
                FileChooser fileChooser = new FileChooser();
                File file = fileChooser.showOpenDialog(stage);
                if (file == null)
                    return;
                String fileName = file.getAbsolutePath();
                FileReader inFile = new FileReader(fileName);
                BufferedReader in = new BufferedReader(inFile);
                String line;
                while ((line = in.readLine()) != null) {
                    String[] tokens = line.split(",");
                    Category category = Category.valueOf(tokens[1]);

                    int xcoordinate = Integer.parseInt(tokens[2]);
                    int ycoordinate = Integer.parseInt(tokens[3]);
                    Position newPosition = new Position(xcoordinate, ycoordinate);
                    String name = tokens[4];

                    if (tokens[0].equals("Named")) {
                        NamedPlace newNamedPlace = new NamedPlace(name, category, newPosition);
                        centerPane.getChildren().add(newNamedPlace);
                        addToPlaceByNameMap(newNamedPlace);
                        addToPlaceByPositionMap(newNamedPlace);
                        addToCategoryPlaceMap(newNamedPlace);
                    } else if (tokens[0].equals("Described")) {
                        String description = tokens[5];
                        DescribedPlace newDescribedPlace = new DescribedPlace(name, category, newPosition, description);
                        centerPane.getChildren().add(newDescribedPlace);
                        addToPlaceByNameMap(newDescribedPlace);
                        addToPlaceByPositionMap(newDescribedPlace);
                        addToCategoryPlaceMap(newDescribedPlace);
                    } else
                        return;
                }

                in.close();
                inFile.close();
            } catch (FileNotFoundException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "No such file");
                alert.showAndWait();
            } catch (IOException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Error");
                alert.showAndWait();
            }
        }
    }

    public Category getCategoryFromListView() {
        Category category;
        if (categoriesView.getSelectionModel().getSelectedItem() != null) {
            category = (Category) categoriesView.getSelectionModel().getSelectedItem();
            return category;
        } else
            category = Category.None;
        return category;
    }

    private void newNamedPlace(Position newPosition, Category category) {
        NamedPlaceDialog dialog = new NamedPlaceDialog();
        Optional<ButtonType> answer = dialog.showAndWait();
        if (answer.isPresent() && answer.get() == ButtonType.OK) {
            String name = dialog.getName();
            if (name.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Error, enter the information");
                alert.showAndWait();
                return;
            }
            NamedPlace newNamedPlace = new NamedPlace(name, category, newPosition);

            addToPlaceByNameMap(newNamedPlace);
            addToPlaceByPositionMap(newNamedPlace);
            addToCategoryPlaceMap(newNamedPlace);
            changed = true;
            centerPane.getChildren().add(newNamedPlace);
        }
    }

    private void newDescribedPlace(Position newPosition, Category category) {
        DescribedPlaceDialog dialog = new DescribedPlaceDialog();
        Optional<ButtonType> answer = dialog.showAndWait();
        if (answer.isPresent() && answer.get() == ButtonType.OK) {
            String name = dialog.getName();
            String description = dialog.getDescription();
            if (name.isEmpty() || description.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Error, enter the information");
                alert.showAndWait();
                return;
            }
            DescribedPlace newDescribedPlace = new DescribedPlace(name, category, newPosition, description);

            addToPlaceByNameMap(newDescribedPlace);
            addToPlaceByPositionMap(newDescribedPlace);
            addToCategoryPlaceMap(newDescribedPlace);
            changed = true;
            centerPane.getChildren().add(newDescribedPlace);
        }
    }

    public class ClickHandler implements EventHandler<MouseEvent> {
        @Override
        public void handle(MouseEvent event) {
            double x = event.getX();
            double y = event.getY();
            Position newPosition = new Position((int) x, (int) y);
            Category category = getCategoryFromListView();
            Place placeAvailable = placePositionMap.get(newPosition);
            if (placeAvailable == null) {
                if (namedButton.isSelected()) {
                    newNamedPlace(newPosition, category);
                }
                if (describedButton.isSelected()) {
                    newDescribedPlace(newPosition, category);
                }
            } else
                new Alert(Alert.AlertType.ERROR, "There is already a place at this location").showAndWait();

            centerPane.setOnMouseClicked(null);
            centerPane.setCursor(Cursor.DEFAULT);
        }


    }

    private void addToPlaceByPositionMap(Place place) {
        Position position = place.getPosition();
        placePositionMap.put(position, place);
    }

    private void addToPlaceByNameMap(Place place) {
        String name = place.getName();
        Set<Place> placeSet = placeByNameMap.get(name);
        if (placeSet == null) {
            placeSet = new HashSet<>();
            placeByNameMap.put(name, placeSet);
        }
        placeSet.add(place);

    }

    private void addToCategoryPlaceMap(Place place) {
        Category category = place.getCategory();
        Set<Place> placeSet = categoryPlaceMap.get(category);
        if (placeSet == null) {
            placeSet = new HashSet<>();
            categoryPlaceMap.put(category, placeSet);
        }
        placeSet.add(place);
    }


    class AddNewPlaceHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            centerPane.setCursor(Cursor.CROSSHAIR);
            centerPane.setOnMouseClicked(new ClickHandler());
        }
    }


    class SaveHandler implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent event) {
            try {
                FileChooser fileChooser = new FileChooser();
                File file = fileChooser.showSaveDialog(stage);
                if (file == null)
                    return;
                String fileName = file.getAbsolutePath();

                FileWriter outFile = new FileWriter(fileName);
                PrintWriter out = new PrintWriter(outFile);
                for (Place p : placePositionMap.values()) {
                    out.println(p.toString());
                }
                out.close();
                outFile.close();
                changed = false;
            } catch (FileNotFoundException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "No such file");
                alert.showAndWait();
            } catch (IOException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Error");
                alert.showAndWait();
            }
        }
    }

    class SearchHandler implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent event) {
            clearMarkedPlaces();
            Place.selectedPlace.clear();
            String name = searchField.getText();
            Set<Place> places = placeByNameMap.get(name);
            if (places == null)
                new Alert(Alert.AlertType.ERROR, "No place with that name").showAndWait();
            else {
                for (Place p : places) {
                    p.setVisible(true);
                    p.setMarked();
                    Place.selectedPlace.add(p);
                }

            }
        }
    }


    class RemovePlaceHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            removeFromPlaceByNameMap();
            removeFromPlaceByPositionMap();
            removeFromPlaceByCategoryMap();
            changed = true;
        }
    }

    public void removeFromPlaceByCategoryMap() {
        Iterator<Place> iter = Place.selectedPlace.iterator();
        while (iter.hasNext()) {
            Place place = iter.next();
            Category category = place.getCategory();
            Set<Place> places = categoryPlaceMap.get(category);
            places.remove(place);
            if (places.isEmpty())
                categoryPlaceMap.remove(category);
            place.setVisible(false);
            iter.remove();

        }
    }


    public void removeFromPlaceByNameMap() {
        Iterator<Place> iter = Place.selectedPlace.iterator();
        while (iter.hasNext()) {
            Place place = iter.next();
            String name = place.getName();
            Set<Place> places = placeByNameMap.get(name);
            places.remove(place);
            if (places.isEmpty())
                placeByNameMap.remove(name);
            place.setVisible(false);


        }

    }


    public void removeFromPlaceByPositionMap() {
        Iterator<Place> iter = Place.selectedPlace.iterator();
        while (iter.hasNext()) {
            Place place = iter.next();
            Position position = place.getPosition();
            placePositionMap.remove(position);
            place.setVisible(false);


        }
    }

    class CoordinatesHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            int ycoordinate;
            int xcoordinate;

            InputCoordiantesDialog dialog = new InputCoordiantesDialog();
            Optional<ButtonType> answer = dialog.showAndWait();
            if (answer.isPresent() && answer.get() == ButtonType.OK) {
                try {
                    xcoordinate = Integer.parseInt(dialog.getXcoordinate());
                    ycoordinate = Integer.parseInt(dialog.getYcoordinate());
                    Position position = new Position(xcoordinate, ycoordinate);

                    Place place = placePositionMap.get(position);
                    if (place == null)
                        new Alert(Alert.AlertType.ERROR, "There is nothing at that position").showAndWait();
                    else {
                        clearMarkedPlaces();
                        place.setVisible(true);
                        place.setMarked();
                        Place.selectedPlace.add(place);
                    }

                } catch (NumberFormatException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Not a number");
                    alert.showAndWait();
                }
            }
        }
    }

    class HidePlaceHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            Iterator<Place> iter = Place.selectedPlace.iterator();
            while (iter.hasNext()) {
                Place place = iter.next();
                place.setVisible(false);
                iter.remove();

            }
        }
    }

    class HideCategoryHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            Category category = getCategoryFromListView();
            Set<Place> places = categoryPlaceMap.get(category);
            if (!(places == null)) {
                Iterator<Place> iterator = places.iterator();
                while (iterator.hasNext()) {
                    Place place = iterator.next();
                    place.setVisible(false);
                    hiddenCategories.add(category);
                }
            }
        }
    }

    class ExitItemHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
        }
    }

    class ExitHandler implements EventHandler<WindowEvent> {
        @Override
        public void handle(WindowEvent windowEvent) {
            if (changed) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "There are changes that has not been saved, exit anyway?");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.CANCEL)
                    windowEvent.consume();
            }
        }

    }
}




