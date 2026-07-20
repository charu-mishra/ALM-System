USE alm_system;

-- 1. USERS DATA
INSERT INTO users (username, password_hash, role)
VALUES
('admin', 'admin123', 'ADMIN'),
('analyst', 'analyst123', 'ANALYST'),
('manager', 'manager123', 'MANAGER');

-- 2. ASSETS DATA
INSERT INTO assets (
    asset_name,
    asset_type,
    principal_amount,
    interest_rate,
    rate_type,
    maturity_date,
    currency,
    duration,
    is_rate_sensitive,
    is_liquid,
    credit_status,
    asset_status
)
VALUES
(
    'Home Loan Portfolio',
    'LOAN',
    1000000.00,
    8.5000,
    'FIXED',
    '2030-12-31',
    'INR',
    4.5000,
    TRUE,
    FALSE,
    'STANDARD',
    'ACTIVE'
),
(
    'Corporate Bond Investment',
    'BOND',
    2000000.00,
    7.2000,
    'FIXED',
    '2029-06-30',
    'INR',
    3.8000,
    TRUE,
    FALSE,
    'STANDARD',
    'ACTIVE'
),
(
    'Cash Reserve',
    'CASH',
    500000.00,
    3.5000,
    'VARIABLE',
    NULL,
    'INR',
    0.1000,
    FALSE,
    TRUE,
    'STANDARD',
    'ACTIVE'
),
(
    'Overdue Business Loan',
    'LOAN',
    750000.00,
    10.0000,
    'VARIABLE',
    '2028-03-31',
    'INR',
    2.7000,
    TRUE,
    FALSE,
    'OVERDUE',
    'ACTIVE'
);

-- 3. LIABILITIES DATA
INSERT INTO liabilities (
    liability_name,
    liability_type,
    principal_amount,
    interest_rate,
    rate_type,
    maturity_date,
    currency,
    duration,
    is_rate_sensitive,
    is_short_term,
    liability_status
)
VALUES
(
    'Savings Deposits',
    'DEPOSIT',
    800000.00,
    4.0000,
    'VARIABLE',
    NULL,
    'INR',
    0.5000,
    TRUE,
    TRUE,
    'ACTIVE'
),
(
    'Fixed Deposits',
    'DEPOSIT',
    1200000.00,
    6.5000,
    'FIXED',
    '2027-06-30',
    'INR',
    2.0000,
    TRUE,
    FALSE,
    'ACTIVE'
),
(
    'Interbank Borrowing',
    'BORROWING',
    700000.00,
    6.8000,
    'VARIABLE',
    '2026-03-31',
    'INR',
    1.2000,
    TRUE,
    TRUE,
    'ACTIVE'
);

-- 4. CASH FLOWS DATA
-- Inflows come from assets.
-- Outflows come from liabilities.
INSERT INTO cash_flows (
    asset_id,
    liability_id,
    flow_date,
    amount,
    flow_type
)
VALUES
(1, NULL, '2026-01-31', 85000.00, 'INFLOW'),
(2, NULL, '2026-01-31', 144000.00, 'INFLOW'),
(3, NULL, '2026-01-31', 15000.00, 'INFLOW'),
(NULL, 1, '2026-01-31', 32000.00, 'OUTFLOW'),
(NULL, 2, '2026-01-31', 78000.00, 'OUTFLOW'),
(NULL, 3, '2026-01-31', 47600.00, 'OUTFLOW');

-- 5. MARKET RATES DATA
INSERT INTO market_rates (
    rate_date,
    rate_type,
    tenor_months,
    interest_rate,
    currency
)
VALUES
('2026-01-01', 'DEPOSIT_RATE', 1, 4.0000, 'INR'),
('2026-01-01', 'DEPOSIT_RATE', 12, 6.5000, 'INR'),
('2026-01-01', 'LOAN_RATE', 12, 8.5000, 'INR'),
('2026-01-01', 'BOND_RATE', 60, 7.2000, 'INR'),
('2026-01-01', 'INTERBANK_RATE', 6, 6.8000, 'INR');

-- 6. SCENARIOS DATA
INSERT INTO scenarios (
    scenario_name,
    interest_rate_shift_bp,
    liquidity_shock_pct,
    credit_shock_pct,
    scenario_date,
    description
)
VALUES
(
    'Base Case',
    0,
    0.0000,
    0.0000,
    '2026-01-01',
    'Normal market condition without any shock'
),
(
    'Interest Rate Hike +1%',
    100,
    0.0000,
    0.0000,
    '2026-01-01',
    'Interest rates increase by 100 basis points or 1 percent'
),
(
    'Interest Rate Cut -0.5%',
    -50,
    0.0000,
    0.0000,
    '2026-01-01',
    'Interest rates decrease by 50 basis points or 0.5 percent'
),
(
    'Liquidity Stress',
    0,
    15.0000,
    0.0000,
    '2026-01-01',
    'Institution faces 15 percent liquidity stress'
),
(
    'Credit Stress',
    0,
    0.0000,
    10.0000,
    '2026-01-01',
    'Credit risk increases by 10 percent'
);

-- 7. RISK METRICS DATA
INSERT INTO risk_metrics (
    scenario_id,
    reporting_date,
    total_assets,
    total_liabilities,
    net_interest_income,
    liquidity_ratio,
    duration_gap,
    credit_risk_score
)
VALUES
(
    1,
    '2026-01-01',
    4250000.00,
    2700000.00,
    86500.00,
    1.5741,
    1.8500,
    0.2500
),
(
    2,
    '2026-01-01',
    4250000.00,
    2700000.00,
    72000.00,
    1.5741,
    2.1000,
    0.2500
),
(
    4,
    '2026-01-01',
    4250000.00,
    2700000.00,
    86500.00,
    1.3370,
    1.8500,
    0.2500
);

-- 8. SCENARIO RESULTS DATA
INSERT INTO scenario_results (
    scenario_id,
    metric_name,
    baseline_value,
    scenario_value,
    impact_value
)
VALUES
(
    2,
    'Net Interest Income',
    86500.00,
    72000.00,
    -14500.00
),
(
    3,
    'Net Interest Income',
    86500.00,
    94000.00,
    7500.00
),
(
    4,
    'Liquidity Ratio',
    1.5741,
    1.3370,
    -0.2371
),
(
    5,
    'Credit Risk Score',
    0.2500,
    0.3500,
    0.1000
);

-- 9. REPORT HISTORY DATA
INSERT INTO report_history (
    report_name,
    report_type,
    generated_by,
    summary
)
VALUES
(
    'Initial Asset Report',
    'ASSET_REPORT',
    1,
    'Summary of assets loaded during initial database setup'
),
(
    'Initial Liability Report',
    'LIABILITY_REPORT',
    1,
    'Summary of liabilities loaded during initial database setup'
),
(
    'Initial Risk Report',
    'RISK_REPORT',
    2,
    'Baseline risk metrics generated for ALM system'
),
(
    'Scenario Analysis Report',
    'SCENARIO_REPORT',
    2,
    'Impact analysis for interest rate, liquidity, and credit stress scenarios'
);