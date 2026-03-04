package service;

import java.math.BigDecimal;
import java.time.YearMonth;

public class MonthlySummary {
    private final YearMonth month;
    private final BigDecimal totalAmount;

    public MonthlySummary(YearMonth month, BigDecimal totalAmount) {
        this.month = month;
        this.totalAmount = totalAmount;
    }

    public YearMonth getMonth() {
        return month;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
}
