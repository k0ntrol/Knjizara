-- MariaDB dump 10.19  Distrib 10.4.32-MariaDB, for Win64 (AMD64)
--
-- Host: 127.0.0.1    Database: knjizara
-- ------------------------------------------------------
-- Server version	10.4.32-MariaDB

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
-- Table structure for table `adresa`
--

DROP TABLE IF EXISTS `adresa`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `adresa` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `grad` varchar(100) DEFAULT NULL,
  `drzava_id` int(11) NOT NULL,
  `naziv_ulice` varchar(255) DEFAULT NULL,
  `broj_ulice` varchar(20) DEFAULT NULL,
  `postanski_broj` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `drzava_id` (`drzava_id`),
  CONSTRAINT `adresa_ibfk_1` FOREIGN KEY (`drzava_id`) REFERENCES `drzava` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `adresa`
--

LOCK TABLES `adresa` WRITE;
/*!40000 ALTER TABLE `adresa` DISABLE KEYS */;
INSERT INTO `adresa` VALUES (2,'Zagreb',2,'Ilica','12','10000'),(3,'Sarajevo',3,'Ferhadija','7','71000'),(4,'Novi Sad',1,'Zmaj Jovina','8','21000'),(5,'Podgorica',4,'Slobode','15','81000'),(7,'Beograd',1,'Baje Sekulića','12','11000'),(9,'Beograd',1,'Knez Mihailova','54','11000'),(10,'Podgorica',4,'Bokeška','14','81101'),(11,'Beograd',1,'Kneza Lazara','30','11000'),(12,'Beograd',1,'Knez Mihajlova','5','11000'),(13,'Bar',4,'Makedonska','G18','85000'),(14,'Beograd',1,'Knez Mihajlova','13','11000'),(15,'Bar',4,'Bokeška','10','85000'),(16,'Podgorica',4,'Bokeska','10','81000'),(17,'Bar',4,'Makedonska','20','85000'),(18,'Beograd',1,'Sime Milosevica','6','101801'),(19,'Beograd',1,'Nade Puric','29','101801'),(20,'Podgorica',4,'Bokeska','15','81000'),(21,'Sarajevo',3,'Kazazi','23','71000'),(22,'Podgorica',4,'Njegoseva','18','81000'),(23,'Bar',4,'Bjelise','BB','85000');
/*!40000 ALTER TABLE `adresa` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `autor`
--

DROP TABLE IF EXISTS `autor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `autor` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `ime` varchar(100) DEFAULT NULL,
  `prezime` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `autor`
--

LOCK TABLES `autor` WRITE;
/*!40000 ALTER TABLE `autor` DISABLE KEYS */;
INSERT INTO `autor` VALUES (1,'Ivo','Andrić'),(2,'Meša','Selimović'),(3,'Danilo','Kiš'),(4,'Isidora','Sekulić'),(5,'Miroslav','Krleža'),(6,'Desanka','Maksimovic'),(9,'Miloš','Crnjanski');
/*!40000 ALTER TABLE `autor` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `autor_knjiga`
--

DROP TABLE IF EXISTS `autor_knjiga`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `autor_knjiga` (
  `autor_id` int(11) NOT NULL,
  `knjiga_id` int(11) NOT NULL,
  PRIMARY KEY (`autor_id`,`knjiga_id`),
  KEY `knjiga_id` (`knjiga_id`),
  CONSTRAINT `autor_knjiga_ibfk_1` FOREIGN KEY (`autor_id`) REFERENCES `autor` (`id`),
  CONSTRAINT `autor_knjiga_ibfk_2` FOREIGN KEY (`knjiga_id`) REFERENCES `knjiga` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `autor_knjiga`
--

LOCK TABLES `autor_knjiga` WRITE;
/*!40000 ALTER TABLE `autor_knjiga` DISABLE KEYS */;
INSERT INTO `autor_knjiga` VALUES (1,1),(1,2),(1,6),(2,2),(2,7),(3,3),(3,8),(4,4),(4,9),(5,5),(5,10);
/*!40000 ALTER TABLE `autor_knjiga` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `distributer`
--

DROP TABLE IF EXISTS `distributer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `distributer` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `ime` varchar(255) DEFAULT NULL,
  `broj_telefona` varchar(20) NOT NULL,
  `adresa_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `adresa_id` (`adresa_id`),
  CONSTRAINT `distributer_ibfk_1` FOREIGN KEY (`adresa_id`) REFERENCES `adresa` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `distributer`
--

LOCK TABLES `distributer` WRITE;
/*!40000 ALTER TABLE `distributer` DISABLE KEYS */;
INSERT INTO `distributer` VALUES (1,'Delfi','+38166598675',14),(2,'Mladinska knjiga','+38512345678',2),(3,'Šahinpašić','+38733222333',3),(4,'Penguin Random House','+12125554789',4),(5,'Akademik','+38220223344',5),(7,'MegaKnjiga','+38166321554',7),(8,'BookStore','+38164187567',11);
/*!40000 ALTER TABLE `distributer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `distributer_izdavac`
--

DROP TABLE IF EXISTS `distributer_izdavac`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `distributer_izdavac` (
  `distributer_id` int(11) NOT NULL,
  `izdavac_id` int(11) NOT NULL,
  PRIMARY KEY (`distributer_id`,`izdavac_id`),
  KEY `izdavac_id` (`izdavac_id`),
  CONSTRAINT `distributer_izdavac_ibfk_1` FOREIGN KEY (`distributer_id`) REFERENCES `distributer` (`id`),
  CONSTRAINT `distributer_izdavac_ibfk_2` FOREIGN KEY (`izdavac_id`) REFERENCES `izdavac` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `distributer_izdavac`
--

LOCK TABLES `distributer_izdavac` WRITE;
/*!40000 ALTER TABLE `distributer_izdavac` DISABLE KEYS */;
INSERT INTO `distributer_izdavac` VALUES (1,1),(2,2),(4,3),(5,5);
/*!40000 ALTER TABLE `distributer_izdavac` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `drzava`
--

DROP TABLE IF EXISTS `drzava`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `drzava` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `naziv` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `drzava`
--

LOCK TABLES `drzava` WRITE;
/*!40000 ALTER TABLE `drzava` DISABLE KEYS */;
INSERT INTO `drzava` VALUES (1,'Srbija'),(2,'Hrvatska'),(3,'Bosna i Hercegovina'),(4,'Crna Gora');
/*!40000 ALTER TABLE `drzava` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `izdavac`
--

DROP TABLE IF EXISTS `izdavac`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `izdavac` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `ime` varchar(255) NOT NULL,
  `godina_osnivanja` year(4) DEFAULT NULL,
  `adresa_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `adresa_id` (`adresa_id`),
  CONSTRAINT `izdavac_ibfk_1` FOREIGN KEY (`adresa_id`) REFERENCES `adresa` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `izdavac`
--

LOCK TABLES `izdavac` WRITE;
/*!40000 ALTER TABLE `izdavac` DISABLE KEYS */;
INSERT INTO `izdavac` VALUES (1,'Laguna',1997,12),(2,'Vulkan',1990,2),(3,'Buybook',2005,3),(4,'Prosveta',1945,4),(5,'Oktoih',1998,5),(7,'Dereta',1987,9),(8,'Štampar Makarije',1999,10);
/*!40000 ALTER TABLE `izdavac` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `kategorija`
--

DROP TABLE IF EXISTS `kategorija`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `kategorija` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `naziv` varchar(255) DEFAULT NULL,
  `roditelj_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `roditelj_id` (`roditelj_id`),
  CONSTRAINT `kategorija_ibfk_1` FOREIGN KEY (`roditelj_id`) REFERENCES `kategorija` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=40 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `kategorija`
--

LOCK TABLES `kategorija` WRITE;
/*!40000 ALTER TABLE `kategorija` DISABLE KEYS */;
INSERT INTO `kategorija` VALUES (1,'Književnost',NULL),(2,'Istorija',NULL),(3,'Roman',1),(4,'Poezija',1),(5,'Naučna fantastika',1),(6,'Nauka',NULL),(7,'Umjetnost',NULL),(8,'Tehnologija',NULL),(9,'Filozofija',NULL),(10,'Putovanja',NULL),(11,'Fizika',6),(12,'Biologija',6),(13,'Hemija',6),(14,'Likovna umjetnost',7),(15,'Muzika',7),(16,'Arhitektura',7),(17,'Računarstvo',8),(18,'Robotika',8),(19,'Nanotehnologija',8),(20,'Etika',9),(21,'Logika',9),(22,'Metafizika',9),(23,'Turistički vodiči',10),(24,'Kulturološka istraživanja',10),(25,'Avanturistička putovanja',10),(26,'Antička istorija',2),(27,'Savremena istorija',2),(28,'Kulturna istorija',2),(30,'Lirika',NULL),(32,'Poezija',30);
/*!40000 ALTER TABLE `kategorija` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `knjiga`
--

DROP TABLE IF EXISTS `knjiga`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `knjiga` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `ISBN` varchar(13) DEFAULT NULL,
  `naslov` varchar(255) DEFAULT NULL,
  `broj_stranica` int(11) DEFAULT NULL,
  `cena` decimal(10,2) DEFAULT NULL,
  `distributer_id` int(11) NOT NULL,
  `izdavac_id` int(11) NOT NULL,
  `kategorija_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `distributer_id` (`distributer_id`),
  KEY `izdavac_id` (`izdavac_id`),
  KEY `kategorija_id` (`kategorija_id`),
  CONSTRAINT `knjiga_ibfk_1` FOREIGN KEY (`distributer_id`) REFERENCES `distributer` (`id`),
  CONSTRAINT `knjiga_ibfk_2` FOREIGN KEY (`izdavac_id`) REFERENCES `izdavac` (`id`),
  CONSTRAINT `knjiga_ibfk_3` FOREIGN KEY (`kategorija_id`) REFERENCES `kategorija` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `knjiga`
--

LOCK TABLES `knjiga` WRITE;
/*!40000 ALTER TABLE `knjiga` DISABLE KEYS */;
INSERT INTO `knjiga` VALUES (1,'9788635501234','Na Drini ćuprija',352,12.76,1,1,3),(2,'9788674465432','Tvrđava',456,14.46,2,2,3),(3,'9788661491234','Bašta, pepeo',224,11.06,3,3,3),(4,'9788610012345','Zapisi starog Beogradjanina',412,17.01,4,4,1),(5,'9788645909876','Povratak Filipa Latinovicza',288,11.91,5,5,3),(6,'9788610023456','Hasanaginica',160,7.66,1,1,4),(7,'9788645504321','Derviš i smrt',384,15.31,2,2,3),(8,'9788660032154','Enciklopedija mrtvih',208,10.21,3,3,3),(9,'9788610034567','Kronika o paličnjacima',320,13.61,4,4,2),(10,'9788645912345','Zlatno runo',192,9.36,5,5,5);
/*!40000 ALTER TABLE `knjiga` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `kupac`
--

DROP TABLE IF EXISTS `kupac`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `kupac` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `ime` varchar(100) DEFAULT NULL,
  `prezime` varchar(100) DEFAULT NULL,
  `broj_telefona` varchar(20) NOT NULL,
  `broj_transakcija` int(11) DEFAULT NULL,
  `vrsta_kupca_id` int(11) NOT NULL,
  `adresa_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `vrsta_kupca_id` (`vrsta_kupca_id`),
  KEY `fk_kupac_adresa` (`adresa_id`),
  CONSTRAINT `fk_kupac_adresa` FOREIGN KEY (`adresa_id`) REFERENCES `adresa` (`id`),
  CONSTRAINT `kupac_ibfk_1` FOREIGN KEY (`vrsta_kupca_id`) REFERENCES `vrsta_kupca` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `kupac`
--

LOCK TABLES `kupac` WRITE;
/*!40000 ALTER TABLE `kupac` DISABLE KEYS */;
INSERT INTO `kupac` VALUES (20,'Filip','Cokovski','+38267032475',4,2,13),(21,'Marko','Stefanovic','+38255123123',1,1,18),(22,'Marko','Jankovic','+38266123123',2,1,19),(23,'Janko','Filipovic','+38267455234',1,1,22),(24,'Emir','Muhovic','+38268590797',1,2,23);
/*!40000 ALTER TABLE `kupac` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `prodavac`
--

DROP TABLE IF EXISTS `prodavac`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `prodavac` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `ime` varchar(100) DEFAULT NULL,
  `prezime` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `prodavac`
--

LOCK TABLES `prodavac` WRITE;
/*!40000 ALTER TABLE `prodavac` DISABLE KEYS */;
INSERT INTO `prodavac` VALUES (2,'Marko','Markovic'),(3,'Janko','Jankovic'),(4,'Jovan','Jovanovic'),(5,'Ana','Jovanovic'),(6,'Milena','Stankovic');
/*!40000 ALTER TABLE `prodavac` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `racun`
--

DROP TABLE IF EXISTS `racun`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `racun` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `datum` date DEFAULT NULL,
  `ukupna_cena` decimal(10,2) DEFAULT NULL,
  `vrsta_kupovine_id` int(11) NOT NULL,
  `prodavac_id` int(11) DEFAULT NULL,
  `kupac_id` int(11) NOT NULL,
  `adresaZaDostavu_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `vrsta_kupovine_id` (`vrsta_kupovine_id`),
  KEY `prodavac_id` (`prodavac_id`),
  KEY `kupac_id` (`kupac_id`),
  KEY `adresaZaDostavu_id` (`adresaZaDostavu_id`),
  CONSTRAINT `racun_ibfk_1` FOREIGN KEY (`vrsta_kupovine_id`) REFERENCES `vrsta_kupovine` (`id`),
  CONSTRAINT `racun_ibfk_2` FOREIGN KEY (`prodavac_id`) REFERENCES `prodavac` (`id`),
  CONSTRAINT `racun_ibfk_3` FOREIGN KEY (`kupac_id`) REFERENCES `kupac` (`id`),
  CONSTRAINT `racun_ibfk_4` FOREIGN KEY (`adresaZaDostavu_id`) REFERENCES `adresa` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `racun`
--

LOCK TABLES `racun` WRITE;
/*!40000 ALTER TABLE `racun` DISABLE KEYS */;
INSERT INTO `racun` VALUES (25,'2025-02-13',24.00,1,2,20,NULL),(26,'2025-02-13',64.65,1,3,21,NULL),(27,'2025-02-13',51.32,1,4,22,NULL),(29,'2025-02-13',70.32,1,4,22,NULL),(30,'2025-02-13',1020.00,2,NULL,20,20),(31,'2025-02-13',48.58,2,NULL,20,21),(32,'2025-02-13',9.36,1,3,23,NULL),(33,'2025-02-13',7.65,2,NULL,24,23);
/*!40000 ALTER TABLE `racun` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `stavka`
--

DROP TABLE IF EXISTS `stavka`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `stavka` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `kolicina` int(11) DEFAULT NULL,
  `popust` decimal(5,2) DEFAULT NULL,
  `jedinicna_cena` decimal(10,2) DEFAULT NULL,
  `racun_id` int(11) NOT NULL,
  `knjiga_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `racun_id` (`racun_id`),
  KEY `knjiga_id` (`knjiga_id`),
  CONSTRAINT `stavka_ibfk_1` FOREIGN KEY (`racun_id`) REFERENCES `racun` (`id`),
  CONSTRAINT `stavka_ibfk_2` FOREIGN KEY (`knjiga_id`) REFERENCES `knjiga` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=48 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `stavka`
--

LOCK TABLES `stavka` WRITE;
/*!40000 ALTER TABLE `stavka` DISABLE KEYS */;
INSERT INTO `stavka` VALUES (32,1,40.00,8.68,25,2),(33,2,40.00,7.66,25,1),(34,3,0.00,15.31,26,7),(35,2,0.00,9.36,26,10),(36,2,15.00,7.96,27,10),(37,4,20.00,8.85,27,3),(38,2,12.00,13.47,29,7),(39,3,15.00,14.46,29,4),(40,200,60.00,5.10,30,1),(41,2,55.00,3.45,31,6),(42,4,55.00,5.74,31,1),(43,4,50.00,4.68,31,10),(44,1,0.00,9.36,32,10),(45,1,80.00,2.55,33,1),(46,1,80.00,2.04,33,8),(47,1,80.00,3.06,33,7);
/*!40000 ALTER TABLE `stavka` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `vip_kartica`
--

DROP TABLE IF EXISTS `vip_kartica`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `vip_kartica` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `datum_izdavanja` date DEFAULT NULL,
  `popust` decimal(5,2) DEFAULT NULL,
  `jeAktivna` tinyint(1) DEFAULT NULL,
  `kupac_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `kupac_id` (`kupac_id`),
  CONSTRAINT `vip_kartica_ibfk_1` FOREIGN KEY (`kupac_id`) REFERENCES `kupac` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `vip_kartica`
--

LOCK TABLES `vip_kartica` WRITE;
/*!40000 ALTER TABLE `vip_kartica` DISABLE KEYS */;
INSERT INTO `vip_kartica` VALUES (17,'2025-02-13',40.00,1,20),(18,'2025-02-13',80.00,1,24);
/*!40000 ALTER TABLE `vip_kartica` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `vrsta_kupca`
--

DROP TABLE IF EXISTS `vrsta_kupca`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `vrsta_kupca` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `naziv` varchar(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `vrsta_kupca`
--

LOCK TABLES `vrsta_kupca` WRITE;
/*!40000 ALTER TABLE `vrsta_kupca` DISABLE KEYS */;
INSERT INTO `vrsta_kupca` VALUES (1,'Standardni'),(2,'VIP');
/*!40000 ALTER TABLE `vrsta_kupca` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `vrsta_kupovine`
--

DROP TABLE IF EXISTS `vrsta_kupovine`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `vrsta_kupovine` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `naziv` varchar(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `vrsta_kupovine`
--

LOCK TABLES `vrsta_kupovine` WRITE;
/*!40000 ALTER TABLE `vrsta_kupovine` DISABLE KEYS */;
INSERT INTO `vrsta_kupovine` VALUES (1,'Prodavnica'),(2,'Online');
/*!40000 ALTER TABLE `vrsta_kupovine` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-02-13 23:03:33
