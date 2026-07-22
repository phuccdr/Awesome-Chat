# Đồng bộ Panel Gallery và Bàn phím hoàn tất

Tôi đã thực hiện các thay đổi để đồng bộ hóa việc hiển thị của panel gallery và bàn phím trong màn hình chat.

## Các thay đổi đã thực hiện

### [ChatFragment.kt](file:///D:/RikkeiSoft/BaseProject/awesome_chat_app/features/conversation/src/main/java/com/rikkeisoft/awesome/ui/chat/ChatFragment.kt)

- **Theo dõi bàn phím**: Đã thêm `WindowInsetsListener` vào root view để phát hiện khi bàn phím hiển thị. Nếu bàn phím hiện lên, ứng dụng sẽ tự động ẩn panel gallery.
- **Xử lý Focus**: Kích hoạt lại listener `onFocusChange` cho ô nhập liệu. Khi người dùng nhấn vào ô nhập liệu để gõ văn bản, panel gallery sẽ bị ẩn ngay lập tức để nhường chỗ cho bàn phím.
- **Tự động ẩn bàn phím**: Đảm bảo rằng khi mở panel gallery (thông qua nút thêm ảnh), bàn phím sẽ được ẩn đi thông qua `DeviceUtil.hideSoftKeyboard`.

## Kết quả kiểm tra

- [x] Khi nhấn vào `btnAddImage`, bàn phím ẩn và panel gallery hiện.
- [x] Khi panel gallery đang hiện và người dùng nhấn vào `edtInputMessage`, panel gallery ẩn và bàn phím hiện.
- [x] Khi bàn phím đang hiện (do focus hoặc cách khác) và hệ thống báo IME visible, panel gallery sẽ được đặt về trạng thái ẩn.

> [!NOTE]
> Việc sử dụng `WindowInsetsListener` là cách hiện đại và chính xác nhất để theo dõi trạng thái bàn phím trên các phiên bản Android mới, thay vì dựa vào các callback cũ không ổn định.
