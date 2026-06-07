-- ═══════════════════════════════════════════════════════════
--  FleetFlow - Database Migration V1
--  File: src/main/resources/db/migration/V1__init.sql
--
--  This file creates ALL tables for the FleetFlow app.
--  Flyway runs this ONCE automatically when the app starts.
--  NEVER edit this file after it has been run.
--  If you need to change the schema, create V2__something.sql
-- ═══════════════════════════════════════════════════════════

-- ── 1. USERS ────────────────────────────────────────────
-- Every person who can log in has a User account.
-- Role determines what they can do:
--   ADMIN      = full access, manage everything
--   DISPATCHER = assign deliveries, manage drivers/vehicles
--   DRIVER     = view and update their own shipments only
CREATE TABLE users (
    id            BIGSERIAL PRIMARY KEY,
    email         VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role          VARCHAR(20)  NOT NULL CHECK (role IN ('ADMIN', 'DISPATCHER', 'DRIVER')),
    active        BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- Index: speeds up login queries that search by email
CREATE INDEX idx_users_email ON users(email);


-- ── 2. DRIVERS ──────────────────────────────────────────
-- A Driver is a person who physically delivers shipments.
-- Every Driver has exactly ONE User account (for login).
-- The user_id column links to the users table.
CREATE TABLE drivers (
    id             BIGSERIAL PRIMARY KEY,
    user_id        BIGINT       NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    full_name      VARCHAR(255) NOT NULL,
    license_number VARCHAR(100) NOT NULL UNIQUE,
    phone          VARCHAR(20)  NOT NULL,
    available      BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at     TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- Index: dispatchers frequently search for available drivers
CREATE INDEX idx_drivers_available ON drivers(available);


-- ── 3. WAREHOUSES ───────────────────────────────────────
-- A Warehouse is a physical location where goods are stored.
-- Shipments originate FROM a warehouse.
-- Routes are ASSIGNED TO a warehouse.
CREATE TABLE warehouses (
    id            BIGSERIAL PRIMARY KEY,
    name          VARCHAR(255) NOT NULL,
    city          VARCHAR(100) NOT NULL,
    address       VARCHAR(500) NOT NULL,
    contact_phone VARCHAR(20)  NOT NULL,
    created_at    TIMESTAMP    NOT NULL DEFAULT NOW()
);


-- ── 4. VEHICLES ─────────────────────────────────────────
-- A Vehicle is a truck/van used to carry shipments.
-- Status: ACTIVE = ready to use, MAINTENANCE = being repaired
CREATE TABLE vehicles (
    id           BIGSERIAL PRIMARY KEY,
    plate_number VARCHAR(50)    NOT NULL UNIQUE,
    model        VARCHAR(255)   NOT NULL,
    capacity_kg  DECIMAL(10, 2) NOT NULL CHECK (capacity_kg > 0),
    status       VARCHAR(20)    NOT NULL DEFAULT 'ACTIVE'
                     CHECK (status IN ('ACTIVE', 'MAINTENANCE')),
    created_at   TIMESTAMP      NOT NULL DEFAULT NOW()
);

-- Index: dispatchers frequently check for active vehicles
CREATE INDEX idx_vehicles_status ON vehicles(status);


-- ── 5. ROUTES ───────────────────────────────────────────
-- A Route is a predefined delivery path from one city to another.
-- It originates from a Warehouse.
-- Example: "Addis to Hawassa" route from "Addis Bole Warehouse"
CREATE TABLE routes (
    id               BIGSERIAL PRIMARY KEY,
    warehouse_id     BIGINT       NOT NULL REFERENCES warehouses(id),
    name             VARCHAR(255) NOT NULL,
    origin_city      VARCHAR(100) NOT NULL,
    destination_city VARCHAR(100) NOT NULL,
    estimated_hours  INTEGER      NOT NULL CHECK (estimated_hours > 0),
    created_at       TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- Index: looking up routes by warehouse is very common
CREATE INDEX idx_routes_warehouse ON routes(warehouse_id);


-- ── 6. CUSTOMERS ────────────────────────────────────────
-- A Customer is the person/company that PLACED the order.
-- They are the one whose goods are being delivered.
-- Note: Customers do not have login accounts in this system.
CREATE TABLE customers (
    id         BIGSERIAL PRIMARY KEY,
    full_name  VARCHAR(255) NOT NULL,
    email      VARCHAR(255),
    phone      VARCHAR(20)  NOT NULL,
    address    VARCHAR(500),
    created_at TIMESTAMP    NOT NULL DEFAULT NOW()
);


-- ── 7. SHIPMENTS ────────────────────────────────────────
-- A Shipment is the core entity of the whole system.
-- It represents ONE delivery job.
-- It links together: a Driver, a Vehicle, a Route,
-- a Customer, and the originating Warehouse.
--
-- Status flow:
--   PENDING → PICKED_UP → IN_TRANSIT → DELIVERED
--   (any status can go to) → CANCELLED
CREATE TABLE shipments (
    id                  BIGSERIAL    PRIMARY KEY,
    driver_id           BIGINT       NOT NULL REFERENCES drivers(id),
    vehicle_id          BIGINT       NOT NULL REFERENCES vehicles(id),
    route_id            BIGINT       NOT NULL REFERENCES routes(id),
    customer_id         BIGINT       NOT NULL REFERENCES customers(id),
    origin_warehouse_id BIGINT       NOT NULL REFERENCES warehouses(id),
    tracking_code       VARCHAR(50)  NOT NULL UNIQUE,
    status              VARCHAR(20)  NOT NULL DEFAULT 'PENDING'
                            CHECK (status IN ('PENDING','PICKED_UP','IN_TRANSIT','DELIVERED','CANCELLED')),
    weight              DECIMAL(10,2),
    description         TEXT,
    created_at          TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- Indexes: the most common queries on shipments
CREATE INDEX idx_shipments_status        ON shipments(status);
CREATE INDEX idx_shipments_tracking_code ON shipments(tracking_code);
CREATE INDEX idx_shipments_driver        ON shipments(driver_id);
CREATE INDEX idx_shipments_customer      ON shipments(customer_id);


-- ── 8. AUDIT LOG ────────────────────────────────────────
-- Every time a shipment status changes, we record it here.
-- This creates a full history / trail of who did what and when.
-- Example: "dispatcher@email.com changed status from PENDING to PICKED_UP at 10:30am"
CREATE TABLE audit_log (
    id          BIGSERIAL    PRIMARY KEY,
    shipment_id BIGINT       NOT NULL REFERENCES shipments(id) ON DELETE CASCADE,
    actor       VARCHAR(255) NOT NULL,  -- email of the user who made the change
    old_status  VARCHAR(20),            -- what the status was before
    new_status  VARCHAR(20)  NOT NULL,  -- what the status changed to
    changed_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- Index: very common to fetch all logs for one shipment
CREATE INDEX idx_audit_shipment ON audit_log(shipment_id);


-- ═══════════════════════════════════════════════════════
--  SEED DATA (optional starter data for testing)
--  This inserts one ADMIN user so you can log in
--  immediately after the app starts for the first time.
--
--  Email:    admin@fleetflow.com
--  Password: admin123
--  (password is BCrypt hashed - the app will verify it)
-- ═══════════════════════════════════════════════════════
INSERT INTO users (email, password_hash, role, active)
VALUES (
    'admin@fleetflow.com',
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6PBDC',
    'ADMIN',
    TRUE
);
