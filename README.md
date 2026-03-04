# Ứng Dụng Quản Lý Chi Tiêu Cá Nhân Theo Tháng

## 1. Mục tiêu
Ứng dụng desktop Java Swing để quản lý chi tiêu cá nhân theo tháng cho 1 người dùng duy nhất (không cần đăng nhập), lưu trữ dữ liệu bằng file CSV.

## 2. Chức năng đã hiện thực
- Quản lý danh mục chi tiêu: thêm, sửa, xóa, xem danh sách.
- Quản lý khoản chi: thêm, sửa, xóa, xem danh sách.
- Lọc danh sách khoản chi theo danh mục và theo tháng.
- Tổng hợp tổng số tiền chi theo tháng.
- Đọc và ghi dữ liệu CSV với hỗ trợ escape dấu phẩy và dấu nháy kép.

## 3. Kiến trúc mã nguồn
- `model`: mô hình dữ liệu (`Category`, `ExpenseCategory`, `Expense`).
- `storage`: truy cập dữ liệu CSV (`CategoryCsvRepository`, `ExpenseCsvRepository`, `CsvUtils`).
- `service`: nghiệp vụ và kiểm tra dữ liệu (`ExpenseManagerService`, `MonthlySummary`).
- `ui`: giao diện Swing theo tab (`CategoryPanel`, `ExpensePanel`, `SummaryPanel`, `FinanceAppFrame`).
- `Main`: entry point hiện tại.

## 4. Cấu trúc thư mục
```text
.
├─ src/
│  ├─ Main.java
│  ├─ model/
│  ├─ service/
│  ├─ storage/
│  └─ ui/
├─ data/
│  ├─ categories.csv
│  └─ expenses.csv
├─ build-exe.ps1
├─ build-exe.bat
└─ README.md
```

## 5. Định dạng dữ liệu CSV
### `categories.csv`
Header:
```csv
id,name,description,monthlyBudgetLimit
```
Ý nghĩa cột:
- `id`: UUID.
- `name`: tên danh mục.
- `description`: mô tả.
- `monthlyBudgetLimit`: hạn mức tháng, có thể để trống.

### `expenses.csv`
Header:
```csv
id,categoryId,amount,currency,occurredAt,note
```
Ý nghĩa cột:
- `id`: UUID.
- `categoryId`: UUID tham chiếu danh mục.
- `amount`: số tiền chi (> 0).
- `currency`: mã tiền tệ, rỗng sẽ mặc định thành `VND`.
- `occurredAt`: ngày chi theo định dạng `yyyy-MM-dd`.
- `note`: ghi chú.

## 6. Quy tắc nghiệp vụ
- Tên danh mục là bắt buộc và không được trùng (không phân biệt hoa thường).
- Hạn mức tháng nếu nhập phải >= 0.
- Khoản chi phải gắn với danh mục hợp lệ.
- Số tiền chi phải > 0.
- Không cho xóa danh mục nếu đã có khoản chi tham chiếu.

## 7. Chạy từ source
Yêu cầu: JDK 21+.

Lệnh compile:
```powershell
$sources = Get-ChildItem -Path src -Recurse -Filter *.java | ForEach-Object { $_.FullName }
javac -encoding UTF-8 -d out\production\personal-finance-management $sources
```

Lệnh chạy:
```powershell
java -cp out\production\personal-finance-management Main
```

## 8. Đóng gói EXE
Script hỗ trợ:
- `build-exe.ps1`
- `build-exe.bat`

Chạy build:
```powershell
.\build-exe.ps1
```

EXE đầu ra:
```text
build\dist\PersonalExpenseManager\PersonalExpenseManager.exe
```

Lưu ý phân phối:
- Không copy riêng file `.exe`.
- Cần copy toàn bộ thư mục `build\dist\PersonalExpenseManager` sang máy khác.

## 9. Trạng thái entry point hiện tại
`Main.java` hiện chỉ in thông báo `Hello and welcome...`, chưa khởi chạy cửa sổ Swing.

Điều này có nghĩa:
- Chạy từ source bằng `Main` chỉ ra console message.
- Build EXE bằng script hiện tại cũng chưa mở GUI.

Để chạy GUI đúng mục tiêu, cần cập nhật `Main.java` để khởi tạo `ExpenseManagerService` và `FinanceAppFrame`.

## 10. Dữ liệu mẫu mặc định
Hiện tại:
- `data/categories.csv`: chỉ có header.
- `data/expenses.csv`: chỉ có header.

Bạn có thể nhập dữ liệu trực tiếp trong app (sau khi nối `Main` với GUI) hoặc chỉnh tay file CSV theo đúng schema ở trên.
