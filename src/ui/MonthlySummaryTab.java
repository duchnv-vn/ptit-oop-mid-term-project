package ui;

import java.awt.BorderLayout;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * Monthly summary placeholder panel (Swing).
 */
public class MonthlySummaryTab extends JPanel {

    public MonthlySummaryTab() {
        setLayout(new BorderLayout(8, 8));

        JLabel title = new JLabel("<html><h2>Monthly Expense Summary</h2></html>");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));

        String placeholderText = "Monthly summary features will be implemented here.\n\n" +
                "Features:\n" +
                "- Select month to view\n" +
                "- Total expenses for the month\n" +
                "- Breakdown by category\n" +
                "- Budget vs actual comparison\n" +
                "- Visual charts and indicators";

        JTextArea placeholder = new JTextArea(placeholderText);
        placeholder.setEditable(false);
        placeholder.setLineWrap(true);
        placeholder.setWrapStyleWord(true);

        add(title, BorderLayout.NORTH);
        add(new JScrollPane(placeholder), BorderLayout.CENTER);
    }
}
