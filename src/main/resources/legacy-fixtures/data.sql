-- 20 Diverse Clinical Records (P101 - P120)

-- P101: Penicillin & Amoxicillin Anaphylaxis Case (Primary Demo Trigger)
INSERT INTO patients (patient_id, first_name, last_name, age, gender) VALUES ('P101', 'John', 'Doe', 45, 'MALE');
INSERT INTO patient_allergies (patient_id, allergy) VALUES ('P101', 'Penicillin'), ('P101', 'Amoxicillin');
INSERT INTO patient_conditions (patient_id, condition) VALUES ('P101', 'Hypertension'), ('P101', 'Obesity Class I');
INSERT INTO patient_medications (patient_id, medication) VALUES ('P101', 'Lisinopril 10mg');

-- P102: Sulfa Allergy / Asthma
INSERT INTO patients (patient_id, first_name, last_name, age, gender) VALUES ('P102', 'Jane', 'Smith', 32, 'FEMALE');
INSERT INTO patient_allergies (patient_id, allergy) VALUES ('P102', 'Sulfa drugs');
INSERT INTO patient_conditions (patient_id, condition) VALUES ('P102', 'Moderate Persistent Asthma');
INSERT INTO patient_medications (patient_id, medication) VALUES ('P102', 'Albuterol HFA Inhaler'), ('P102', 'Fluticasone 110mcg');

-- P103: NSAID / Aspirin Allergy / Peptic Ulcer
INSERT INTO patients (patient_id, first_name, last_name, age, gender) VALUES ('P103', 'Robert', 'Brown', 58, 'MALE');
INSERT INTO patient_allergies (patient_id, allergy) VALUES ('P103', 'Aspirin'), ('P103', 'Ibuprofen (NSAIDs)');
INSERT INTO patient_conditions (patient_id, condition) VALUES ('P103', 'Peptic Ulcer Disease'), ('P103', 'Osteoarthritis');
INSERT INTO patient_medications (patient_id, medication) VALUES ('P103', 'Acetaminophen 500mg PRN'), ('P103', 'Pantoprazole 40mg');

-- P104: NKDA (No Known Drug Allergies) / GAD
INSERT INTO patients (patient_id, first_name, last_name, age, gender) VALUES ('P104', 'Emily', 'Davis', 29, 'FEMALE');
INSERT INTO patient_conditions (patient_id, condition) VALUES ('P104', 'Generalized Anxiety Disorder');
INSERT INTO patient_medications (patient_id, medication) VALUES ('P104', 'Sertraline 50mg');

-- P105: Fluoroquinolone Contraindication / Diabetes
INSERT INTO patients (patient_id, first_name, last_name, age, gender) VALUES ('P105', 'Michael', 'Wilson', 63, 'MALE');
INSERT INTO patient_allergies (patient_id, allergy) VALUES ('P105', 'Ciprofloxacin (Fluoroquinolones)');
INSERT INTO patient_conditions (patient_id, condition) VALUES ('P105', 'Type 2 Diabetes Mellitus'), ('P105', 'Diabetic Peripheral Neuropathy');
INSERT INTO patient_medications (patient_id, medication) VALUES ('P105', 'Metformin 1000mg BID'), ('P105', 'Gabapentin 300mg TID');

-- P106: Codeine Allergy / Anticoagulation
INSERT INTO patients (patient_id, first_name, last_name, age, gender) VALUES ('P106', 'Sarah', 'Miller', 71, 'FEMALE');
INSERT INTO patient_allergies (patient_id, allergy) VALUES ('P106', 'Codeine');
INSERT INTO patient_conditions (patient_id, condition) VALUES ('P106', 'Non-valvular Atrial Fibrillation'), ('P106', 'Osteoporosis');
INSERT INTO patient_medications (patient_id, medication) VALUES ('P106', 'Apixaban 5mg BID'), ('P106', 'Alendronate 70mg weekly');

-- P107: Latex Allergy / Environmental Rhinitis
INSERT INTO patients (patient_id, first_name, last_name, age, gender) VALUES ('P107', 'David', 'Taylor', 40, 'MALE');
INSERT INTO patient_allergies (patient_id, allergy) VALUES ('P107', 'Latex');
INSERT INTO patient_conditions (patient_id, condition) VALUES ('P107', 'Perennial Allergic Rhinitis');
INSERT INTO patient_medications (patient_id, medication) VALUES ('P107', 'Fluticasone Propionate Nasal Spray');

-- P108: ACE Inhibitor Induced Angioedema / Cardiac
INSERT INTO patients (patient_id, first_name, last_name, age, gender) VALUES ('P108', 'Jessica', 'Anderson', 52, 'FEMALE');
INSERT INTO patient_allergies (patient_id, allergy) VALUES ('P108', 'Lisinopril (ACE Inhibitors)');
INSERT INTO patient_conditions (patient_id, condition) VALUES ('P108', 'Essential Hypertension'), ('P108', 'Hyperlipidemia');
INSERT INTO patient_medications (patient_id, medication) VALUES ('P108', 'Losartan 50mg'), ('P108', 'Atorvastatin 20mg');

-- P109: Iodine / Contrast Dye / Renal Impairment
INSERT INTO patients (patient_id, first_name, last_name, age, gender) VALUES ('P109', 'James', 'Thomas', 67, 'MALE');
INSERT INTO patient_allergies (patient_id, allergy) VALUES ('P109', 'Iodinated Radiocontrast'), ('P109', 'Shellfish');
INSERT INTO patient_conditions (patient_id, condition) VALUES ('P109', 'Chronic Kidney Disease Stage 3b');
INSERT INTO patient_medications (patient_id, medication) VALUES ('P109', 'Amlodipine 5mg');

-- P110: Cephalosporin Sensitivity / Migraine
INSERT INTO patients (patient_id, first_name, last_name, age, gender) VALUES ('P110', 'Amanda', 'Jackson', 38, 'FEMALE');
INSERT INTO patient_allergies (patient_id, allergy) VALUES ('P110', 'Cephalexin (Cephalosporins)');
INSERT INTO patient_conditions (patient_id, condition) VALUES ('P110', 'Chronic Intractable Migraine');
INSERT INTO patient_medications (patient_id, medication) VALUES ('P110', 'Sumatriptan 50mg PRN'), ('P110', 'Propranolol 40mg BID');

-- P111: No Allergies / GERD
INSERT INTO patients (patient_id, first_name, last_name, age, gender) VALUES ('P111', 'Daniel', 'White', 50, 'MALE');
INSERT INTO patient_conditions (patient_id, condition) VALUES ('P111', 'Gastroesophageal Reflux Disease (GERD)');
INSERT INTO patient_medications (patient_id, medication) VALUES ('P111', 'Omeprazole 20mg daily');

-- P112: Penicillin Allergy / Endocrine
INSERT INTO patients (patient_id, first_name, last_name, age, gender) VALUES ('P112', 'Olivia', 'Harris', 24, 'FEMALE');
INSERT INTO patient_allergies (patient_id, allergy) VALUES ('P112', 'Penicillin G');
INSERT INTO patient_conditions (patient_id, condition) VALUES ('P112', 'Polycystic Ovary Syndrome (PCOS)');
INSERT INTO patient_medications (patient_id, medication) VALUES ('P112', 'Spironolactone 50mg');

-- P113: Tetracycline Sensitivity / COPD
INSERT INTO patients (patient_id, first_name, last_name, age, gender) VALUES ('P113', 'Matthew', 'Martin', 61, 'MALE');
INSERT INTO patient_allergies (patient_id, allergy) VALUES ('P113', 'Doxycycline (Tetracyclines)');
INSERT INTO patient_conditions (patient_id, condition) VALUES ('P113', 'COPD Gold Stage II');
INSERT INTO patient_medications (patient_id, medication) VALUES ('P113', 'Tiotropium Bromide Respimat');

-- P114: Morphine / Opioid Intolerance / Chronic Pain
INSERT INTO patients (patient_id, first_name, last_name, age, gender) VALUES ('P114', 'Sophia', 'Thompson', 44, 'FEMALE');
INSERT INTO patient_allergies (patient_id, allergy) VALUES ('P114', 'Morphine Sulfate');
INSERT INTO patient_conditions (patient_id, condition) VALUES ('P114', 'Fibromyalgia Syndrome');
INSERT INTO patient_medications (patient_id, medication) VALUES ('P114', 'Duloxetine 60mg daily');

-- P115: No Allergies / IBD
INSERT INTO patients (patient_id, first_name, last_name, age, gender) VALUES ('P115', 'Andrew', 'Garcia', 35, 'MALE');
INSERT INTO patient_conditions (patient_id, condition) VALUES ('P115', 'Ulcerative Colitis');
INSERT INTO patient_medications (patient_id, medication) VALUES ('P115', 'Mesalamine 1.2g TID');

-- P116: Sulfa Allergy / Severe Heart Failure
INSERT INTO patients (patient_id, first_name, last_name, age, gender) VALUES ('P116', 'Isabella', 'Martinez', 68, 'FEMALE');
INSERT INTO patient_allergies (patient_id, allergy) VALUES ('P116', 'Sulfamethoxazole');
INSERT INTO patient_conditions (patient_id, condition) VALUES ('P116', 'Congestive Heart Failure (HFrEF, EF 30%)');
INSERT INTO patient_medications (patient_id, medication) VALUES ('P116', 'Furosemide 40mg daily'), ('P116', 'Carvedilol 12.5mg BID');

-- P117: Macrolide Allergy / Gout
INSERT INTO patients (patient_id, first_name, last_name, age, gender) VALUES ('P117', 'Joshua', 'Robinson', 55, 'MALE');
INSERT INTO patient_allergies (patient_id, allergy) VALUES ('P117', 'Azithromycin (Macrolides)'), ('P117', 'Clarithromycin');
INSERT INTO patient_conditions (patient_id, condition) VALUES ('P117', 'Chronic Hyperuricemia with Gout');
INSERT INTO patient_medications (patient_id, medication) VALUES ('P117', 'Allopurinol 300mg daily');

-- P118: Severe Anaphylactic Food & Drug Cross-Sensitivities
INSERT INTO patients (patient_id, first_name, last_name, age, gender) VALUES ('P118', 'Mia', 'Clark', 19, 'FEMALE');
INSERT INTO patient_allergies (patient_id, allergy) VALUES ('P118', 'Peanuts'), ('P118', 'Tree Nuts'), ('P118', 'Amoxicillin');
INSERT INTO patient_conditions (patient_id, condition) VALUES ('P118', 'Severe Atopic Dermatitis');
INSERT INTO patient_medications (patient_id, medication) VALUES ('P118', 'Cetirizine 10mg daily');

-- P119: Heparin Induced Thrombocytopenia (HIT)
INSERT INTO patients (patient_id, first_name, last_name, age, gender) VALUES ('P119', 'Ethan', 'Rodriguez', 73, 'MALE');
INSERT INTO patient_allergies (patient_id, allergy) VALUES ('P119', 'Heparin (HIT Type II)');
INSERT INTO patient_conditions (patient_id, condition) VALUES ('P119', 'Deep Vein Thrombosis (History)');
INSERT INTO patient_medications (patient_id, medication) VALUES ('P119', 'Rivaroxaban 20mg daily');

-- P120: Cross-Reactive Beta-Lactam / Autoimmune
INSERT INTO patients (patient_id, first_name, last_name, age, gender) VALUES ('P120', 'Charlotte', 'Lewis', 48, 'FEMALE');
INSERT INTO patient_allergies (patient_id, allergy) VALUES ('P120', 'Penicillin V'), ('P120', 'Cephalexin'), ('P120', 'Ampicillin');
INSERT INTO patient_conditions (patient_id, condition) VALUES ('P120', 'Seropositive Rheumatoid Arthritis'), ('P120', 'Hypothyroidism');
INSERT INTO patient_medications (patient_id, medication) VALUES ('P120', 'Methotrexate 15mg weekly'), ('P120', 'Levothyroxine 75mcg');