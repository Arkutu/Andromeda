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
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class MainHome extends Application {

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #F0F4FF;");

        // Left sidebar
        VBox sidebar = createSidebar();
        root.setLeft(sidebar);

        // Main content
        VBox mainContent = createMainContent();
        root.setCenter(mainContent);

        Scene scene = new Scene(root, 1200, 800);
        primaryStage.setTitle("Mario Hospital - Dashboard");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private VBox createSidebar() {
        VBox sidebar = new VBox(10);
        sidebar.setPadding(new Insets(20));
        sidebar.setStyle("-fx-background-color: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");

        Label hospitalName = new Label("Mario Hospital");
        hospitalName.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        // Add menu items
        VBox menuItems = new VBox(15);
        menuItems.getChildren().addAll(
                createMenuItem("Dashboard", true),
                createMenuItem("Appointments", false),
                createMenuItem("Doctors", false),
                createMenuItem("Patients", false),
                createMenuItem("Records", false)
        );

        sidebar.getChildren().addAll(hospitalName, new Separator(), menuItems);
        return sidebar;
    }

    private HBox createMenuItem(String text, boolean isSelected) {
        HBox item = new HBox(10);
        item.setAlignment(Pos.CENTER_LEFT);

        // Create a placeholder circle instead of an image
        Circle placeholder = new Circle(10, isSelected ? Color.web("#4A6FFF") : Color.GRAY);

        Label label = new Label(text);

        item.getChildren().addAll(placeholder, label);

        if (isSelected) {
            item.setStyle("-fx-background-color: #E8EEFF; -fx-padding: 10; -fx-background-radius: 5;");
            label.setTextFill(Color.web("#4A6FFF"));
        } else {
            item.setStyle("-fx-padding: 10;");
            label.setTextFill(Color.GRAY);
        }

        return item;
    }

    private VBox createMainContent() {
        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(20));

        // Welcome banner
        HBox welcomeBanner = createWelcomeBanner();

        // Health metrics
        HBox healthMetrics = createHealthMetrics();

        // Charts and reports
        HBox chartsAndReports = createChartsAndReports();

        mainContent.getChildren().addAll(welcomeBanner, healthMetrics, chartsAndReports);
        return mainContent;
    }

    private HBox createWelcomeBanner() {
        HBox banner = new HBox();
        banner.setStyle("-fx-background-color: #4A6FFF; -fx-background-radius: 10;");
        banner.setPadding(new Insets(20));

        VBox textContent = new VBox(10);
        Label welcomeLabel = new Label("Welcome Neri Kwang !");
        welcomeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        welcomeLabel.setTextFill(Color.WHITE);

        Label subLabel = new Label("Let's check your health data with us. Click here to get more free bots!");
        subLabel.setTextFill(Color.WHITE);

        Button connectButton = new Button("Connect to Doctor");
        connectButton.setStyle("-fx-background-color: white; -fx-text-fill: #4A6FFF;");

        textContent.getChildren().addAll(welcomeLabel, subLabel, connectButton);

        banner.getChildren().add(textContent);
        return banner;
    }

    private HBox createHealthMetrics() {
        HBox metrics = new HBox(20);

        metrics.getChildren().addAll(
                createMetricCard("Blood Pressure", "110/70", "mmHg", "+5% Higher Than Last Month"),
                createMetricCard("Heart Rate", "85", "bmp", "-2% Lower Than Last Month"),
                createMetricCard("Glucose Level", "75-90", "", "-1% Lower Than Last Month"),
                createMetricCard("Blood Count", "9,456", "/mL", "+1% Higher Than Last Month")
        );

        return metrics;
    }

    private VBox createMetricCard(String title, String value, String unit, String comparison) {
        VBox card = new VBox(5);
        card.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-background-radius: 10;");

        Label titleLabel = new Label(title);
        titleLabel.setTextFill(Color.GRAY);

        Label valueLabel = new Label(value + " " + unit);
        valueLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        Label comparisonLabel = new Label(comparison);
        comparisonLabel.setTextFill(Color.GRAY);
        comparisonLabel.setFont(Font.font("Arial", 12));

        card.getChildren().addAll(titleLabel, valueLabel, comparisonLabel);
        return card;
    }

    private HBox createChartsAndReports() {
        HBox content = new HBox(20);

        // Heart rate chart
        VBox chartBox = new VBox(10);
        Label chartTitle = new Label("Performance Heart Rate");
        chartTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Heart Rate Over Time");
        XYChart.Series series = new XYChart.Series();
        series.setName("Heart Rate");

        // Add dummy data
        series.getData().add(new XYChart.Data(1, 70));
        series.getData().add(new XYChart.Data(2, 75));
        series.getData().add(new XYChart.Data(3, 72));
        series.getData().add(new XYChart.Data(4, 78));
        series.getData().add(new XYChart.Data(5, 73));

        lineChart.getData().add(series);
        lineChart.setCreateSymbols(false);

        chartBox.getChildren().addAll(chartTitle, lineChart);

        // Health reports
        VBox reportsBox = new VBox(10);
        Label reportsTitle = new Label("Health Reports Document");
        reportsTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        VBox reportsList = new VBox(5);
        reportsList.getChildren().addAll(
                createReportItem("Medical Check Up Report.pdf", "23 MB"),
                createReportItem("Blood Count Report.docs", "12 MB"),
                createReportItem("Glucose Level Report.docs", "8 MB"),
                createReportItem("Heart Rate Report.pdf", "5 MB")
        );

        reportsBox.getChildren().addAll(reportsTitle, reportsList);

        content.getChildren().addAll(chartBox, reportsBox);
        return content;
    }

    private HBox createReportItem(String name, String size) {
        HBox item = new HBox(10);
        item.setAlignment(Pos.CENTER_LEFT);

        // Create a placeholder circle instead of an image
        Circle placeholder = new Circle(10, Color.GRAY);

        VBox textContent = new VBox(2);
        Label nameLabel = new Label(name);
        Label sizeLabel = new Label(size);
        sizeLabel.setTextFill(Color.GRAY);
        textContent.getChildren().addAll(nameLabel, sizeLabel);

        item.getChildren().addAll(placeholder, textContent);
        return item;
    }

    public static void main(String[] args) {
        launch(args);
    }
}