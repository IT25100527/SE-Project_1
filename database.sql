-- =====================================================================
-- Rx+ PHARMACORE - Database setup (MySQL / Aiven)
-- Run this once (e.g. with MySQL Workbench, DBeaver, or IntelliJ's
-- Database tool window connected to your Aiven MySQL) BEFORE starting
-- the Spring Boot app (ddl-auto=none, so Hibernate will NOT create
-- this table for you).
-- =====================================================================

CREATE TABLE IF NOT EXISTS suppliers (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    supplier_code     VARCHAR(30)  UNIQUE,
    supplier_name     VARCHAR(255) NOT NULL,
    contact_person    VARCHAR(255) NOT NULL,
    designation       VARCHAR(255),
    phone             VARCHAR(50)  NOT NULL,
    email             VARCHAR(255) NOT NULL,
    category          VARCHAR(150) NOT NULL,
    status            VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    business_reg_no   VARCHAR(100),
    payment_terms     VARCHAR(100),
    address           VARCHAR(500),
    created_at        DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at        DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Seed sample data (matches the original screenshot/mockup) - only
-- runs if the table is currently empty.
INSERT INTO suppliers
    (supplier_code, supplier_name, contact_person, designation, phone, email,
     category, status, business_reg_no, payment_terms, address)
SELECT * FROM (SELECT
    'SUP-00001', 'MedPharma Lanka Ltd', 'Kamal Perera', 'Sales Manager',
    '011 234 5678 / 077 889 9000', 'info@medpharma.lk', 'Tablets & Capsules',
    'ACTIVE', 'PV-98234-X', 'Net 30 Days', 'No. 45, Galle Road, Colombo 03, Sri Lanka'
UNION ALL SELECT
    'SUP-00002', 'Lanka Health Medical', 'Sunil Jayasuriya', 'Procurement Officer',
    '077 123 4567', 'sales@lhmedical.com', 'Medical Equipment',
    'ACTIVE', 'PV-77213-A', 'Net 45 Days', 'No. 12, Kandy Road, Kadawatha, Sri Lanka'
UNION ALL SELECT
    'SUP-00003', 'BioCare Pharma Products', 'Nimali Silva', 'Business Development Lead',
    '033 456 7890', 'contact@biocare.lk', 'Syrups & Liquids',
    'INACTIVE', 'PV-55321-B', 'Net 15 Days', 'No. 8, Negombo Road, Wattala, Sri Lanka'
) AS seed_data
WHERE NOT EXISTS (SELECT 1 FROM suppliers);
