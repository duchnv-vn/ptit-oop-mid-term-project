
import javax.swing.SwingUtilities;
import service.ExpenseManagerService;
import ui.FinanceAppFrame;

public class Main {
    public static void main(String[] args) {
        ExpenseManagerService service = new ExpenseManagerService();

        SwingUtilities.invokeLater(() -> {
            FinanceAppFrame frame = new FinanceAppFrame(service);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
