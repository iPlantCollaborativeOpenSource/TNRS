CREATE TABLE reference (
  referenceID INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
  identifier VARCHAR(250) DEFAULT NULL,
  bibliographicCitationVerbatim VARCHAR(500) DEFAULT NULL,
  title VARCHAR(250) DEFAULT NULL,
  authors VARCHAR(250) DEFAULT NULL,
  publicationYear VARCHAR(12) DEFAULT NULL,
  issue VARCHAR(12) DEFAULT NULL,
  pages VARCHAR(12) DEFAULT NULL,
  publisher VARCHAR(250) DEFAULT NULL,
  isbn VARCHAR(250) DEFAULT NULL,
  url VARCHAR(500) DEFAULT NULL,
  PRIMARY KEY(referenceID)
)
ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

CREATE TABLE source (
  sourceID INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
  referenceID INTEGER UNSIGNED DEFAULT NULL,
  sourceName VARCHAR(250) DEFAULT NULL UNIQUE,
  sourceUrl VARCHAR(500) DEFAULT NULL,
  PRIMARY KEY(sourceID),
  INDEX source_FKIndex1(referenceID),
  INDEX source_Index1(sourceName),
  FOREIGN KEY(referenceID) REFERENCES reference(referenceID) ON DELETE  NO ACTION ON UPDATE CASCADE
)
ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

CREATE TABLE name (
  nameID INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
  scientificName VARCHAR(250) NOT NULL,
  scientificNameAuthorship VARCHAR(250) DEFAULT NULL,
  nameRank VARCHAR(25) DEFAULT NULL,
  nameParts INTEGER(1) DEFAULT NULL,
  namePublicationYear INTEGER UNSIGNED DEFAULT NULL,
  taxonomicStatus VARCHAR(250) DEFAULT NULL,
  taxonRemarks VARCHAR(250) DEFAULT NULL,
  namePublicationReferenceID INTEGER UNSIGNED DEFAULT NULL,
  scientificNameWithAuthor VARCHAR(250) NOT NULL,
  genusHybridX VARCHAR(1) DEFAULT NULL,
  genus VARCHAR(100) DEFAULT NULL,
  speciesHybridX VARCHAR(1) DEFAULT NULL,
  specificEpithet VARCHAR(100) DEFAULT NULL,
  rankIndicator VARCHAR(12) DEFAULT NULL COMMENT 'Rank abbreviation used to form name string, if applicable',
  infraspecificHybridX VARCHAR(1) DEFAULT NULL,
  infraspecificEpithet VARCHAR(100) DEFAULT NULL,
  infraspecificHybridX2 VARCHAR(1) DEFAULT NULL,
  infraspecificRank2 VARCHAR(15) DEFAULT NULL,
  infraspecificEpithet2 VARCHAR(100) DEFAULT NULL,
  otherEpithet VARCHAR(100) DEFAULT NULL COMMENT 'Epithets for tribe, section, etc.',
  isHybrid INT(1) NOT NULL DEFAULT 0,
  isNamedHybrid INT(1) DEFAULT NULL DEFAULT 0,
  isHybridFormula INT(1) DEFAULT NULL DEFAULT 0,
  defaultSubclass VARCHAR(100) DEFAULT NULL,
  defaultFamily VARCHAR(100) DEFAULT NULL,
  defaultAcceptedNameID INTEGER UNSIGNED DEFAULT NULL,
  defaultAcceptance VARCHAR(1) DEFAULT NULL,
  PRIMARY KEY(nameID),
  INDEX name_FK1(namePublicationReferenceID),
  INDEX name_scientificName(scientificName),
  INDEX name_scientificNameAuthorship(scientificNameAuthorship),
  INDEX name_nameRank(nameRank),
  INDEX name_nameParts(nameParts),
  INDEX name_taxonomicStatus(taxonomicStatus),
  INDEX name_scientificNameWithAuthor(scientificNameWithAuthor),
  INDEX name_isHybrid(isHybrid),
  INDEX name_genus(genus),
  INDEX name_specificEpithet(specificEpithet),
  INDEX name_otherEpithet(otherEpithet),
  INDEX name_rankIndicator(rankIndicator),
  INDEX name_infraspecificEpithet(infraspecificEpithet),
  INDEX name_infraspecificRank2(infraspecificRank2),
  INDEX name_infraspecificEpithet2(infraspecificEpithet2),
  INDEX name_defaultSubclass(defaultSubclass),
  INDEX name_defaultFamily(defaultFamily),
  INDEX name_FK_defaultAcceptedNameID(defaultAcceptedNameID),
  INDEX name_defaultAcceptance(defaultAcceptance),
  FOREIGN KEY(namePublicationReferenceID) REFERENCES reference(referenceID) ON DELETE NO ACTION ON UPDATE CASCADE
)
ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

CREATE TABLE name_source (
  nameSourceID INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
  nameID INTEGER UNSIGNED NOT NULL,
  sourceID INTEGER UNSIGNED NOT NULL,
  nameSourceUrl VARCHAR(250) DEFAULT NULL,
  nameSourceOriginalID VARCHAR(250) DEFAULT NULL,
  dateAccessed DATE DEFAULT NULL,
  dateCreated TIMESTAMP NOT NULL,
  PRIMARY KEY(nameSourceID),
  INDEX nameSource_nameSourceOriginalID(nameSourceOriginalID),
  INDEX nameSource_FKIndex1(nameID),
  INDEX nameSource_FKIndex2(sourceID),
  FOREIGN KEY(nameID) REFERENCES name(nameID) ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY(sourceID) REFERENCES source(sourceID) ON DELETE RESTRICT ON UPDATE CASCADE
)
ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

CREATE TABLE classification (
  classificationID INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
  sourceID INTEGER UNSIGNED NOT NULL,
  nameID INTEGER UNSIGNED NOT NULL,
  parentNameID INTEGER UNSIGNED DEFAULT NULL,
  leftIndex INTEGER UNSIGNED DEFAULT NULL,
  rightIndex INTEGER UNSIGNED DEFAULT NULL,
  PRIMARY KEY(classificationID),
  INDEX classification_default_FKIndex1(parentNameID),
  INDEX classification_default_FKIndex2(nameID),
  INDEX classification_FKIndex3(sourceID),
  INDEX classification_leftIndex(leftIndex),
  INDEX classification_rightIndex(rightIndex),
  FOREIGN KEY(parentNameID) REFERENCES name(nameID) ON DELETE RESTRICT ON UPDATE CASCADE,
  FOREIGN KEY(nameID) REFERENCES name(nameID) ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY(sourceID) REFERENCES source(sourceID) ON DELETE RESTRICT ON UPDATE CASCADE
)
ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

CREATE TABLE `synonym` (
  synonymID INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
  sourceID INTEGER UNSIGNED NOT NULL,
  nameID INTEGER UNSIGNED NOT NULL,
  acceptedNameID INTEGER UNSIGNED DEFAULT NULL,
  acceptance varchar(1) DEFAULT NULL,
  synonymType VARCHAR(250) DEFAULT NULL,
  synonymSourceUrl VARCHAR(250) DEFAULT NULL,
  referenceID INTEGER UNSIGNED DEFAULT NULL,
  PRIMARY KEY(synonymID),
  INDEX synonym_FKIndex1(acceptedNameID),
  INDEX synonym_acceptance(acceptance),
  INDEX synonym_FKIndex2(nameID),
  INDEX synonym_FKIndex3(sourceID),
  INDEX synonym_FKIndex4(referenceID),
  INDEX synonym_synonymType(synonymType),
  FOREIGN KEY(nameID) REFERENCES name(nameID) ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY(sourceID) REFERENCES source(sourceID) ON DELETE RESTRICT ON UPDATE CASCADE,
  FOREIGN KEY(referenceID) REFERENCES reference(referenceID) ON DELETE NO ACTION ON UPDATE CASCADE
)
ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;


