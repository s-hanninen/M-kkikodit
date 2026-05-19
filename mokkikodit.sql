DROP DATABASE IF EXISTS mokkikodit;
CREATE DATABASE mokkikodit;
USE mokkikodit;

CREATE TABLE asiakas (
  etu_nimi VARCHAR(100) NOT NULL,
  suku_nimi VARCHAR(100) NOT NULL,
  asiakas_id INT NOT NULL AUTO_INCREMENT,
  puh_nro VARCHAR(100),
  sposti VARCHAR(100),
  osoite VARCHAR(255) NOT NULL,
  last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (asiakas_id),
  KEY idx_suku_nimi (suku_nimi)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE tyontekija (
  etu_nimi VARCHAR(100) NOT NULL,
  suku_nimi VARCHAR(100) NOT NULL,
  tyontekija_id INT NOT NULL AUTO_INCREMENT,
  sposti VARCHAR(100),
  last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (tyontekija_id),
  KEY idx_suku_nimi (suku_nimi)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE mokki (
  nimi VARCHAR(100) NOT NULL,
  sijainti VARCHAR(255) NOT NULL,
  mokki_id INT NOT NULL AUTO_INCREMENT,
  kapasiteetti INT NOT NULL,
  hinta DECIMAL(10,2) NOT NULL,
  last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (mokki_id),
  KEY idx_suku_nimi (nimi)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE varaus (
  varaus_id INT NOT NULL AUTO_INCREMENT,
  asiakas_id INT NOT NULL,
  mokki_id INT NOT NULL,
  tyontekija_id INT NOT NULL,
  alku_pvm DATE NOT NULL,
  loppu_pvm DATE NOT NULL,
  last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (varaus_id),
  FOREIGN KEY (asiakas_id) REFERENCES asiakas(asiakas_id),
  FOREIGN KEY (mokki_id) REFERENCES mokki(mokki_id),
  FOREIGN KEY (tyontekija_id) REFERENCES tyontekija(tyontekija_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE lasku (
  lasku_id INT NOT NULL AUTO_INCREMENT,
  summa DECIMAL(10,2) NOT NULL,
  erapaiva DATE NOT NULL,
  varaus_id INT NOT NULL,
  last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (lasku_id),
  FOREIGN KEY (varaus_id) REFERENCES varaus(varaus_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


INSERT INTO mokkikodit.asiakas (etu_nimi, suku_nimi, puh_nro, sposti, osoite) VALUES ('Maija', 'Meikäläinen', '0401234567', 'maijameikalainen@gmail.com', 'Helsinginkatu 1A, 01000 Helsinki');

SELECT * FROM asiakas;
