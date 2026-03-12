      CREATE DATABASE usecase212_assets;
      \c usecase212_assets
      -- Table: buildings
      CREATE TABLE buildings (
          id INTEGER PRIMARY KEY,
          city TEXT NOT NULL,
          zip INTEGER NOT NULL,
          street TEXT NOT NULL  -- contains street name and house number
      );

      -- Table: contacts
      CREATE TABLE contacts (
          building_id INTEGER NOT NULL,
          role TEXT NOT NULL,  -- e.g., 'Hausmeister'
          email TEXT,
          phonenumber TEXT,
          first_name TEXT,
          last_name TEXT,
          FOREIGN KEY (building_id) REFERENCES buildings(id)
      );