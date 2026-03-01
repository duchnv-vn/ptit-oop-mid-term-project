# ptit-oop-mid-term-project
Dự án bài tập giữa kỳ môn Lập Trình Hướng Đối Tượng.

## Project layout
- src/main/java/com/ptit/pfm/model: Core domain entities for the terminal-based personal finance app (user, income, expense, saving and related categories).
- src/main/resources: Place configuration or seed data here as it is added.
- src/test/java: Reserved for unit tests once business logic is implemented.

## Core entities
- User: Application user owning all data.
- Expense + ExpenseCategory: Outgoing transactions grouped by category with optional monthly budget caps.
- Income + IncomeCategory: Incoming transactions grouped by category and source type.
- Saving + SavingCategory: Savings goals with target amounts and due dates.

