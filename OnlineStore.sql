CREATE DATABASE OnlineStore;

-- اختيار قاعدة البيانات
USE OnlineStore;

-- إنشاء جدول المستخدمين
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY, -- معرف المستخدم
    username VARCHAR(50) NOT NULL, -- اسم المستخدم
    password VARCHAR(255) NOT NULL, -- كلمة المرور
    balance DECIMAL(10, 2) DEFAULT 0.0, -- الرصيد
    role ENUM('seller', 'buyer') NOT NULL -- دور المستخدم (بائع/مشتري)
);

-- إنشاء جدول المنتجات
CREATE TABLE products (
    id INT AUTO_INCREMENT PRIMARY KEY, -- معرف المنتج
    name VARCHAR(100) NOT NULL, -- اسم المنتج
    price DECIMAL(10, 2) NOT NULL, -- السعر
    sellerId INT NOT NULL, -- معرف البائع
    stock INT NOT NULL, -- الكمية المتوفرة
    FOREIGN KEY (sellerId) REFERENCES users(id) ON DELETE CASCADE -- ربط البائع بجدول المستخدمين
);

-- إنشاء جدول السلال
CREATE TABLE carts (
    id INT AUTO_INCREMENT PRIMARY KEY, -- معرف السلة
    userId INT NOT NULL, -- معرف المستخدم
    productId INT NOT NULL, -- معرف المنتج
    quantity INT NOT NULL, -- الكمية المطلوبة
    FOREIGN KEY (userId) REFERENCES users(id) ON DELETE CASCADE, -- ربط السلة بالمشتري
    FOREIGN KEY (productId) REFERENCES products(id) ON DELETE CASCADE -- ربط السلة بالمنتج
);

-- إنشاء جدول الطلبات
CREATE TABLE orders (
    id INT AUTO_INCREMENT PRIMARY KEY, -- معرف الطلب
    buyerId INT NOT NULL, -- معرف المشتري
    productId INT NOT NULL, -- معرف المنتج
    quantity INT NOT NULL, -- الكمية المطلوبة
    totalPrice DECIMAL(10, 2) NOT NULL, -- السعر الإجمالي
    deliveryFee DECIMAL(10, 2) DEFAULT 0.0, -- رسوم التوصيل
    FOREIGN KEY (buyerId) REFERENCES users(id) ON DELETE CASCADE, -- ربط الطلب بالمشتري
    FOREIGN KEY (productId) REFERENCES products(id) ON DELETE CASCADE -- ربط الطلب بالمنتج
);