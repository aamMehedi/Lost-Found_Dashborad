package com.lostandfound;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class DashboardWindow {

    private BorderPane mainLayout;
    private Button btnOverview;
    private Button btnLog;
    private Button btnSearch;

    public void showDashboard(Stage stage) {
        stage.setTitle("Lost & Found Admin Dashboard");

        mainLayout = new BorderPane();
        mainLayout.setStyle("-fx-background-color: #f8fafc;"); // Light slate canvas

        // ==========================================
        // 1. TOP ROUTING HEADER
        // ==========================================
        HBox topHeader = new HBox(30);
        topHeader.setPadding(new Insets(15, 30, 15, 30));
        topHeader.setAlignment(Pos.CENTER_LEFT);
        topHeader.setStyle("-fx-background-color: #0f172a; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 10, 0, 0, 4);");

        Label titleLabel = new Label("L&F PORTAL");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
        titleLabel.setStyle("-fx-text-fill: #3b82f6; -fx-letter-spacing: 1.5px;");

        // Routing Navigation Buttons
        HBox navigationBox = new HBox(10);
        navigationBox.setAlignment(Pos.CENTER_LEFT);

        btnOverview = createNavButton("Overview");
        btnLog = createNavButton("Log New Asset");
        btnSearch = createNavButton("Search Database");

        navigationBox.getChildren().addAll(btnOverview, btnLog, btnSearch);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label adminProfile = new Label("Admin Active ⚙");
        adminProfile.setStyle("-fx-text-fill: #94a3b8; -fx-font-weight: bold; -fx-font-size: 13px;");
        
        topHeader.getChildren().addAll(titleLabel, navigationBox, spacer, adminProfile);
        mainLayout.setTop(topHeader);

        // ==========================================
        // 2. ROUTING LOGIC (Event Handlers)
        // ==========================================
        btnOverview.setOnAction(e -> switchView("overview"));
        btnLog.setOnAction(e -> switchView("log"));
        btnSearch.setOnAction(e -> switchView("search"));

        // Default view on login entry
        switchView("overview");

        // Build Scene
        Scene scene = new Scene(mainLayout, 1100, 700);
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }

    /**
     * View Router Core
     */
    private void switchView(String viewName) {
        // Reset navigation styles
        resetNavStyles();

        // Load targeted view layout into center screen pane
        switch (viewName.toLowerCase()) {
            case "overview":
                setActiveNavStyle(btnOverview);
                mainLayout.setCenter(createOverviewView());
                break;
            case "log":
                setActiveNavStyle(btnLog);
                mainLayout.setCenter(createLogView());
                break;
            case "search":
                setActiveNavStyle(btnSearch);
                mainLayout.setCenter(createSearchView());
                break;
        }
    }

    // ==========================================
    // VIEW BUILDER 1: Overview (Visual Analytics)
    // ==========================================
    private VBox createOverviewView() {
        VBox root = new VBox(25);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.TOP_CENTER);

        Label viewTitle = new Label("System Analytics Overview");
        viewTitle.setFont(Font.font("System", FontWeight.BOLD, 22));
        viewTitle.setStyle("-fx-text-fill: #1e293b;");
        viewTitle.setAlignment(Pos.CENTER_LEFT);
        viewTitle.setMaxWidth(Double.MAX_VALUE);

        // Circle Visualizer Component (JavaFX PieChart)
        // Values are placeholders; they will connect to SQLite counts later!
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("Total Records (12)", 12),
                new PieChart.Data("Active Lost (4)", 4),
                new PieChart.Data("Active Found (5)", 5),
                new PieChart.Data("Success Claimed (3)", 3)
        );

        PieChart circleVisualizer = new PieChart(pieChartData);
        circleVisualizer.setTitle("Asset Distribution Matrix");
        circleVisualizer.setLabelsVisible(true);
        circleVisualizer.setLegendVisible(true);
        circleVisualizer.setPrefSize(450, 450);
        circleVisualizer.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-padding: 20; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.03), 8, 0, 0, 2);");

        // Clean breakdown card summary wrapper
        HBox statsGrid = new HBox(20);
        statsGrid.setAlignment(Pos.CENTER);
        statsGrid.getChildren().addAll(
                createMiniStatCard("TOTAL RECORDS", "12", "#3b82f6"),
                createMiniStatCard("ACTIVE LOST", "4", "#ef4444"),
                createMiniStatCard("ACTIVE FOUND", "5", "#f59e0b"),
                createMiniStatCard("CLAIMED", "3", "#10b981")
        );

        root.getChildren().addAll(viewTitle, circleVisualizer, statsGrid);
        return root;
    }

    // ==========================================
    // VIEW BUILDER 2: Log Asset Form
    // ==========================================
    private VBox createLogView() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        root.setMaxWidth(500); // Keep form centered and bounded nicely
        root.setAlignment(Pos.TOP_LEFT);

        Label viewTitle = new Label("Registry: Log New Asset");
        viewTitle.setFont(Font.font("System", FontWeight.BOLD, 22));
        viewTitle.setStyle("-fx-text-fill: #1e293b;");

        VBox formCard = new VBox(15);
        formCard.setPadding(new Insets(30));
        formCard.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-border-color: #e2e8f0; -fx-border-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.02), 10, 0, 0, 4);");

        TextField nameField = createModernTextField("Item Name");
        
        ComboBox<String> categoryBox = new ComboBox<>();
        categoryBox.getItems().addAll("Electronics", "Keys", "Wallets/Bags", "Clothing", "Other");
        categoryBox.setPromptText("Select Category");
        categoryBox.setMaxWidth(Double.MAX_VALUE);
        categoryBox.setStyle("-fx-background-color: #f1f5f9; -fx-border-color: #cbd5e1; -fx-border-radius: 4; -fx-padding: 6;");

        TextArea descArea = new TextArea();
        descArea.setPromptText("Item Description details...");
        descArea.setPrefHeight(100);
        descArea.setWrapText(true);
        descArea.setStyle("-fx-control-inner-background: #f1f5f9; -fx-border-color: #cbd5e1; -fx-border-radius: 4;");

        ComboBox<String> statusBox = new ComboBox<>();
        statusBox.getItems().addAll("Lost", "Found", "Claimed");
        statusBox.setValue("Lost");
        statusBox.setMaxWidth(Double.MAX_VALUE);
        statusBox.setStyle("-fx-background-color: #f1f5f9; -fx-border-color: #cbd5e1; -fx-border-radius: 4; -fx-padding: 6;");

        Button uploadImgBtn = new Button("📸 Attach Image Reference File");
        uploadImgBtn.setMaxWidth(Double.MAX_VALUE);
        uploadImgBtn.setStyle("-fx-background-color: #475569; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10; -fx-background-radius: 4; -fx-cursor: hand;");
        
        Label imgPathLabel = new Label("No image attached");
        imgPathLabel.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 11px; -fx-font-style: italic;");

        Button saveButton = new Button("Commit Record to SQLite");
        saveButton.setMaxWidth(Double.MAX_VALUE);
        saveButton.setStyle("-fx-background-color: #2563eb; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 12; -fx-background-radius: 4; -fx-cursor: hand;");

        formCard.getChildren().addAll(nameField, categoryBox, descArea, statusBox, uploadImgBtn, imgPathLabel, saveButton);
        root.getChildren().addAll(viewTitle, formCard);
        return root;
    }

    // ==========================================
    // VIEW BUILDER 3: Search Engine Database
    // ==========================================
    private VBox createSearchView() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(30));

        Label viewTitle = new Label("Query Engine: Asset Database Search");
        viewTitle.setFont(Font.font("System", FontWeight.BOLD, 22));
        viewTitle.setStyle("-fx-text-fill: #1e293b;");

        HBox filterBar = new HBox(12);
        filterBar.setAlignment(Pos.CENTER_LEFT);
        
        TextField searchField = createModernTextField("🔍 Enter tracking keywords, categories or descriptions...");
        searchField.setPrefWidth(550);
        
        Button searchBtn = new Button("Filter Rows");
        searchBtn.setStyle("-fx-background-color: #0f172a; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 22; -fx-background-radius: 4; -fx-cursor: hand;");
        filterBar.getChildren().addAll(searchField, searchBtn);

        TableView<Object> tableView = new TableView<>();
        VBox.setVgrow(tableView, Priority.ALWAYS);
        tableView.setStyle("-fx-background-color: white; -fx-border-color: #e2e8f0; -fx-border-radius: 6;");
        
        TableColumn<Object, String> colName = new TableColumn<>("Item Name");
        colName.setPrefWidth(220);
        TableColumn<Object, String> colCategory = new TableColumn<>("Category");
        colCategory.setPrefWidth(150);
        TableColumn<Object, String> colStatus = new TableColumn<>("Status");
        colStatus.setPrefWidth(140);
        TableColumn<Object, String> colDate = new TableColumn<>("Date Logged");
        colDate.setPrefWidth(200);
        
        tableView.getColumns().addAll(colName, colCategory, colStatus, colDate);
        tableView.setPlaceholder(new Label("Query returned 0 matches from storage."));

        root.getChildren().addAll(viewTitle, filterBar, tableView);
        return root;
    }

    // ==========================================
    // STYLING HELPERS
    // ==========================================
    private Button createNavButton(String title) {
        Button btn = new Button(title);
        btn.setFont(Font.font("System", FontWeight.BOLD, 13));
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #94a3b8; -fx-padding: 8 16; -fx-background-radius: 4; -fx-cursor: hand;");
        
        // Hover reactions
        btn.setOnMouseEntered(e -> {
            if (!btn.getStyle().contains("#f8fafc")) { // Only change color if it's not the active page
                btn.setStyle("-fx-background-color: #1e293b; -fx-text-fill: #cbd5e1; -fx-padding: 8 16; -fx-background-radius: 4; -fx-cursor: hand;");
            }
        });
        btn.setOnMouseExited(e -> {
            if (!btn.getStyle().contains("#f8fafc")) {
                btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #94a3b8; -fx-padding: 8 16; -fx-background-radius: 4; -fx-cursor: hand;");
            }
        });
        return btn;
    }

    private void resetNavStyles() {
        String baseStyle = "-fx-background-color: transparent; -fx-text-fill: #94a3b8; -fx-padding: 8 16; -fx-background-radius: 4; -fx-cursor: hand;";
        btnOverview.setStyle(baseStyle);
        btnLog.setStyle(baseStyle);
        btnSearch.setStyle(baseStyle);
    }

    private void setActiveNavStyle(Button activeBtn) {
        activeBtn.setStyle("-fx-background-color: #2563eb; -fx-text-fill: #f8fafc; -fx-padding: 8 16; -fx-background-radius: 4; -fx-cursor: hand;");
    }

    private TextField createModernTextField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setStyle("-fx-background-color: #f1f5f9; -fx-border-color: #cbd5e1; -fx-border-radius: 4; -fx-padding: 8; -fx-text-fill: #1e293b;");
        return tf;
    }

    private VBox createMiniStatCard(String title, String value, String colorHex) {
        VBox card = new VBox(2);
        card.setPadding(new Insets(10, 20, 10, 20));
        card.setMinWidth(160);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 6; -fx-border-color: " + colorHex + "; -fx-border-width: 0 0 3 0; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.02), 6, 0, 0, 2);");

        Label titleLbl = new Label(title);
        titleLbl.setStyle("-fx-text-fill: #64748b; -fx-font-size: 10px; -fx-font-weight: bold;");
        Label valLbl = new Label(value);
        valLbl.setFont(Font.font("System", FontWeight.BOLD, 20));
        valLbl.setStyle("-fx-text-fill: #1e293b;");

        card.getChildren().addAll(titleLbl, valLbl);
        return card;
    }
}