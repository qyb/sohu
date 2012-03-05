/*
SQLyog Enterprise - MySQL GUI v7.02 
MySQL - 5.5.17-log : Database - test_scss
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;

USE `test_scss`;

/*Table structure for table `scss_acl` */

DROP TABLE IF EXISTS `scss_acl`;

CREATE TABLE `scss_acl` (
  `id` bigint(8) NOT NULL AUTO_INCREMENT COMMENT 'acl sub resource id',
  `resource_id` bigint(8) NOT NULL COMMENT 'resource id (bucket or object)',
  `resource_type` varchar(1) COLLATE utf8_bin NOT NULL DEFAULT 'O' COMMENT 'o=object, b=bucket',
  `accessor_id` bigint(8) NOT NULL COMMENT 'user or group which is granted to',
  `accessor_type` varchar(1) COLLATE utf8_bin NOT NULL DEFAULT 'U' COMMENT 'u=user, g=group',
  `permission` varchar(1) COLLATE utf8_bin NOT NULL DEFAULT 'R' COMMENT 'predefined permissions',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `acl_r_a_r` (`resource_id`,`resource_type`,`accessor_id`,`accessor_type`,`permission`),
  KEY `acl_resource_index` (`resource_id`),
  KEY `acl_accessor_index` (`accessor_id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

/*Table structure for table `scss_bucket` */

DROP TABLE IF EXISTS `scss_bucket`;

CREATE TABLE `scss_bucket` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(255) COLLATE utf8_bin NOT NULL COMMENT 'bucketname',
  `owner_id` bigint(20) NOT NULL COMMENT 'bucket的所有者',
  `expriration_enabled` tinyint(1) NOT NULL COMMENT '过期许可',
  `logging_enabled` tinyint(1) DEFAULT NULL COMMENT 'tell if logging enabled (reserved)',
  `meta` text COLLATE utf8_bin COMMENT 'user meta',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否允许被删除',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `modify_time` datetime NOT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`),
  KEY `FK_Bucket_to_User` (`owner_id`),
  CONSTRAINT `FK_Bucket_to_User` FOREIGN KEY (`owner_id`) REFERENCES `scss_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=133 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

/*Table structure for table `scss_bucket_lifecycle` */

DROP TABLE IF EXISTS `scss_bucket_lifecycle`;

CREATE TABLE `scss_bucket_lifecycle` (
  `id` bigint(8) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `bucket_id` bigint(8) NOT NULL COMMENT 'bucketid',
  `expiration_rule` text COLLATE utf8_bin COMMENT 'expiration rules for objects in this bucket',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `FK_Lifecycle_to_Bucket` (`bucket_id`),
  CONSTRAINT `FK_Lifecycle_to_Bucket` FOREIGN KEY (`bucket_id`) REFERENCES `scss_bucket` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

/*Table structure for table `scss_group` */

DROP TABLE IF EXISTS `scss_group`;

CREATE TABLE `scss_group` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(50) COLLATE utf8_bin NOT NULL COMMENT '组名称',
  `user_ids` text COLLATE utf8_bin NOT NULL COMMENT '组成员关联',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

/*Table structure for table `scss_log` */

DROP TABLE IF EXISTS `scss_log`;

CREATE TABLE `scss_log` (
  `id` bigint(8) NOT NULL COMMENT 'id auto-increase',
  `level` int(4) NOT NULL COMMENT 'log level',
  `action` varchar(20) COLLATE utf8_bin NOT NULL COMMENT 'operation action type (get,delete...)',
  `user_id` bigint(8) NOT NULL COMMENT 'the user who make this action',
  `resource_type` varchar(20) COLLATE utf8_bin NOT NULL COMMENT 'bucket,object,acl,user,group...',
  `resource_id` varchar(8) COLLATE utf8_bin NOT NULL COMMENT 'the id of the resource that operation on',
  `server` varchar(100) COLLATE utf8_bin NOT NULL,
  `client_name` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `client_addr` varchar(30) COLLATE utf8_bin DEFAULT NULL,
  `timestamp` datetime NOT NULL,
  `message` text COLLATE utf8_bin NOT NULL COMMENT 'log message',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `log_action_index` (`action`),
  KEY `log_level_index` (`level`),
  KEY `log_resource_index` (`resource_id`,`resource_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

/*Table structure for table `scss_object` */

DROP TABLE IF EXISTS `scss_object`;

CREATE TABLE `scss_object` (
  `id` bigint(8) NOT NULL AUTO_INCREMENT,
  `key` varchar(100) COLLATE utf8_bin NOT NULL,
  `bfs_file` bigint(8) NOT NULL,
  `owner_id` bigint(8) NOT NULL,
  `bucket_id` bigint(8) NOT NULL,
  `meta` text COLLATE utf8_bin,
  `sys_meta` text COLLATE utf8_bin,
  `etag` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `size` bigint(8) DEFAULT NULL,
  `media_type` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `version_enabled` tinyint(1) DEFAULT NULL,
  `version` varchar(1024) COLLATE utf8_bin DEFAULT NULL,
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  `expiration_time` datetime DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `modify_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `key` (`key`,`owner_id`,`bucket_id`),
  KEY `FK_Object_to_User` (`owner_id`),
  KEY `FK_Object_to_Bucket` (`bucket_id`),
  CONSTRAINT `FK_Object_to_Bucket` FOREIGN KEY (`bucket_id`) REFERENCES `scss_bucket` (`id`),
  CONSTRAINT `FK_Object_to_User` FOREIGN KEY (`owner_id`) REFERENCES `scss_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=161 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

/*Table structure for table `scss_user` */

DROP TABLE IF EXISTS `scss_user`;

CREATE TABLE `scss_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `sohu_id` varchar(36) COLLATE utf8_bin NOT NULL COMMENT '搜狐id',
  `access_id` varchar(20) COLLATE utf8_bin NOT NULL COMMENT 'access_id',
  `access_key` varchar(40) COLLATE utf8_bin NOT NULL COMMENT 'access key',
  `status` varchar(1) COLLATE utf8_bin NOT NULL DEFAULT 'b' COMMENT '状态：a=active，b=disable,c=cancel,d=delete',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`),
  UNIQUE KEY `access_key` (`access_key`),
  UNIQUE KEY `sohu_id` (`sohu_id`)
) ENGINE=InnoDB AUTO_INCREMENT=178 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

/*Table structure for table `scss_user_profile` */

DROP TABLE IF EXISTS `scss_user_profile`;

CREATE TABLE `scss_user_profile` (
  `id` bigint(8) NOT NULL,
  `nick_name` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `real_name` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `phone_num` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `country` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `state` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `city` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `address` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `avartar` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT 'user picture',
  `gendar` varchar(1) COLLATE utf8_bin DEFAULT NULL,
  `created_time` datetime DEFAULT NULL COMMENT 'datetime of user is created',
  `last_logon` datetime DEFAULT NULL COMMENT 'datetime of the user last logon',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;