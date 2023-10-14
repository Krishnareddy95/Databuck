-- MySQL dump 10.13  Distrib 5.5.53, for debian-linux-gnu (x86_64)
--
-- Host: localhost    Database: databuck_app_db
-- ------------------------------------------------------
-- Server version	5.5.53-0ubuntu0.14.04.1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `Role`
--

DROP TABLE IF EXISTS `Role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Role` (
  `idRole` bigint(20) NOT NULL AUTO_INCREMENT,
  `roleName` varchar(100) DEFAULT NULL,
  `description` varchar(500) DEFAULT NULL,
  `createdAt` datetime DEFAULT NULL,
  `updatedAt` datetime DEFAULT NULL,
  PRIMARY KEY (`idRole`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Role`
--

LOCK TABLES `Role` WRITE;
/*!40000 ALTER TABLE `Role` DISABLE KEYS */;
INSERT INTO `Role` VALUES (1,'Admin','Admin Role','2016-03-14 00:00:00','2016-03-14 00:00:00'),(2,'Marketer','Marketer Role','2016-03-14 00:00:00','2016-03-14 00:00:00'),(3,'Assistant','','2016-03-15 08:09:51','2016-03-15 08:09:51'),(4,'Developer','Group for Developers','2016-03-15 05:56:52','2016-03-15 05:56:52'),(5,'Students','','2016-03-15 08:00:31','2016-03-15 08:00:31'),(6,'group1','','2016-03-17 07:49:37','2016-03-17 07:49:37');
/*!40000 ALTER TABLE `Role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `RoleTask`
--

DROP TABLE IF EXISTS `RoleTask`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `RoleTask` (
  `idRoleTask` bigint(20) NOT NULL AUTO_INCREMENT,
  `idRole` bigint(20) DEFAULT NULL,
  `idTask` bigint(20) DEFAULT NULL,
  `accessControl` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`idRoleTask`),
  KEY `idRole` (`idRole`),
  KEY `idTask` (`idTask`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Task`
--

DROP TABLE IF EXISTS `Task`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Task` (
  `idTask` bigint(20) NOT NULL AUTO_INCREMENT,
  `taskName` varchar(500) DEFAULT NULL,
  `createdAt` datetime DEFAULT NULL,
  `updatedAt` datetime DEFAULT NULL,
  PRIMARY KEY (`idTask`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `User`
--

DROP TABLE IF EXISTS `User`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `User` (
  `idUser` bigint(20) NOT NULL AUTO_INCREMENT,
  `firstName` varchar(100) DEFAULT NULL,
  `lastName` varchar(100) DEFAULT NULL,
  `salt` varchar(100) DEFAULT NULL,
  `password` varchar(150) DEFAULT NULL,
  `company` varchar(100) DEFAULT NULL,
  `department` varchar(100) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `active` tinyint(4) DEFAULT '1',
  `createdAt` datetime DEFAULT NULL,
  `updatedAt` datetime DEFAULT NULL,
  `userType` tinyint(4) DEFAULT '0',
  PRIMARY KEY (`idUser`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `User`
--

LOCK TABLES `User` WRITE;
/*!40000 ALTER TABLE `User` DISABLE KEYS */;
INSERT INTO `User` VALUES (1,'Admin','User','09b2271b67c6d537b1dd55a294f43ab3','$2a$10$p4It/9O4qq03P5XADE9eyObiGA9esqjPxERjDhGcamNWDxoTI9hsK','DataBuck','DataBuck','admin@databuck.com',1,'2016-03-12 00:00:00','2016-03-12 00:00:00',1);
/*!40000 ALTER TABLE `User` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `UserRole`
--

DROP TABLE IF EXISTS `UserRole`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `UserRole` (
  `idUserRole` bigint(20) NOT NULL AUTO_INCREMENT,
  `idUser` bigint(20) DEFAULT NULL,
  `idRole` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`idUserRole`),
  KEY `idUser` (`idUser`),
  KEY `idRole` (`idRole`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `UserRole`
--

LOCK TABLES `UserRole` WRITE;
/*!40000 ALTER TABLE `UserRole` DISABLE KEYS */;
INSERT INTO `UserRole` VALUES (1,1,1);
/*!40000 ALTER TABLE `UserRole` ENABLE KEYS */;
UNLOCK TABLES;

RENAME TABLE Task TO Module, RoleTask TO RoleModule;

INSERT INTO `Module` (`idTask`, `taskName`, `createdAt`, `updatedAt`) VALUES
(1, 'Data Connection', now(), now()),
(2, 'Data Template', now(), now()),
(3, 'Extend Template & Rule', now(), now()),
(4, 'Validation Check', now(), now()),
(5, 'Tasks', now(), now()),
(6, 'Results', now(), now()),
(7, 'User Settings', now(), now());

INSERT INTO `RoleModule` (`idRoleTask`, `idRole`, `idTask`, `accessControl`) VALUES
(1, 1, 1, 'C-R-U-D'),
(2, 1, 2, 'C-R-U-D'),
(3, 1, 3, 'C-R-U-D'),
(4, 1, 4, 'C-R-U-D'),
(5, 1, 5, 'C-R-U-D'),
(6, 1, 6, 'C-R-U-D'),
(7, 1, 7, 'C-R-U-D');
--
-- Table structure for table `appConfig`
--

DROP TABLE IF EXISTS `appConfig`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `appConfig` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `numberExecutor` int(11) NOT NULL,
  `numberApp` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `application`
--

DROP TABLE IF EXISTS `application`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `application` (
  `id` bigint(20) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  `output_type` varchar(255) DEFAULT 'HBASE',
  `output_path` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `data_blend`
--

DROP TABLE IF EXISTS `data_blend`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `data_blend` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `datasource_id` bigint(20) NOT NULL,
  `expression` varchar(255) NOT NULL,
  `alias` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `datasource_id` (`datasource_id`),
  CONSTRAINT `data_blend_ibfk_1` FOREIGN KEY (`datasource_id`) REFERENCES `data_source` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `data_filter`
--

DROP TABLE IF EXISTS `data_filter`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `data_filter` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `expression` text NOT NULL,
  `datasource_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `datasource_id` (`datasource_id`),
  CONSTRAINT `data_filter_ibfk_1` FOREIGN KEY (`datasource_id`) REFERENCES `data_source` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `data_source`
--

DROP TABLE IF EXISTS `data_source`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `data_source` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `type` varchar(500) DEFAULT NULL,
  `FORMAT` varchar(500) DEFAULT NULL,
  `url` varchar(255) NOT NULL,
  `username` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `filter` varchar(1000) NOT NULL DEFAULT '',
  `app_id` bigint(20) NOT NULL,
  `seq` bigint(20) NOT NULL DEFAULT '0',
  `checkSource` varchar(200) DEFAULT 'normal',
  `RowAddSource` varchar(200) DEFAULT NULL,
  `query` varchar(10) DEFAULT NULL,
  `incrementalType` varchar(10) DEFAULT NULL,
  `whereCondition` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `app_id` (`app_id`),
  CONSTRAINT `data_source_ibfk_1` FOREIGN KEY (`app_id`) REFERENCES `application` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `dataset_definition`
--

DROP TABLE IF EXISTS `dataset_definition`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dataset_definition` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `column_name` varchar(255) NOT NULL,
  `display_name` varchar(255) NOT NULL,
  `format` varchar(255) NOT NULL,
  `seq` bigint(20) NOT NULL,
  `datasource_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `datasource_id` (`datasource_id`),
  CONSTRAINT `dataset_definition_ibfk_1` FOREIGN KEY (`datasource_id`) REFERENCES `data_source` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `dtbkdbsess`
--

DROP TABLE IF EXISTS `dtbkdbsess`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dtbkdbsess` (
  `id` varchar(40) NOT NULL,
  `ip_address` varchar(45) NOT NULL,
  `timestamp` int(10) NOT NULL,
  `data` blob NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `hiveSource`
--

DROP TABLE IF EXISTS `hiveSource`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `hiveSource` (
  `idHiveSource` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `description` varchar(255) NOT NULL,
  `idDataSchema` int(11) NOT NULL,
  `tableName` varchar(255) NOT NULL,
  PRIMARY KEY (`idHiveSource`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `listAppOwner`
--

DROP TABLE IF EXISTS `listAppOwner`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `listAppOwner` (
  `idlistAppOwner` bigint(20) NOT NULL AUTO_INCREMENT,
  `idApp` bigint(11) NOT NULL,
  `idGroup` int(11) NOT NULL,
  PRIMARY KEY (`idlistAppOwner`),
  KEY `lapowner_ibfk_1` (`idApp`),
  CONSTRAINT `lapowner_ibfk_1` FOREIGN KEY (`idApp`) REFERENCES `listApplications` (`idApp`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `listAppSchedule`
--

DROP TABLE IF EXISTS `listAppSchedule`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `listAppSchedule` (
  `idlistAppSchedule` bigint(20) NOT NULL AUTO_INCREMENT,
  `idSchedule` bigint(20) NOT NULL,
  `idApp` bigint(20) NOT NULL,
  `listAppSchedulecol` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`idlistAppSchedule`),
  KEY `listAppSchedule_ibfk_1` (`idSchedule`),
  KEY `listAppSchedule_ibfk_2` (`idApp`),
  CONSTRAINT `listAppSchedule_ibfk_1` FOREIGN KEY (`idSchedule`) REFERENCES `listSchedule` (`idSchedule`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `listAppSchedule_ibfk_2` FOREIGN KEY (`idApp`) REFERENCES `listApplications` (`idApp`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `listApplications`
--

DROP TABLE IF EXISTS `listApplications`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `listApplications` (
  `idApp` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(500) NOT NULL DEFAULT '',
  `description` varchar(500) DEFAULT NULL,
  `appType` varchar(45) NOT NULL,
  `idData` int(20) NOT NULL,
  `idRightData` int(11) DEFAULT NULL,
  `createdBy` bigint(20) NOT NULL,
  `createdAt` datetime DEFAULT NULL,
  `updatedAt` datetime DEFAULT NULL,
  `updatedBy` bigint(20) DEFAULT NULL,
  `fileNameValidation` varchar(20) NOT NULL,
  `entityColumn` varchar(100) NOT NULL,
  `colOrderValidation` varchar(20) NOT NULL,
  `matchingThreshold` double DEFAULT '0',
  `nonNullCheck` varchar(45) DEFAULT NULL,
  `numericalStatCheck` varchar(10) DEFAULT NULL,
  `stringStatCheck` varchar(10) DEFAULT NULL,
  `recordAnomalyCheck` varchar(10) DEFAULT NULL,
  `incrementalMatching` varchar(10) NOT NULL DEFAULT 'N',
  `incrementalTimestamp` datetime DEFAULT NULL,
  `dataDriftCheck` varchar(10) NOT NULL DEFAULT 'N',
  `updateFrequency` varchar(100) NOT NULL DEFAULT 'Never',
  `frequencyDays` int(11) DEFAULT NULL,
  `recordCountAnomaly` varchar(10) NOT NULL DEFAULT 'N',
  `recordCountAnomalyThreshold` double NOT NULL DEFAULT '0',
  `timeSeries` varchar(500) NOT NULL DEFAULT 'None',
  `keyGroupRecordCountAnomaly` varchar(500) DEFAULT NULL,
  `outOfNormCheck` varchar(50) DEFAULT 'N',
  `applyRules` varchar(10) NOT NULL DEFAULT 'N',
  `applyDerivedColumns` varchar(10) NOT NULL DEFAULT 'N',
  `csvDir` VARCHAR(500) DEFAULT NULL,
  `groupEquality` VARCHAR(20) NOT NULL DEFAULT 'N',
  PRIMARY KEY (`idApp`),
  KEY `lapp_ibfk_1` (`idData`),
  CONSTRAINT `lapp_ibfk_1` FOREIGN KEY (`idData`) REFERENCES `listDataSources` (`idData`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `listBenfordRules`
--

DROP TABLE IF EXISTS `listBenfordRules`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `listBenfordRules` (
  `idlistBenfordRules` bigint(20) NOT NULL AUTO_INCREMENT,
  `idDA` bigint(20) NOT NULL,
  `idCol` bigint(20) NOT NULL,
  PRIMARY KEY (`idlistBenfordRules`),
  KEY `listBenfordR_ibfk_1` (`idDA`),
  CONSTRAINT `listBenfordR_ibfk_1` FOREIGN KEY (`idDA`) REFERENCES `listDAStandardRules` (`idDA`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `listColRules`
--

DROP TABLE IF EXISTS `listColRules`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `listColRules` (
  `idListColrules` int(11) NOT NULL AUTO_INCREMENT,
  `idData` int(11) DEFAULT NULL,
  `idCol` int(11) DEFAULT NULL,
  `ruleName` varchar(45) DEFAULT NULL,
  `description` varchar(50) DEFAULT NULL,
  `createdAt` date DEFAULT NULL,
  `ruleType` varchar(45) DEFAULT NULL,
  `expression` varchar(1000) DEFAULT NULL,
  `external` varchar(245) DEFAULT NULL,
  `externalDatasetName` varchar(245) DEFAULT NULL,
  `idRightData` int(100) DEFAULT NULL,
  `matchingRules` varchar(245) DEFAULT NULL,
  `matchType` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`idListColrules`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `listCustomRules`
--

DROP TABLE IF EXISTS `listCustomRules`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `listCustomRules` (
  `idlistCustomRules` bigint(20) NOT NULL AUTO_INCREMENT,
  `idDA` bigint(20) NOT NULL,
  `leftExp` varchar(200) NOT NULL,
  `doOperator` varchar(45) NOT NULL,
  `rightExp` varchar(200) NOT NULL,
  PRIMARY KEY (`idlistCustomRules`),
  KEY `listCustomRule_ibfk_1` (`idDA`),
  CONSTRAINT `listCustomRule_ibfk_1` FOREIGN KEY (`idDA`) REFERENCES `listDAStandardRules` (`idDA`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `listDAStandardRules`
--

DROP TABLE IF EXISTS `listDAStandardRules`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `listDAStandardRules` (
  `idDA` bigint(20) NOT NULL AUTO_INCREMENT,
  `idApp` bigint(20) NOT NULL,
  `bendford` varchar(1) DEFAULT 'N',
  `outlier` varchar(1) DEFAULT 'N',
  `custom` varchar(1) DEFAULT 'N',
  PRIMARY KEY (`idDA`),
  KEY `ldasr_ibfk_1` (`idApp`),
  CONSTRAINT `ldasr_ibfk_1` FOREIGN KEY (`idApp`) REFERENCES `listApplications` (`idApp`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `listDFColRule`
--

DROP TABLE IF EXISTS `listDFColRule`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `listDFColRule` (
  `idDFC` bigint(20) NOT NULL AUTO_INCREMENT,
  `idApp` bigint(20) NOT NULL,
  `idCol` int(11) NOT NULL,
  `stat` varchar(1) NOT NULL,
  `listRule` varchar(1) NOT NULL,
  `formatRule` varchar(1) NOT NULL,
  `refRule` varchar(1) NOT NULL,
  PRIMARY KEY (`idDFC`),
  KEY `listDFColRule_ibfk_1` (`idApp`),
  CONSTRAINT `listDFColRule_ibfk_1` FOREIGN KEY (`idApp`) REFERENCES `listApplications` (`idApp`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `listDFColSpecRules`
--

DROP TABLE IF EXISTS `listDFColSpecRules`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `listDFColSpecRules` (
  `idlistDFColSpecRules` bigint(20) NOT NULL AUTO_INCREMENT,
  `idDFC` bigint(20) NOT NULL,
  `ruleType` varchar(45) NOT NULL,
  `leftExp` varchar(45) NOT NULL,
  `doOperation` varchar(45) NOT NULL,
  `rightExp` varchar(45) NOT NULL,
  PRIMARY KEY (`idlistDFColSpecRules`),
  KEY `listdfcsr_ibfk_1` (`idDFC`),
  CONSTRAINT `listdfcsr_ibfk_1` FOREIGN KEY (`idDFC`) REFERENCES `listDFColRule` (`idDFC`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `listDFSetComparisonRule`
--

DROP TABLE IF EXISTS `listDFSetComparisonRule`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `listDFSetComparisonRule` (
  `idlistDFSetComparisonRule` bigint(20) NOT NULL AUTO_INCREMENT,
  `idDFSet` bigint(20) NOT NULL,
  `comparisonType` varchar(45) NOT NULL,
  `comparisonMethod` varchar(45) NOT NULL,
  `comparisonDuration` int(11) NOT NULL,
  `threshold` int(11) NOT NULL,
  PRIMARY KEY (`idlistDFSetComparisonRule`),
  KEY `listDFSCR_ibfk_1` (`idDFSet`),
  CONSTRAINT `listDFSCR_ibfk_1` FOREIGN KEY (`idDFSet`) REFERENCES `listDFSetRule` (`idDFSet`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `listDFSetRule`
--

DROP TABLE IF EXISTS `listDFSetRule`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `listDFSetRule` (
  `idDFSet` bigint(20) NOT NULL AUTO_INCREMENT,
  `idApp` bigint(20) NOT NULL,
  `count` varchar(1) NOT NULL,
  `sum` varchar(1) NOT NULL,
  `correlation` varchar(1) NOT NULL,
  `statisticalParam` varchar(1) DEFAULT NULL,
  `duplicateFile` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`idDFSet`),
  KEY `listDFS_ibfk_1` (`idApp`),
  CONSTRAINT `listDFS_ibfk_1` FOREIGN KEY (`idApp`) REFERENCES `listApplications` (`idApp`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `listDFTranRule`
--

DROP TABLE IF EXISTS `listDFTranRule`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `listDFTranRule` (
  `idDFT` bigint(20) NOT NULL AUTO_INCREMENT,
  `idApp` bigint(20) NOT NULL,
  `dupRow` varchar(1) NOT NULL,
  `seqRow` varchar(1) NOT NULL,
  `seqIDcol` bigint(20) NOT NULL,
  `threshold` double NOT NULL DEFAULT '0',
  `type` varchar(50) NOT NULL,
  PRIMARY KEY (`idDFT`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `listDMCriteria`
--

DROP TABLE IF EXISTS `listDMCriteria`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `listDMCriteria` (
  `idlistDMCriteria` bigint(20) NOT NULL AUTO_INCREMENT,
  `idDM` bigint(20) NOT NULL,
  `leftSideExp` varchar(500) NOT NULL DEFAULT '',
  `rightSideExp` varchar(500) NOT NULL DEFAULT '',
  PRIMARY KEY (`idlistDMCriteria`),
  KEY `listDMCriteria_ibfk_1` (`idDM`),
  CONSTRAINT `listDMCriteria_ibfk_1` FOREIGN KEY (`idDM`) REFERENCES `listDMRules` (`idDM`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `listDMRules`
--

DROP TABLE IF EXISTS `listDMRules`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `listDMRules` (
  `idDM` bigint(20) NOT NULL AUTO_INCREMENT,
  `idApp` bigint(20) NOT NULL,
  `matchType` varchar(45) NOT NULL,
  `matchType2` varchar(45) NOT NULL,
  PRIMARY KEY (`idDM`),
  KEY `listDMRules_ibfk_1` (`idApp`),
  CONSTRAINT `listDMRules_ibfk_1` FOREIGN KEY (`idApp`) REFERENCES `listApplications` (`idApp`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `listDataAccess`
--

DROP TABLE IF EXISTS `listDataAccess`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `listDataAccess` (
  `idlistDataAccess` int(11) NOT NULL AUTO_INCREMENT,
  `idData` int(11) NOT NULL,
  `hostName` varchar(45) NOT NULL,
  `portName` varchar(45) NOT NULL,
  `userName` varchar(45) NOT NULL,
  `pwd` varchar(45) NOT NULL,
  `schemaName` varchar(45) NOT NULL,
  `folderName` varchar(1000) NOT NULL DEFAULT '',
  `query` varchar(45) NOT NULL DEFAULT '',
  `incrementalType` varchar(10) DEFAULT NULL,
  `idDataSchema` bigint(20) NOT NULL,
  `whereCondition` varchar(500) DEFAULT NULL,
  `domain` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`idlistDataAccess`),
  KEY `listda_ibfk_1` (`idData`),
  CONSTRAINT `listdataaccess_ibfk_1` FOREIGN KEY (`idData`) REFERENCES `listDataSources` (`idData`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `listdataaccess_ibfk_2` FOREIGN KEY (`idData`) REFERENCES `listDataSources` (`idData`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `listDataBlend`
--

DROP TABLE IF EXISTS `listDataBlend`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `listDataBlend` (
  `idDataBlend` int(11) NOT NULL AUTO_INCREMENT,
  `idData` int(11) NOT NULL,
  `idColumn` bigint(20) DEFAULT NULL,
  `name` varchar(45) NOT NULL,
  `description` varchar(45) NOT NULL,
  `expression` varchar(500) DEFAULT NULL,
  `columnName` varchar(100) DEFAULT NULL,
  `derivedColType` varchar(50) DEFAULT NULL,
  `columnValue` varchar(200) DEFAULT NULL,
  `columnValueType` varchar(20) DEFAULT NULL,
  `createdAt` datetime DEFAULT NULL,
  `updatedAt` datetime DEFAULT NULL,
  `createdBy` bigint(20) DEFAULT NULL,
  `updatedBy` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`idDataBlend`),
  KEY `listDataBlend_ibfk_1` (`idData`),
  CONSTRAINT `listDataBlend_ibfk_1` FOREIGN KEY (`idData`) REFERENCES `listDataSources` (`idData`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `listDataBlendColDefinitions`
--

DROP TABLE IF EXISTS `listDataBlendColDefinitions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `listDataBlendColDefinitions` (
  `idCol` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `idDataBlend` int(11) NOT NULL,
  `colExpression` varchar(200) NOT NULL,
  PRIMARY KEY (`idCol`),
  KEY `listdbcd_ibfk_1` (`idDataBlend`),
  CONSTRAINT `listDataBlendcoldefinitions_ibfk_1` FOREIGN KEY (`idDataBlend`) REFERENCES `listDataBlend` (`idDataBlend`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `listDataBlendFilterDefinitions`
--

DROP TABLE IF EXISTS `listDataBlendFilterDefinitions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `listDataBlendFilterDefinitions` (
  `idFilter` int(11) NOT NULL AUTO_INCREMENT,
  `idDataBlend` int(11) NOT NULL,
  `name` varchar(45) NOT NULL,
  `filteringExp` varchar(200) NOT NULL,
  PRIMARY KEY (`idFilter`),
  KEY `ldbfd_ibfk_1` (`idDataBlend`),
  CONSTRAINT `ldbfd_ibfk_1` FOREIGN KEY (`idDataBlend`) REFERENCES `listDataBlend` (`idDataBlend`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `listDataBlendRowAdd`
--

DROP TABLE IF EXISTS `listDataBlendRowAdd`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `listDataBlendRowAdd` (
  `idRowAdd` int(11) NOT NULL AUTO_INCREMENT,
  `idDataBlend` int(11) NOT NULL,
  `rowAddExpression` varchar(200) NOT NULL,
  PRIMARY KEY (`idRowAdd`),
  KEY `idDataBlend` (`idDataBlend`),
  CONSTRAINT `listDataBlendRowAdd_ibfk_1` FOREIGN KEY (`idDataBlend`) REFERENCES `listDataBlend` (`idDataBlend`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `listDataDefinition`
--

DROP TABLE IF EXISTS `listDataDefinition`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `listDataDefinition` (
  `idColumn` bigint(20) NOT NULL AUTO_INCREMENT,
  `idData` int(11) NOT NULL,
  `columnName` varchar(45) NOT NULL,
  `displayName` varchar(200) NOT NULL,
  `format` varchar(45) DEFAULT NULL,
  `hashValue` varchar(1) NOT NULL DEFAULT 'N',
  `numericalStat` varchar(1) NOT NULL DEFAULT 'N',
  `stringStat` varchar(1) NOT NULL DEFAULT 'N',
  `nullCountThreshold` double DEFAULT '0',
  `numericalThreshold` double DEFAULT '0',
  `stringStatThreshold` double DEFAULT '0',
  `KBE` varchar(1) DEFAULT 'N',
  `dgroup` varchar(1) DEFAULT 'N',
  `dupkey` varchar(1) DEFAULT 'N',
  `measurement` varchar(1) DEFAULT 'N',
  `blend` varchar(1) NOT NULL DEFAULT 'N',
  `idCol` int(11) DEFAULT NULL,
  `incrementalCol` varchar(10) NOT NULL DEFAULT 'N',
  `idDataSchema` bigint(20) NOT NULL DEFAULT '0',
  `nonNull` varchar(20) NOT NULL DEFAULT 'N',
  `primaryKey` varchar(20) NOT NULL DEFAULT 'N',
  `recordAnomaly` varchar(20) NOT NULL DEFAULT 'N',
  `recordAnomalyThreshold` double DEFAULT '0',
  `dataDrift` varchar(10) NOT NULL DEFAULT 'N',
  `dataDriftThreshold` double NOT NULL DEFAULT '0',
  `outOfNormStat` varchar(50) DEFAULT 'N',
  `outOfNormStatThreshold` double NOT NULL DEFAULT '0',
  `isMasked` VARCHAR( 10 ) DEFAULT NULL,
  PRIMARY KEY (`idColumn`),
  KEY `listdd_ibfk_1` (`idData`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `listDataFiles`
--

DROP TABLE IF EXISTS `listDataFiles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `listDataFiles` (
  `idDataFile` int(11) NOT NULL AUTO_INCREMENT,
  `idData` int(11) NOT NULL,
  `fileName` varchar(500) NOT NULL,
  PRIMARY KEY (`idDataFile`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `listDataSchema`
--

DROP TABLE IF EXISTS `listDataSchema`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `listDataSchema` (
  `idDataSchema` bigint(20) NOT NULL AUTO_INCREMENT,
  `schemaName` varchar(500) DEFAULT NULL,
  `schemaType` varchar(500) DEFAULT NULL,
  `ipAddress` varchar(255) NOT NULL,
  `databaseSchema` varchar(255) NOT NULL,
  `username` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `port` varchar(255) NOT NULL,
  `domain` varchar(500) DEFAULT NULL,
  `createdAt` datetime NOT NULL,
  `updatedAt` datetime NOT NULL,
  `createdBy` bigint(20) NOT NULL,
  `updatedBy` bigint(11) NOT NULL,
  PRIMARY KEY (`idDataSchema`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `listDataSources`
--

DROP TABLE IF EXISTS `listDataSources`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `listDataSources` (
  `idData` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(500) NOT NULL DEFAULT '',
  `description` varchar(60) NOT NULL,
  `dataLocation` varchar(45) NOT NULL,
  `dataSource` varchar(45) NOT NULL,
  `createdBy` int(11) NOT NULL,
  `idDataBlend` int(11) DEFAULT NULL,
  `createdAt` datetime DEFAULT NULL,
  `updatedAt` datetime DEFAULT NULL,
  `updatedBy` bigint(20) DEFAULT NULL,
  `schemaName` varchar(500) DEFAULT NULL,
  `idDataSchema` bigint(20) NOT NULL,
  `ignoreRowsCount` int(11) NOT NULL,
  PRIMARY KEY (`idData`),
  KEY `listDataSources_ibfk_1` (`idDataBlend`),
  CONSTRAINT `listDataSources_ibfk_1` FOREIGN KEY (`idDataBlend`) REFERENCES `listDataBlend` (`idDataBlend`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `listExceptionMessage`
--

DROP TABLE IF EXISTS `listExceptionMessage`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `listExceptionMessage` (
  `idResults` bigint(20) NOT NULL AUTO_INCREMENT,
  `idApp` bigint(20) NOT NULL,
  `idRule` bigint(20) NOT NULL,
  `sourceName` varchar(45) NOT NULL,
  `message` varchar(200) NOT NULL,
  PRIMARY KEY (`idResults`),
  KEY `listExceptionMessage_ibfk_1` (`idApp`),
  CONSTRAINT `listExceptionMessage_ibfk_1` FOREIGN KEY (`idApp`) REFERENCES `listApplications` (`idApp`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `listFMRules`
--

DROP TABLE IF EXISTS `listFMRules`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `listFMRules` (
  `idFM` int(11) NOT NULL AUTO_INCREMENT,
  `idApp` int(11) NOT NULL,
  `dupCheck` varchar(10) DEFAULT NULL,
  `filter` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`idFM`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `listOutlierRule`
--

DROP TABLE IF EXISTS `listOutlierRule`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `listOutlierRule` (
  `idlistStdDevRules` bigint(20) NOT NULL AUTO_INCREMENT,
  `idDA` bigint(20) NOT NULL,
  `idCol` bigint(20) NOT NULL,
  `dmethod` varchar(45) NOT NULL,
  `threshold` varchar(45) NOT NULL,
  `averageRange` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`idlistStdDevRules`),
  KEY `listOutlierRule_ibfk_1` (`idDA`),
  CONSTRAINT `listOutlierRule_ibfk_1` FOREIGN KEY (`idDA`) REFERENCES `listDAStandardRules` (`idDA`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `listParameters`
--

DROP TABLE IF EXISTS `listParameters`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `listParameters` (
  `idlistParameters` bigint(20) NOT NULL AUTO_INCREMENT,
  `idResults` bigint(20) NOT NULL,
  `parameter` varchar(45) NOT NULL,
  `value` varchar(45) NOT NULL,
  PRIMARY KEY (`idlistParameters`),
  KEY `listParameters_ibfk_1` (`idResults`),
  CONSTRAINT `listParameters_ibfk_1` FOREIGN KEY (`idResults`) REFERENCES `listExceptionMessage` (`idResults`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `listRefFunctions`
--

DROP TABLE IF EXISTS `listRefFunctions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `listRefFunctions` (
  `idFunctions` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `description` varchar(45) NOT NULL,
  PRIMARY KEY (`idFunctions`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `listRefFunctions`
--

LOCK TABLES `listRefFunctions` WRITE;
/*!40000 ALTER TABLE `listRefFunctions` DISABLE KEYS */;
INSERT INTO `listRefFunctions` VALUES (8,'abs','abs'),(9,'acros','acros'),(10,'asin','asin'),(11,'atan','atan'),(12,'atan2','atan2'),(13,'bin','bin'),(14,'cbrt','cbrt'),(15,'ceil','ceil'),(16,'conv','conv'),(17,'cos','cos'),(18,'cosh','cosh'),(19,'exp','exp'),(20,'expm1','expm1'),(21,'factorial','factorial'),(22,'floor','floor'),(23,'hex','hex'),(24,'hypot','hypot'),(25,'log','log'),(26,'log10','log10'),(27,'log1p','log1p'),(28,'log2','log2'),(29,'pmod','pmod'),(30,'pow','pow'),(31,'rint','rint'),(32,'round','round'),(33,'shiftLeft','shiftLeft'),(34,'shiftRight','shiftRight'),(35,'shiftRightUnsigned','shiftRightUnsigned'),(36,'signum','signum'),(37,'sin','sin'),(38,'sinh','sinh'),(39,'sqrt','sqrt'),(40,'tan','tan'),(41,'tanh','tanh'),(42,'toDegrees','toDegrees'),(43,'toRadians','toRadians'),(44,'unhex','unhex'),(45,'ascii','ascii'),(46,'base64','base64'),(47,'concat','concat'),(48,'concat_ws','concat_ws'),(49,'decode','decode'),(50,'encode','encode'),(51,'format_number','format_number'),(52,'format_string','format_string'),(53,'get_json_object','get_json_object'),(54,'initcap','initcap'),(55,'instr','instr'),(56,'length','length'),(57,'levenshtein','levenshtein'),(58,'locate','locate'),(59,'lower','lower'),(60,'lpad','lpad'),(61,'ltrim','ltrim'),(62,'printf','printf'),(63,'regexp_extract','regexp_extract'),(64,'regexp_replace','regexp_replace'),(65,'repeat','repeat'),(66,'reverse','reverse'),(67,'rpad','rpad'),(68,'rtrim','rtrim'),(69,'soundex','soundex'),(70,'space','space'),(71,'split','split'),(72,'substring','substring'),(73,'substring_index','substring_index'),(74,'translate','translate'),(75,'trim','trim'),(76,'unbase64','unbase64'),(77,'upper','upper');
/*!40000 ALTER TABLE `listRefFunctions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `listSchedule`
--

DROP TABLE IF EXISTS `listSchedule`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `listSchedule` (
  `idSchedule` bigint(20) NOT NULL AUTO_INCREMENT,
  `time` varchar(45) NOT NULL,
  `name` varchar(45) NOT NULL,
  `description` varchar(200) DEFAULT '',
  `frequency` varchar(45) NOT NULL,
  `scheduleDay` varchar(255) DEFAULT NULL,
  `exceptionMatching` varchar(10) NOT NULL DEFAULT 'N',
  PRIMARY KEY (`idSchedule`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `listTrigger`
--

DROP TABLE IF EXISTS `listTrigger`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `listTrigger` (
  `idlistTrigger` bigint(20) NOT NULL AUTO_INCREMENT,
  `appid` bigint(20) NOT NULL,
  `appid2` bigint(20) NOT NULL,
  PRIMARY KEY (`idlistTrigger`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `matching_key`
--

DROP TABLE IF EXISTS `matching_key`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `matching_key` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `left_column` varchar(255) NOT NULL,
  `right_column` varchar(255) NOT NULL,
  `app_id` bigint(20) NOT NULL,
  `match_type` tinyint(4) DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `app_id` (`app_id`),
  CONSTRAINT `matching_key_ibfk_1` FOREIGN KEY (`app_id`) REFERENCES `application` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `runScheduledTasks`
--

DROP TABLE IF EXISTS `runScheduledTasks`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `runScheduledTasks` (
  `id` int(11) NOT NULL,
  `idApp` int(11) NOT NULL,
  `status` varchar(50) DEFAULT 'started',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

INSERT INTO runScheduledTasks VALUES(1,1,'started');

--
-- Table structure for table `scheduledTasks`
--

DROP TABLE IF EXISTS `scheduledTasks`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `scheduledTasks` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `idApp` int(10) NOT NULL,
  `idSchedule` int(10) NOT NULL,
  `status` varchar(20) NOT NULL,
  `runDate` date DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `userRegistrationCode`
--

DROP TABLE IF EXISTS `userRegistrationCode`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `userRegistrationCode` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `idUser` bigint(20) NOT NULL,
  `email` varchar(100) NOT NULL,
  `code` varchar(100) NOT NULL,
  `createdAt` datetime NOT NULL,
  `alive` tinyint(4) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;


/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2017-02-09  7:39:55
