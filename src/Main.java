import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import service.ExpenseManagerService;
import ui.FinanceAppFrame;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }

            try {
                ExpenseManagerService service = new ExpenseManagerService(resolveDataDirectory());
                FinanceAppFrame frame = new FinanceAppFrame(service);
                frame.setVisible(true);
            } catch (RuntimeException ex) {
                JOptionPane.showMessageDialog(
                        null,
                        "Failed to start application: " + ex.getMessage(),
                        "Startup Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });
    }

    private static Path resolveDataDirectory() {
        Path localDataDir = Paths.get("data");
        if (Files.exists(localDataDir)) {
            return localDataDir;
        }

        return Paths.get(System.getProperty("user.home"), ".personal-expense-manager", "data");
    }
}
