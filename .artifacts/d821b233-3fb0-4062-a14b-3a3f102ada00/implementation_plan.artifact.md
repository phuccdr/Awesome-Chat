# Đồng bộ Panel Gallery và Bàn phím

Người dùng muốn khi hiển thị `panelGallery` thì bàn phím sẽ ẩn và ngược lại, khi bàn phím hiển thị thì `panelGallery` sẽ ẩn.

## Các thay đổi đề xuất

### [Conversation Feature](file:///D:/RikkeiSoft/BaseProject/awesome_chat_app/features/conversation)

#### [MODIFY] [ChatFragment.kt](file:///D:/RikkeiSoft/BaseProject/awesome_chat_app/features/conversation/src/main/java/com/rikkeisoft/awesome/ui/chat/ChatFragment.kt)

- Thêm `WindowInsetsListener` để theo dõi trạng thái hiển thị của bàn phím.
- Khi bàn phím hiển thị, gọi `viewModel.togglePanel(false)` để ẩn panel gallery.
- Xử lý thêm sự kiện focus của `EditText` để ẩn panel gallery ngay lập tức khi người dùng nhấn vào ô nhập liệu.

## Kế hoạch xác minh

### Kiểm tra thủ công
1. Mở màn hình chat.
2. Nhấn nút thêm ảnh (btnAddImage):
   - Kỳ vọng: Bàn phím ẩn (nếu đang hiện), panel gallery hiện lên.
3. Nhấn vào ô nhập liệu (edtInputMessage):
   - Kỳ vọng: Panel gallery ẩn đi, bàn phím hiện lên.
4. Chuyển đổi qua lại giữa bàn phím và panel gallery nhiều lần để đảm bảo không bị xung đột.
