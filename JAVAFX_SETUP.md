# Hướng dẫn Setup cho dự án


## Giải pháp: Download & Cài đặt JDK

Để chạy và biên dịch ứng dụng bạn cần cài JDK. Khuyến nghị dùng JDK 17 trở lên (JDK 17 hoặc JDK 21+). Dưới đây là các bước tải và cài đặt trên Windows.

### Bước 1 — Tải JDK

- Tùy chọn phổ biến và miễn phí:
	- Eclipse Temurin (Adoptium): https://adoptium.net
	- Oracle JDK: https://www.oracle.com/java/technologies/downloads/

- Ví dụ (Eclipse Temurin):
	1. Mở https://adoptium.net
	2. Chọn phiên bản (ví dụ: `17` hoặc `21`) và hệ điều hành `Windows x64`.
	3. Tải file `.msi` (hoặc `.zip` nếu muốn cài thủ công).

### Bước 2 — Cài đặt

- Chạy file `.msi` vừa tải và làm theo hướng dẫn. Khi có tuỳ chọn, bật `Set JAVA_HOME` hoặc `Add to PATH` nếu có.
- Nếu cài bằng file `.zip`, giải nén vào ví dụ `C:\Program Files\Java\jdk-17.x.x`.

### Bước 3 — Thiết lập `JAVA_HOME` và `PATH` (nếu cần)

GUI (Windows):
1. Mở `Settings` → `System` → `About` → `Advanced system settings` → `Environment Variables`.
2. Trong `System variables` nhấn `New...` (nếu chưa có) hoặc `Edit...` cho `JAVA_HOME` và đặt giá trị là đường dẫn đến thư mục JDK, ví dụ:

```
JAVA_HOME=C:\Program Files\Java\jdk-17.0.x
```

3. Chỉnh biến `Path`: thêm `%JAVA_HOME%\bin` vào `Path` nếu chưa có.

Command-line (PowerShell chạy dưới quyền admin):
```powershell
setx JAVA_HOME "C:\Program Files\Java\jdk-17.0.x" /M
setx PATH "%PATH%;%JAVA_HOME%\\bin" /M
```
Lưu ý: `setx` ghi vào registry và có hiệu lực cho cửa sổ terminal mới.

### Bước 4 — Kiểm tra cài đặt

Mở Command Prompt hoặc PowerShell mới và chạy:
```powershell
java -version
javac -version
```
Bạn sẽ thấy thông tin phiên bản JDK đã cài (ví dụ `openjdk version "17.0.x"`). Nếu lệnh không tìm thấy, kiểm tra lại `JAVA_HOME` và `Path`.

### Ghi chú tương thích
- Nếu bạn từng sử dụng JavaFX trước đây, không cần JavaFX SDK nữa vì giao diện đã chuyển sang Swing.
- Luôn dùng phiên bản JDK tương thích với yêu cầu dự án; JDK 17 là lựa chọn an toàn cho hầu hết dự án học thuật.


# Hướng dẫn cài đặt UI — Swing 

Giao diện người dùng của dự án đã được chuyển từ JavaFX sang Swing. Bạn không cần tải hoặc cấu hình JavaFX SDK nữa.

Tài liệu này hướng dẫn cách biên dịch và chạy ứng dụng Swing trên Windows.

## Yêu cầu
- JDK (khuyến nghị 17 trở lên). Đảm bảo `javac` và `java` có trong `PATH`.

## Biên dịch & Chạy

Windows (cmd) — dùng script có sẵn:

1. Mở Command Prompt trong thư mục gốc của dự án.
2. Chạy:

```bat
build-fx.bat
```

Script sẽ thu tất cả file `.java` và biên dịch vào thư mục `build`.

Chạy ứng dụng:

```bat
run-fx.bat
```

PowerShell (lựa chọn thay thế):

```powershell
# Biên dịch tất cả source vào thư mục build
javac -d build -encoding UTF-8 (Get-ChildItem -Recurse -Filter *.java -Path src | ForEach-Object {$_.FullName})
# Chạy
java -cp build Main
```

## VSCode
Cài đặt Extension Pack for Java. Không cần thêm thư viện JavaFX.

Nếu bạn đã thêm các jar JavaFX vào `java.project.referencedLibraries`, có thể xóa các mục đó.

## Các file và script chính
- `src/Main.java` — entry point (launcher Swing)
- `src/ui/*` — các panel và frame Swing (ví dụ: `FinanceAppFrame`, `CategoryPanel`,...)
- `build-fx.bat` — script biên dịch (đặt tên giữ nguyên để tương thích)
- `run-fx.bat` — script chạy (khởi chạy `Main`)

## Kiểm tra nhanh
- Biên dịch: `build-fx.bat`
- Chạy: `run-fx.bat`


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
