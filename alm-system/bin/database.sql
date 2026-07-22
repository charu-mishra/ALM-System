CREATE DATABASE IF NOT EXISTS alm_system;
USE alm_system;

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS report_history;
DROP TABLE IF EXISTS scenario_results;
DROP TABLE IF EXISTS risk_metrics;
DROP TABLE IF EXISTS scenarios;
DROP TABLE IF EXISTS market_rates;
DROP TABLE IF EXISTS cash_flows;
DROP TABLE IF EXISTS liabilities;
DROP TABLE IF EXISTS assets;
DROP TABLE IF EXISTS users;

SET FOREIGN_KEY_CHECKS = 1;

-- 1. USERS TABLE
-- Stores application users such as admin, analyst, manager.
CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'ANALYST',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. ASSETS TABLE
-- Stores what the institution owns: loans, bonds, investments, cash.
CREATE TABLE assets (
    asset_id INT AUTO_INCREMENT PRIMARY KEY,
    asset_name VARCHAR(100) NOT NULL,
    asset_type VARCHAR(50) NOT NULL,
    principal_amount DECIMAL(20,2) NOT NULL,
    interest_rate DECIMAL(7,4) NOT NULL,
    rate_type VARCHAR(20) NOT NULL,
    maturity_date DATE,
    currency VARCHAR(10) DEFAULT 'INR',
    duration DECIMAL(10,4),
    is_rate_sensitive BOOLEAN DEFAULT FALSE,
    is_liquid BOOLEAN DEFAULT FALSE,
    credit_status VARCHAR(20) DEFAULT 'STANDARD',
    asset_status VARCHAR(20) DEFAULT 'ACTIVE'
);

-- 3. LIABILITIES TABLE
-- Stores what the institution owes: deposits, borrowings, debt.
CREATE TABLE liabilities (
    liability_id INT AUTO_INCREMENT PRIMARY KEY,
    liability_name VARCHAR(100) NOT NULL,
    liability_type VARCHAR(50) NOT NULL,
    principal_amount DECIMAL(20,2) NOT NULL,
    interest_rate DECIMAL(7,4) NOT NULL,
    rate_type VARCHAR(20) NOT NULL,
    maturity_date DATE,
    currency VARCHAR(10) DEFAULT 'INR',
    duration DECIMAL(10,4),
    is_rate_sensitive BOOLEAN DEFAULT FALSE,
    is_short_term BOOLEAN DEFAULT FALSE,
    liability_status VARCHAR(20) DEFAULT 'ACTIVE'
);

-- 4. CASH FLOWS TABLE
-- Stores expected inflows and outflows for liquidity risk analysis.
CREATE TABLE cash_flows (
    flow_id INT AUTO_INCREMENT PRIMARY KEY,
    asset_id INT NULL,
    liability_id INT NULL,
    flow_date DATE NOT NULL,
    amount DECIMAL(20,2) NOT NULL,
    flow_type VARCHAR(20) NOT NULL,

    CONSTRAINT fk_cashflow_asset
        FOREIGN KEY (asset_id) REFERENCES assets(asset_id)
        ON DELETE SET NULL
        ON UPDATE CASCADE,

    CONSTRAINT fk_cashflow_liability
        FOREIGN KEY (liability_id) REFERENCES liabilities(liability_id)
        ON DELETE SET NULL
        ON UPDATE CASCADE
);

-- 5. MARKET RATES TABLE
-- Stores reference interest rates from the market.
CREATE TABLE market_rates (
    rate_id INT AUTO_INCREMENT PRIMARY KEY,
    rate_date DATE NOT NULL,
    rate_type VARCHAR(50) NOT NULL,
    tenor_months INT NOT NULL,
    interest_rate DECIMAL(7,4) NOT NULL,
    currency VARCHAR(10) DEFAULT 'INR'
);

-- 6. SCENARIOS TABLE
-- Stores what-if simulations such as rate hike, liquidity stress, credit stress.
CREATE TABLE scenarios (
    scenario_id INT AUTO_INCREMENT PRIMARY KEY,
    scenario_name VARCHAR(100) NOT NULL,
    interest_rate_shift_bp INT DEFAULT 0,
    liquidity_shock_pct DECIMAL(7,4) DEFAULT 0.0000,
    credit_shock_pct DECIMAL(7,4) DEFAULT 0.0000,
    scenario_date DATE NOT NULL,
    description VARCHAR(255)
);

-- 7. RISK METRICS TABLE
-- Stores calculated risk results.
CREATE TABLE risk_metrics (
    metric_id INT AUTO_INCREMENT PRIMARY KEY,
    scenario_id INT NULL,
    reporting_date DATE NOT NULL,
    total_assets DECIMAL(20,2) DEFAULT 0.00,
    total_liabilities DECIMAL(20,2) DEFAULT 0.00,
    net_interest_income DECIMAL(20,2) DEFAULT 0.00,
    liquidity_ratio DECIMAL(10,4) DEFAULT 0.0000,
    duration_gap DECIMAL(10,4) DEFAULT 0.0000,
    credit_risk_score DECIMAL(10,4) DEFAULT 0.0000,

    CONSTRAINT fk_risk_metric_scenario
        FOREIGN KEY (scenario_id) REFERENCES scenarios(scenario_id)
        ON DELETE SET NULL
        ON UPDATE CASCADE
);

-- 8. SCENARIO RESULTS TABLE
-- Stores before-and-after values for scenario analysis.
CREATE TABLE scenario_results (
    result_id INT AUTO_INCREMENT PRIMARY KEY,
    scenario_id INT NOT NULL,
    metric_name VARCHAR(100) NOT NULL,
    baseline_value DECIMAL(20,2) DEFAULT 0.00,
    scenario_value DECIMAL(20,2) DEFAULT 0.00,
    impact_value DECIMAL(20,2) DEFAULT 0.00,

    CONSTRAINT fk_scenario_result_scenario
        FOREIGN KEY (scenario_id) REFERENCES scenarios(scenario_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

-- 9. REPORT HISTORY TABLE
-- Stores records of generated reports.
CREATE TABLE report_history (
    report_id INT AUTO_INCREMENT PRIMARY KEY,
    report_name VARCHAR(100) NOT NULL,
    report_type VARCHAR(50) NOT NULL,
    generated_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    generated_by INT NULL,
    summary VARCHAR(255),

    CONSTRAINT fk_report_user
        FOREIGN KEY (generated_by) REFERENCES users(user_id)
        ON DELETE SET NULL
        ON UPDATE CASCADE
);