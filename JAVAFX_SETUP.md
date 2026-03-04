# Hướng dẫn Setup JavaFX cho dự án

## Lưu ý
Phiên bản Java và JavaFX phải tương thích với nhau (ví dụ: JavaFX 23 yêu cầu Java 21+.)

## Giải pháp: Download JavaFX SDK

### Bước 1: Download JavaFX SDK

1. Truy cập: https://gluonhq.com/products/javafx/
2. Download: **JavaFX version 23 SDK** (Windows x64)
3. Link trực tiếp:
   ```
   https://download2.gluonhq.com/openjfx/23.0.1/openjfx-23.0.1_windows-x64_bin-sdk.zip
   ```

---

### Bước 2: Extract JavaFX SDK

Extract vào thư mục: **`C:\JavaFX\`**

Sau khi extract sẽ có:
```
C:\JavaFX\javafx-sdk-23\
├── bin/
├── lib/
│   ├── javafx-base.jar
│   ├── javafx-controls.jar
│   ├── javafx-fxml.jar
│   ├── javafx-graphics.jar
│   └── ...
└── legal/
```

---

### Bước 3: Cập nhật build scripts (nếu cần)

Nếu JavaFX SDK được extract vào thư mục khác, sửa 2 files:

**build-fx.bat** - dòng 3:
```batch
set JAVA_FX_SDK=C:\JavaFX\javafx-sdk-23
```
Sửa đường dẫn đúng nơi bạn extract JavaFX.

**run-fx.bat** - dòng 3:
```batch
set JAVA_FX_SDK=C:\JavaFX\javafx-sdk-23
```

---

### Bước 4: Build và Run Application

**Build:**
```bash
build-fx.bat
```

**Run:**
```bash
run-fx.bat
```

---

## Cấu hình IDE (VSCode)

### VSCode:
1. Cài extension: **Extension Pack for Java**
2. Mở file: `.vscode/settings.json`
3. Thêm:
```json
{
    "java.project.referencedLibraries": [
        "C:\\JavaFX\\javafx-sdk-23.0.1\\lib\\javafx-base.jar",
        "C:\\JavaFX\\javafx-sdk-23.0.1\\lib\\javafx-controls.jar",
        "C:\\JavaFX\\javafx-sdk-23.0.1\\lib\\javafx-fxml.jar",
        "C:\\JavaFX\\javafx-sdk-23.0.1\\lib\\javafx-graphics.jar"
    ]
}
```

---

## Files đã tạo

✅ **UI Files:**
- `src/ui/PersonalExpenseManagement.java` - Main JavaFX application
- `src/ui/CategoriesTab.java` - Tab quản lý danh mục
- `src/ui/ExpensesTab.java` - Tab quản lý chi tiêu
- `src/ui/MonthlySummaryTab.java` - Tab tổng kết tháng

✅ **Build Scripts:**
- `build-fx.bat` - Biên dịch với JavaFX
- `run-fx.bat` - Chạy ứng dụng JavaFX

---

## Kiểm tra JavaFX đã setup

Mở cmd, gõ lệnh `build-fx.bat`, nếu thấy:
```
Building JavaFX Application...
Build completed!
```

→ JavaFX đã sẵn sàng! Chạy `run-fx.bat` để mở ứng dụng.

## Quy trình
Lần đầu tiên hoặc sau khi sửa code:
`build-fx.bat`
`run-fx.bat`

Các lần tiếp theo (không sửa code): `run-fx.bat`
