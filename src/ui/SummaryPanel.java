package ui;

import service.ExpenseManagerService;
import service.MonthlySummary;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.List;

public class SummaryPanel extends JPanel {
    private final ExpenseManagerService service;
    private final DefaultTableModel tableModel;

    public SummaryPanel(ExpenseManagerService service) {
        this.service = service;
        setLayout(new BorderLayout(8, 8));

        tableModel = new DefaultTableModel(new Object[]{"Month", "Total Expense"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> reloadData());
        actions.add(refreshButton);
        add(actions, BorderLayout.NORTH);
    }

    public void reloadData() {
        List<MonthlySummary> summaries = service.getMonthlySummaries();
        tableModel.setRowCount(0);
        for (MonthlySummary summary : summaries) {
            tableModel.addRow(new Object[]{
                    summary.getMonth().toString(),
                    summary.getTotalAmount().stripTrailingZeros().toPlainString()
            });
        }
    }
}
