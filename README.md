# Ciné — Ứng dụng Android đặt vé xem phim

Java · XML · Material Components · SQLite · min SDK 24. Không backend/API,
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
├── data/        Repository, DatabaseHelper, SessionManager
├── adapter/     MovieAdapter, ShowtimeAdapter, SeatAdapter, BookingAdapter
└── ui/          BaseActivity, SplashActivity và 8 Activity chức năng
app/src/main/res/
├── drawable/    6 poster VectorDrawable placeholder, nền, icon
├── layout/      activity_*.xml, item_*.xml
└── values/      strings.xml, colors.xml, styles.xml
```

- Các Activity lấy dữ liệu qua `Repository.getInstance(context)`.
- Repository singleton đọc/ghi SQLite cục bộ (`cine.db`), khởi tạo 6 phim,
  18 suất chiếu, một tài khoản và 5 ghế đặt sẵn cho từng suất chiếu.
- Đặt vé chuyển sang màn hình thanh toán demo. Chọn thẻ/Ví MoMo/Ví ZaloPay mô
  phỏng; có thể xác nhận thành công hoặc thử luồng thất bại. Chỉ thanh toán
  thành công mới tạo vé và giữ ghế, trong một giao dịch SQLite nguyên tử.
- Người dùng, phim, suất chiếu, ghế, vé và bản ghi thanh toán được lưu cục bộ
  nên vẫn còn sau khi đóng/mở lại app. SessionManager lưu `userId` bằng
  SharedPreferences.
- Màn hình được bảo vệ ở BaseActivity; đăng xuất xóa back stack.
- Dùng RecyclerView với GridLayoutManager cho phim và 30 ghế (A–E × 6).
- Ghế xanh: trống; vàng: đang chọn; xám: đã đặt. Giới hạn 6 ghế.
- Không kết nối cổng thanh toán, không thu thập dữ liệu thẻ; mã đặt vé chỉ để minh họa.
- Chuỗi và màu layout nằm trong resource; không có chữ/màu hard-code trong XML layout.

## Luồng kiểm thử thủ công

1. Mở app lần đầu → Đăng nhập. Nhấn đăng nhập khi trống: lỗi dưới 2 ô.
2. Nhập email sai; kiểm tra báo lỗi định dạng. Sai mật khẩu: báo lỗi dưới ô.
3. Đăng ký: kiểm tra trống, email, SĐT, mật khẩu tối thiểu 6 ký tự, khớp mật khẩu.
4. Đăng ký email trùng trả false. Tài khoản mới đăng nhập được trong phiên hiện tại.
5. Đăng nhập bằng tài khoản test, tắt/mở app: vào danh sách phim.
6. Chọn phim → xem 3 suất → chọn ghế. Ghế bận không bấm được.
7. Chọn 6 ghế; ghế thứ 7 báo Snackbar. Bỏ chọn cập nhật tiền, chưa chọn không đặt được.
8. Vào thanh toán, thử mô phỏng thất bại: không tạo vé và ghế vẫn trống; xác nhận demo thành công: hiện mã vé và phương thức thanh toán.
9. Mở Vé của tôi, kiểm tra vé; đóng/mở app và kiểm tra dữ liệu vẫn còn.
10. Quay lại cùng suất chiếu: ghế đã đặt đổi màu xám. Đăng xuất → Back không trở về trang được bảo vệ.

## Database và thanh toán

DatabaseHelper tạo schema SQLite và dữ liệu phim mẫu khi chạy lần đầu. Thanh
toán chỉ là mô phỏng; cần backend và tích hợp nhà cung cấp thanh toán để xử lý
giao dịch thật. Không lưu hoặc gửi thông tin thẻ trong ứng dụng demo này.

## Kiểm thử Repository

Trong Android Studio: chạy `RepositoryTest` trên emulator/thiết bị, hoặc
`./gradlew :app:connectedDebugAndroidTest` sau khi cấu hình Gradle/Android SDK
và khởi động thiết bị kiểm thử.

Môi trường bản xem trước Figma chỉ chạy React/Vite, không có Android SDK/JDK;
project native cần được build và kiểm tra hiển thị trên Android Studio.
