-- src/main/resources/db/migration/V1__init_schema.sql

-- ── Products ────────────────────────────────────────────────────
CREATE TABLE products (
  id          BIGSERIAL PRIMARY KEY,
  name        VARCHAR(200) NOT NULL,
  name_ar     VARCHAR(200),
  barcode     VARCHAR(100) UNIQUE,
  category    VARCHAR(100),
  price       NUMERIC(10, 2) NOT NULL CHECK (price >= 0),
  cost        NUMERIC(10, 2) DEFAULT 0,
  stock       INTEGER NOT NULL DEFAULT 0 CHECK (stock >= 0),
  emoji       VARCHAR(10),
  active      BOOLEAN NOT NULL DEFAULT TRUE,
  created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
  updated_at  TIMESTAMP NOT NULL DEFAULT NOW()
);

-- ── Orders ──────────────────────────────────────────────────────
CREATE TABLE orders (
  id              BIGSERIAL PRIMARY KEY,
  order_number    VARCHAR(50) UNIQUE NOT NULL,
  status          VARCHAR(20) NOT NULL DEFAULT 'COMPLETED',
  payment_method  VARCHAR(30) NOT NULL DEFAULT 'CASH',
  subtotal        NUMERIC(10, 2) NOT NULL,
  discount        NUMERIC(10, 2) NOT NULL DEFAULT 0,
  tax             NUMERIC(10, 2) NOT NULL DEFAULT 0,
  total           NUMERIC(10, 2) NOT NULL,
  notes           TEXT,
  created_at      TIMESTAMP NOT NULL DEFAULT NOW()
);

-- ── Order Items ─────────────────────────────────────────────────
CREATE TABLE order_items (
  id          BIGSERIAL PRIMARY KEY,
  order_id    BIGINT NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
  product_id  BIGINT REFERENCES products(id) ON DELETE SET NULL,
  name        VARCHAR(200) NOT NULL,
  price       NUMERIC(10, 2) NOT NULL,
  quantity    INTEGER NOT NULL CHECK (quantity > 0),
  subtotal    NUMERIC(10, 2) NOT NULL
);

-- ── Settings ────────────────────────────────────────────────────
CREATE TABLE settings (
  id              BIGSERIAL PRIMARY KEY,
  key             VARCHAR(100) UNIQUE NOT NULL,
  value           TEXT,
  category        VARCHAR(50) DEFAULT 'GENERAL',
  updated_at      TIMESTAMP NOT NULL DEFAULT NOW()
);

-- ── Indexes ─────────────────────────────────────────────────────
CREATE INDEX idx_products_barcode  ON products(barcode);
CREATE INDEX idx_products_category ON products(category);
CREATE INDEX idx_orders_created    ON orders(created_at);
CREATE INDEX idx_order_items_order ON order_items(order_id);
