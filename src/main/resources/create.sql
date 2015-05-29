SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";

CREATE TABLE IF NOT EXISTS `ma_items` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `playerID` int(11) NOT NULL,
  `itemID` varchar(128) NOT NULL,
  `itemDamage` int(11) NOT NULL,
  `qty` int(11) NOT NULL,
  `itemMeta` text NOT NULL,
  `enchantments` varchar(512) NOT NULL,
  `lore` varchar(2048) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM  DEFAULT CHARSET=latin1;

CREATE TABLE IF NOT EXISTS `ma_items_list` (
  `itemID` int(11) NOT NULL,
  `itemSubID` int(11) NOT NULL,
  `name` varchar(50) CHARACTER SET latin1 NOT NULL,
  `img` varchar(50) CHARACTER SET latin1 NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_czech_ci;

CREATE TABLE IF NOT EXISTS `ma_languages` (
  `idl` int(11) NOT NULL AUTO_INCREMENT,
  `lang` char(2) COLLATE utf8_czech_ci NOT NULL,
  `description` varchar(60) COLLATE utf8_czech_ci NOT NULL,
  PRIMARY KEY (`idl`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_czech_ci;

CREATE TABLE IF NOT EXISTS `ma_logs` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `component` varchar(255) CHARACTER SET utf8 COLLATE utf8_czech_ci NOT NULL,
  `level` varchar(255) NOT NULL,
  `text` text CHARACTER SET utf8 COLLATE utf8_czech_ci NOT NULL,
  `time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1;

CREATE TABLE IF NOT EXISTS `ma_menu_items` (
  `idm` int(11) NOT NULL,
  `url` varchar(50) CHARACTER SET utf8 COLLATE utf8_czech_ci NOT NULL,
  `ico` varchar(100) CHARACTER SET utf8 COLLATE utf8_czech_ci NOT NULL,
  `is_private` enum('0','1') CHARACTER SET utf8 COLLATE utf8_czech_ci NOT NULL DEFAULT '0',
  `dropable` tinyint(1) NOT NULL DEFAULT '0',
  `idp` int(11) NOT NULL,
  PRIMARY KEY (`idm`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE IF NOT EXISTS `ma_menu_locale` (
  `idm` int(11) NOT NULL,
  `name` varchar(10) COLLATE utf8_czech_ci NOT NULL,
  `lang` char(2) COLLATE utf8_czech_ci NOT NULL DEFAULT 'cz'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_czech_ci;

CREATE TABLE IF NOT EXISTS `ma_offers` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `playerID` int(11) NOT NULL,
  `itemID` varchar(255) NOT NULL,
  `itemDamage` int(11) NOT NULL,
  `qty` int(11) NOT NULL,
  `itemMeta` varchar(255) NOT NULL,
  `enchantments` varchar(512) NOT NULL,
  `lore` varchar(2048) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `price` decimal(10,2) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1;

CREATE TABLE IF NOT EXISTS `ma_pins` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `playerID` int(11) NOT NULL,
  `pins` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE IF NOT EXISTS `ma_players` (
  `id` int(16) NOT NULL AUTO_INCREMENT,
  `playerName` varchar(32) NOT NULL,
  `uuid` varchar(50) NOT NULL,
  `locked` tinyint(1) NOT NULL DEFAULT '0',
  `password` char(40) NOT NULL DEFAULT '',
  `money` decimal(32,2) NOT NULL DEFAULT '10000.00',
  `admin` tinyint(1) NOT NULL DEFAULT '0',
  `lang` char(2) CHARACTER SET utf8 COLLATE utf8_czech_ci NOT NULL DEFAULT 'en',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM  DEFAULT CHARSET=latin1;

CREATE TABLE IF NOT EXISTS `ma_points` (
  `id` int(64) NOT NULL AUTO_INCREMENT,
  `type` varchar(32) NOT NULL,
  `creator` int(32) NOT NULL,
  `block` varchar(512) NOT NULL,
  `x` int(64) NOT NULL,
  `y` int(64) NOT NULL,
  `z` int(64) NOT NULL,
  `delete` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM  DEFAULT CHARSET=latin1;