package com.example.andromeda;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import javafx.scene.paint.Color;
import javafx.animation.FadeTransition;
import javafx.util.Duration;

import java.io.InputStream;

public class PatientScreen {

    private VBox customFieldsContainer;
    private ObservableList<String> patientList = FXCollections.observableArrayList(
            "John Doe", "Jane Smith", "Alice Johnson", "Bob Williams"
    );

    public BorderPane createContent() {
        BorderPane content = new BorderPane();
        content.getStylesheets().add(getClass().getClassLoader().getResource("styles.css").toExternalForm());
        content.getStyleClass().add("patient-screen");

        // Main content
        HBox mainContent = new HBox(20);
        mainContent.setPadding(new Insets(20));

        // Patient list (left side)
        VBox patientListArea = createPatientListArea();

        // Patient details (right side)
        ScrollPane patientDetailsScroll = new ScrollPane(createPatientDetailsArea());
        patientDetailsScroll.setFitToWidth(true);
        patientDetailsScroll.setFitToHeight(true);

        mainContent.getChildren().addAll(patientListArea, patientDetailsScroll);
        HBox.setHgrow(patientDetailsScroll, Priority.ALWAYS);

        content.setCenter(mainContent);

        return content;
    }

    private VBox createPatientListArea() {
        VBox patientListArea = new VBox(15);
        patientListArea.setPrefWidth(250);
        patientListArea.getStyleClass().add("patient-list-area");

        Label titleLabel = new Label("Patients");
        titleLabel.getStyleClass().add("section-title");

        HBox searchBox = new HBox(10);
        searchBox.getStyleClass().add("search-box");
        TextField search = new TextField();
        search.setPromptText("Search patients");
        FontIcon searchIcon = new FontIcon(FontAwesomeSolid.SEARCH);
        searchIcon.setIconColor(Color.gray(0.6));
        searchBox.getChildren().addAll(searchIcon, search);

        ListView<String> patientListView = new ListView<>(patientList);
        patientListView.getStyleClass().add("patient-list");
        VBox.setVgrow(patientListView, Priority.ALWAYS);

        patientListView.setCellFactory(lv -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    HBox cellContent = new HBox(10);
                    cellContent.setAlignment(Pos.CENTER_LEFT);

                    FontIcon userIcon = new FontIcon(FontAwesomeSolid.USER);
                    userIcon.setIconColor(Color.gray(0.6));

                    Label nameLabel = new Label(item);
                    nameLabel.setStyle("-fx-font-weight: bold;");

                    cellContent.getChildren().addAll(userIcon, nameLabel);
                    setGraphic(cellContent);
                }
            }
        });

        Button addPatientBtn = new Button("Add New Patient");
        addPatientBtn.setGraphic(new FontIcon(FontAwesomeSolid.USER_PLUS));
        addPatientBtn.getStyleClass().addAll("add-patient-btn", "action-button");
        addPatientBtn.setOnAction(e -> showAddPatientDialog());

        patientListArea.getChildren().addAll(titleLabel, searchBox, patientListView, addPatientBtn);
        return patientListArea;
    }

    private VBox createPatientDetailsArea() {
        VBox patientDetailsArea = new VBox(20);
        patientDetailsArea.getStyleClass().add("patient-details-area");

        // Patient Info Section
        HBox patientInfo = createPatientInfoSection();
        patientDetailsArea.getChildren().add(patientInfo);

        // Tabs for different sections
        TabPane tabPane = new TabPane();
        tabPane.getTabs().addAll(
                createTab("Overview", createOverviewContent()),
                createTab("Appointments", new Label("Appointments content")),
                createTab("Medical Records", new Label("Medical Records content")),
                createTab("Billing", new Label("Billing content"))
        );
        tabPane.getStyleClass().add("patient-tabs");
        patientDetailsArea.getChildren().add(tabPane);
        VBox.setVgrow(tabPane, Priority.ALWAYS);

        return patientDetailsArea;
    }

    private HBox createPatientInfoSection() {
        HBox patientInfo = new HBox(20);
        patientInfo.getStyleClass().add("patient-info");

        StackPane imageContainer = new StackPane();
        imageContainer.setPrefSize(100, 100);
        imageContainer.getStyleClass().add("patient-image-container");

        ImageView patientImage = new ImageView();
        patientImage.setFitHeight(100);
        patientImage.setFitWidth(100);
        patientImage.getStyleClass().add("patient-image");

        FontIcon placeholderIcon = new FontIcon(FontAwesomeSolid.USER);
        placeholderIcon.setIconSize(80);
        placeholderIcon.setIconColor(Color.LIGHTGRAY);

        imageContainer.getChildren().add(placeholderIcon);

        // Try to load the image
        try {
            InputStream imageStream = getClass().getResourceAsStream("/images/patient-placeholder.jpg");
            if (imageStream != null) {
                Image image = new Image(imageStream);
                patientImage.setImage(image);
                imageContainer.getChildren().clear();
                imageContainer.getChildren().add(patientImage);
            }
        } catch (Exception e) {
            System.err.println("Error loading patient image: " + e.getMessage());
        }

        VBox patientDetails = new VBox(5);
        Label nameLabel = new Label("John Doe");
        nameLabel.getStyleClass().add("patient-name");
        Label idLabel = new Label("Patient ID: 12345");
        idLabel.getStyleClass().add("patient-id");

        patientDetails.getChildren().addAll(nameLabel, idLabel);

        Button editBtn = new Button("Edit");
        editBtn.setGraphic(new FontIcon(FontAwesomeSolid.EDIT));
        editBtn.getStyleClass().addAll("edit-btn", "action-button");

        patientInfo.getChildren().addAll(imageContainer, patientDetails, editBtn);
        HBox.setHgrow(patientDetails, Priority.ALWAYS);
        return patientInfo;
    }

    private Tab createTab(String title, javafx.scene.Node content) {
        Tab tab = new Tab(title);
        tab.setContent(content);
        return tab;
    }

    private VBox createOverviewContent() {
        VBox overview = new VBox(20);
        overview.setPadding(new Insets(20));

        // Custom fields section
        customFieldsContainer = new VBox(10);
        customFieldsContainer.getStyleClass().add("custom-fields-container");

        Button addFieldBtn = new Button("Add Field");
        addFieldBtn.setGraphic(new FontIcon(FontAwesomeSolid.PLUS));
        addFieldBtn.getStyleClass().addAll("add-field-btn", "action-button");
        addFieldBtn.setOnAction(e -> addCustomField());

        overview.getChildren().addAll(customFieldsContainer, addFieldBtn);
        return overview;
    }

    private void addCustomField() {
        HBox fieldRow = new HBox(10);
        fieldRow.getStyleClass().add("field-row");

        TextField fieldName = new TextField();
        fieldName.setPromptText("Field Name");
        fieldName.getStyleClass().add("field-name");

        TextField fieldValue = new TextField();
        fieldValue.setPromptText("Field Value");
        fieldValue.getStyleClass().add("field-value");

        Button removeBtn = new Button();
        removeBtn.setGraphic(new FontIcon(FontAwesomeSolid.TRASH));
        removeBtn.getStyleClass().add("remove-field-btn");
        removeBtn.setOnAction(e -> customFieldsContainer.getChildren().remove(fieldRow));

        fieldRow.getChildren().addAll(fieldName, fieldValue, removeBtn);
        customFieldsContainer.getChildren().add(fieldRow);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(500), fieldRow);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();
    }

    private void showAddPatientDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Add New Patient");
        dialog.getDialogPane().getStyleClass().add("add-patient-dialog");

        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        content.setStyle("-fx-background-color: white; -fx-background-radius: 10px;");

        Label titleLabel = new Label("Add New Patient");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        TextField nameField = new TextField();
        nameField.setPromptText("Patient Name");
        nameField.getStyleClass().add("dialog-text-field");

        Button addFieldBtn = new Button("Add Custom Field");
        addFieldBtn.setGraphic(new FontIcon(FontAwesomeSolid.PLUS));
        addFieldBtn.getStyleClass().addAll("add-field-btn", "action-button");
        addFieldBtn.setOnAction(e -> {
            HBox fieldRow = new HBox(10);
            TextField fieldName = new TextField();
            fieldName.setPromptText("Field Name");
            TextField fieldValue = new TextField();
            fieldValue.setPromptText("Field Value");
            fieldRow.getChildren().addAll(fieldName, fieldValue);
            content.getChildren().add(content.getChildren().size() - 1, fieldRow);
        });

        Button saveBtn = new Button("Save");
        saveBtn.setGraphic(new FontIcon(FontAwesomeSolid.SAVE));
        saveBtn.getStyleClass().addAll("save-btn", "action-button");
        saveBtn.setOnAction(e -> dialog.close());

        content.getChildren().addAll(titleLabel, nameField, addFieldBtn, saveBtn);
        dialog.getDialogPane().setContent(content);
        dialog.showAndWait();
    }
}