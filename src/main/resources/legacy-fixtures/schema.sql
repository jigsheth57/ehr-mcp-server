DROP TABLE IF EXISTS patient_medications CASCADE;
DROP TABLE IF EXISTS patient_conditions CASCADE;
DROP TABLE IF EXISTS patient_allergies CASCADE;
DROP TABLE IF EXISTS patients CASCADE;

CREATE TABLE patients (
    patient_id VARCHAR(10) PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    age INT NOT NULL,
    gender VARCHAR(10) NOT NULL
);

CREATE TABLE patient_allergies (
    patient_id VARCHAR(10) REFERENCES patients(patient_id) ON DELETE CASCADE,
    allergy VARCHAR(100) NOT NULL
);

CREATE TABLE patient_conditions (
    patient_id VARCHAR(10) REFERENCES patients(patient_id) ON DELETE CASCADE,
    condition VARCHAR(100) NOT NULL
);

CREATE TABLE patient_medications (
    patient_id VARCHAR(10) REFERENCES patients(patient_id) ON DELETE CASCADE,
    medication VARCHAR(100) NOT NULL
);

CREATE INDEX idx_allergies_pid ON patient_allergies(patient_id);
CREATE INDEX idx_conditions_pid ON patient_conditions(patient_id);
CREATE INDEX idx_medications_pid ON patient_medications(patient_id);