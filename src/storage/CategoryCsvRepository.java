package storage;

import model.ExpenseCategory;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CategoryCsvRepository {
    private static final String HEADER = "id,name,description,monthlyBudgetLimit";

    private final Path filePath;

    public CategoryCsvRepository(Path filePath) {
        this.filePath = filePath;
    }

    public List<ExpenseCategory> load() {
        if (!Files.exists(filePath)) {
            return new ArrayList<>();
        }

        try {
            List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
            List<ExpenseCategory> categories = new ArrayList<>();

            for (int lineIndex = 0; lineIndex < lines.size(); lineIndex++) {
                String line = lines.get(lineIndex);
                if (line.trim().isEmpty()) {
                    continue;
                }

                if (lineIndex == 0 && HEADER.equalsIgnoreCase(line.trim())) {
                    continue;
                }

                List<String> columns = CsvUtils.parseLine(line);
                if (columns.size() != 4) {
                    throw new IllegalStateException("Invalid categories CSV format at line " + (lineIndex + 1));
                }

                UUID id = UUID.fromString(columns.get(0).trim());
                String name = columns.get(1).trim();
                String description = columns.get(2).trim();
                String monthlyLimitRaw = columns.get(3).trim();
                BigDecimal monthlyLimit = monthlyLimitRaw.isEmpty() ? null : new BigDecimal(monthlyLimitRaw);

                categories.add(new ExpenseCategory(id, name, description, monthlyLimit));
            }

            return categories;
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read categories CSV: " + filePath, e);
        }
    }

    public void save(List<ExpenseCategory> categories) {
        try {
            Path parent = filePath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }

            List<String> lines = new ArrayList<>();
            lines.add(HEADER);
            for (ExpenseCategory category : categories) {
                String monthlyLimit = category.getMonthlyBudgetLimit() == null
                        ? ""
                        : category.getMonthlyBudgetLimit().stripTrailingZeros().toPlainString();
                lines.add(CsvUtils.toCsvLine(
                        category.getId().toString(),
                        category.getName(),
                        category.getDescription(),
                        monthlyLimit
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
            throw new IllegalStateException("Failed to write categories CSV: " + filePath, e);
        }
    }
}
