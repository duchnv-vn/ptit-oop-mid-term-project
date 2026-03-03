package ui;

import service.ExpenseManagerService;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;
import java.awt.Dimension;

public class FinanceAppFrame extends JFrame {
    private final CategoryPanel categoryPanel;
    private final ExpensePanel expensePanel;
    private final SummaryPanel summaryPanel;

    public FinanceAppFrame(ExpenseManagerService service) {
        super("Personal Monthly Expense Manager");

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(960, 620));
        setLocationRelativeTo(null);

        categoryPanel = new CategoryPanel(service, this::reloadAll);
        expensePanel = new ExpensePanel(service, this::reloadAll);
        summaryPanel = new SummaryPanel(service);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Categories", categoryPanel);
        tabs.addTab("Expenses", expensePanel);
        tabs.addTab("Monthly Summary", summaryPanel);

        tabs.addChangeListener(e -> {
            if (tabs.getSelectedComponent() == summaryPanel) {
                summaryPanel.reloadData();
            }
        });

        setContentPane(tabs);
        reloadAll();
    }

    private void reloadAll() {
        categoryPanel.reloadData();
        expensePanel.reloadData();
        summaryPanel.reloadData();
    }
}
