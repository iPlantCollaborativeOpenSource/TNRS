SET FOREIGN_KEY_CHECKS=0;
USE `taxamatch`;

#
# Structure for the `auth_abbrev_tropicos` table :
#

CREATE TABLE `auth_abbrev_tropicos` (
  `AUTH_ABBR` varchar(100) NOT NULL,
  `AUTH_FULL` varchar(200) NOT NULL,
  UNIQUE KEY `AUTH_ABBR` (`AUTH_ABBR`,`AUTH_FULL`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

#
# Structure for the `genlist_tropicos` table :
#

CREATE TABLE `genlist_tropicos` (
  `GENUS_ID` int(11) NOT NULL auto_increment,
  `GENUS` varchar(50) default NULL,
  `AUTHORITY` varchar(150) default NULL,
  `GEN_LENGTH` int(2) default NULL,
  `NEAR_MATCH_GENUS` varchar(50) default NULL,
  `SEARCH_GENUS_NAME` varchar(50) default NULL,
  PRIMARY KEY  (`GENUS_ID`),
  UNIQUE KEY `GENUS_ID_2` (`GENUS_ID`),
  KEY `GENUS_ID` (`GENUS_ID`),
  KEY `GEN_LENGTH` (`GEN_LENGTH`),
  KEY `NEAR_MATCH_GENUS` (`NEAR_MATCH_GENUS`)
) ENGINE=MyISAM AUTO_INCREMENT=65792 DEFAULT CHARSET=utf8;

#
# Structure for the `genus_id_matches_tropicos` table :
#

CREATE TABLE `genus_id_matches_tropicos` (
  `genus_id` varchar(150) NOT NULL,
  `genus` varchar(50) default NULL,
  `genus_ed` int(11) default NULL,
  `phonetic_flag` char(1) default NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

#
# Structure for the `species_id_matches_tropicos` table :
#

CREATE TABLE `species_id_matches_tropicos` (
  `species_id` varchar(150) NOT NULL,
  `genus_species` varchar(50) default NULL,
  `genus_ed` int(11) default NULL,
  `species_ed` int(11) default NULL,
  `gen_sp_ed` int(11) default NULL,
  `phonetic_flag` char(1) default NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

#
# Structure for the `splist_tropicos` table :
#

CREATE TABLE `splist_tropicos` (
  `SPECIES_ID` varchar(150) NOT NULL,
  `GENUS_ORIG` varchar(50) default NULL,
  `SPECIES` varchar(60) default NULL,
  `GENUS_ID` varchar(150) default NULL,
  `AUTHORITY` varchar(150) default NULL,
  `SP_LENGTH` int(2) default NULL,
  `NEAR_MATCH_SPECIES` varchar(60) default NULL,
  `SEARCH_SPECIES_NAME` varchar(60) default NULL,
  KEY `GENUS_ID` (`GENUS_ID`),
  KEY `SP_LENGTH` (`SP_LENGTH`),
  KEY `NEAR_MATCH_SPECIES` (`NEAR_MATCH_SPECIES`),
  KEY `SPECIES_ID` (`SPECIES_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

