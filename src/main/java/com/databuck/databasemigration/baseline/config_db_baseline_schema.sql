-- MySQL dump 10.13  Distrib 5.7.28, for Linux (x86_64)
--
-- Host: localhost    Database: databuck_app_db
-- ------------------------------------------------------
-- Server version	5.7.28-0ubuntu0.18.04.4

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
-- Table structure for table `Module`
--

DROP TABLE IF EXISTS `Module`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Module` (
  `idTask` bigint(20) NOT NULL AUTO_INCREMENT,
  `taskName` varchar(500) DEFAULT NULL,
  `createdAt` datetime DEFAULT NULL,
  `updatedAt` datetime DEFAULT NULL,
  PRIMARY KEY (`idTask`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Module`
--

LOCK TABLES `Module` WRITE;
/*!40000 ALTER TABLE `Module` DISABLE KEYS */;
INSERT INTO `Module` VALUES (1,'Data Connection','2017-06-16 10:53:49','2017-06-16 10:53:49'),(2,'Data Template','2017-06-16 10:53:49','2017-06-16 10:53:49'),(3,'Extend Template & Rule','2017-06-16 10:53:49','2017-06-16 10:53:49'),(4,'Validation Check','2017-06-16 10:53:49','2017-06-16 10:53:49'),(5,'Tasks','2017-06-16 10:53:49','2017-06-16 10:53:49'),(6,'Results','2017-06-16 10:53:49','2017-06-16 10:53:49'),(7,'User Settings','2017-06-16 10:53:49','2017-06-16 10:53:49');
/*!40000 ALTER TABLE `Module` ENABLE KEYS */;
UNLOCK TABLES;

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
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Role`
--

LOCK TABLES `Role` WRITE;
/*!40000 ALTER TABLE `Role` DISABLE KEYS */;
INSERT INTO `Role` VALUES (1,'Admin','Admin Role','2016-03-14 00:00:00','2016-03-14 00:00:00'),(2,'Marketer','Marketer Role','2016-03-14 00:00:00','2016-03-14 00:00:00'),(3,'Assistant','','2016-03-15 08:09:51','2016-03-15 08:09:51'),(4,'Developer','Group for Developers','2016-03-15 05:56:52','2016-03-15 05:56:52'),(5,'Students','','2016-03-15 08:00:31','2016-03-15 08:00:31'),(6,'group1','','2016-03-17 07:49:37','2016-03-17 07:49:37'),(7,'Tester','','2019-04-11 05:15:21','2019-04-11 05:15:21');
/*!40000 ALTER TABLE `Role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `RoleModule`
--

DROP TABLE IF EXISTS `RoleModule`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `RoleModule` (
  `idRoleTask` bigint(20) NOT NULL AUTO_INCREMENT,
  `idRole` bigint(20) DEFAULT NULL,
  `idTask` bigint(20) DEFAULT NULL,
  `accessControl` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`idRoleTask`),
  KEY `idRole` (`idRole`),
  KEY `idTask` (`idTask`)
) ENGINE=InnoDB AUTO_INCREMENT=85 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `RoleModule`
--

LOCK TABLES `RoleModule` WRITE;
/*!40000 ALTER TABLE `RoleModule` DISABLE KEYS */;
INSERT INTO `RoleModule` VALUES (1,1,1,'C-R-U-D'),(2,1,2,'C-R-U-D'),(3,1,3,'C-R-U-D'),(4,1,4,'C-R-U-D'),(5,1,5,'C-R-U-D'),(6,1,6,'C-R-U-D'),(7,1,7,'C-R-U-D'),(11,4,6,'C-R'),(51,3,1,'C-R-U-D'),(52,3,2,'C-R-U-D'),(53,3,3,'C-R-U-D'),(54,3,4,'C-R-U-D'),(55,3,5,'C-R-U-D'),(56,3,6,'C-R-U-D'),(75,2,1,'R-U-D'),(76,2,2,'C-R-U-D'),(77,2,3,'C-R'),(78,2,4,'C-R-U-D'),(79,2,5,'C-R-U-D'),(80,2,6,'R-D'),(83,7,1,'C-R-U-D'),(84,7,2,'C-R-U');
/*!40000 ALTER TABLE `RoleModule` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `SynonymLibrary`
--

DROP TABLE IF EXISTS `SynonymLibrary`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `SynonymLibrary` (
  `synonyms_Id` int(11) NOT NULL,
  `domain_Id` int(11) NOT NULL,
  `tableColumn` varchar(200) DEFAULT NULL,
  `possiblenames` varchar(200) DEFAULT NULL
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
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `User`
--

LOCK TABLES `User` WRITE;
/*!40000 ALTER TABLE `User` DISABLE KEYS */;
INSERT INTO `User` VALUES (1,'Admin','User','09b2271b67c6d537b1dd55a294f43ab3','$2a$10$p4It/9O4qq03P5XADE9eyObiGA9esqjPxERjDhGcamNWDxoTI9hsK','DataBuck','DataBuck','admin@databuck.com',1,'2016-03-12 00:00:00','2016-03-12 00:00:00',1),(2,'test','user',NULL,'$2a$10$pzaPyKG0yLmzrpAEnUOwJObckYAPDKQYfEcK9HlghTd5cffZhhEwa',NULL,NULL,'testuser@databuck.com',1,'2020-01-09 06:57:12','2020-01-09 06:57:12',1),(3,'User','Test',NULL,'$2a$10$9njMn3e266ZuP9z/4WDc6uaaFzJEzk1.2GLph5/V6e6ddrNHonrey',NULL,NULL,'usertest@databuck.com',1,'2020-01-09 08:30:22','2020-01-09 08:30:22',1),(4,'PPP','KKK',NULL,'$2a$10$2tRsA/65zhT8ZM6qcE1eq..2FrNyhk46tN/xONNTw7.RlezyAEjgq',NULL,NULL,'pk@databuck.com',1,'2020-01-09 08:32:44','2020-01-09 08:32:44',1);
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
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `UserRole`
--

LOCK TABLES `UserRole` WRITE;
/*!40000 ALTER TABLE `UserRole` DISABLE KEYS */;
INSERT INTO `UserRole` VALUES (1,1,1),(2,2,2),(20,4,1);
/*!40000 ALTER TABLE `UserRole` ENABLE KEYS */;
UNLOCK TABLES;

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
  KEY `datasource_id` (`datasource_id`)
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
) ENGINE=InnoDB AUTO_INCREMENT=241 DEFAULT CHARSET=latin1;
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
) ENGINE=InnoDB AUTO_INCREMENT=7562 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `domain`
--

DROP TABLE IF EXISTS `domain`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `domain` (
  `domainId` int(11) NOT NULL AUTO_INCREMENT,
  `domainName` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`domainId`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `domain`
--

LOCK TABLES `domain` WRITE;
/*!40000 ALTER TABLE `domain` DISABLE KEYS */;
INSERT INTO `domain` VALUES (1,'Banking'),(2,'Telecom'),(3,'Finance'),(4,'Medical'),(5,'Advertisement'),(6,'Others');
/*!40000 ALTER TABLE `domain` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Table structure for table `file_monitor_rules`
--

DROP TABLE IF EXISTS `file_monitor_rules`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `file_monitor_rules` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `bucketName` varchar(255) DEFAULT NULL,
  `dayOfCheck` int(11) DEFAULT NULL,
  `fileCount` int(11) DEFAULT NULL,
  `filePattern` varchar(255) DEFAULT NULL,
  `fileSizeThreshold` int(11) DEFAULT NULL,
  `folderPath` varchar(255) DEFAULT NULL,
  `frequency` varchar(255) DEFAULT NULL,
  `idApp` int(11) NOT NULL,
  `lastProcessedDate` datetime DEFAULT NULL,
  `timeOfCheck` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `file_tracking_history`
--

DROP TABLE IF EXISTS `file_tracking_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `file_tracking_history` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `bucketName` varchar(255) DEFAULT NULL,
  `date` date DEFAULT NULL,
  `dayOfMonth` int(11) NOT NULL,
  `dayOfWeek` int(11) DEFAULT NULL,
  `dayOfYear` int(11) NOT NULL,
  `fileArrivalDate` datetime DEFAULT NULL,
  `fileArrivalTime` varchar(255) DEFAULT NULL,
  `fileMonitorRuleId` bigint(20) NOT NULL,
  `fileName` varchar(255) DEFAULT NULL,
  `fileSize` bigint(20) NOT NULL,
  `folderPath` varchar(255) DEFAULT NULL,
  `hourOfDay` varchar(255) DEFAULT NULL,
  `idApp` bigint(20) NOT NULL,
  `month` varchar(255) DEFAULT NULL,
  `requestId` varchar(255) DEFAULT NULL,
  `run` int(11) NOT NULL,
  `status` varchar(255) DEFAULT NULL,
  `statusMessage` varchar(255) DEFAULT NULL,
  `connectionName` varchar(255) DEFAULT NULL,
  `trackingDate` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `file_tracking_summary`
--

DROP TABLE IF EXISTS `file_tracking_summary`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `file_tracking_summary` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `countStatus` varchar(255) DEFAULT NULL,
  `date` date DEFAULT NULL,
  `dayOfMonth` int(11) NOT NULL,
  `dayOfWeek` int(11) DEFAULT NULL,
  `dayOfYear` int(11) NOT NULL,
  `fileCount` int(11) DEFAULT NULL,
  `fileSizeStatus` varchar(255) DEFAULT NULL,
  `hourOfDay` varchar(255) DEFAULT NULL,
  `idApp` bigint(20) NOT NULL,
  `lastUpdateTimeStamp` datetime DEFAULT NULL,
  `month` varchar(255) DEFAULT NULL,
  `run` int(11) NOT NULL,
  `fileMonitorRules_id` bigint(20) DEFAULT NULL,
  `trackingDate` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_8hgxxxvqb3ssk22j151jkbrck` (`fileMonitorRules_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `grouptouser`
--

DROP TABLE IF EXISTS `grouptouser`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `grouptouser` (
  `idGroup` bigint(20) DEFAULT NULL,
  `idUser` bigint(20) DEFAULT NULL,
  KEY `idGrouptoGroup_idx` (`idGroup`),
  KEY `idUsertoUser_idx` (`idUser`),
  CONSTRAINT `idGrouptoGroup` FOREIGN KEY (`idGroup`) REFERENCES `projgroup` (`idGroup`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `idUsertoUser` FOREIGN KEY (`idUser`) REFERENCES `User` (`idUser`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
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
  `tableName` text NOT NULL,
  `columnName` text,
  `columnType` text,
  `recordCount` bigint(20) DEFAULT NULL,
  `totalTables` bigint(20) DEFAULT NULL,
  `completedTables` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`idHiveSource`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `inProcessFiles`
--

DROP TABLE IF EXISTS `inProcessFiles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `inProcessFiles` (
  `fileCompletePath` varchar(1000) DEFAULT NULL,
  `status` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `listAdvancedRules`
--

DROP TABLE IF EXISTS `listAdvancedRules`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `listAdvancedRules` (
  `ruleId` int(11) NOT NULL AUTO_INCREMENT,
  `idData` int(20) DEFAULT NULL,
  `ruleType` varchar(500) DEFAULT NULL,
  `columnName` varchar(500) DEFAULT NULL,
  `ruleExpr` text,
  `ruleSql` text,
  `idListColrules` int(20) DEFAULT NULL,
  `isCustomRuleEligible` varchar(10) DEFAULT NULL,
  `isRuleActive` varchar(10) DEFAULT 'N',
  PRIMARY KEY (`ruleId`)
) ENGINE=InnoDB AUTO_INCREMENT=5937 DEFAULT CHARSET=latin1;
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
  `name` text NOT NULL,
  `description` text,
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
  `csvDir` varchar(500) DEFAULT NULL,
  `groupEquality` varchar(20) NOT NULL DEFAULT 'N',
  `groupEqualityThreshold` double DEFAULT '0',
  `buildHistoricFingerPrint` varchar(20) DEFAULT 'N',
  `historicStartDate` datetime DEFAULT NULL,
  `historicEndDate` datetime DEFAULT NULL,
  `historicDateFormat` varchar(200) DEFAULT NULL,
  `active` varchar(10) DEFAULT 'yes',
  `lengthCheck` varchar(45) DEFAULT NULL,
  `correlationcheck` varchar(10) DEFAULT 'N',
  `project_id` varchar(20) DEFAULT NULL,
  `timelinessKeyCheck` varchar(5) DEFAULT NULL,
  `defaultCheck` varchar(12) DEFAULT NULL,
  `defaultValues` varchar(12) DEFAULT NULL,
  `patternCheck` varchar(10) DEFAULT 'N',
  `dateRuleCheck` varchar(5) DEFAULT NULL,
  `badData` varchar(5) DEFAULT NULL,
  `idLeftData` int(11) DEFAULT NULL,
  `prefix1` varchar(100) DEFAULT NULL,
  `prefix2` varchar(100) DEFAULT NULL,
  `dGroupNullCheck` varchar(10) DEFAULT NULL,
  `dGroupDateRuleCheck` varchar(100) DEFAULT NULL,
  `fuzzylogic` varchar(5) DEFAULT NULL,
  `fileMonitoringType` varchar(200) DEFAULT NULL,
  `createdByUser` varchar(1000) DEFAULT NULL,
  `validityThreshold` double(40,2) DEFAULT NULL,
  `dGroupDataDriftCheck` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`idApp`),
  KEY `lapp_ibfk_1` (`idData`),
  CONSTRAINT `lapp_ibfk_1` FOREIGN KEY (`idData`) REFERENCES `listDataSources` (`idData`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=360 DEFAULT CHARSET=latin1;
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
-- Table structure for table `listColGlobalRules`
--

DROP TABLE IF EXISTS `listColGlobalRules`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `listColGlobalRules` (
  `idListColrules` int(11) NOT NULL AUTO_INCREMENT,
  `ruleName` varchar(45) DEFAULT NULL,
  `description` varchar(50) DEFAULT NULL,
  `createdAt` date DEFAULT NULL,
  `expression` varchar(1000) DEFAULT NULL,
  `domain_id` int(100) DEFAULT NULL,
  `project_id` bigint(20) DEFAULT NULL,
  `ruleType` varchar(45) DEFAULT NULL,
  `externalDatasetName` varchar(245) DEFAULT NULL,
  `idRightData` int(100) DEFAULT NULL,
  `matchingRules` varchar(245) DEFAULT NULL,
  `createdByUser` varchar(1000) DEFAULT NULL,
  PRIMARY KEY (`idListColrules`),
  KEY `lcolrule_proj_id_idx` (`project_id`),
  CONSTRAINT `lcolrule_proj_id4` FOREIGN KEY (`project_id`) REFERENCES `project` (`idProject`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8;
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
  `sourcetemplateone` varchar(45) DEFAULT NULL,
  `sourcetemplatesecond` varchar(45) DEFAULT NULL,
  `createdByUser` varchar(1000) DEFAULT NULL,
  PRIMARY KEY (`idListColrules`)
) ENGINE=InnoDB AUTO_INCREMENT=61 DEFAULT CHARSET=utf8;
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
) ENGINE=InnoDB AUTO_INCREMENT=300 DEFAULT CHARSET=latin1;
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
) ENGINE=InnoDB AUTO_INCREMENT=300 DEFAULT CHARSET=latin1;
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
) ENGINE=InnoDB AUTO_INCREMENT=579 DEFAULT CHARSET=latin1;
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
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=latin1;
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
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=latin1;
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
  `hostName` varchar(500) NOT NULL,
  `portName` varchar(45) NOT NULL,
  `userName` text NOT NULL,
  `pwd` text NOT NULL,
  `schemaName` text NOT NULL,
  `folderName` text NOT NULL,
  `queryString` text,
  `query` text NOT NULL,
  `incrementalType` varchar(10) DEFAULT NULL,
  `dateFormat` varchar(50) DEFAULT NULL,
  `sliceStart` varchar(10) DEFAULT NULL,
  `sliceEnd` varchar(10) DEFAULT NULL,
  `idDataSchema` bigint(20) NOT NULL,
  `whereCondition` varchar(500) DEFAULT NULL,
  `domain` text,
  `fileHeader` varchar(10) DEFAULT 'Y',
  `metaData` varchar(1000) DEFAULT NULL,
  `isRawData` varchar(20) DEFAULT NULL,
  `sslEnb` varchar(10) DEFAULT NULL,
  `sslTrustStorePath` varchar(200) DEFAULT NULL,
  `trustPassword` varchar(100) DEFAULT NULL,
  `hivejdbchost` varchar(45) DEFAULT NULL,
  `hivejdbcport` varchar(45) DEFAULT NULL,
  `gatewayPath` varchar(250) DEFAULT NULL,
  `jksPath` varchar(250) DEFAULT NULL,
  `zookeeperUrl` varchar(250) DEFAULT NULL,
  PRIMARY KEY (`idlistDataAccess`),
  KEY `listda_ibfk_1` (`idData`),
  CONSTRAINT `listdataaccess_ibfk_1` FOREIGN KEY (`idData`) REFERENCES `listDataSources` (`idData`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `listdataaccess_ibfk_2` FOREIGN KEY (`idData`) REFERENCES `listDataSources` (`idData`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=288 DEFAULT CHARSET=latin1;
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
  `createdByUser` varchar(1000) DEFAULT NULL,
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
  `isMasked` varchar(10) DEFAULT NULL,
  `partitionBy` varchar(10) DEFAULT 'N',
  `lengthCheck` varchar(45) DEFAULT NULL,
  `lengthValue` varchar(100) DEFAULT NULL,
  `applyrule` varchar(45) DEFAULT 'N',
  `startDate` varchar(5) DEFAULT NULL,
  `timelinessKey` varchar(5) DEFAULT NULL,
  `endDate` varchar(5) DEFAULT NULL,
  `defaultCheck` varchar(12) DEFAULT NULL,
  `defaultValues` varchar(12) DEFAULT NULL,
  `patternCheck` varchar(10) DEFAULT 'N',
  `patterns` varchar(500) DEFAULT NULL,
  `dateRule` varchar(5) DEFAULT NULL,
  `badData` varchar(5) DEFAULT NULL,
  `dateFormat` varchar(50) DEFAULT NULL,
  `correlationcolumn` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`idColumn`),
  KEY `listdd_ibfk_1` (`idData`)
) ENGINE=InnoDB AUTO_INCREMENT=6745 DEFAULT CHARSET=latin1;
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
  `gss_jaas` varchar(500) DEFAULT NULL,
  `krb5conf` varchar(500) DEFAULT NULL,
  `autoGenerate` varchar(10) DEFAULT 'N',
  `suffixes` varchar(1000) DEFAULT NULL,
  `prefixes` varchar(1000) DEFAULT NULL,
  `createdAt` datetime NOT NULL,
  `updatedAt` datetime NOT NULL,
  `createdBy` bigint(20) NOT NULL,
  `updatedBy` bigint(11) NOT NULL,
  `project_id` int(10) DEFAULT NULL,
  `sslEnb` varchar(10) DEFAULT NULL,
  `sslTrustStorePath` varchar(200) DEFAULT NULL,
  `trustPassword` varchar(100) DEFAULT NULL,
  `hivejdbcport` varchar(45) DEFAULT NULL,
  `hivejdbchost` varchar(45) DEFAULT NULL,
  `Action` varchar(100) DEFAULT NULL,
  `gatewayPath` varchar(250) DEFAULT NULL,
  `jksPath` varchar(250) DEFAULT NULL,
  `zookeeperUrl` varchar(250) DEFAULT NULL,
  `createdByUser` varchar(1000) DEFAULT NULL,
  `folderPath` varchar(1000) DEFAULT NULL,
  `fileNamePattern` varchar(250) DEFAULT NULL,
  `fileDataFormat` varchar(250) DEFAULT NULL,
  `headerPresent` varchar(10) DEFAULT NULL,
  `headerFilePath` varchar(1000) DEFAULT NULL,
  `headerFileNamePattern` varchar(250) DEFAULT NULL,
  `headerFileDataFormat` varchar(250) DEFAULT NULL,
  `bucketName` varchar(500) DEFAULT NULL,
  `accessKey` varchar(1000) DEFAULT NULL,
  `secretKey` varchar(1000) DEFAULT NULL,
  PRIMARY KEY (`idDataSchema`)
) ENGINE=InnoDB AUTO_INCREMENT=56 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `listDataSources`
--

DROP TABLE IF EXISTS `listDataSources`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `listDataSources` (
  `idData` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(1000) NOT NULL DEFAULT '',
  `description` varchar(1000) NOT NULL,
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
  `active` varchar(10) DEFAULT 'yes',
  `project_id` int(20) DEFAULT NULL,
  `profilingEnabled` varchar(20) DEFAULT NULL,
  `advancedRulesEnabled` varchar(20) DEFAULT NULL,
  `createdByUser` varchar(1000) DEFAULT NULL,
  PRIMARY KEY (`idData`),
  KEY `listDataSources_ibfk_1` (`idDataBlend`),
  CONSTRAINT `listDataSources_ibfk_1` FOREIGN KEY (`idDataBlend`) REFERENCES `listDataBlend` (`idDataBlend`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=288 DEFAULT CHARSET=latin1;
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
-- Table structure for table `listModelGovernance`
--

DROP TABLE IF EXISTS `listModelGovernance`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `listModelGovernance` (
  `idModel` bigint(20) NOT NULL AUTO_INCREMENT,
  `idApp` bigint(20) NOT NULL,
  `modelGovernanceType` varchar(50) DEFAULT NULL,
  `modelIdCol` varchar(50) DEFAULT NULL,
  `decileCol` varchar(50) DEFAULT NULL,
  `expectedPercentage` double NOT NULL DEFAULT '0',
  `thresholdPercentage` double NOT NULL DEFAULT '0',
  `leftSourceSliceStart` varchar(50) DEFAULT NULL,
  `leftSourceSliceEnd` varchar(50) DEFAULT NULL,
  `rightSourceSliceStart` varchar(50) DEFAULT NULL,
  `rightSourceSliceEnd` varchar(50) DEFAULT NULL,
  `measurementExpression` varchar(1000) DEFAULT NULL,
  `matchingExpression` varchar(1000) DEFAULT NULL,
  PRIMARY KEY (`idModel`)
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
-- Table structure for table `listSchedule`
--

DROP TABLE IF EXISTS `listSchedule`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `listSchedule` (
  `idSchedule` bigint(20) NOT NULL AUTO_INCREMENT,
  `time` time DEFAULT NULL,
  `name` varchar(45) NOT NULL,
  `description` varchar(200) DEFAULT '',
  `frequency` varchar(45) NOT NULL,
  `scheduleDay` varchar(255) DEFAULT NULL,
  `exceptionMatching` varchar(10) NOT NULL DEFAULT 'N',
  `project_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`idSchedule`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `listStatisticalMatchingConfig`
--

DROP TABLE IF EXISTS `listStatisticalMatchingConfig`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `listStatisticalMatchingConfig` (
  `idStat` bigint(20) NOT NULL AUTO_INCREMENT,
  `idApp` bigint(20) NOT NULL,
  `leftSideExp` varchar(500) NOT NULL,
  `rightSideExp` varchar(500) NOT NULL,
  `recordCountType` varchar(20) DEFAULT NULL,
  `recordCountThreshold` double DEFAULT '0',
  `measurementSum` varchar(1) DEFAULT 'N',
  `measurementSumType` varchar(20) DEFAULT NULL,
  `measurementSumThreshold` double DEFAULT '0',
  `measurementMean` varchar(1) DEFAULT 'N',
  `measurementMeanType` varchar(20) DEFAULT NULL,
  `measurementMeanThreshold` double DEFAULT '0',
  `measurementStdDev` varchar(1) DEFAULT 'N',
  `measurementStdDevType` varchar(20) DEFAULT NULL,
  `measurementStdDevThreshold` double DEFAULT '0',
  `groupBy` varchar(1) DEFAULT 'N',
  PRIMARY KEY (`idStat`)
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
-- Table structure for table `list_batch_schema`
--

DROP TABLE IF EXISTS `list_batch_schema`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `list_batch_schema` (
  `idBatchSchema` bigint(20) NOT NULL AUTO_INCREMENT,
  `schemaBatchName` text NOT NULL,
  `schemaBatchType` text NOT NULL,
  `batchFileLocation` text NOT NULL,
  `totalSchemas` bigint(20) DEFAULT NULL,
  `completedSchemas` bigint(20) DEFAULT NULL,
  `idDataSchemas` text,
  PRIMARY KEY (`idBatchSchema`)
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
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `project`
--

DROP TABLE IF EXISTS `project`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `project` (
  `idProject` bigint(20) NOT NULL AUTO_INCREMENT,
  `projectName` varchar(100) DEFAULT NULL,
  `projectDescription` varchar(100) DEFAULT NULL,
  `createdAt` datetime DEFAULT NULL,
  `updatedAt` datetime DEFAULT NULL,
  PRIMARY KEY (`idProject`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `projecttoActDiruser`
--

DROP TABLE IF EXISTS `projecttoActDiruser`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `projecttoActDiruser` (
  `idProject` bigint(20) DEFAULT NULL,
  `idUser` varchar(110) DEFAULT NULL,
  `isOwner` varchar(1) NOT NULL,
  KEY `fk5_idx` (`idProject`),
  CONSTRAINT `fk5` FOREIGN KEY (`idProject`) REFERENCES `project` (`idProject`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `projecttogroup`
--

DROP TABLE IF EXISTS `projecttogroup`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `projecttogroup` (
  `idProject` bigint(20) DEFAULT NULL,
  `idGroup` bigint(20) DEFAULT NULL,
  `isOwner` varchar(1) NOT NULL,
  KEY `fk1_idx` (`idProject`),
  KEY `fk2_idx` (`idGroup`),
  CONSTRAINT `fk1` FOREIGN KEY (`idProject`) REFERENCES `project` (`idProject`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk2` FOREIGN KEY (`idGroup`) REFERENCES `projgroup` (`idGroup`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `projecttouser`
--

DROP TABLE IF EXISTS `projecttouser`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `projecttouser` (
  `idProject` bigint(20) DEFAULT NULL,
  `idUser` varchar(100) DEFAULT NULL,
  `isOwner` varchar(1) NOT NULL,
  KEY `fk3_idx` (`idProject`),
  KEY `fk4_idx` (`idUser`),
  CONSTRAINT `fk3` FOREIGN KEY (`idProject`) REFERENCES `project` (`idProject`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `projgroup`
--

DROP TABLE IF EXISTS `projgroup`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `projgroup` (
  `idGroup` bigint(20) NOT NULL AUTO_INCREMENT,
  `groupName` varchar(100) DEFAULT NULL,
  `description` varchar(100) DEFAULT NULL,
  `createdAt` date DEFAULT NULL,
  `updatedAt` date DEFAULT NULL,
  PRIMARY KEY (`idGroup`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ruleFields`
--

DROP TABLE IF EXISTS `ruleFields`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ruleFields` (
  `rule_id` int(11) NOT NULL,
  `usercolumns` varchar(100) NOT NULL,
  `possiblenames` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ruleTosynonym`
--

DROP TABLE IF EXISTS `ruleTosynonym`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ruleTosynonym` (
  `rule_id` int(11) NOT NULL,
  `synonym_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `rule_Template_Mapping`
--

DROP TABLE IF EXISTS `rule_Template_Mapping`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `rule_Template_Mapping` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `templateid` int(11) NOT NULL,
  `ruleId` varchar(100) NOT NULL,
  `ruleName` varchar(300) NOT NULL,
  `ruleExpression` varchar(300) NOT NULL,
  `ruleType` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `runScheduledTasks`
--

DROP TABLE IF EXISTS `runScheduledTasks`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `runScheduledTasks` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `idApp` int(11) NOT NULL,
  `status` varchar(50) DEFAULT 'started',
  `startTime` datetime DEFAULT NULL,
  `endTime` datetime DEFAULT NULL,
  `processId` bigint(20) DEFAULT NULL,
  `deployMode` varchar(250) DEFAULT NULL,
  `sparkAppId` varchar(1000) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=119 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `runTemplateTasks`
--

DROP TABLE IF EXISTS `runTemplateTasks`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `runTemplateTasks` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `idData` int(11) NOT NULL,
  `status` varchar(50) DEFAULT NULL,
  `startTime` datetime DEFAULT NULL,
  `endTime` datetime DEFAULT NULL,
  `processId` bigint(20) DEFAULT NULL,
  `deployMode` varchar(250) DEFAULT NULL,
  `sparkAppId` varchar(1000) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=343 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `runningtaskStatus`
--

DROP TABLE IF EXISTS `runningtaskStatus`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `runningtaskStatus` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `idApp` int(11) NOT NULL,
  `status` varchar(50) DEFAULT NULL,
  `start_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=668 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

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
  `dateTime` datetime DEFAULT NULL,
  `project_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `schema_version`
--

DROP TABLE IF EXISTS `schema_version`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `schema_version` (
  `installed_rank` int(11) NOT NULL,
  `version` varchar(50) DEFAULT NULL,
  `description` varchar(200) NOT NULL,
  `type` varchar(20) NOT NULL,
  `script` varchar(1000) NOT NULL,
  `checksum` int(11) DEFAULT NULL,
  `installed_by` varchar(100) NOT NULL,
  `installed_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `execution_time` int(11) NOT NULL,
  `success` tinyint(1) NOT NULL,
  PRIMARY KEY (`installed_rank`),
  KEY `schema_version_s_idx` (`success`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `secure_API`
--

DROP TABLE IF EXISTS `secure_API`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `secure_API` (
  `accessTokenId` text,
  `secretAccessToken` text
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sub_task_status`
--

DROP TABLE IF EXISTS `sub_task_status`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sub_task_status` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `Date` date DEFAULT NULL,
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
  `rules` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `symbol`
--

DROP TABLE IF EXISTS `symbol`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `symbol` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `symbol` varchar(45) DEFAULT NULL,
  `Type` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=268 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `symbol`
--

LOCK TABLES `symbol` WRITE;
/*!40000 ALTER TABLE `symbol` DISABLE KEYS */;
INSERT INTO `symbol` VALUES (1,'!','Operator'),(2,'%','Operator'),(3,'&','Operator'),(4,'*','Operator'),(5,'+','Operator'),(6,'-','Operator'),(7,'/','Operator'),(8,'<','Operator'),(9,'<=','Operator'),(10,'<=>','Operator'),(11,'=','Operator'),(12,'==','Operator'),(13,'>','Operator'),(14,'>=','Operator'),(15,'^','Operator'),(16,'abs','Function'),(17,'acos','Function'),(18,'add_months','Function'),(19,'and','Function'),(20,'approx_count_distinct','Function'),(21,'approx_percentile','Function'),(22,'array','Function'),(23,'array_contains','Function'),(24,'ascii','Function'),(25,'asin','Function'),(26,'assert_true','Function'),(27,'atan','Function'),(28,'atan2','Function'),(29,'avg','Function'),(30,'base64','Function'),(31,'bigint','Function'),(32,'bin','Function'),(33,'binary','Function'),(34,'bit_length','Function'),(35,'boolean','Function'),(36,'bround','Function'),(37,'cast','Function'),(38,'cbrt','Function'),(39,'ceil','Function'),(40,'ceiling','Function'),(41,'char','Function'),(42,'char_length','Function'),(43,'character_length','Function'),(44,'chr','Function'),(45,'coalesce','Function'),(46,'collect_list','Function'),(47,'collect_set','Function'),(48,'concat','Function'),(49,'concat_ws','Function'),(50,'conv','Function'),(51,'corr','Function'),(52,'cos','Function'),(53,'cosh','Function'),(54,'cot','Function'),(55,'count','Function'),(56,'count_min_sketch','Function'),(57,'covar_pop','Function'),(58,'covar_samp','Function'),(59,'crc32','Function'),(60,'cube','Function'),(61,'cume_dist','Function'),(62,'current_database','Function'),(63,'current_date','Function'),(64,'current_timestamp','Function'),(65,'date','Function'),(66,'date_add','Function'),(67,'date_format','Function'),(68,'date_sub','Function'),(69,'date_trunc','Function'),(70,'datediff','Function'),(71,'day','Function'),(72,'dayofmonth','Function'),(73,'dayofweek','Function'),(74,'dayofyear','Function'),(75,'decimal','Function'),(76,'decode','Function'),(77,'degrees','Function'),(78,'dense_rank','Function'),(79,'double','Function'),(80,'e','Function'),(81,'elt','Function'),(82,'encode','Function'),(83,'exp','Function'),(84,'explode','Function'),(85,'explode_outer','Function'),(86,'expm1','Function'),(87,'factorial','Function'),(88,'find_in_set','Function'),(89,'first','Function'),(90,'first_value','Function'),(91,'float','Function'),(92,'floor','Function'),(93,'format_number','Function'),(94,'format_string','Function'),(95,'from_json','Function'),(96,'from_unixtime','Function'),(97,'from_utc_timestamp','Function'),(98,'get_json_object','Function'),(99,'greatest','Function'),(100,'grouping','Function'),(101,'grouping_id','Function'),(102,'hash','Function'),(103,'hex','Function'),(104,'hour','Function'),(105,'hypot','Function'),(106,'if','Function'),(107,'ifnull','Function'),(108,'in','Function'),(109,'initcap','Function'),(110,'inline','Function'),(111,'inline_outer','Function'),(112,'input_file_block_length','Function'),(113,'input_file_block_start','Function'),(114,'input_file_name','Function'),(115,'instr','Function'),(116,'int','Function'),(117,'isnan','Function'),(118,'isnotnull','Function'),(119,'isnull','Function'),(120,'java_method','Function'),(121,'json_tuple','Function'),(122,'kurtosis','Function'),(123,'lag','Function'),(124,'last','Function'),(125,'last_day','Function'),(126,'last_value','Function'),(127,'lcase','Function'),(128,'lead','Function'),(129,'least','Function'),(130,'left','Function'),(131,'length','Function'),(132,'levenshtein','Function'),(133,'like','Function'),(134,'ln','Function'),(135,'locate','Function'),(136,'log','Function'),(137,'log10','Function'),(138,'log1p','Function'),(139,'log2','Function'),(140,'lower','Function'),(141,'lpad','Function'),(142,'ltrim','Function'),(143,'map','Function'),(144,'map_keys','Function'),(145,'map_values','Function'),(146,'max','Function'),(147,'md5','Function'),(148,'mean','Function'),(149,'min','Function'),(150,'minute','Function'),(151,'mod','Function'),(152,'monotonically_increasing_id','Function'),(153,'month','Function'),(154,'months_between','Function'),(155,'named_struct','Function'),(156,'nanvl','Function'),(157,'negative','Function'),(158,'next_day','Function'),(159,'not','Function'),(160,'now','Function'),(161,'ntile','Function'),(162,'nullif','Function'),(163,'nvl','Function'),(164,'nvl2','Function'),(165,'octet_length','Function'),(166,'or','Function'),(167,'parse_url','Function'),(168,'percent_rank','Function'),(169,'percentile','Function'),(170,'percentile_approx','Function'),(171,'pi','Function'),(172,'pmod','Function'),(173,'posexplode','Function'),(174,'posexplode_outer','Function'),(175,'position','Function'),(176,'positive','Function'),(177,'pow','Function'),(178,'power','Function'),(179,'printf','Function'),(180,'quarter','Function'),(181,'radians','Function'),(182,'rand','Function'),(183,'randn','Function'),(184,'rank','Function'),(185,'reflect','Function'),(186,'regexp_extract','Function'),(187,'regexp_replace','Function'),(188,'repeat','Function'),(189,'replace','Function'),(190,'reverse','Function'),(191,'right','Function'),(192,'rint','Function'),(193,'rlike','Function'),(194,'rollup','Function'),(195,'round','Function'),(196,'row_number','Function'),(197,'rpad','Function'),(198,'rtrim','Function'),(199,'second','Function'),(200,'sentences','Function'),(201,'sha','Function'),(202,'sha1','Function'),(203,'sha2','Function'),(204,'shiftleft','Function'),(205,'shiftright','Function'),(206,'shiftrightunsigned','Function'),(207,'sign','Function'),(208,'signum','Function'),(209,'sin','Function'),(210,'sinh','Function'),(211,'size','Function'),(212,'skewness','Function'),(213,'smallint','Function'),(214,'sort_array','Function'),(215,'soundex','Function'),(216,'space','Function'),(217,'spark_partition_id','Function'),(218,'split','Function'),(219,'sqrt','Function'),(220,'stack','Function'),(221,'std','Function'),(222,'stddev','Function'),(223,'stddev_pop','Function'),(224,'stddev_samp','Function'),(225,'str_to_map','Function'),(226,'string','Function'),(227,'struct','Function'),(228,'substr','Function'),(229,'substring','Function'),(230,'substring_index','Function'),(231,'sum','Function'),(232,'tan','Function'),(233,'tanh','Function'),(234,'timestamp','Function'),(235,'tinyint','Function'),(236,'to_date','Function'),(237,'to_json','Function'),(238,'to_timestamp','Function'),(239,'to_unix_timestamp','Function'),(240,'to_utc_timestamp','Function'),(241,'translate','Function'),(242,'trim','Function'),(243,'trunc','Function'),(244,'ucase','Function'),(245,'unbase64','Function'),(246,'unhex','Function'),(247,'unix_timestamp','Function'),(248,'upper','Function'),(249,'uuid','Function'),(250,'var_pop','Function'),(251,'var_samp','Function'),(252,'variance','Function'),(253,'weekofyear','Function'),(254,'when','Function'),(255,'window','Function'),(256,'xpath','Function'),(257,'xpath_boolean','Function'),(258,'xpath_double','Function'),(259,'xpath_float','Function'),(260,'xpath_int','Function'),(261,'xpath_long','Function'),(262,'xpath_number','Function'),(263,'xpath_short','Function'),(264,'xpath_string','Function'),(265,'year','Function'),(266,'|','Operator'),(267,'~','Operator');
/*!40000 ALTER TABLE `symbol` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `task_progress_status`
--

DROP TABLE IF EXISTS `task_progress_status`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `task_progress_status` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `Date` date DEFAULT NULL,
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
  `rules` varchar(10) DEFAULT NULL,
  `dfread` varchar(1000) DEFAULT NULL,
  `dfread2` varchar(1000) DEFAULT NULL,
  `matchingStatus` varchar(1000) DEFAULT NULL,
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

-- Dump completed on 2020-01-14  9:10:43
