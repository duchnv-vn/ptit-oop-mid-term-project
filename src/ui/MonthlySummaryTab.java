package ui;

import javafx.scene.control.Tab;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * Monthly Summary Tab - Display expense summaries by month.
 */
public class MonthlySummaryTab extends Tab {

    public MonthlySummaryTab() {
        setText("Monthly Summary");
        setClosable(false);

        VBox content = new VBox(10);
        content.setStyle("-fx-padding: 20;");

        Label title = new Label("Monthly Expense Summary");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label placeholder = new Label("Monthly summary features will be implemented here.\n\n" +
                "Features:\n" +
                "- Select month to view\n" +
                "- Total expenses for the month\n" +
                "- Breakdown by category\n" +
                "- Budget vs actual comparison\n" +
                "- Visual charts and indicators");
        placeholder.setStyle("-fx-font-size: 14px;");

        content.getChildren().addAll(title, placeholder);
        setContent(content);
    }
}
