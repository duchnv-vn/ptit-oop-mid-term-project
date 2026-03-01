package storage;

import model.Expense;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ExpenseCsvRepository {
    private static final String HEADER = "id,categoryId,amount,currency,occurredAt,note";

    private final Path filePath;

    public ExpenseCsvRepository(Path filePath) {
        this.filePath = filePath;
    }

    public List<Expense> load() {
        if (!Files.exists(filePath)) {
            return new ArrayList<>();
        }

        try {
            List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
            List<Expense> expenses = new ArrayList<>();

            for (int lineIndex = 0; lineIndex < lines.size(); lineIndex++) {
                String line = lines.get(lineIndex);
                if (line.trim().isEmpty()) {
                    continue;
                }

                if (lineIndex == 0 && HEADER.equalsIgnoreCase(line.trim())) {
                    continue;
                }

                List<String> columns = CsvUtils.parseLine(line);
                if (columns.size() != 6) {
                    throw new IllegalStateException("Invalid expenses CSV format at line " + (lineIndex + 1));
                }

                UUID id = UUID.fromString(columns.get(0).trim());
                UUID categoryId = UUID.fromString(columns.get(1).trim());
                BigDecimal amount = new BigDecimal(columns.get(2).trim());
                String currency = columns.get(3).trim();
                LocalDate occurredAt = LocalDate.parse(columns.get(4).trim());
                String note = columns.get(5).trim();

                expenses.add(new Expense(id, categoryId, amount, currency, occurredAt, note));
            }

            return expenses;
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read expenses CSV: " + filePath, e);
        }
    }

    public void save(List<Expense> expenses) {
        try {
            Path parent = filePath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }

            List<String> lines = new ArrayList<>();
            lines.add(HEADER);

            for (Expense expense : expenses) {
                lines.add(CsvUtils.toCsvLine(
                        expense.getId().toString(),
                        expense.getCategoryId().toString(),
                        expense.getAmount().stripTrailingZeros().toPlainString(),
                        expense.getCurrency(),
                        expense.getOccurredAt().toString(),
                        expense.getNote()
                ));
            }

            Files.write(
                    filePath,
                    lines,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            );
        } catch (IOException e) {
            throw new IllegalStateException("Failed to write expenses CSV: " + filePath, e);
        }
    }
}
