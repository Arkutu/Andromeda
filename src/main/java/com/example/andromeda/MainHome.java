package com.example.andromeda;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import org.json.JSONObject;

import java.util.logging.Logger;
import java.util.logging.Level;

public class MainHome extends Application {

    private static final Logger LOGGER = Logger.getLogger(MainHome.class.getName());
    private static final String PRIMARY_COLOR = "#3498db";
    private static final String SECONDARY_COLOR = "#ffffff";
    private static final String API_URL = "http://localhost:8080"; // Update this to your actual API URL

    private String userFullName = "User";

    private BorderPane root;
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        fetchUserData();

        root = new BorderPane();
        root.getStyleClass().add("root");

        // Top bar
        HBox topBar = createTopBar();
        root.setTop(topBar);

        // Left sidebar
        VBox sidebar = createSidebar();
        root.setLeft(sidebar);

        // Main content
        navigateToDashboard();

        Scene scene = new Scene(root, 1200, 800);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        primaryStage.setTitle("Andromeda Healthcare");
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    private void fetchUserData() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL + "/api/user/current"))
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JSONObject userData = new JSONObject(response.body());
                userFullName = userData.getString("fullName");
            } else {
                LOGGER.log(Level.WARNING, "Failed to fetch user data. Status code: " + response.statusCode());
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error fetching user data", e);
        }
    }

    private HBox createTopBar() {
        HBox topBar = new HBox();
        topBar.getStyleClass().add("top-bar");
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(10, 20, 10, 20));

        Label hospitalName = new Label("Andromeda");
        hospitalName.getStyleClass().add("hospital-name");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button menuButton = createIconButton(FontAwesomeSolid.BARS);
        Button messageButton = createIconButton(FontAwesomeSolid.ENVELOPE);
        Button notificationButton = createIconButton(FontAwesomeSolid.BELL);

        topBar.getChildren().addAll(hospitalName, spacer, menuButton, messageButton, notificationButton);
        return topBar;
    }

    private Button createIconButton(FontAwesomeSolid icon) {
        Button button = new Button();
        button.getStyleClass().add("icon-button");
        FontIcon fontIcon = new FontIcon(icon);
        button.setGraphic(fontIcon);
        return button;
    }

    private VBox createSidebar() {
        VBox sidebar = new VBox(10);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPadding(new Insets(20));

        VBox menuItems = new VBox(5);
        menuItems.getChildren().addAll(
                createMenuItem("Dashboard", FontAwesomeSolid.HOME, true),
                createMenuItem("Patients", FontAwesomeSolid.USER),
                createMenuItem("Doctors", FontAwesomeSolid.USER_MD),
                createMenuItem("Stats", FontAwesomeSolid.CHART_BAR),
                createMenuItem("Settings", FontAwesomeSolid.COG)
        );

        Separator separator = new Separator();
        separator.getStyleClass().add("menu-separator");

        VBox otherItems = new VBox(5);
        otherItems.getChildren().addAll(
                createMenuItem("Support", FontAwesomeSolid.QUESTION_CIRCLE),
                createMenuItem("Logout", FontAwesomeSolid.SIGN_OUT_ALT)
        );

        sidebar.getChildren().addAll(menuItems, separator, otherItems);
        return sidebar;
    }

    private HBox createMenuItem(String text, FontAwesomeSolid iconCode, boolean isActive) {
        HBox item = new HBox(10);
        item.getStyleClass().addAll("menu-item", isActive ? "menu-item-active" : "");
        item.setAlignment(Pos.CENTER_LEFT);
        item.setPadding(new Insets(10, 15, 10, 15));

        FontIcon icon = new FontIcon(iconCode);
        icon.getStyleClass().add("menu-icon");

        Label label = new Label(text);
        label.getStyleClass().add("menu-text");

        item.getChildren().addAll(icon, label);

        item.setOnMouseClicked(event -> {
            if (text.equals("Patients")) {
                navigateToPatientScreen();
            } else if (text.equals("Dashboard")) {
                navigateToDashboard();
            }
            // Add other navigation logic for other menu items as needed
        });

        return item;
    }

    private void navigateToPatientScreen() {
        PatientScreen patientScreen = new PatientScreen();
        BorderPane patientContent = patientScreen.createContent();
        root.setCenter(patientContent);
    }

    private void navigateToDashboard() {
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setContent(createMainContent());
        root.setCenter(scrollPane);
    }




    private HBox createMenuItem(String text, FontAwesomeSolid iconCode) {
        return createMenuItem(text, iconCode, false);
    }

    private VBox createMainContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));

        Label dashboardLabel = new Label("Dashboard");
        dashboardLabel.getStyleClass().add("dashboard-title");

        // Welcome banner
        HBox welcomeBanner = createWelcomeBanner();

        // Health metrics
        HBox healthMetrics = createHealthMetrics();

        // Charts and reports
        HBox chartsAndReports = new HBox(20);
        chartsAndReports.getChildren().addAll(createHeartRateChart(), createHealthReports());

        content.getChildren().addAll(dashboardLabel, welcomeBanner, healthMetrics, chartsAndReports);
        return content;
    }

    private HBox createWelcomeBanner() {
        HBox banner = new HBox();
        banner.getStyleClass().add("welcome-banner");
        banner.setPadding(new Insets(20));

        VBox textContent = new VBox(10);
        Label welcomeText = new Label("Welcome " + userFullName + "!");
        welcomeText.getStyleClass().add("welcome-title");
        Label subText = new Label("Let's check your health stats with us. Care with us!");
        subText.getStyleClass().add("welcome-subtitle");

        Button connectButton = new Button("Connect to Doctor");
        connectButton.getStyleClass().add("connect-button");

        textContent.getChildren().addAll(welcomeText, subText, connectButton);

        ImageView doctorsImage = new ImageView(new Image(getClass().getResourceAsStream("/images/docs.jpg")));
        doctorsImage.setFitHeight(120);
        doctorsImage.setPreserveRatio(true);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        banner.getChildren().addAll(textContent, spacer, doctorsImage);
        return banner;
    }

    private HBox createHealthMetrics() {
        HBox metrics = new HBox(20);
        metrics.getChildren().addAll(
                createMetricCard("Blood Pressure", "110/70", "mmHg", FontAwesomeSolid.HEART),
                createMetricCard("Heart Rate", "85", "bmp", FontAwesomeSolid.HEARTBEAT),
                createMetricCard("Glucose Level", "75-90", "mg/dL", FontAwesomeSolid.TINT),
                createMetricCard("Blood Count", "9,456", "ml", FontAwesomeSolid.VIAL)
        );
        return metrics;
    }

    private VBox createMetricCard(String title, String value, String unit, FontAwesomeSolid iconCode) {
        VBox card = new VBox(10);
        card.getStyleClass().add("metric-card");
        card.setPadding(new Insets(15));

        HBox iconContainer = new HBox();
        iconContainer.getStyleClass().add("metric-icon-container");
        FontIcon icon = new FontIcon(iconCode);
        icon.getStyleClass().add("metric-icon");
        iconContainer.getChildren().add(icon);

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("metric-title");
        Label valueLabel = new Label(value);
        valueLabel.getStyleClass().add("metric-value");
        Label unitLabel = new Label(unit);
        unitLabel.getStyleClass().add("metric-unit");

        card.getChildren().addAll(iconContainer, titleLabel, valueLabel, unitLabel);
        return card;
    }

    private VBox createHeartRateChart() {
        VBox chartContainer = new VBox(10);
        chartContainer.getStyleClass().add("chart-container");
        chartContainer.setPadding(new Insets(15));

        HBox titleBox = new HBox();
        titleBox.setAlignment(Pos.CENTER_LEFT);
        Label chartTitle = new Label("Performance Heart Rate");
        chartTitle.getStyleClass().add("chart-title");
        Label averageLabel = new Label("72 bmp Average");
        averageLabel.getStyleClass().add("chart-average");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        titleBox.getChildren().addAll(chartTitle, spacer, averageLabel);

        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setCreateSymbols(false);
        lineChart.setLegendVisible(false);
        lineChart.getStyleClass().add("blue-chart");

        XYChart.Series series = new XYChart.Series();
        series.getData().add(new XYChart.Data(1, 70));
        series.getData().add(new XYChart.Data(2, 75));
        series.getData().add(new XYChart.Data(3, 72));
        series.getData().add(new XYChart.Data(4, 78));
        series.getData().add(new XYChart.Data(5, 71));
        series.getData().add(new XYChart.Data(6, 76));
        series.getData().add(new XYChart.Data(7, 73));

        lineChart.getData().add(series);

        chartContainer.getChildren().addAll(titleBox, lineChart);
        return chartContainer;
    }

    private VBox createHealthReports() {
        VBox reportsContainer = new VBox(10);
        reportsContainer.getStyleClass().add("reports-container");
        reportsContainer.setPadding(new Insets(15));

        HBox titleBox = new HBox();
        titleBox.setAlignment(Pos.CENTER_LEFT);
        Label reportsTitle = new Label("Health Reports Document");
        reportsTitle.getStyleClass().add("reports-title");
        Button viewAllButton = new Button("View All");
        viewAllButton.getStyleClass().add("view-all-button");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        titleBox.getChildren().addAll(reportsTitle, spacer, viewAllButton);

        VBox reportsList = new VBox(10);
        reportsList.getChildren().addAll(
                createReportItem("Medical Check Up Report.pdf", "23 MB"),
                createReportItem("Blood Count Report.docx", "15 MB"),
                createReportItem("Glucose Level Report.docx", "8 MB"),
                createReportItem("Heart Rate Report.pdf", "12 MB")
        );

        reportsContainer.getChildren().addAll(titleBox, reportsList);
        return reportsContainer;
    }

    private HBox createReportItem(String fileName, String fileSize) {
        HBox item = new HBox(10);
        item.setAlignment(Pos.CENTER_LEFT);

        FontIcon fileIcon = new FontIcon(FontAwesomeSolid.FILE_ALT);
        fileIcon.getStyleClass().add("file-icon");

        VBox fileInfo = new VBox(5);
        Label nameLabel = new Label(fileName);
        nameLabel.getStyleClass().add("file-name");
        Label sizeLabel = new Label(fileSize);
        sizeLabel.getStyleClass().add("file-size");
        fileInfo.getChildren().addAll(nameLabel, sizeLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button downloadButton = new Button();
        downloadButton.getStyleClass().add("download-button");
        FontIcon downloadIcon = new FontIcon(FontAwesomeSolid.DOWNLOAD);
        downloadButton.setGraphic(downloadIcon);

        item.getChildren().addAll(fileIcon, fileInfo, spacer, downloadButton);
        return item;
    }

    public static void main(String[] args) {
        launch(args);
    }
}