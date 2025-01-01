CREATE DATABASE OnlineStore;


USE OnlineStore;

CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY, 
    username VARCHAR(50) NOT NULL, 
    password VARCHAR(255) NOT NULL,
    balance DECIMAL(10, 2) DEFAULT 0.0,
    role ENUM('seller', 'buyer') NOT NULL 
);


CREATE TABLE products (
    id INT AUTO_INCREMENT PRIMARY KEY, 
    name VARCHAR(100) NOT NULL, 
    price DECIMAL(10, 2) NOT NULL, 
    sellerId INT NOT NULL,  
    stock INT NOT NULL, 
    FOREIGN KEY (sellerId) REFERENCES users(id) ON DELETE CASCADE 
);


CREATE TABLE carts (
    id INT AUTO_INCREMENT PRIMARY KEY,  
    userId INT NOT NULL, 
    productId INT NOT NULL, 
    quantity INT NOT NULL, 
    FOREIGN KEY (userId) REFERENCES users(id) ON DELETE CASCADE, 
    FOREIGN KEY (productId) REFERENCES products(id) ON DELETE CASCADE 
);


CREATE TABLE orders (
    id INT AUTO_INCREMENT PRIMARY KEY, 
    buyerId INT NOT NULL, 
    productId INT NOT NULL, 
    quantity INT NOT NULL, 
    totalPrice DECIMAL(10, 2) NOT NULL, 
    deliveryFee DECIMAL(10, 2) DEFAULT 0.0, 
    FOREIGN KEY (buyerId) REFERENCES users(id) ON DELETE CASCADE,  
    FOREIGN KEY (productId) REFERENCES products(id) ON DELETE CASCADE 
);
