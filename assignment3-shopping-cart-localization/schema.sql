-- Shopping Cart Localization Database Schema
-- Drop and recreate database
DROP DATABASE IF EXISTS shopping_cart_localization;
CREATE DATABASE shopping_cart_localization CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE shopping_cart_localization;

-- Cart Records Table: stores each shopping session
CREATE TABLE IF NOT EXISTS cart_records (
    id INT AUTO_INCREMENT PRIMARY KEY,
    total_items INT NOT NULL,
    total_cost DECIMAL(10, 2) NOT NULL,
    language VARCHAR(10) NOT NULL DEFAULT 'en_US',
    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Cart Items Table: stores individual items in each cart session
CREATE TABLE IF NOT EXISTS cart_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    cart_record_id INT NOT NULL,
    item_number INT NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    quantity INT NOT NULL,
    subtotal DECIMAL(10, 2) NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (cart_record_id) REFERENCES cart_records(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Localization Strings Table: stores translated UI strings
CREATE TABLE IF NOT EXISTS localization_strings (
    id INT AUTO_INCREMENT PRIMARY KEY,
    language VARCHAR(10) NOT NULL,
    `key` VARCHAR(100) NOT NULL,
    value TEXT NOT NULL,
    UNIQUE KEY unique_lang_key (language, `key`),
    INDEX idx_language (language)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insert localization data for English (en_US)
INSERT INTO localization_strings (language, `key`, value) VALUES
('en_US', 'select.language', 'Select the language:'),
('en_US', 'confirm.language', 'Confirm Language'),
('en_US', 'enter.number.of.items', 'Enter number of items:'),
('en_US', 'enter.items', 'Enter Items'),
('en_US', 'enter.price', 'Enter price for item:'),
('en_US', 'enter.quantity', 'Enter quantity for item:'),
('en_US', 'item', 'Item'),
('en_US', 'items', 'Items'),
('en_US', 'price', 'Price'),
('en_US', 'quantity', 'Quantity'),
('en_US', 'item.total', 'Item total:'),
('en_US', 'cart.total', 'Total cost:'),
('en_US', 'calculate', 'Calculate'),
('en_US', 'calculate.total', 'Calculate Total'),
('en_US', 'total', 'Total:');

-- Insert localization data for Finnish (fi_FI)
INSERT INTO localization_strings (language, `key`, value) VALUES
('fi_FI', 'select.language', 'Valitse kieli:'),
('fi_FI', 'confirm.language', 'Vahvista kieli'),
('fi_FI', 'enter.number.of.items', 'Syötä ostettavien tuotteiden määrä:'),
('fi_FI', 'enter.items', 'Lisää tuotteet'),
('fi_FI', 'enter.price', 'Syötä tuotteen hinta:'),
('fi_FI', 'enter.quantity', 'Syötä tuotteen määrä:'),
('fi_FI', 'item', 'Tuote'),
('fi_FI', 'items', 'Tuotteet'),
('fi_FI', 'price', 'Hinta'),
('fi_FI', 'quantity', 'Määrä'),
('fi_FI', 'item.total', 'Tuotteen kokonaishinta:'),
('fi_FI', 'cart.total', 'Ostoskorin yhteishinta:'),
('fi_FI', 'calculate', 'Laske'),
('fi_FI', 'calculate.total', 'Laske yhteensä'),
('fi_FI', 'total', 'Yhteensä:');

-- Insert localization data for Swedish (sv_SE)
INSERT INTO localization_strings (language, `key`, value) VALUES
('sv_SE', 'select.language', 'Välj språk:'),
('sv_SE', 'confirm.language', 'Bekräfta språk'),
('sv_SE', 'enter.number.of.items', 'Ange antalet varor att köpa:'),
('sv_SE', 'enter.items', 'Lägg till varor'),
('sv_SE', 'enter.price', 'Ange priset för varan:'),
('sv_SE', 'enter.quantity', 'Ange mängden varor:'),
('sv_SE', 'item', 'Vara'),
('sv_SE', 'items', 'Varor'),
('sv_SE', 'price', 'Pris'),
('sv_SE', 'quantity', 'Mängd'),
('sv_SE', 'item.total', 'Varans totala kostnad:'),
('sv_SE', 'cart.total', 'Total kostnad:'),
('sv_SE', 'calculate', 'Beräkna'),
('sv_SE', 'calculate.total', 'Beräkna total'),
('sv_SE', 'total', 'Total:');

-- Insert localization data for Japanese (ja_JP)
INSERT INTO localization_strings (language, `key`, value) VALUES
('ja_JP', 'select.language', '言語を選択してください:'),
('ja_JP', 'confirm.language', '言語を確認'),
('ja_JP', 'enter.number.of.items', '購入する商品の数を入力してください:'),
('ja_JP', 'enter.items', '商品を追加'),
('ja_JP', 'enter.price', '商品の価格を入力してください:'),
('ja_JP', 'enter.quantity', '商品の数量を入力してください:'),
('ja_JP', 'item', '商品'),
('ja_JP', 'items', '商品'),
('ja_JP', 'price', '価格'),
('ja_JP', 'quantity', '数量'),
('ja_JP', 'item.total', '商品の合計金額:'),
('ja_JP', 'cart.total', '合計金額:'),
('ja_JP', 'calculate', '計算'),
('ja_JP', 'calculate.total', '合計を計算'),
('ja_JP', 'total', '合計:');

-- Insert localization data for Arabic (ar_AR)
INSERT INTO localization_strings (language, `key`, value) VALUES
('ar_AR', 'select.language', 'اختر اللغة:'),
('ar_AR', 'confirm.language', 'تأكيد اللغة'),
('ar_AR', 'enter.number.of.items', 'أدخل عدد العناصر المراد شراؤها:'),
('ar_AR', 'enter.items', 'إضافة العناصر'),
('ar_AR', 'enter.price', 'أدخل السعر للعنصر:'),
('ar_AR', 'enter.quantity', 'أدخل الكمية:'),
('ar_AR', 'item', 'عنصر'),
('ar_AR', 'items', 'عناصر'),
('ar_AR', 'price', 'السعر'),
('ar_AR', 'quantity', 'الكمية'),
('ar_AR', 'item.total', 'إجمالي العنصر:'),
('ar_AR', 'cart.total', 'إجمالي التكلفة:'),
('ar_AR', 'calculate', 'احسب'),
('ar_AR', 'calculate.total', 'احسب الإجمالي'),
('ar_AR', 'total', 'الإجمالي:');

-- Create indexes for better query performance
CREATE INDEX idx_cart_records_language ON cart_records(language);
CREATE INDEX idx_cart_records_timestamp ON cart_records(timestamp);
CREATE INDEX idx_cart_items_cart_record_id ON cart_items(cart_record_id);

