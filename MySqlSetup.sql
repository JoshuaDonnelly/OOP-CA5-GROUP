CREATE DATABASE IF NOT EXISTS GuitarEcommerce;
USE GuitarEcommerce;

-- Dropping Tables if they exsists
DROP TABLE IF EXISTS order_items;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS customers;
DROP TABLE IF EXISTS products;
DROP TABLE IF EXISTS categories;

-- Creating categories
CREATE TABLE categories (
	id INT AUTO_INCREMENT PRIMARY KEY,
	name VARCHAR(80) NOT NULL
);

CREATE TABLE products (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    price DOUBLE NOT NULL,
    description TEXT,
    category_id INT,
    stock INT DEFAULT 0,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL
);


CREATE TABLE customers (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    address TEXT NOT NULL,
    phone VARCHAR(20) NOT NULL
);


CREATE TABLE orders (
    id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT NOT NULL,
    total_price FLOAT NOT NULL,
    FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE
);

INSERT INTO categories (name) VALUES 
('Electric Guitars'),
('Acoustic Guitars'),
('Bass Guitars'),
('Accessories');

INSERT INTO products (name, price, description, category_id, stock) VALUES
('Stratocaster', 1200.99, 'Classic electric guitar.', 1, 10),
('Gibson', 2300.50, 'Iconic electric guitar.', 1, 5),
('Yamaha', 300.00, 'Affordable acoustic guitar.', 2, 15),
('Taylor', 999.99, 'Premium acoustic guitar.', 2, 8),
('Jazz Bass', 1500.75, 'Versatile bass.', 3, 7),
('Guitar Strings', 9.99, 'High-quality strings.', 4, 50);


INSERT INTO customers (name, email, address, phone) VALUES
('John Doe', 'johndoe@example.com', '123 Main St, NY, USA', '+1234567890'),
('Jane Smith', 'janesmith@example.com', '456 Oak St, CA, USA', '+1987654321');


INSERT INTO orders (customer_id, total_price) VALUES
(1, 1200.99), 
(2, 300.00);

