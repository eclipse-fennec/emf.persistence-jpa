-- Enhanced database schema with sample data for OneToMany containment testing
-- This file demonstrates the expected database structure for EMF containment relationships
-- NOTE: Folder renamed from "civitas" to "glt" for consistency

-- Drop tables if they exist (for H2 compatibility)
DROP TABLE IF EXISTS contacts;
DROP TABLE IF EXISTS buildings;

-- Table: buildings
CREATE TABLE buildings (
    id INTEGER PRIMARY KEY,
    city TEXT NOT NULL,
    zip INTEGER NOT NULL,
    street TEXT NOT NULL  -- contains street name and house number
);

-- Table: contacts (child table with foreign key to buildings)
CREATE TABLE contacts (
    building_id INTEGER NOT NULL,  -- FK to buildings.id (containment relationship)
    role TEXT NOT NULL,            -- e.g., 'Hausmeister', 'Owner', 'Tenant'
    email TEXT,
    phonenumber TEXT,
    first_name TEXT,
    last_name TEXT,
    FOREIGN KEY (building_id) REFERENCES buildings(id)
);

-- Sample data for buildings
INSERT INTO buildings (id, city, zip, street) VALUES
    (1, 'Berlin', 10115, 'Alexanderplatz 1'),
    (2, 'Hamburg', 20095, 'Hafenstraße 42'),
    (3, 'Munich', 80331, 'Marienplatz 8');

-- Sample data for contacts (multiple contacts per building to test OneToMany)
INSERT INTO contacts (building_id, role, email, phonenumber, first_name, last_name) VALUES
    -- Building 1 contacts
    (1, 'Hausmeister', 'max.mueller@example.com', '+49-30-12345678', 'Max', 'Mueller'),
    (1, 'Owner', 'anna.schmidt@example.com', '+49-30-87654321', 'Anna', 'Schmidt'),
    (1, 'Tenant', 'peter.wagner@example.com', '+49-30-11111111', 'Peter', 'Wagner'),
    
    -- Building 2 contacts  
    (2, 'Hausmeister', 'hans.fischer@example.com', '+49-40-22222222', 'Hans', 'Fischer'),
    (2, 'Owner', 'maria.weber@example.com', '+49-40-33333333', 'Maria', 'Weber'),
    
    -- Building 3 contacts
    (3, 'Hausmeister', 'klaus.bauer@example.com', '+49-89-44444444', 'Klaus', 'Bauer'),
    (3, 'Owner', 'sabine.meyer@example.com', '+49-89-55555555', 'Sabine', 'Meyer'),
    (3, 'Tenant', 'thomas.schulz@example.com', '+49-89-66666666', 'Thomas', 'Schulz'),
    (3, 'Tenant', 'lisa.hoffmann@example.com', '+49-89-77777777', 'Lisa', 'Hoffmann');

-- Verify data integrity
-- Expected result: Should show building with their contacts count
SELECT 
    b.id as building_id,
    b.city,
    b.street,
    COUNT(c.building_id) as contact_count
FROM buildings b
LEFT JOIN contacts c ON b.id = c.building_id
GROUP BY b.id, b.city, b.street
ORDER BY b.id;