# MechKeySound

App Android tạo tiếng phím cơ khi gõ bằng **bàn phím ngoài** (Bluetooth/USB) kết nối
với điện thoại. Khác với các app "keyboard sound" trên Play Store (chỉ áp dụng cho
bàn phím ảo trên màn hình), app này dùng `AccessibilityService` để bắt sự kiện phím
thật từ thiết bị vật lý.

## Vì sao không có sẵn file APK build rồi?

Mình không có môi trường build Android (Android SDK / Gradle wrapper / mạng) trong
sandbox này, nên không tạo được file `.apk` chạy ngay. Thay vào đó đây là **toàn bộ
source code** một project Android Studio hoàn chỉnh, bạn chỉ cần mở lên và bấm Run.

## Cách build KHÔNG CẦN MÁY TÍNH (chỉ dùng điện thoại + Termux)

Project đã có sẵn file `.github/workflows/build.yml` để GitHub tự build APK giúp bạn
trên máy chủ của họ (miễn phí). Bạn chỉ cần đẩy code lên GitHub bằng Termux:

1. **Tạo tài khoản GitHub** (miễn phí) tại github.com bằng trình duyệt điện thoại.
2. Tạo **Personal Access Token**: vào github.com → bấm avatar góc phải → Settings →
   Developer settings → Personal access tokens → Tokens (classic) → Generate new token
   → tick quyền `repo` → Generate → **copy token lại** (chỉ hiện 1 lần).
3. Tạo repo mới: vào github.com → nút **+** → New repository → đặt tên `MechKeySound`
   → chọn **Public** → Create repository (không tick thêm README).
4. Mở **Termux**, chạy lần lượt:
   ```
   pkg update -y
   pkg install git -y
   termux-setup-storage
   ```
5. Giải nén file zip mình gửi bằng ZArchiver ra thư mục `Download/MechKeySound`,
   rồi trong Termux chạy:
   ```
   cd ~/storage/downloads/MechKeySound
   git init
   git add .
   git config user.email "ban@example.com"
   git config user.name "Ten Cua Ban"
   git commit -m "first commit"
   git branch -M main
   git remote add origin https://github.com/TEN_GITHUB_CUA_BAN/MechKeySound.git
   git push -u origin main
   ```
   Khi hỏi username/password: nhập username GitHub, còn password thì **dán Personal
   Access Token** đã copy ở bước 2 (không phải mật khẩu GitHub thật).
6. Sau khi push xong, vào repo trên github.com → tab **Actions** → sẽ thấy workflow
   đang chạy (mất khoảng 3-5 phút) → khi có dấu tích xanh → bấm vào job đó → kéo
   xuống mục **Artifacts** → tải file `MechKeySound-debug-apk` (là file zip chứa APK).
7. Giải nén file zip đó bằng ZArchiver → được file `app-debug.apk` → bấm cài đặt
   (có thể cần bật "Cài ứng dụng không rõ nguồn gốc" cho app quản lý file bạn dùng).

## Cách build bằng máy tính (nếu sau này có)

1. Cài **Android Studio** (bản mới nhất) trên máy tính.
2. Mở Android Studio → **Open** → chọn thư mục `MechKeySound` (thư mục chứa file này).
3. Đợi Gradle sync xong (lần đầu sẽ tự tải Gradle wrapper, SDK... cần mạng).
4. **Thêm file âm thanh**: bỏ 4 file mp3 tiếng phím cơ vào
   `app/src/main/res/raw/`, đặt tên đúng là:
   - `key1.mp3`, `key2.mp3`, `key3.mp3`, `key4.mp3`
   (Có thể lấy sound pack miễn phí, hợp pháp từ freesound.org, hoặc tự thu âm bàn
   phím cơ của bạn — không dùng file tải lậu để tránh vi phạm bản quyền.)
5. Cắm điện thoại (bật USB debugging) hoặc dùng máy ảo → bấm **Run ▶**.

## Cách dùng

1. Mở app **MechKeySound** trên điện thoại → bấm **"Mở cài đặt Accessibility"**.
2. Trong danh sách Accessibility, tìm **MechKeySound** → bật lên → xác nhận cảnh báo quyền.
3. Kết nối bàn phím Bluetooth/USB như bình thường → gõ thử, sẽ nghe tiếng phím cơ.
4. Quay lại app để chỉnh âm lượng bằng thanh trượt.

## Ghi chú kỹ thuật

- `MechKeySoundService.kt` là phần lõi: lắng nghe `onKeyEvent()`, lọc để chỉ phát âm
  thanh khi phím đến từ bàn phím vật lý thật (không phải bàn phím ảo).
- Cần quyền Accessibility vì Android không cho app thường bắt sự kiện phím toàn hệ
  thống — đây là giới hạn bảo mật của Android, không có cách nào né được.
- App không thu thập hay gửi dữ liệu đi đâu cả, mọi thứ chạy local.

## Giới hạn cần biết

- Một số ROM Android (đặc biệt Xiaomi/MIUI, Oppo/ColorOS) hạn chế thêm các
  AccessibilityService chạy nền — có thể cần vào cài đặt riêng của hãng để "khóa"
  app chạy nền / cho phép tự khởi động.
- Với Android 13+ có thể cần cấp thêm quyền thông báo khi bật Accessibility service.
