SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";

CREATE TABLE IF NOT EXISTS `ma_items` (
  `id` int(64) NOT NULL AUTO_INCREMENT,
  `playerID` int(11) NOT NULL,
  `itemID` varchar(128) NOT NULL,
  `itemDamage` int(11) NOT NULL,
  `qty` int(11) NOT NULL,
  `itemMeta` text NOT NULL,
  `enchantments` varchar(512) NOT NULL,
  `lore` varchar(2048) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM  DEFAULT CHARSET=latin1;

CREATE TABLE IF NOT EXISTS `ma_players` (
  `id` int(16) NOT NULL AUTO_INCREMENT,
  `playerName` varchar(32) NOT NULL,
  `uuid` varchar(50) NOT NULL,
  `money` decimal(11,2) NOT NULL DEFAULT '0.00',
  `locked` tinyint(1) NOT NULL DEFAULT '0',
  `password` char(40) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM  DEFAULT CHARSET=latin1;