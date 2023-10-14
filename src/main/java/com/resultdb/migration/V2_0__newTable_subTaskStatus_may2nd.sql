-- phpMyAdmin SQL Dump
-- version 4.0.10deb1
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: May 02, 2017 at 02:57 PM
-- Server version: 5.5.35-1ubuntu1
-- PHP Version: 5.5.9-1ubuntu4

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `missingresultsdb`
--

-- --------------------------------------------------------

--
-- Table structure for table `sub_task_status`
--
DROP TABLE IF EXISTS `sub_task_status`;

CREATE TABLE `sub_task_status` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
   `Date` Date DEFAULT NULL,
  `idapp` bigint(20) DEFAULT NULL,
  `appname` varchar(1000) DEFAULT NULL,
  `rca` varchar(50) DEFAULT NULL,
  `gbrca` varchar(50) DEFAULT NULL,
  `numstat` varchar(50) DEFAULT NULL,
  `strstat` varchar(50) DEFAULT NULL,
  `nullcheck` varchar(50) DEFAULT NULL,
  `dupidcheck` varchar(50) DEFAULT NULL,
  `dupallcheck` varchar(50) DEFAULT NULL,
  `ra` varchar(50) DEFAULT NULL,
  `datadrift` varchar(50) DEFAULT NULL,
  `regex` varchar(1000) DEFAULT NULL,
  `orphan` varchar(1000) DEFAULT NULL,
  `referential` varchar(1000) DEFAULT NULL,
  `crossreferential` varchar(1000) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;