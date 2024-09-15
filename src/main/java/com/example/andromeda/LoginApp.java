package com.example.andromeda;

import javafx.application.Application;
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

import org.json.JSONObject;

public class LoginApp extends Application {

    private static final Logger LOGGER = Logger.getLogger(LoginApp.class.getName());
    private static final String PRIMARY_COLOR = "#3498db";
    private static final String SECONDARY_COLOR = "#ffffff";
    private static final String API_URL = "http://localhost:8080"; // Update this to your actual API URL

    private TextField emailField;
    private PasswordField passwordField;

    @Override
    public void start(Stage primaryStage) {
        showLoginScreen(primaryStage);
    }

    private void showLoginScreen(Stage stage) {
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
        stage.setTitle("Andromeda Healthcare - Login");
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
        Hyperlink createAccountLink = new Hyperlink("CREATE ACCOUNT");
        createAccountLink.getStyleClass().add("create-account-link");
        createAccountLink.setStyle("-fx-text-fill: " + PRIMARY_COLOR + ";");
        createAccountLink.setOnAction(e -> showRegistrationScreen(stage));
        topRightBox.getChildren().add(createAccountLink);

        Label loginLabel = new Label("Log into Andromeda");
        loginLabel.getStyleClass().add("login-label");
        loginLabel.setStyle("-fx-text-fill: " + PRIMARY_COLOR + ";");

        VBox form = createLoginForm();
        Button signInButton = new Button("SIGN IN");
        signInButton.getStyleClass().add("sign-in-button");
        signInButton.setStyle("-fx-background-color: " + PRIMARY_COLOR + "; -fx-text-fill: " + SECONDARY_COLOR + ";");
        signInButton.setOnAction(e -> handleLogin(stage));

        vbox.getChildren().addAll(topRightBox, loginLabel, form, signInButton);
        return vbox;
    }

    private VBox createLoginForm() {
        VBox form = new VBox(20);

        emailField = new TextField();
        emailField.setPromptText("Email address");
        emailField.getStyleClass().add("text-field");

        passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.getStyleClass().add("text-field");

        CheckBox rememberMe = new CheckBox("Remember me");
        rememberMe.getStyleClass().add("check-box");

        form.getChildren().addAll(emailField, passwordField, rememberMe);
        return form;
    }

    private void handleLogin(Stage stage) {
        String email = emailField.getText();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Please enter both email and password.");
            return;
        }

        try {
            HttpClient client = HttpClient.newHttpClient();
            JSONObject json = new JSONObject();
            json.put("email", email);
            json.put("password", password);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL + "/api/auth/login"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json.toString()))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                showAlert("Success", "Login successful!");
                showMainHomeScreen(stage);
            } else {
                JSONObject errorResponse = new JSONObject(response.body());
                String errorMessage = errorResponse.optString("message", "Login failed. Please check your credentials.");
                showAlert("Error", errorMessage);
            }
        } catch (IOException | InterruptedException ex) {
            LOGGER.log(Level.SEVERE, "An error occurred while trying to log in", ex);
            showAlert("Error", "An error occurred while trying to log in. Please try again later.");
        }
    }

    private void showRegistrationScreen(Stage stage) {
        RegistrationForm registrationForm = new RegistrationForm();
        registrationForm.show(stage);
    }

    private void showMainHomeScreen(Stage stage) {
        try {
            MainHome mainHome = new MainHome();
            mainHome.start(new Stage());  // Start MainHome in a new stage
            stage.close();  // Close the login stage
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error opening MainHome screen", e);
            showAlert("Error", "Unable to open the main screen. Please try again.");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}