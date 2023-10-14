/* --------------- Creating table for Databuck Reporting UI - User token management -------------------------------- */
DROP TABLE IF EXISTS `user_token`;

CREATE TABLE IF NOT EXISTS `user_token` (
  `row_id` int(11) NOT NULL AUTO_INCREMENT,
  `idUser` bigint(20) DEFAULT NULL,
  `userName` varchar(255) DEFAULT NULL,
  `email` VARCHAR(255) NOT NULL,
  `userRole` bigint(20) DEFAULT NULL,
  `userRoleName` varchar(255) DEFAULT NULL,
  `loginTime` datetime NOT NULL,
  `expiryTime` datetime NOT NULL,
  `token` varchar(255) NOT NULL UNIQUE,
  `status` varchar(10) DEFAULT NULL,
  `activeDirectoryUser` VARCHAR(10) NOT NULL,
  PRIMARY KEY (`row_id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=latin1;