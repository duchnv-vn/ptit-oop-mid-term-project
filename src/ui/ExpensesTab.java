package ui;

import javafx.scene.control.Tab;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * Expenses Tab - Manage expenses with filters.
 */
public class ExpensesTab extends Tab {

    public ExpensesTab() {
        setText("Expenses");
        setClosable(false);

        VBox content = new VBox(10);
        content.setStyle("-fx-padding: 20;");

        Label title = new Label("Expenses Management");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label placeholder = new Label("Expense management features will be implemented here.\n\n" +
                "Features:\n" +
                "- Add new expense\n" +
                "- Edit expense\n" +
                "- Delete expense\n" +
                "- Filter by category\n" +
                "- Filter by date range");
        placeholder.setStyle("-fx-font-size: 14px;");

        content.getChildren().addAll(title, placeholder);
        setContent(content);
    }
}
