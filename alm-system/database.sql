CREATE DATABASE IF NOT EXISTS alm_system;
USE alm_system;

CREATE TABLE IF NOT EXISTS users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS assets (
    asset_id INT PRIMARY KEY AUTO_INCREMENT,
    asset_name VARCHAR(100) NOT NULL,
    principal_amount DECIMAL(15,2) NOT NULL,
    interest_rate DECIMAL(5,2) NOT NULL,
    maturity_date DATE,
    currency VARCHAR(10),
    asset_type VARCHAR(50),
    rate_type VARCHAR(20)
);

CREATE TABLE IF NOT EXISTS liabilities (
    liability_id INT PRIMARY KEY AUTO_INCREMENT,
    liability_name VARCHAR(100) NOT NULL,
    principal_amount DECIMAL(15,2) NOT NULL,
    interest_rate DECIMAL(5,2) NOT NULL,
    maturity_date DATE,
    currency VARCHAR(10),
    liability_type VARCHAR(50),
    rate_type VARCHAR(20)
);

CREATE TABLE IF NOT EXISTS scenarios (
    scenario_id INT PRIMARY KEY AUTO_INCREMENT,
    scenario_name VARCHAR(100) NOT NULL,
    interest_change DECIMAL(5,2) NOT NULL,
    created_on DATE
);