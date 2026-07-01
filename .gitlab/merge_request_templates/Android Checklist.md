# CheckList Android

## Coding convention

- [ ]  Tích hợp lint vào gitlab, android studio để check coding convention
- [ ] Có code theo mô hình của project không?
- [ ] Có copy ảnh drawable đúng folder không?
- [ ] Các resource đã thêm đúng file(string, color, dimens), đúng anotaions không?

## Bảo mật

- [ ] Việc đặt log đã dùng đúng level chưa, đã cài đặt để chỉ log ở variant debug chưa?
- [ ] Việc sử dụng key đã lưu đúng vị trí, có được mã hoá hay không?
- [ ] Thư viện add thêm đã được confirm chưa?

## Logic

- [ ] Logic so với yêu cầu. Logic có rõ ràng, có nhầm lẫn gì không?

## UI

- [ ] Sử dụng viewGroup đã phù hợp chưa.
- [ ] Có bị chồng chéo nhiều viewGroup với nhau không
- [ ] Dùng recyclerView đã chỉ định đúng chiều cao, chiều rộng chưa?
  Khi để wrap content, chiều cao, chiều rộng tuỳ thuộc vào nội dung sẽ làm hiệu năng của
  recyclerView kém đi, sai tư duy

- [ ] Sử dụng constrainLayout có bị thừa thuộc tính không

## Một số lỗi khác

- [ ] Đã đồng bộ version thư viện giữa các module chưa?
- [ ] Đã thêm proguard cho thư viện vừa thêm chưa?
- [ ] Có dùng handler, timer chỉ để cố xử lý cho TH gọi trực tiếp không chạy được không
- [ ] Dùng handler, timer có chủ động huỷ khi kết thúc vòng đời không
- [ ] Có sử dụng try catch, exception phù hợp không
- [ ] Việc sử dụng let, apply, also có phù hợp ko
  ae thường dùng let để chạy code cho != null, nhưng lại thành quên ko xử lý TH == null

## Git

- [ ] Tên author đúng chưa?
- [ ] Tên nhánh đúng yêu cầu chưa?
- [ ] Nội dung commit hợp lý chưa?
