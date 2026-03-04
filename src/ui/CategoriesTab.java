package ui;

import java.awt.BorderLayout;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * Categories placeholder panel (Swing).
 */
public class CategoriesTab extends JPanel {

    public CategoriesTab() {
        setLayout(new BorderLayout(8, 8));

        JLabel title = new JLabel("<html><h2>Categories Management</h2></html>");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));

        String placeholderText = "Category management features will be implemented here.\n\n" +
                "Features:\n" +
                "- Add new category\n" +
                "- Edit category\n" +
                "- Delete category\n" +
                "- View all categories";

        JTextArea placeholder = new JTextArea(placeholderText);
        placeholder.setEditable(false);
        placeholder.setLineWrap(true);
        placeholder.setWrapStyleWord(true);

        add(title, BorderLayout.NORTH);
        add(new JScrollPane(placeholder), BorderLayout.CENTER);
    }
}
