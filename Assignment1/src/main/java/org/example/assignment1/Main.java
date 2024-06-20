package org.example.assignment1;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Main extends Application {
    private Scene chartScene, tableScene;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Economic Status of 6 Months of Swiss Chalet");

        // Create a bar chart and a button to switch to the table view
        BarChart<String, Number> barChart = createBarChart();
        Button toTableViewButton = createButton("Click to switch Table View", e -> primaryStage.setScene(tableScene));
        VBox chartLayout = createVBox(Pos.CENTER, 10, barChart, toTableViewButton);
        chartScene = new Scene(chartLayout, 800, 600);

        // Create a table view and a button to switch to the bar chart view
        TableView<MonthIncome> tableView = createTableView();
        Button toChartViewButton = createButton("Click to switch to Chart View", e -> primaryStage.setScene(chartScene));
        VBox tableLayout = createVBox(Pos.CENTER, 10, tableView, toChartViewButton);
        tableScene = new Scene(tableLayout, 800, 600);

        // Set the initial scene to the bar chart view
        primaryStage.setScene(chartScene);
        primaryStage.show();
    }

    // Method to create the bar chart
    private BarChart<String, Number> createBarChart() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Months");
        yAxis.setLabel("Income");

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setData(getChartData());
        return barChart;
    }

    // Method to create the table view
    private TableView<MonthIncome> createTableView() {
        TableView<MonthIncome> tableView = new TableView<>(getMonthIncomeData());
        TableColumn<MonthIncome, String> monthColumn = new TableColumn<>("Month");
        monthColumn.setCellValueFactory(new PropertyValueFactory<>("month"));
        TableColumn<MonthIncome, Integer> incomeColumn = new TableColumn<>("Income");
        incomeColumn.setCellValueFactory(new PropertyValueFactory<>("income"));
        tableView.getColumns().addAll(monthColumn, incomeColumn);
        return tableView;
    }

    // Method to create a button with a specified text and event handler
    private Button createButton(String text, javafx.event.EventHandler<javafx.event.ActionEvent> eventHandler) {
        Button button = new Button(text);
        button.setOnAction(eventHandler);
        return button;
    }

    // Method to create a VBox layout with specified alignment, spacing, and children nodes
    private VBox createVBox(Pos alignment, double spacing, javafx.scene.Node... children) {
        VBox vbox = new VBox(spacing, children);
        vbox.setAlignment(alignment);
        return vbox;
    }

    // Method to fetch data for the bar chart from the database
    private ObservableList<XYChart.Series<String, Number>> getChartData() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT Months, Income FROM SwissChalet")) {
            while (resultSet.next()) {
                series.getData().add(new XYChart.Data<>(resultSet.getString("Months"), resultSet.getInt("Income")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return FXCollections.observableArrayList(series);
    }

    // Method to fetch data for the table view from the database
    private ObservableList<MonthIncome> getMonthIncomeData() {
        ObservableList<MonthIncome> data = FXCollections.observableArrayList();
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT Months, Income FROM SwissChalet")) {
            while (resultSet.next()) {
                data.add(new MonthIncome(resultSet.getString("Months"), resultSet.getInt("Income")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

    // Main method to launch the application
    public static void main(String[] args) {
        launch(args);
    }

    // Class to represent a month and its income
    public static class MonthIncome {
        private final String month;
        private final int income;

        public MonthIncome(String month, int income) {
            this.month = month;
            this.income = income;
        }

        public String getMonth() {
            return month;
        }

        public int getIncome() {
            return income;
        }
    }
}
