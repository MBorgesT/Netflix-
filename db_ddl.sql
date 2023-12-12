CREATE DATABASE `main` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci */;


CREATE TABLE `chunk_hash` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `media_id` int(11) NOT NULL,
  `file_name` varchar(100) NOT NULL,
  `hash` blob NOT NULL,
  PRIMARY KEY (`id`)
);


CREATE TABLE `media_metadata` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(100) NOT NULL,
  `description` varchar(1000) DEFAULT NULL,
  `folder_name` varchar(30) NOT NULL,
  `upload_status` varchar(20) NOT NULL,
  PRIMARY KEY (`id`)
);


CREATE TABLE `mesh_stream_availability` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `media_id` int(11) NOT NULL,
  `client_ip` varchar(100) NOT NULL,
  PRIMARY KEY (`id`)
);


CREATE TABLE `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(100) NOT NULL,
  `password` blob NOT NULL,
  `role` varchar(20) NOT NULL,
  PRIMARY KEY (`id`)
);
