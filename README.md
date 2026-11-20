# Ciné — Ứng dụng Android đặt vé xem phim

Java · XML · Material Components · min SDK 24. Không database, không API,
không thư viện ảnh và không thanh toán thật.

## Mở project

1. Giải nén và mở thư mục `android` (thư mục chứa `settings.gradle`) trong Android Studio.
2. Dùng **JDK 17**, cài Android SDK 35 và đồng bộ Gradle.
   Project kèm Gradle Wrapper 8.9; lần đầu cần kết nối Internet để tải dependencies.
3. Chạy module `app` trên máy ảo hoặc thiết bị Android 7.0 trở lên.
4. Đăng nhập: **test@gmail.com / 123456**.

## Kiến trúc

```
app/src/main/java/com/example/movieticket/
├── model/       User, Movie, Showtime, Booking
├── data/        Repository, SessionManager
├── adapter/     MovieAdapter, ShowtimeAdapter, SeatAdapter, BookingAdapter
└── ui/          BaseActivity, SplashActivity và 8 Activity chức năng
app/src/main/res/
├── drawable/    6 poster VectorDrawable placeholder, nền, icon
├── layout/      activity_*.xml, item_*.xml
└── values/      strings.xml, colors.xml, styles.xml
```

- Các Activity chỉ lấy và gửi dữ liệu qua `Repository.getInstance()`.
- Repository singleton lưu dữ liệu trong RAM, khởi tạo 6 phim, 18 suất chiếu,
  một tài khoản và 5 ghế đặt sẵn cho từng suất chiếu.
- Đặt vé cập nhật ghế bận và danh sách vé **trong cùng phiên chạy**.
- SessionManager chỉ lưu `userId` bằng SharedPreferences. Mở lại app vẫn giữ
  phiên đăng nhập; dữ liệu tài khoản mới, ghế mới đặt và vé sẽ mất khi tiến
  trình bị hủy, đúng chủ đích dữ liệu giả trong bộ nhớ.
- Màn hình được bảo vệ ở BaseActivity; đăng xuất xóa back stack.
- Dùng RecyclerView với GridLayoutManager cho phim và 30 ghế (A–E × 6).
- Ghế xanh: trống; vàng: đang chọn; xám: đã đặt. Giới hạn 6 ghế.
- Không có QR code thanh toán hoặc mã soát vé thật; mã đặt vé chỉ để minh họa.
- Chuỗi và màu layout nằm trong resource; không có chữ/màu hard-code trong XML layout.

## Luồng kiểm thử thủ công

1. Mở app lần đầu → Đăng nhập. Nhấn đăng nhập khi trống: lỗi dưới 2 ô.
2. Nhập email sai; kiểm tra báo lỗi định dạng. Sai mật khẩu: báo lỗi dưới ô.
3. Đăng ký: kiểm tra trống, email, SĐT, mật khẩu tối thiểu 6 ký tự, khớp mật khẩu.
4. Đăng ký email trùng trả false. Tài khoản mới đăng nhập được trong phiên hiện tại.
5. Đăng nhập bằng tài khoản test, tắt/mở app: vào danh sách phim.
6. Chọn phim → xem 3 suất → chọn ghế. Ghế bận không bấm được.
7. Chọn 6 ghế; ghế thứ 7 báo Snackbar. Bỏ chọn cập nhật tiền, chưa chọn không đặt được.
8. Đặt vé → mã đặt → Vé của tôi → đầy đủ thông tin vé.
9. Quay lại cùng suất chiếu: ghế đã đặt đổi màu xám.
10. Đăng xuất → Back không trở về trang được bảo vệ.

## Thay SQLite sau này

Mỗi phương thức Repository có `// TODO: thay bằng SQLite`. Thay phần thân
các phương thức bằng truy vấn SQLite, giữ nguyên chữ ký phương thức.
Không cần đổi Adapter hoặc UI. Không lưu mật khẩu dạng rõ trong ứng dụng thật.

## Kiểm thử Repository

Trong Android Studio: chạy `RepositoryTest`, hoặc `./gradlew :app:testDebugUnitTest`
sau khi cấu hình Gradle/Android SDK. Test sử dụng drawable ID được Android sinh.

Môi trường bản xem trước Figma chỉ chạy React/Vite, không có Android SDK/JDK;
project native cần được build và kiểm tra hiển thị trên Android Studio.
