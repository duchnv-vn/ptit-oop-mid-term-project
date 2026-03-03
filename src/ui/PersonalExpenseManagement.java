package ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

import java.nio.file.Paths;

/**
 * Main JavaFX Application for Personal Finance Management.
 * Launches the GUI with TabPane interface.
 */
public class PersonalExpenseManagement extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Personal Finance Management");

        // Create main window with tabs
        TabPane root = new TabPane();
        root.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        // Create tabs
        CategoriesTab categoriesTab = new CategoriesTab();
        ExpensesTab expensesTab = new ExpensesTab();
        MonthlySummaryTab summaryTab = new MonthlySummaryTab();

        root.getTabs().addAll(categoriesTab, expensesTab, summaryTab);

        // Setup scene
        Scene scene = new Scene(root, 1000, 700);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
