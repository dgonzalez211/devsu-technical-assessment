CREATE DATABASE IF NOT EXISTS customer_identity;
CREATE DATABASE IF NOT EXISTS financial_movement;

USE customer_identity;

CREATE TABLE persons
(
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    uuid           CHAR(36)    NOT NULL UNIQUE,
    identification VARCHAR(20) NOT NULL,
    firstName      VARCHAR(50) NOT NULL,
    lastName       VARCHAR(50) NOT NULL,
    gender         ENUM('MALE', 'FEMALE', 'OTHER') NOT NULL,
    age            INT         NOT NULL,
    birthDate      DATE,
    address        VARCHAR(255),
    email          VARCHAR(100),
    status         ENUM('ACTIVE', 'INACTIVE', 'SUSPENDED', 'DELETED', 'LOCKED', 'CLOSED') NOT NULL DEFAULT 'ACTIVE',
    createdAt      DATETIME    NOT NULL,
    updatedAt      DATETIME,
    createdBy      VARCHAR(50) NOT NULL,
    updatedBy      VARCHAR(50),
    version        BIGINT DEFAULT 0,
    CONSTRAINT uk_person_identification UNIQUE (identification),
    CONSTRAINT uk_person_email UNIQUE (email)
);

CREATE INDEX idx_person_last_name ON persons (lastName);
CREATE INDEX idx_person_status ON persons (status);

CREATE TABLE customers
(
    person_id             BIGINT PRIMARY KEY,
    customer_id           CHAR(36)     NOT NULL,
    password              VARCHAR(100) NOT NULL,
    customer_status       ENUM('ACTIVE', 'INACTIVE', 'SUSPENDED', 'DELETED', 'LOCKED', 'CLOSED') NOT NULL,
    last_login_at         DATETIME,
    password_reset_token  VARCHAR(100),
    password_reset_expiry DATETIME,
    failed_login_attempts INT DEFAULT 0,
    locked_until          DATETIME,
    CONSTRAINT uk_customer_customer_id UNIQUE (customer_id),
    CONSTRAINT fk_customer_person FOREIGN KEY (person_id) REFERENCES persons (id) ON DELETE CASCADE
);

CREATE TABLE phones
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    number        VARCHAR(20) NOT NULL,
    type          ENUM('MOBILE', 'HOME', 'WORK', 'FAX', 'OTHER') NOT NULL,
    primary_phone BOOLEAN DEFAULT FALSE,
    notes         VARCHAR(100),
    person_id     BIGINT      NOT NULL,
    createdAt     DATETIME    NOT NULL,
    updatedAt     DATETIME,
    CONSTRAINT uk_phone_number_person UNIQUE (number, person_id),
    CONSTRAINT fk_phone_person FOREIGN KEY (person_id) REFERENCES persons (id) ON DELETE CASCADE
);

CREATE TABLE accounts
(
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    uuid              CHAR(36)       NOT NULL UNIQUE,
    accountNumber     VARCHAR(20)    NOT NULL,
    accountType       ENUM('SAVINGS', 'CHECKING', 'LOAN', 'CREDIT', 'INVESTMENT', 'FIXED_DEPOSIT') NOT NULL,
    initialBalance    DECIMAL(19, 4) NOT NULL,
    currentBalance    DECIMAL(19, 4) NOT NULL,
    status            ENUM('ACTIVE', 'INACTIVE', 'FROZEN', 'CLOSED', 'PENDING_APPROVAL') NOT NULL,
    currencyCode      VARCHAR(3)     NOT NULL DEFAULT 'USD',
    customer_id       BIGINT         NOT NULL,
    openedAt          DATETIME       NOT NULL,
    lastTransactionAt DATETIME,
    closedAt          DATETIME,
    interestRate      DECIMAL(5, 2),
    overdraftLimit    DECIMAL(19, 4),
    createdAt         DATETIME       NOT NULL,
    updatedAt         DATETIME,
    CONSTRAINT uk_account_account_number UNIQUE (accountNumber),
    CONSTRAINT fk_account_customer FOREIGN KEY (customer_id) REFERENCES customers (person_id) ON DELETE CASCADE
);

SET @person1_uuid = UUID();
SET @person2_uuid = UUID();
SET @person3_uuid = UUID();
SET @person4_uuid = UUID();
SET @person5_uuid = UUID();

INSERT INTO persons (uuid, identification, firstName, lastName, gender, age, birthDate, address, email, status, createdAt, createdBy, version)
VALUES
    (@person1_uuid, 'ABCDE12345', 'John', 'Doe', 'MALE', 35, '1988-05-15', '123 Main St, Anytown', 'john.doe@example.com', 'ACTIVE', NOW(), 'SYSTEM', 0),
    (@person2_uuid, 'FGHIJ67890', 'Jane', 'Smith', 'FEMALE', 28, '1995-08-22', '456 Oak Ave, Somewhere', 'jane.smith@example.com', 'ACTIVE', NOW(), 'SYSTEM', 0),
    (@person3_uuid, 'KLMNO12345', 'Robert', 'Johnson', 'MALE', 42, '1981-03-10', '789 Pine Rd, Elsewhere', 'robert.johnson@example.com', 'ACTIVE', NOW(), 'SYSTEM', 0),
    (@person4_uuid, 'PQRST67890', 'Maria', 'Garcia', 'FEMALE', 31, '1992-11-05', '101 Cedar Ln, Nowhere', 'maria.garcia@example.com', 'ACTIVE', NOW(), 'SYSTEM', 0),
    (@person5_uuid, 'UVWXY12345', 'David', 'Brown', 'MALE', 45, '1978-07-30', '202 Elm St, Anywhere', 'david.brown@example.com', 'INACTIVE', NOW(), 'SYSTEM', 0);

SET @person1_id = (SELECT id FROM persons WHERE uuid = @person1_uuid);
SET @person2_id = (SELECT id FROM persons WHERE uuid = @person2_uuid);
SET @person3_id = (SELECT id FROM persons WHERE uuid = @person3_uuid);
SET @person4_id = (SELECT id FROM persons WHERE uuid = @person4_uuid);
SET @person5_id = (SELECT id FROM persons WHERE uuid = @person5_uuid);

SET @customer1_uuid = UUID();
SET @customer2_uuid = UUID();
SET @customer3_uuid = UUID();
SET @customer4_uuid = UUID();
SET @customer5_uuid = UUID();

INSERT INTO customers (person_id, customer_id, password, customer_status, last_login_at, failed_login_attempts)
VALUES
    (@person1_id, @customer1_uuid, '$2a$10$Xt8Yt1EoWvZj3rZbgRlKXOQQhHRJlj7BKJJg5.yQzJ/Zs5pXVEJyO', 'ACTIVE', NOW(), 0),
    (@person2_id, @customer2_uuid, '$2a$10$Xt8Yt1EoWvZj3rZbgRlKXOQQhHRJlj7BKJJg5.yQzJ/Zs5pXVEJyO', 'ACTIVE', NOW(), 0),
    (@person3_id, @customer3_uuid, '$2a$10$Xt8Yt1EoWvZj3rZbgRlKXOQQhHRJlj7BKJJg5.yQzJ/Zs5pXVEJyO', 'ACTIVE', NOW(), 0),
    (@person4_id, @customer4_uuid, '$2a$10$Xt8Yt1EoWvZj3rZbgRlKXOQQhHRJlj7BKJJg5.yQzJ/Zs5pXVEJyO', 'ACTIVE', NOW(), 0),
    (@person5_id, @customer5_uuid, '$2a$10$Xt8Yt1EoWvZj3rZbgRlKXOQQhHRJlj7BKJJg5.yQzJ/Zs5pXVEJyO', 'INACTIVE', NOW(), 0);

INSERT INTO phones (number, type, primary_phone, notes, person_id, createdAt)
VALUES
    ('+12025550123', 'MOBILE', TRUE, 'Personal mobile', @person1_id, NOW()),
    ('+12025550124', 'HOME', FALSE, 'Home phone', @person1_id, NOW()),
    ('+12025550125', 'MOBILE', TRUE, 'Personal mobile', @person2_id, NOW()),
    ('+12025550126', 'WORK', FALSE, 'Office phone', @person2_id, NOW()),
    ('+12025550127', 'MOBILE', TRUE, 'Personal mobile', @person3_id, NOW()),
    ('+12025550128', 'MOBILE', TRUE, 'Personal mobile', @person4_id, NOW()),
    ('+12025550129', 'HOME', FALSE, 'Home phone', @person4_id, NOW()),
    ('+12025550130', 'MOBILE', TRUE, 'Personal mobile', @person5_id, NOW());

INSERT INTO accounts (uuid, accountNumber, accountType, initialBalance, currentBalance, status, currencyCode, customer_id, openedAt, createdAt)
VALUES
    (UUID(), '1000100010001', 'SAVINGS', 1000.00, 1250.50, 'ACTIVE', 'USD', @person1_id, NOW(), NOW()),
    (UUID(), '1000100010002', 'CHECKING', 2500.00, 1875.25, 'ACTIVE', 'USD', @person1_id, NOW(), NOW()),
    (UUID(), '1000100010003', 'SAVINGS', 5000.00, 5125.75, 'ACTIVE', 'USD', @person2_id, NOW(), NOW()),
    (UUID(), '1000100010004', 'CREDIT', 0.00, -500.00, 'ACTIVE', 'USD', @person3_id, NOW(), NOW()),
    (UUID(), '1000100010005', 'CHECKING', 3000.00, 3250.00, 'ACTIVE', 'USD', @person4_id, NOW(), NOW()),
    (UUID(), '1000100010006', 'SAVINGS', 10000.00, 10500.00, 'ACTIVE', 'USD', @person4_id, NOW(), NOW()),
    (UUID(), '1000100010007', 'FIXED_DEPOSIT', 25000.00, 25000.00, 'ACTIVE', 'USD', @person5_id, NOW(), NOW());

USE financial_movement;

CREATE TABLE persons
(
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    uuid           CHAR(36)    NOT NULL UNIQUE,
    identification VARCHAR(20) NOT NULL,
    firstName      VARCHAR(50) NOT NULL,
    lastName       VARCHAR(50) NOT NULL,
    gender         ENUM('MALE', 'FEMALE', 'OTHER') NOT NULL,
    age            INT         NOT NULL,
    birthDate      DATE,
    address        VARCHAR(255),
    email          VARCHAR(100),
    status         ENUM('ACTIVE', 'INACTIVE', 'SUSPENDED', 'DELETED', 'LOCKED', 'CLOSED') NOT NULL DEFAULT 'ACTIVE',
    createdAt      DATETIME    NOT NULL,
    updatedAt      DATETIME,
    createdBy      VARCHAR(50) NOT NULL,
    updatedBy      VARCHAR(50),
    version        BIGINT DEFAULT 0,
    CONSTRAINT uk_person_identification UNIQUE (identification),
    CONSTRAINT uk_person_email UNIQUE (email)
);

CREATE INDEX idx_person_last_name ON persons (lastName);
CREATE INDEX idx_person_status ON persons (status);

CREATE TABLE customers
(
    person_id             BIGINT PRIMARY KEY,
    customer_id           CHAR(36)     NOT NULL,
    password              VARCHAR(100) NOT NULL,
    customer_status       ENUM('ACTIVE', 'INACTIVE', 'SUSPENDED', 'DELETED', 'LOCKED', 'CLOSED') NOT NULL,
    last_login_at         DATETIME,
    password_reset_token  VARCHAR(100),
    password_reset_expiry DATETIME,
    failed_login_attempts INT DEFAULT 0,
    locked_until          DATETIME,
    CONSTRAINT uk_customer_customer_id UNIQUE (customer_id),
    CONSTRAINT fk_customer_person FOREIGN KEY (person_id) REFERENCES persons (id) ON DELETE CASCADE
);

CREATE TABLE accounts
(
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    uuid              CHAR(36)       NOT NULL UNIQUE,
    accountNumber     VARCHAR(20)    NOT NULL,
    accountType       ENUM('SAVINGS', 'CHECKING', 'LOAN', 'CREDIT', 'INVESTMENT', 'FIXED_DEPOSIT') NOT NULL,
    initialBalance    DECIMAL(19, 4) NOT NULL,
    currentBalance    DECIMAL(19, 4) NOT NULL,
    status            ENUM('ACTIVE', 'INACTIVE', 'FROZEN', 'CLOSED', 'PENDING_APPROVAL') NOT NULL,
    currencyCode      VARCHAR(3)     NOT NULL DEFAULT 'USD',
    customer_id       BIGINT         NOT NULL,
    openedAt          DATETIME       NOT NULL,
    lastTransactionAt DATETIME,
    closedAt          DATETIME,
    interestRate      DECIMAL(5, 2),
    overdraftLimit    DECIMAL(19, 4),
    createdAt         DATETIME       NOT NULL,
    updatedAt         DATETIME,
    CONSTRAINT uk_account_account_number UNIQUE (accountNumber),
    CONSTRAINT fk_account_customer FOREIGN KEY (customer_id) REFERENCES customers (person_id) ON DELETE CASCADE
);

CREATE INDEX idx_account_status ON accounts (status);
CREATE INDEX idx_account_customer_id ON accounts (customer_id);

CREATE TABLE movements
(
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    uuid             CHAR(36)       NOT NULL UNIQUE,
    movement_date    DATETIME       NOT NULL,
    movement_type    ENUM('DEPOSIT', 'WITHDRAWAL', 'TRANSFER_IN', 'TRANSFER_OUT', 'PAYMENT', 'INTEREST', 'FEE', 'ADJUSTMENT') NOT NULL,
    amount           DECIMAL(19, 4) NOT NULL,
    balance          DECIMAL(19, 4) NOT NULL,
    description      VARCHAR(255),
    account_id       BIGINT         NOT NULL,
    reference_number VARCHAR(50),
    created_at       DATETIME       NOT NULL,
    updated_at       DATETIME,
    status           ENUM('PENDING', 'PROCESSING', 'COMPLETED', 'FAILED', 'REVERSED') NOT NULL DEFAULT 'COMPLETED',
    CONSTRAINT fk_movement_account FOREIGN KEY (account_id) REFERENCES accounts (id) ON DELETE CASCADE
);

CREATE INDEX idx_movement_date ON movements (movement_date);
CREATE INDEX idx_movement_account_id ON movements (account_id);
CREATE INDEX idx_movement_status ON movements (status);

INSERT INTO persons (uuid, identification, firstName, lastName, gender, age, birthDate, address, email, status, createdAt, createdBy, version)
VALUES
    (@person1_uuid, 'ABCDE12345', 'John', 'Doe', 'MALE', 35, '1988-05-15', '123 Main St, Anytown', 'john.doe@example.com', 'ACTIVE', NOW(), 'SYSTEM', 0),
    (@person2_uuid, 'FGHIJ67890', 'Jane', 'Smith', 'FEMALE', 28, '1995-08-22', '456 Oak Ave, Somewhere', 'jane.smith@example.com', 'ACTIVE', NOW(), 'SYSTEM', 0),
    (@person3_uuid, 'KLMNO12345', 'Robert', 'Johnson', 'MALE', 42, '1981-03-10', '789 Pine Rd, Elsewhere', 'robert.johnson@example.com', 'ACTIVE', NOW(), 'SYSTEM', 0),
    (@person4_uuid, 'PQRST67890', 'Maria', 'Garcia', 'FEMALE', 31, '1992-11-05', '101 Cedar Ln, Nowhere', 'maria.garcia@example.com', 'ACTIVE', NOW(), 'SYSTEM', 0),
    (@person5_uuid, 'UVWXY12345', 'David', 'Brown', 'MALE', 45, '1978-07-30', '202 Elm St, Anywhere', 'david.brown@example.com', 'INACTIVE', NOW(), 'SYSTEM', 0);

SET @fm_person1_id = (SELECT id FROM persons WHERE uuid = @person1_uuid);
SET @fm_person2_id = (SELECT id FROM persons WHERE uuid = @person2_uuid);
SET @fm_person3_id = (SELECT id FROM persons WHERE uuid = @person3_uuid);
SET @fm_person4_id = (SELECT id FROM persons WHERE uuid = @person4_uuid);
SET @fm_person5_id = (SELECT id FROM persons WHERE uuid = @person5_uuid);

INSERT INTO customers (person_id, customer_id, password, customer_status, last_login_at, failed_login_attempts)
VALUES
    (@fm_person1_id, @customer1_uuid, '$2a$10$Xt8Yt1EoWvZj3rZbgRlKXOQQhHRJlj7BKJJg5.yQzJ/Zs5pXVEJyO', 'ACTIVE', NOW(), 0),
    (@fm_person2_id, @customer2_uuid, '$2a$10$Xt8Yt1EoWvZj3rZbgRlKXOQQhHRJlj7BKJJg5.yQzJ/Zs5pXVEJyO', 'ACTIVE', NOW(), 0),
    (@fm_person3_id, @customer3_uuid, '$2a$10$Xt8Yt1EoWvZj3rZbgRlKXOQQhHRJlj7BKJJg5.yQzJ/Zs5pXVEJyO', 'ACTIVE', NOW(), 0),
    (@fm_person4_id, @customer4_uuid, '$2a$10$Xt8Yt1EoWvZj3rZbgRlKXOQQhHRJlj7BKJJg5.yQzJ/Zs5pXVEJyO', 'ACTIVE', NOW(), 0),
    (@fm_person5_id, @customer5_uuid, '$2a$10$Xt8Yt1EoWvZj3rZbgRlKXOQQhHRJlj7BKJJg5.yQzJ/Zs5pXVEJyO', 'INACTIVE', NOW(), 0);

SET @account1_uuid = UUID();
SET @account2_uuid = UUID();
SET @account3_uuid = UUID();
SET @account4_uuid = UUID();
SET @account5_uuid = UUID();
SET @account6_uuid = UUID();
SET @account7_uuid = UUID();

INSERT INTO accounts (uuid, accountNumber, accountType, initialBalance, currentBalance, status, currencyCode, customer_id, openedAt, createdAt)
VALUES
    (@account1_uuid, '1000100010001', 'SAVINGS', 1000.00, 1250.50, 'ACTIVE', 'USD', @person1_uuid, NOW(), NOW()),
    (@account2_uuid, '1000100010002', 'CHECKING', 2500.00, 1875.25, 'ACTIVE', 'USD', @person1_uuid, NOW(), NOW()),
    (@account3_uuid, '1000100010003', 'SAVINGS', 5000.00, 5125.75, 'ACTIVE', 'USD', @person2_uuid, NOW(), NOW()),
    (@account4_uuid, '1000100010004', 'CREDIT', 0.00, -500.00, 'ACTIVE', 'USD', @person3_uuid, NOW(), NOW()),
    (@account5_uuid, '1000100010005', 'CHECKING', 3000.00, 3250.00, 'ACTIVE', 'USD', @person4_uuid, NOW(), NOW()),
    (@account6_uuid, '1000100010006', 'SAVINGS', 10000.00, 10500.00, 'ACTIVE', 'USD', @person4_uuid, NOW(), NOW()),
    (@account7_uuid, '1000100010007', 'FIXED_DEPOSIT', 25000.00, 25000.00, 'ACTIVE', 'USD', @person5_uuid, NOW(), NOW());

SET @account1_id = (SELECT id FROM accounts WHERE uuid = @account1_uuid);
SET @account2_id = (SELECT id FROM accounts WHERE uuid = @account2_uuid);
SET @account3_id = (SELECT id FROM accounts WHERE uuid = @account3_uuid);
SET @account4_id = (SELECT id FROM accounts WHERE uuid = @account4_uuid);
SET @account5_id = (SELECT id FROM accounts WHERE uuid = @account5_uuid);
SET @account6_id = (SELECT id FROM accounts WHERE uuid = @account6_uuid);
SET @account7_id = (SELECT id FROM accounts WHERE uuid = @account7_uuid);

INSERT INTO movements (uuid, movement_date, movement_type, amount, balance, description, account_id, reference_number, created_at, status)
VALUES
    (UUID(), DATE_SUB(NOW(), INTERVAL 30 DAY), 'DEPOSIT', 1000.00, 1000.00, 'Initial deposit', @account1_id, 'MOV1234567890-1234', DATE_SUB(NOW(), INTERVAL 30 DAY), 'COMPLETED'),
    (UUID(), DATE_SUB(NOW(), INTERVAL 25 DAY), 'DEPOSIT', 500.00, 1500.00, 'Salary deposit', @account1_id, 'MOV1234567891-2345', DATE_SUB(NOW(), INTERVAL 25 DAY), 'COMPLETED'),
    (UUID(), DATE_SUB(NOW(), INTERVAL 20 DAY), 'WITHDRAWAL', 250.00, 1250.00, 'ATM withdrawal', @account1_id, 'MOV1234567892-3456', DATE_SUB(NOW(), INTERVAL 20 DAY), 'COMPLETED'),
    (UUID(), DATE_SUB(NOW(), INTERVAL 15 DAY), 'INTEREST', 0.50, 1250.50, 'Monthly interest', @account1_id, 'MOV1234567893-4567', DATE_SUB(NOW(), INTERVAL 15 DAY), 'COMPLETED'),
    (UUID(), DATE_SUB(NOW(), INTERVAL 30 DAY), 'DEPOSIT', 2500.00, 2500.00, 'Initial deposit', @account2_id, 'MOV1234567894-5678', DATE_SUB(NOW(), INTERVAL 30 DAY), 'COMPLETED'),
    (UUID(), DATE_SUB(NOW(), INTERVAL 20 DAY), 'WITHDRAWAL', 500.00, 2000.00, 'Service payment', @account2_id, 'MOV1234567895-6789', DATE_SUB(NOW(), INTERVAL 20 DAY), 'COMPLETED'),
    (UUID(), DATE_SUB(NOW(), INTERVAL 15 DAY), 'WITHDRAWAL', 125.00, 1875.00, 'Supermarket purchase', @account2_id, 'MOV1234567896-7890', DATE_SUB(NOW(), INTERVAL 15 DAY), 'COMPLETED'),
    (UUID(), DATE_SUB(NOW(), INTERVAL 10 DAY), 'DEPOSIT', 0.25, 1875.25, 'Rounding adjustment', @account2_id, 'MOV1234567897-8901', DATE_SUB(NOW(), INTERVAL 10 DAY), 'COMPLETED'),
    (UUID(), DATE_SUB(NOW(), INTERVAL 30 DAY), 'DEPOSIT', 5000.00, 5000.00, 'Initial deposit', @account3_id, 'MOV1234567898-9012', DATE_SUB(NOW(), INTERVAL 30 DAY), 'COMPLETED'),
    (UUID(), DATE_SUB(NOW(), INTERVAL 15 DAY), 'INTEREST', 125.75, 5125.75, 'Quarterly interest', @account3_id, 'MOV1234567899-0123', DATE_SUB(NOW(), INTERVAL 15 DAY), 'COMPLETED'),
    (UUID(), DATE_SUB(NOW(), INTERVAL 30 DAY), 'DEPOSIT', 0.00, 0.00, 'Credit line opening', @account4_id, 'MOV1234567900-1234', DATE_SUB(NOW(), INTERVAL 30 DAY), 'COMPLETED'),
    (UUID(), DATE_SUB(NOW(), INTERVAL 20 DAY), 'WITHDRAWAL', 500.00, -500.00, 'Appliance purchase', @account4_id, 'MOV1234567901-2345', DATE_SUB(NOW(), INTERVAL 20 DAY), 'COMPLETED'),
    (UUID(), DATE_SUB(NOW(), INTERVAL 30 DAY), 'DEPOSIT', 3000.00, 3000.00, 'Initial deposit', @account5_id, 'MOV1234567902-3456', DATE_SUB(NOW(), INTERVAL 30 DAY), 'COMPLETED'),
    (UUID(), DATE_SUB(NOW(), INTERVAL 20 DAY), 'DEPOSIT', 500.00, 3500.00, 'Customer deposit', @account5_id, 'MOV1234567903-4567', DATE_SUB(NOW(), INTERVAL 20 DAY), 'COMPLETED'),
    (UUID(), DATE_SUB(NOW(), INTERVAL 10 DAY), 'WITHDRAWAL', 250.00, 3250.00, 'Expense withdrawal', @account5_id, 'MOV1234567904-5678', DATE_SUB(NOW(), INTERVAL 10 DAY), 'COMPLETED'),
    (UUID(), DATE_SUB(NOW(), INTERVAL 30 DAY), 'DEPOSIT', 10000.00, 10000.00, 'Initial deposit', @account6_id, 'MOV1234567905-6789', DATE_SUB(NOW(), INTERVAL 30 DAY), 'COMPLETED'),
    (UUID(), DATE_SUB(NOW(), INTERVAL 15 DAY), 'INTEREST', 500.00, 10500.00, 'Semiannual interest', @account6_id, 'MOV1234567906-7890', DATE_SUB(NOW(), INTERVAL 15 DAY), 'COMPLETED'),
    (UUID(), DATE_SUB(NOW(), INTERVAL 30 DAY), 'DEPOSIT', 25000.00, 25000.00, 'Fixed-term deposit', @account7_id, 'MOV1234567907-8901', DATE_SUB(NOW(), INTERVAL 30 DAY), 'COMPLETED');