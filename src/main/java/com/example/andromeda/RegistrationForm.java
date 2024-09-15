package com.example.andromeda;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.io.IOException;
import java.io.File;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.regex.Pattern;

import org.json.JSONObject;
import org.json.JSONException;

public class RegistrationForm {

    private static final Logger LOGGER = Logger.getLogger(RegistrationForm.class.getName());
    private static final String PRIMARY_COLOR = "#3498db";
    private static final String SECONDARY_COLOR = "#ffffff";
    private static final String API_URL = "http://localhost:8080"; // Update this to your actual API URL

    private TextField fullNameField;
    private TextField emailField;
    private TextField usernameField;
    private PasswordField passwordField;

    public void show(Stage stage) {
        BorderPane root = new BorderPane();

        VBox leftSide = createLeftSide();
        VBox rightSide = createRightSide(stage);

        root.setLeft(leftSide);
        root.setRight(rightSide);

        Scene scene = new Scene(root, 900, 600);

        String stylesheetUrl = getClass().getResource("/styles.css").toExternalForm();
        if (stylesheetUrl != null) {
            scene.getStylesheets().add(stylesheetUrl);
        } else {
            LOGGER.warning("Stylesheet not found");
        }

        stage.setScene(scene);
        stage.setTitle("Andromeda Healthcare - Registration");
        stage.show();
    }

    private VBox createLeftSide() {
        VBox vbox = new VBox(30);
        vbox.setStyle("-fx-background-color: " + PRIMARY_COLOR + ";");
        vbox.setPrefWidth(450);
        vbox.setPadding(new Insets(60));
        vbox.setAlignment(Pos.CENTER_LEFT);

        // Load the hospital logo
        ImageView logoImageView = new ImageView();
        try {
            File file = new File("/home/Arktech/Projects/Andromeda/images/and.jpg");
            Image logoImage = new Image(file.toURI().toString());
            logoImageView.setImage(logoImage);
            logoImageView.setFitWidth(80);
            logoImageView.setFitHeight(80);
            logoImageView.setPreserveRatio(true);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to load hospital logo", e);
        }

        Label title = new Label("Andromeda Healthcare");
        title.getStyleClass().add("title-label");
        title.setStyle("-fx-text-fill: " + SECONDARY_COLOR + ";");

        Label subtitle = new Label("Healthcare Management System");
        subtitle.getStyleClass().add("subtitle-label");
        subtitle.setStyle("-fx-text-fill: " + SECONDARY_COLOR + ";");

        Label description = new Label("A powerful, yet intuitive solution for managing and analyzing patient data, enhancing healthcare delivery through advanced technology.");
        description.getStyleClass().add("description-label");
        description.setStyle("-fx-text-fill: " + SECONDARY_COLOR + ";");
        description.setWrapText(true);

        vbox.getChildren().addAll(logoImageView, title, subtitle, description);
        return vbox;
    }

    private VBox createRightSide(Stage stage) {
        VBox vbox = new VBox(30);
        vbox.setStyle("-fx-background-color: " + SECONDARY_COLOR + ";");
        vbox.setPrefWidth(450);
        vbox.setPadding(new Insets(60));
        vbox.setAlignment(Pos.TOP_LEFT);

        HBox topRightBox = new HBox();
        topRightBox.setAlignment(Pos.TOP_RIGHT);
        Hyperlink loginLink = new Hyperlink("LOG IN");
        loginLink.getStyleClass().add("create-account-link");
        loginLink.setStyle("-fx-text-fill: " + PRIMARY_COLOR + ";");
        loginLink.setOnAction(e -> showLoginScreen(stage));
        topRightBox.getChildren().add(loginLink);

        Label registerLabel = new Label("Create an account");
        registerLabel.getStyleClass().add("login-label");
        registerLabel.setStyle("-fx-text-fill: " + PRIMARY_COLOR + ";");

        VBox form = createRegistrationForm();
        Button registerButton = new Button("CREATE ACCOUNT");
        registerButton.getStyleClass().add("sign-in-button");
        registerButton.setStyle("-fx-background-color: " + PRIMARY_COLOR + "; -fx-text-fill: " + SECONDARY_COLOR + ";");
        registerButton.setOnAction(e -> handleRegistration(stage));

        vbox.getChildren().addAll(topRightBox, registerLabel, form, registerButton);
        return vbox;
    }

    private VBox createRegistrationForm() {
        VBox form = new VBox(20);

        fullNameField = new TextField();
        fullNameField.setPromptText("Full Name");
        fullNameField.getStyleClass().add("text-field");

        emailField = new TextField();
        emailField.setPromptText("Email Address");
        emailField.getStyleClass().add("text-field");

        usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.getStyleClass().add("text-field");

        passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.getStyleClass().add("text-field");

        form.getChildren().addAll(fullNameField, emailField, usernameField, passwordField);
        return form;
    }

    private void handleRegistration(Stage stage) {
        String fullName = fullNameField.getText();
        String email = emailField.getText();
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (fullName.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Please fill out all the fields.");
            return;
        }

        if (!isValidEmail(email)) {
            showAlert("Error", "Please enter a valid email address.");
            return;
        }

        try {
            HttpClient client = HttpClient.newHttpClient();
            JSONObject json = new JSONObject();
            json.put("fullName", fullName);
            json.put("email", email);
            json.put("username", username);
            json.put("password", password);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL + "/api/auth/register"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json.toString()))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                showAlert("Success", "Registration successful!");
                showLoginScreen(stage);
            } else {
                String errorMessage;
                try {
                    JSONObject errorResponse = new JSONObject(response.body());
                    errorMessage = errorResponse.optString("message", "Registration failed. Please try again.");
                } catch (JSONException e) {
                    errorMessage = "An unexpected error occurred. Please try again later.";
                }
                showAlert("Error", errorMessage);
                LOGGER.log(Level.WARNING, "Registration failed. Status code: " + response.statusCode() + ", Body: " + response.body());
            }
        } catch (IOException | InterruptedException ex) {
            LOGGER.log(Level.SEVERE, "An error occurred while trying to register", ex);
            showAlert("Error", "Unable to connect to the server. Please check your internet connection and try again.");
        }
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pattern = Pattern.compile(emailRegex);
        return email != null && pattern.matcher(email).matches();
    }

    private void showLoginScreen(Stage stage) {
        LoginApp loginApp = new LoginApp();
        loginApp.start(stage);
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}