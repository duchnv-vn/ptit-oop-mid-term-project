package ui;

import java.awt.BorderLayout;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * Expenses placeholder panel (Swing).
 */
public class ExpensesTab extends JPanel {

    public ExpensesTab() {
        setLayout(new BorderLayout(8, 8));

        JLabel title = new JLabel("<html><h2>Expenses Management</h2></html>");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));

        String placeholderText = "Expense management features will be implemented here.\n\n" +
                "Features:\n" +
                "- Add new expense\n" +
                "- Edit expense\n" +
                "- Delete expense\n" +
                "- Filter by category\n" +
                "- Filter by date range";

        JTextArea placeholder = new JTextArea(placeholderText);
        placeholder.setEditable(false);
        placeholder.setLineWrap(true);
        placeholder.setWrapStyleWord(true);

        add(title, BorderLayout.NORTH);
        add(new JScrollPane(placeholder), BorderLayout.CENTER);
    }
}
