-- src/main/resources/db/migration/V2__seed_data.sql

INSERT INTO products (name, name_ar, barcode, category, price, cost, stock, emoji)
VALUES
  ('Coffee',        'قهوة',      '1001', 'مشروبات',  15.00, 5.00,  100, '☕'),
  ('Tea',           'شاي',       '1002', 'مشروبات',  10.00, 3.00,  150, '🍵'),
  ('Water Bottle',  'مياه',      '1003', 'مشروبات',   5.00, 1.00,  200, '💧'),
  ('Sandwich',      'ساندوتش',   '2001', 'طعام',     25.00, 10.00,  50, '🥪'),
  ('Chocolate Bar', 'شوكولاتة',  '3001', 'حلويات',   12.00, 4.00,   80, '🍫');

INSERT INTO settings (key, value, category) VALUES
  ('store_name',    'متجر عربي',    'STORE'),
  ('store_phone',   '0501234567',    'STORE'),
  ('tax_rate',      '15',            'BILLING'),
  ('currency',      'SAR',           'BILLING'),
  ('receipt_note',  'شكراً لزيارتكم','RECEIPT');
