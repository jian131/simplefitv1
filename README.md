# SimpleFit

![Version](https://img.shields.io/badge/version-1.0.0-blue.svg)
![Platform](https://img.shields.io/badge/platform-Android-brightgreen.svg)
![Language](https://img.shields.io/badge/language-Java-orange.svg)

## 📱 Ứng dụng theo dõi tập luyện thể thao trên nền tảng Android

SimpleFit là ứng dụng di động giúp người dùng theo dõi quá trình tập luyện thể thao cá nhân. Ứng dụng cung cấp thư viện bài tập phong phú, cho phép người dùng tạo buổi tập nhanh, theo dõi tiến độ thực hiện, lưu lại lịch sử tập luyện và xem thống kê kết quả tập luyện theo thời gian.

## ✨ Tính năng chính

- **Xác thực người dùng**: Đăng ký, đăng nhập, quản lý tài khoản
- **Thư viện bài tập**: Danh sách bài tập phân loại theo nhóm cơ bắp
- **Buổi tập nhanh (Quick Workout)**: Tạo và tùy chỉnh buổi tập nhanh chóng
- **Quản lý set tập**: Theo dõi số lần lặp, trọng lượng và đánh dấu hoàn thành
- **Đo thời gian tập**: Theo dõi thời gian tập luyện trong mỗi buổi
- **Lịch sử tập luyện**: Xem danh sách và chi tiết các buổi tập đã hoàn thành
- **Thống kê tập luyện**: Theo dõi tổng số buổi tập, thời gian và bài tập đã hoàn thành
- **Quản lý hồ sơ**: Xem và chỉnh sửa thông tin cá nhân
- **Theo dõi tiến độ**: Hiển thị trực quan tiến độ hoàn thành từng bài tập

## 🖼️ Screenshots

*Hãy thêm hình ảnh ứng dụng vào đây*

## 🛠️ Công nghệ sử dụng

- **Ngôn ngữ**: Java
- **Nền tảng**: Android SDK
- **Database**: Firebase Firestore
- **Authentication**: Firebase Authentication
- **Storage**: Firebase Storage

## 📂 Cấu trúc dự án

```
app/src/main/java/com/jian/simplefitv1/
├── activities/           # Các màn hình chính của ứng dụng
├── adapters/             # Bộ điều hợp cho RecyclerViews
├── data/                 # Dữ liệu mẫu và quản lý dữ liệu
├── fragments/            # Các fragment UI
├── models/               # Các lớp mô hình dữ liệu
├── services/             # Các dịch vụ (Firebase, Database)
└── utils/                # Các tiện ích và lớp hỗ trợ
```

## 🚀 Hướng dẫn cài đặt

1. Clone dự án từ GitHub:
   ```bash
   git clone https://github.com/yourusername/SimpleFit.git
   ```

2. Mở dự án bằng Android Studio

3. Kết nối với Firebase:
   - Tạo dự án Firebase mới tại [Firebase Console](https://console.firebase.google.com/)
   - Thêm ứng dụng Android vào dự án Firebase
   - Tải file `google-services.json` và thêm vào thư mục app
   - Bật các dịch vụ: Authentication, Firestore Database, Storage

4. Build và chạy ứng dụng

## 📘 Cách sử dụng

1. **Đăng ký và đăng nhập**: Tạo tài khoản mới hoặc đăng nhập bằng email
2. **Trang chủ**: Xem thông tin tổng quan và truy cập vào các tính năng
3. **Buổi tập nhanh**: 
   - Nhấn nút "+" để bắt đầu buổi tập nhanh
   - Thêm/xóa bài tập theo ý muốn
   - Đánh dấu các set đã hoàn thành
   - Kết thúc và lưu buổi tập
4. **Thư viện bài tập**: Duyệt bài tập theo nhóm cơ hoặc tìm kiếm
5. **Lịch sử tập luyện**: Xem các buổi tập đã hoàn thành và thống kê
6. **Hồ sơ**: Quản lý thông tin tài khoản và cài đặt

---

© 2025 SimpleFit
