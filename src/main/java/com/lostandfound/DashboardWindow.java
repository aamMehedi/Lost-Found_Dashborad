package com.lostandfound;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class DashboardWindow {

    private BorderPane mainLayout;
    private Button btnOverview, btnLog, btnSearch, btnDataLogs;
    private String selectedImagePath = "";
    private Stage primaryStage;

    public void showDashboard(Stage stage) {
        this.primaryStage = stage;
        stage.setTitle("Lost & Found Admin Dashboard");

        mainLayout = new BorderPane();
        mainLayout.setStyle("-fx-background-color: #f8fafc;");

        // TOP ROUTING HEADER
        HBox topHeader = new HBox(25);
        topHeader.setPadding(new Insets(15, 30, 15, 30));
        topHeader.setAlignment(Pos.CENTER_LEFT);
        topHeader.setStyle("-fx-background-color: #0f172a; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 10, 0, 0, 4);");

        Label titleLabel = new Label("L&F PORTAL");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
        titleLabel.setStyle("-fx-text-fill: #3b82f6; -fx-letter-spacing: 1.5px;");

        HBox navigationBox = new HBox(8);
        navigationBox.setAlignment(Pos.CENTER_LEFT);
        btnOverview = createNavButton("Overview");
        btnLog = createNavButton("Log New Asset");
        btnSearch = createNavButton("Search Catalog");
        btnDataLogs = createNavButton("Data Logs");
        navigationBox.getChildren().addAll(btnOverview, btnLog, btnSearch, btnDataLogs);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label adminProfile = new Label("Admin Active ⚙");
        adminProfile.setStyle("-fx-text-fill: #94a3b8; -fx-font-weight: bold; -fx-font-size: 13px;");
        
        topHeader.getChildren().addAll(titleLabel, navigationBox, spacer, adminProfile);
        mainLayout.setTop(topHeader);

        // ROUTING CONTROLS
        btnOverview.setOnAction(e -> switchView("overview"));
        btnLog.setOnAction(e -> switchView("log"));
        btnSearch.setOnAction(e -> switchView("search"));
        btnDataLogs.setOnAction(e -> switchView("datalogs"));

        switchView("overview");

        Scene scene = new Scene(mainLayout, 1180, 740);
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }

    private void switchView(String viewName) {
        resetNavStyles();
        switch (viewName.toLowerCase()) {
            case "overview":
                setActiveNavStyle(btnOverview);
                mainLayout.setCenter(createOverviewView());
                break;
            case "log":
                setActiveNavStyle(btnLog);
                mainLayout.setCenter(createLogView(primaryStage));
                break;
            case "search":
                setActiveNavStyle(btnSearch);
                mainLayout.setCenter(createSearchView());
                break;
            case "datalogs":
                setActiveNavStyle(btnDataLogs);
                mainLayout.setCenter(createDataLogsView());
                break;
        }
    }

    // ==========================================
    // VIEW 1: ANALYTICS OVERVIEW
    // ==========================================
    private VBox createOverviewView() {
        VBox root = new VBox(25);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.TOP_CENTER);

        Label viewTitle = new Label("System Analytics Overview");
        viewTitle.setFont(Font.font("System", FontWeight.BOLD, 22));
        viewTitle.setStyle("-fx-text-fill: #1e293b;");
        viewTitle.setMaxWidth(Double.MAX_VALUE);

        int total = DatabaseHandler.getCountByStatus("TOTAL");
        int lost = DatabaseHandler.getCountByStatus("Lost");
        int found = DatabaseHandler.getCountByStatus("Found");
        int claimed = DatabaseHandler.getCountByStatus("Claimed");
        int dustyCount = DatabaseHandler.getDustyItemCount();

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("Active Lost (" + lost + ")", lost),
                new PieChart.Data("Active Found (" + found + ")", found),
                new PieChart.Data("Success Claimed (" + claimed + ")", claimed)
        );

        PieChart circleVisualizer = new PieChart(pieChartData);
        circleVisualizer.setTitle("Asset Distribution Matrix");
        circleVisualizer.setPrefSize(380, 380);
        circleVisualizer.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-padding: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.02), 8, 0, 0, 2);");

        HBox statsGrid = new HBox(15);
        statsGrid.setAlignment(Pos.CENTER);
        statsGrid.getChildren().addAll(
                createMiniStatCard("TOTAL RECORDS", String.valueOf(total), "#3b82f6"),
                createMiniStatCard("ACTIVE LOST", String.valueOf(lost), "#ef4444"),
                createMiniStatCard("ACTIVE FOUND", String.valueOf(found), "#f59e0b"),
                createMiniStatCard("CLAIMED", String.valueOf(claimed), "#10b981"),
                createMiniStatCard("DUSTY (30+ DAYS)", String.valueOf(dustyCount), "#dc2626")
        );

        root.getChildren().addAll(viewTitle, circleVisualizer, statsGrid);
        return root;
    }

    // ==========================================
    // VIEW 2: LOG NEW ASSET FORM
    // ==========================================
    private VBox createLogView(Stage stage) {
        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        root.setMaxWidth(500);
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

        selectedImagePath = "";
        Button uploadImgBtn = new Button("📸 Attach Image Reference File");
        uploadImgBtn.setMaxWidth(Double.MAX_VALUE);
        uploadImgBtn.setStyle("-fx-background-color: #475569; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10; -fx-background-radius: 4; -fx-cursor: hand;");
        
        Label imgPathLabel = new Label("No image attached");
        imgPathLabel.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 11px; -fx-font-style: italic;");

        uploadImgBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Image Reference File");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
            );
            File file = fileChooser.showOpenDialog(stage);
            if (file != null) {
                selectedImagePath = file.getAbsolutePath();
                imgPathLabel.setText("Attached: " + file.getName());
                imgPathLabel.setStyle("-fx-text-fill: #10b981; -fx-font-size: 11px; -fx-font-weight: bold;");
            }
        });

        Button saveButton = new Button("Commit Record to SQLite");
        saveButton.setMaxWidth(Double.MAX_VALUE);
        saveButton.setStyle("-fx-background-color: #2563eb; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 12; -fx-background-radius: 4; -fx-cursor: hand;");

        saveButton.setOnAction(e -> {
            String name = nameField.getText();
            String category = categoryBox.getValue();
            String desc = descArea.getText();
            String status = statusBox.getValue();
            String dateToday = LocalDate.now().toString();

            if (name.isEmpty() || category == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Item Name and Category fields are mandatory.");
                alert.showAndWait();
                return;
            }

            boolean success = DatabaseHandler.insertItem(name, category, desc, status, dateToday, selectedImagePath);
            if (success) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Asset recorded successfully into transaction history.");
                alert.showAndWait();
                
                nameField.clear();
                categoryBox.setValue(null);
                descArea.clear();
                statusBox.setValue("Lost");
                imgPathLabel.setText("No image attached");
                imgPathLabel.setStyle("-fx-text-fill: #94a3b8; -fx-font-style: italic;");
                selectedImagePath = "";
            }
        });

        formCard.getChildren().addAll(nameField, categoryBox, descArea, statusBox, uploadImgBtn, imgPathLabel, saveButton);
        root.getChildren().addAll(viewTitle, formCard);
        return root;
    }

    // ==========================================
    // VIEW 3: SEARCH CATALOG WITH FILTER DROPDOWNS
    // ==========================================
    private VBox createSearchView() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(30));

        Label viewTitle = new Label("Interactive Asset Search Catalog");
        viewTitle.setFont(Font.font("System", FontWeight.BOLD, 22));
        viewTitle.setStyle("-fx-text-fill: #1e293b;");

        // SEARCH & FILTER BAR
        HBox filterBar = new HBox(12);
        filterBar.setAlignment(Pos.CENTER_LEFT);

        TextField searchField = createModernTextField("🔍 Search name or description...");
        searchField.setPrefWidth(350);

        ComboBox<String> statusFilter = new ComboBox<>();
        statusFilter.getItems().addAll("All Statuses", "Lost", "Found", "Claimed", "🧹 Dusty Only");
        statusFilter.setValue("All Statuses");
        statusFilter.setStyle("-fx-background-color: #f1f5f9; -fx-border-color: #cbd5e1; -fx-border-radius: 4; -fx-padding: 8;");

        ComboBox<String> categoryFilter = new ComboBox<>();
        categoryFilter.getItems().addAll("All Categories", "Electronics", "Keys", "Wallets/Bags", "Clothing", "Other");
        categoryFilter.setValue("All Categories");
        categoryFilter.setStyle("-fx-background-color: #f1f5f9; -fx-border-color: #cbd5e1; -fx-border-radius: 4; -fx-padding: 8;");

        filterBar.getChildren().addAll(searchField, statusFilter, categoryFilter);

        FlowPane cardGrid = new FlowPane();
        cardGrid.setHgap(20);
        cardGrid.setVgap(20);
        cardGrid.setPadding(new Insets(10));

        ScrollPane scrollPane = new ScrollPane(cardGrid);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-border-color: transparent;");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        List<Item> allItems = DatabaseHandler.getAllItems();

        // Multi-Filter Logic
        Runnable applyFilters = () -> {
            String query = searchField.getText() == null ? "" : searchField.getText().toLowerCase();
            String selectedStatus = statusFilter.getValue();
            String selectedCat = categoryFilter.getValue();

            List<Item> filtered = allItems.stream().filter(item -> {
                boolean matchesQuery = query.isEmpty() ||
                        item.getName().toLowerCase().contains(query) ||
                        (item.getDescription() != null && item.getDescription().toLowerCase().contains(query));

                boolean matchesStatus = "All Statuses".equals(selectedStatus) ||
                        ("🧹 Dusty Only".equals(selectedStatus) ? item.isDusty() : item.getStatus().equalsIgnoreCase(selectedStatus));

                boolean matchesCat = "All Categories".equals(selectedCat) || item.getCategory().equalsIgnoreCase(selectedCat);

                return matchesQuery && matchesStatus && matchesCat;
            }).toList();

            renderItemCards(cardGrid, filtered);
        };

        searchField.textProperty().addListener((obs, oldV, newV) -> applyFilters.run());
        statusFilter.setOnAction(e -> applyFilters.run());
        categoryFilter.setOnAction(e -> applyFilters.run());

        // Initial Render
        applyFilters.run();

        root.getChildren().addAll(viewTitle, filterBar, scrollPane);
        return root;
    }

    private void renderItemCards(FlowPane container, List<Item> items) {
        container.getChildren().clear();

        if (items.isEmpty()) {
            Label emptyLabel = new Label("No matching items found.");
            emptyLabel.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 14px; -fx-font-style: italic;");
            container.getChildren().add(emptyLabel);
            return;
        }

        for (Item item : items) {
            container.getChildren().add(createItemCard(item));
        }
    }

    private VBox createItemCard(Item item) {
        VBox card = new VBox(8);
        card.setPrefWidth(260);
        card.setMaxWidth(260);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; "
                + "-fx-border-color: #e2e8f0; -fx-border-radius: 10; "
                + "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.04), 8, 0, 0, 3);");

        StackPane imageFrame = new StackPane();
        imageFrame.setPrefSize(230, 130);
        imageFrame.setStyle("-fx-background-color: #f1f5f9; -fx-background-radius: 6;");

        ImageView imageView = new ImageView();
        imageView.setFitWidth(230);
        imageView.setFitHeight(130);
        imageView.setPreserveRatio(true);

        if (item.getImagePath() != null && !item.getImagePath().trim().isEmpty()) {
            File imgFile = new File(item.getImagePath());
            if (imgFile.exists()) {
                imageView.setImage(new Image(imgFile.toURI().toString()));
                imageFrame.getChildren().add(imageView);
            } else {
                Label noImg = new Label("🖼 Image Not Found");
                noImg.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 11px;");
                imageFrame.getChildren().add(noImg);
            }
        } else {
            Label noImg = new Label("📷 No Image Attached");
            noImg.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 11px;");
            imageFrame.getChildren().add(noImg);
        }

        Label nameLabel = new Label(item.getName());
        nameLabel.setFont(Font.font("System", FontWeight.BOLD, 15));
        nameLabel.setStyle("-fx-text-fill: #0f172a;");
        nameLabel.setWrapText(true);

        HBox badgeBox = new HBox(8);
        Label categoryBadge = new Label(item.getCategory());
        categoryBadge.setStyle("-fx-background-color: #e0f2fe; -fx-text-fill: #0369a1; -fx-padding: 2 6; -fx-background-radius: 4; -fx-font-size: 10px; -fx-font-weight: bold;");
        
        Label statusBadge = new Label(item.getStatus());
        String statusColor = "Claimed".equalsIgnoreCase(item.getStatus()) ? "-fx-background-color: #d1fae5; -fx-text-fill: #047857;" :
                             "Found".equalsIgnoreCase(item.getStatus()) ? "-fx-background-color: #fef3c7; -fx-text-fill: #b45309;" :
                             "-fx-background-color: #fee2e2; -fx-text-fill: #b91c1c;";
        statusBadge.setStyle(statusColor + " -fx-padding: 2 6; -fx-background-radius: 4; -fx-font-size: 10px; -fx-font-weight: bold;");
        badgeBox.getChildren().addAll(categoryBadge, statusBadge);

        String descText = (item.getDescription() == null || item.getDescription().trim().isEmpty()) ? "No details supplied." : item.getDescription();
        Label descLabel = new Label(descText);
        descLabel.setWrapText(true);
        descLabel.setMaxHeight(36);
        descLabel.setStyle("-fx-text-fill: #64748b; -fx-font-size: 12px;");

        Label ageLabel = new Label(item.getAgingBadge());
        ageLabel.setStyle("-fx-text-fill: " + (item.isDusty() ? "#dc2626;" : "#64748b;") + " -fx-font-weight: bold; -fx-font-size: 11px;");

        Label dateLabel = new Label("📅 Logged: " + item.getDateReported());
        dateLabel.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 11px; -fx-font-style: italic;");

        // DYNAMIC ACTION & DELETE BUTTONS
        HBox actionBox = new HBox(6);
        actionBox.setPadding(new Insets(5, 0, 0, 0));

        if (!"Found".equalsIgnoreCase(item.getStatus()) && !"Claimed".equalsIgnoreCase(item.getStatus())) {
            Button btnFound = new Button("Found 🔍");
            btnFound.setStyle("-fx-background-color: #f59e0b; -fx-text-fill: white; -fx-font-size: 10px; -fx-font-weight: bold; -fx-cursor: hand;");
            btnFound.setOnAction(e -> {
                DatabaseHandler.updateItemStatus(item.getId(), "Found");
                switchView("search");
            });
            actionBox.getChildren().add(btnFound);
        }

        if (!"Claimed".equalsIgnoreCase(item.getStatus())) {
            Button btnClaimed = new Button("Claimed ✅");
            btnClaimed.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-font-size: 10px; -fx-font-weight: bold; -fx-cursor: hand;");
            btnClaimed.setOnAction(e -> {
                DatabaseHandler.updateItemStatus(item.getId(), "Claimed");
                switchView("search");
            });
            actionBox.getChildren().add(btnClaimed);
        }

        // 🗑️ DELETE BUTTON
        Button btnDelete = new Button("🗑️");
        btnDelete.setTooltip(new Tooltip("Delete item record permanently"));
        btnDelete.setStyle("-fx-background-color: #fee2e2; -fx-text-fill: #dc2626; -fx-font-size: 10px; -fx-font-weight: bold; -fx-cursor: hand;");
        btnDelete.setOnAction(e -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete '" + item.getName() + "'?", ButtonType.YES, ButtonType.NO);
            Optional<ButtonType> result = confirm.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.YES) {
                DatabaseHandler.deleteItem(item.getId());
                switchView("search");
            }
        });
        actionBox.getChildren().add(btnDelete);

        card.getChildren().addAll(imageFrame, nameLabel, badgeBox, descLabel, ageLabel, dateLabel, actionBox);
        return card;
    }

    // ==========================================
    // VIEW 4: DATA LOGS TABULAR VIEW
    // ==========================================
    private VBox createDataLogsView() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(30));

        Label viewTitle = new Label("System Data Logs Ledger");
        viewTitle.setFont(Font.font("System", FontWeight.BOLD, 22));
        viewTitle.setStyle("-fx-text-fill: #1e293b;");

        TableView<Item> tableView = new TableView<>();
        VBox.setVgrow(tableView, Priority.ALWAYS);
        tableView.setStyle("-fx-background-color: white; -fx-border-color: #e2e8f0; -fx-border-radius: 6;");

        TableColumn<Item, String> colName = new TableColumn<>("Item Name");
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colName.setPrefWidth(160);

        TableColumn<Item, String> colCategory = new TableColumn<>("Category");
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colCategory.setPrefWidth(120);

        TableColumn<Item, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colStatus.setPrefWidth(90);

        TableColumn<Item, String> colDate = new TableColumn<>("Date Logged");
        colDate.setCellValueFactory(new PropertyValueFactory<>("dateReported"));
        colDate.setPrefWidth(120);

        TableColumn<Item, String> colAging = new TableColumn<>("Retention / Age");
        colAging.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAgingBadge()));
        colAging.setPrefWidth(140);

        // ACTION COLUMN WITH DELETE
        TableColumn<Item, Void> colActions = new TableColumn<>("Quick Actions");
        colActions.setPrefWidth(220);
        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button btnFound = new Button("Found");
            private final Button btnClaim = new Button("Claimed");
            private final Button btnDel = new Button("🗑️");
            private final HBox pane = new HBox(5, btnFound, btnClaim, btnDel);

            {
                btnFound.setStyle("-fx-background-color: #f59e0b; -fx-text-fill: white; -fx-font-size: 10px; -fx-font-weight: bold; -fx-cursor: hand;");
                btnClaim.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-font-size: 10px; -fx-font-weight: bold; -fx-cursor: hand;");
                btnDel.setStyle("-fx-background-color: #fee2e2; -fx-text-fill: #dc2626; -fx-font-size: 10px; -fx-font-weight: bold; -fx-cursor: hand;");

                btnFound.setOnAction(e -> {
                    Item item = getTableView().getItems().get(getIndex());
                    DatabaseHandler.updateItemStatus(item.getId(), "Found");
                    switchView("datalogs");
                });

                btnClaim.setOnAction(e -> {
                    Item item = getTableView().getItems().get(getIndex());
                    DatabaseHandler.updateItemStatus(item.getId(), "Claimed");
                    switchView("datalogs");
                });

                btnDel.setOnAction(e -> {
                    Item item = getTableView().getItems().get(getIndex());
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Delete '" + item.getName() + "'?", ButtonType.YES, ButtonType.NO);
                    Optional<ButtonType> res = confirm.showAndWait();
                    if (res.isPresent() && res.get() == ButtonType.YES) {
                        DatabaseHandler.deleteItem(item.getId());
                        switchView("datalogs");
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) setGraphic(null);
                else setGraphic(pane);
            }
        });

        tableView.getColumns().addAll(colName, colCategory, colStatus, colDate, colAging, colActions);
        tableView.setPlaceholder(new Label("No raw log items stored in system database."));

        List<Item> databaseRecords = DatabaseHandler.getAllItems();
        ObservableList<Item> observableList = FXCollections.observableArrayList(databaseRecords);
        tableView.setItems(observableList);

        root.getChildren().addAll(viewTitle, tableView);
        return root;
    }

    // ==========================================
    // STYLING HELPERS
    // ==========================================
    private Button createNavButton(String title) {
        Button btn = new Button(title);
        btn.setFont(Font.font("System", FontWeight.BOLD, 13));
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #94a3b8; -fx-padding: 8 16; -fx-background-radius: 4; -fx-cursor: hand;");
        return btn;
    }

    private void resetNavStyles() {
        String baseStyle = "-fx-background-color: transparent; -fx-text-fill: #94a3b8; -fx-padding: 8 16; -fx-background-radius: 4; -fx-cursor: hand;";
        btnOverview.setStyle(baseStyle);
        btnLog.setStyle(baseStyle);
        btnSearch.setStyle(baseStyle);
        btnDataLogs.setStyle(baseStyle);
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
        card.setPadding(new Insets(10, 16, 10, 16));
        card.setMinWidth(140);
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