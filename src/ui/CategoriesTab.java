package ui;

import javafx.scene.control.Tab;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * Categories Tab - Manage expense categories.
 */
public class CategoriesTab extends Tab {

    public CategoriesTab() {
        setText("Categories");
        setClosable(false);

        VBox content = new VBox(10);
        content.setStyle("-fx-padding: 20;");

        Label title = new Label("Categories Management");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label placeholder = new Label("Category management features will be implemented here.\n\n" +
                "Features:\n" +
                "- Add new category\n" +
                "- Edit category\n" +
                "- Delete category\n" +
                "- View all categories");
        placeholder.setStyle("-fx-font-size: 14px;");

        content.getChildren().addAll(title, placeholder);
        setContent(content);
    }
}
