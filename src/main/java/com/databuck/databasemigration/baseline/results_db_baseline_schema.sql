-- MySQL dump 10.13  Distrib 5.7.28, for Linux (x86_64)
--
-- Host: localhost    Database: databuck_results_db
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
-- Table structure for table `DashBoard_Summary`
--

DROP TABLE IF EXISTS `DashBoard_Summary`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `DashBoard_Summary` (
  `Date` varchar(100) DEFAULT NULL,
  `Run` int(11) DEFAULT NULL,
  `AppId` int(11) DEFAULT NULL,
  `DQI` double DEFAULT NULL,
  `Status` varchar(50) DEFAULT NULL,
  `Key_Metric_1` double DEFAULT NULL,
  `Key_Metric_2` double DEFAULT NULL,
  `Test` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `data_matching_dashboard`
--

DROP TABLE IF EXISTS `data_matching_dashboard`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `data_matching_dashboard` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `idapp` bigint(20) DEFAULT NULL,
  `date` date DEFAULT NULL,
  `run` bigint(20) DEFAULT NULL,
  `validationCheckName` text,
  `source1Name` text,
  `source2Name` text,
  `source1Count` bigint(20) DEFAULT NULL,
  `source1OnlyRecords` bigint(20) DEFAULT NULL,
  `source1Status` text,
  `source2Count` bigint(20) DEFAULT NULL,
  `source2OnlyRecords` bigint(20) DEFAULT NULL,
  `source2Status` text,
  `unMatchedRecords` bigint(20) DEFAULT NULL,
  `unMatchedStatus` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `data_quality_dashboard`
--

DROP TABLE IF EXISTS `data_quality_dashboard`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `data_quality_dashboard` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `IdApp` bigint(20) NOT NULL,
  `date` date DEFAULT NULL,
  `run` bigint(20) DEFAULT NULL,
  `validationCheckName` text,
  `sourceName` text,
  `recordCountStatus` text,
  `nullCountStatus` text,
  `primaryKeyStatus` text,
  `userSelectedFieldStatus` text,
  `numericalFieldStatus` text,
  `stringFieldStatus` text,
  `recordAnomalyStatus` text,
  `dataDriftStatus` text,
  `aggregateDQI` double DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=89 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `data_quality_historic_dashboard`
--

DROP TABLE IF EXISTS `data_quality_historic_dashboard`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `data_quality_historic_dashboard` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `idApp` bigint(20) NOT NULL,
  `validationCheckName` varchar(100) NOT NULL,
  `date` date NOT NULL,
  `run` bigint(20) NOT NULL,
  `createdAt` datetime DEFAULT NULL,
  `dataSetName` varchar(200) DEFAULT NULL,
  `testType` varchar(200) DEFAULT NULL,
  `aggregateDQI` double DEFAULT '0',
  `fileContentValidationStatus` varchar(20) DEFAULT NULL,
  `columnOrderValidationStatus` varchar(20) DEFAULT NULL,
  `absoluteRCDQI` double DEFAULT '0',
  `absoluteRCStatus` varchar(20) DEFAULT NULL,
  `absoluteRCRecordCount` bigint(20) DEFAULT NULL,
  `absoluteRCAverageRecordCount` bigint(20) DEFAULT NULL,
  `aggregateRCDQI` double DEFAULT '0',
  `aggregateRCStatus` varchar(20) DEFAULT NULL,
  `aggregateRCRecordCount` bigint(20) DEFAULT NULL,
  `nullCountDQI` double DEFAULT '0',
  `nullCountStatus` varchar(20) DEFAULT NULL,
  `nullCountColumns` bigint(20) DEFAULT NULL,
  `nullCountColumnsFailed` bigint(20) DEFAULT NULL,
  `primaryKeyDQI` double DEFAULT '0',
  `primaryKeyStatus` varchar(20) DEFAULT NULL,
  `primaryKeyDuplicates` bigint(20) DEFAULT NULL,
  `userSelectedDQI` double DEFAULT '0',
  `userSelectedStatus` varchar(20) DEFAULT NULL,
  `userSelectedDuplicates` bigint(20) DEFAULT NULL,
  `numericalDQI` double DEFAULT '0',
  `numericalStatus` varchar(20) DEFAULT NULL,
  `numericalColumns` bigint(20) DEFAULT NULL,
  `numericalRecordsFailed` bigint(20) DEFAULT NULL,
  `stringDQI` double DEFAULT '0',
  `stringStatus` varchar(20) DEFAULT NULL,
  `stringColumns` bigint(20) DEFAULT NULL,
  `stringRecordsFailed` bigint(20) DEFAULT NULL,
  `recordAnomalyDQI` double DEFAULT '0',
  `recordAnomalyStatus` varchar(20) DEFAULT NULL,
  `recordAnomalyRecords` bigint(20) DEFAULT NULL,
  `recordAnomalyRecordsFailed` bigint(20) DEFAULT NULL,
  `ruleType` varchar(20) DEFAULT NULL,
  `ruleDQI` double DEFAULT '0',
  `dataDriftDQI` double DEFAULT '0',
  `dataDriftStatus` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `data_quality_sql_rules`
--

DROP TABLE IF EXISTS `data_quality_sql_rules`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `data_quality_sql_rules` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `date` date NOT NULL,
  `idapp` mediumtext NOT NULL,
  `run` int(11) NOT NULL,
  `rulename` varchar(500) NOT NULL,
  `total_failed_records` mediumtext NOT NULL,
  `status` tinyint(1) NOT NULL,
  `top_failed_data` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `file_management_run`
--

DROP TABLE IF EXISTS `file_management_run`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `file_management_run` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `appId` int(11) NOT NULL,
  `dateOfRun` datetime NOT NULL,
  `fileName` varchar(500) NOT NULL,
  `hashCode` bigint(20) DEFAULT NULL,
  `missingFiles` text,
  `extraFiles` text,
  `realFileName` varchar(100) DEFAULT NULL,
  `lastProcessedTimestamp` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `historical_matching_run`
--

DROP TABLE IF EXISTS `historical_matching_run`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `historical_matching_run` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `appId` int(11) NOT NULL,
  `Run` int(11) NOT NULL,
  `Date` varchar(100) NOT NULL,
  `tableName` varchar(100) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `processData`
--

DROP TABLE IF EXISTS `processData`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `processData` (
  `idApp` bigint(20) DEFAULT NULL,
  `Run` int(11) DEFAULT NULL,
  `Date` date DEFAULT NULL,
  `folderName` varchar(500) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `result_master_table`
--

DROP TABLE IF EXISTS `result_master_table`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `result_master_table` (
  `appID` int(11) DEFAULT NULL,
  `AppName` text,
  `AppType` text,
  `Result_Category` text,
  `Result_Category1` text,
  `Result_Category2` text,
  `Result_Type` text,
  `Table_Name` text,
  `project_id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
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
  `globalrules` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=134 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

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
  `schemaMatchingTotal` bigint(20) DEFAULT NULL,
  `schemaMatchingCompleted` bigint(20) DEFAULT NULL,
  `defaultcheck` varchar(45) DEFAULT NULL,
  `TimelinessKeyCheck` varchar(45) DEFAULT NULL,
  `patternCheck` varchar(45) DEFAULT NULL,
  `lengthCheck` varchar(50) DEFAULT NULL,
  `badData` varchar(50) DEFAULT NULL,
  `globalrules` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=134 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `template_task_status`
--

DROP TABLE IF EXISTS `template_task_status`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `template_task_status` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `idData` int(11) NOT NULL,
  `taskName` varchar(500) NOT NULL,
  `status` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1759 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2020-01-14 12:21:39
