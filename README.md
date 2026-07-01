# Base-MVVM-Kotlin-Multi Module

MVVM with Kotlin

#### Tech

1. Jetpack Component (Data Binding, Lifecycles, Live Data, Navigation, View Model)
2. MVVM
3. Hilt
4. Glide
5. Retrofit
6. Timber
7. Coroutines, Flow

#### Các bước cài đặt trước khi đẩy code lần đẩu lên git

1. Sửa rootProject.name ở file settings.gradle
2. Sửa tên các package ở module app, feature, tương ứng hãy sửa cả package ở AndroidManifest.xml
3. Xoá các class ở feature App và Demo mà mình ko cần dùng tới
4. Sửa lại applicationId ở file build.gradle ở module app
5. Thay file google service, nếu có, không thì xoá đi.
6. Cân nhắc tạo keystore cho debug variant, để tránh trường hợp khi build ở nhiều máy khác nhau, app
   sẽ bị ghi đè
7. Thử build lại xem còn vấn đề gì ko.

## Author

**VietBH**
