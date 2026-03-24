package main;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.Order;
import model.OrderStatus;
import service.DeliveryService;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

public class Main extends Application {

    TableView<Order> table = new TableView<>();
    ComboBox<String> agentBox = new ComboBox<>(); // ✅ GLOBAL FIX
    Label revenueLabel = new Label("Revenue: ₹0");
    double totalRevenue = 0;

    @Override
    public void start(Stage stage) {

        TabPane tabs = new TabPane();

        tabs.getTabs().add(new Tab("Orders", createOrderUI()));
        tabs.getTabs().add(new Tab("Agents", createAgentUI()));

        Scene scene = new Scene(tabs, 900, 500);

        // 🌙 DARK THEME
        scene.getRoot().setStyle(
                "-fx-base: #1e1e1e;" +
                "-fx-background-color: #1e1e1e;" +
                "-fx-control-inner-background: #2b2b2b;" +
                "-fx-text-fill: white;"
        );

        stage.setTitle("Food Delivery Management System");
        stage.setScene(scene);
        stage.show();

        startAutoUpdate();
    }

    // ================= ORDER UI =================
    private VBox createOrderUI() {

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);

        TextField name = new TextField();
        TextField address = new TextField();
        TextField amount = new TextField();

        form.add(new Label("Name"), 0, 0);
        form.add(name, 1, 0);

        form.add(new Label("Address"), 0, 1);
        form.add(address, 1, 1);

        form.add(new Label("Amount"), 0, 2);
        form.add(amount, 1, 2);

        Button addBtn = new Button("Create Order");
        addBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");

        // ✅ CREATE ORDER
        addBtn.setOnAction(e -> {
            if (name.getText().isEmpty() || address.getText().isEmpty() || amount.getText().isEmpty()) {
                new Alert(Alert.AlertType.ERROR, "Fill all fields!").show();
                return;
            }

            Order o = DeliveryService.createOrder(
                    name.getText(),
                    address.getText(),
                    Double.parseDouble(amount.getText())
            );

            table.getItems().add(o);

            name.clear();
            address.clear();
            amount.clear();
        });

        // ✅ INITIAL LOAD
        refreshAgentBox();

        Button assignBtn = new Button("Assign Agent");
        assignBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");

        assignBtn.setOnAction(e -> {
            Order selected = table.getSelectionModel().getSelectedItem();

            if (selected == null) {
                new Alert(Alert.AlertType.ERROR, "Select an order!").show();
                return;
            }

            if (agentBox.getValue() == null) {
                new Alert(Alert.AlertType.ERROR, "Select an agent!").show();
                return;
            }

            DeliveryService.assignAgent(selected, agentBox.getValue());
            table.refresh();

            new Alert(Alert.AlertType.INFORMATION, "Agent Assigned!").show();
        });

        // ================= FILTER =================
        HBox filterBox = new HBox(10);

        Button allBtn = new Button("All");
        Button pendingBtn = new Button("Pending");
        Button deliveredBtn = new Button("Delivered");

        filterBox.getChildren().addAll(allBtn, pendingBtn, deliveredBtn);

        allBtn.setOnAction(e -> table.getItems().setAll(DeliveryService.getOrders()));

        pendingBtn.setOnAction(e -> table.getItems().setAll(
                DeliveryService.getOrders().stream()
                        .filter(o -> o.getStatus() != OrderStatus.DELIVERED)
                        .toList()
        ));

        deliveredBtn.setOnAction(e -> table.getItems().setAll(
                DeliveryService.getOrders().stream()
                        .filter(o -> o.getStatus() == OrderStatus.DELIVERED)
                        .toList()
        ));

        // ================= TABLE =================
        TableColumn<Order, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(d ->
                new javafx.beans.property.SimpleIntegerProperty(d.getValue().getOrderId()).asObject()
        );

        TableColumn<Order, String> nameCol = new TableColumn<>("Customer");
        nameCol.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(d.getValue().getCustomerName())
        );

        TableColumn<Order, String> agentCol = new TableColumn<>("Agent");
        agentCol.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(
                        d.getValue().getAssignedAgent() == null ? "Not Assigned" : d.getValue().getAssignedAgent()
                )
        );

        TableColumn<Order, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(d.getValue().getStatus().toString())
        );

        statusCol.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);

                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status);

                    switch (status) {
                        case "PENDING": setStyle("-fx-text-fill: orange;"); break;
                        case "PREPARING": setStyle("-fx-text-fill: blue;"); break;
                        case "OUT_FOR_DELIVERY": setStyle("-fx-text-fill: purple;"); break;
                        case "DELIVERED": setStyle("-fx-text-fill: green;"); break;
                    }
                }
            }
        });

        table.getColumns().clear();
        table.getColumns().addAll(idCol, nameCol, agentCol, statusCol);

        revenueLabel.setStyle("-fx-text-fill: #00FFAA; -fx-font-size: 16px;");

        return new VBox(15,
                revenueLabel,
                form,
                addBtn,
                agentBox,
                assignBtn,
                filterBox,
                table
        );
    }

    // ================= AGENT UI =================
    private VBox createAgentUI() {

        Label label = new Label("Agent Name:");
        TextField agentName = new TextField();
        agentName.setPromptText("Enter agent name here...");

        Button addBtn = new Button("Add Agent");
        ListView<String> agentList = new ListView<>();

        addBtn.setOnAction(e -> {
            if (agentName.getText().isEmpty()) {
                new Alert(Alert.AlertType.ERROR, "Enter agent name!").show();
                return;
            }

            String name = agentName.getText();

            DeliveryService.addAgent(name);
            agentList.getItems().add(name);

            // 🔥 FIX: update dropdown
            refreshAgentBox();

            agentName.clear();
        });

        return new VBox(15, label, agentName, addBtn, agentList);
    }

    // ✅ FIXED METHOD
    private void refreshAgentBox() {
        agentBox.getItems().clear();
        for (var a : DeliveryService.getAgents()) {
            agentBox.getItems().add(a.getName());
        }
    }

    // ================= AUTO =================
    private void startAutoUpdate() {

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(5), e -> {

                    for (Order o : DeliveryService.getOrders()) {

                        if (o.getStatus() != OrderStatus.DELIVERED) {
                            DeliveryService.updateStatus(o);
                        }

                        if (o.getStatus() == OrderStatus.DELIVERED && !o.isCounted()) {
                            totalRevenue += o.getAmount();
                            o.setCounted(true);
                        }
                    }

                    revenueLabel.setText("Revenue: ₹" + totalRevenue);
                    table.refresh();
                })
        );

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    public static void main(String[] args) {
        launch(args);
    }
}