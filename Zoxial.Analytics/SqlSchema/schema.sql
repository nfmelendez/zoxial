-- MySQL dump 10.13  Distrib 5.1.41, for debian-linux-gnu (x86_64)
--
-- Host: localhost    Database: fanpagespider
-- ------------------------------------------------------
-- Server version	5.1.41-3ubuntu12.8

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
-- Creates database
--

DROP DATABASE IF EXISTS `zoxialanalytics`;
CREATE DATABASE `zoxialanalytics`;
USE `zoxialanalytics`;

--
-- Table structure for table `charts`
--

DROP TABLE IF EXISTS `charts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `charts` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `page_name` varchar(100) DEFAULT NULL,
  `facebook_page_id` varchar(30) DEFAULT NULL,
  `chart_id` varchar(255) DEFAULT NULL,
  `description` text,
  `chart_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `charts`
--

LOCK TABLES `charts` WRITE;
/*!40000 ALTER TABLE `charts` DISABLE KEYS */;
INSERT INTO `charts` VALUES (1,'Comunidad Movistar Argentina','233815212529','celularesArgentina','Competencia de celulares en argentina\n','Personal vs Movistar vs Claro'),(2,'Personal Argentina','123576404337836','celularesArgentina','Competencia de celulares en argentina\n','Personal vs Movistar vs Claro'),(3,'Claro Argentina','253622671333956','celularesArgentina','Competencia de celulares en argentina\n','Personal vs Movistar vs Claro'),(4,'Cristina Fernández de Kirchner','115689108495633','cristina-kirchner-vs-maurio-macri','CFK vs Mauricio Macri','CFK vs Macri'),(5,'Mauricio Macri','55432788477','cristina-kirchner-vs-maurio-macri','CFK vs Mauricio Macri','CFK vs Macri'),(13,'Workstation','150898748313373','co-working-places','Co-working Places','Co-working Places'),(14,'Urban Station Buenos Aires','195333101666','co-working-places','Co-working Places','Co-working Places'),(15,'Areatres workplace','144736955585915','co-working-places','Co-working Places','Co-working Places'),(16,'Dot Baires Shopping','66632792565','shoppings-ciudad-buenos-aires-dotbaires-unicenter-altopalermo-abasto','Shoppings de la Ciudad de Buenos Aires','Dot baires vs Abasto vs Alto Palermo vs Unicenter'),(17,'Abasto Shopping','99938452589','shoppings-ciudad-buenos-aires-dotbaires-unicenter-altopalermo-abasto','Shoppings de la Ciudad de Buenos Aires','Dot baires vs Abasto vs Alto Palermo vs Unicenter'),(18,'Alto Palermo','122760603905','shoppings-ciudad-buenos-aires-dotbaires-unicenter-altopalermo-abasto','Shoppings de la Ciudad de Buenos Aires','Dot baires vs Abasto vs Alto Palermo vs Unicenter'),(19,'Unicenter','197061933655671','shoppings-ciudad-buenos-aires-dotbaires-unicenter-altopalermo-abasto','Shoppings de la Ciudad de Buenos Aires','Dot baires vs Abasto vs Alto Palermo vs Unicenter'),(20,'River Plate','122435891129438','boca-vs-river','Boca Vs. River','Boca Vs. River'),(21,'Boca','71948513452','boca-vs-river','Boca Vs. River','Boca Vs. River'),(22,'Zoxial','232368976785849','Facebook-apps-development-companies','Facebook apps development companies','Zoxial vs altodot vs SocialTools.me'),(23,'Altodot','149352780733','Facebook-apps-development-companies','Facebook apps development companies','Zoxial vs altodot vs SocialTools.me'),(24,'SocialTools.me','266585266755203','Facebook-apps-development-companies','Facebook apps development companies','Zoxial vs altodot vs SocialTools.me');
/*!40000 ALTER TABLE `charts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `posts`
--

DROP TABLE IF EXISTS `posts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `posts` (
  `id` varchar(255) NOT NULL,
  `comments` bigint(20) DEFAULT NULL,
  `likes` bigint(20) DEFAULT NULL,
  `shares` bigint(20) DEFAULT NULL,
  `from_id` varchar(255) DEFAULT NULL,
  `from_name` varchar(255) DEFAULT NULL,
  `page_name` varchar(255) DEFAULT NULL,
  `created_time` datetime DEFAULT NULL,
  `message` text,
  `engagement` bigint(20) DEFAULT NULL,
  `raw_post` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `posts`
--

