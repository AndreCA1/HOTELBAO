CREATE TABLE `password_recover` (`id` BIGINT NOT NULL AUTO_INCREMENT,
                                 `token` varchar(100) NOT NULL,
                                 `email` varchar(150) NOT NULL,
                                 `expiration` datetime NOT NULL,
                                 PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;