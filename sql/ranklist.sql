/*
Navicat MySQL Data Transfer

Source Server         : 192.168.2.44_3317
Source Server Version : 50505
Source Host           : 192.168.2.44:3317
Source Database       : rank_list_11076

Target Server Type    : MYSQL
Target Server Version : 50505
File Encoding         : 65001

Date: 2017-07-07 20:18:47
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `leader_board_config`
-- ----------------------------
DROP TABLE IF EXISTS `leader_board_config`;
CREATE TABLE `leader_board_config` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `game_id` int(10) NOT NULL,
  `leaderboardid` int(10) NOT NULL,
  `expire_type` enum('0','1','2','3') NOT NULL DEFAULT '0' COMMENT '过期类型，0代表永久，1代表日，2代表周，3代表月',
  `limit_rows` int(10) NOT NULL COMMENT '最大排名数目',
  `score_partion` int(10) NOT NULL,
  `sort_rule` enum('desc','asc') NOT NULL DEFAULT 'desc' COMMENT '排序方式',
  `redis_config` varchar(255) DEFAULT NULL,
  `mysql_config` varchar(255) DEFAULT NULL,
  `create_time` int(11) NOT NULL,
  `extend` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `index_conf` (`game_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of leader_board_config
-- ----------------------------

-- ----------------------------
-- Table structure for `user_info_map`
-- ----------------------------
DROP TABLE IF EXISTS `user_info_map`;
CREATE TABLE `user_info_map` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `uid` int(10) NOT NULL,
  `leaderboardid` int(10) NOT NULL,
  `score` int(10) NOT NULL,
  `create_time` int(10) NOT NULL,
  `expire_time` int(10) DEFAULT NULL,
  `extend` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_user` (`uid`)
) ENGINE=InnoDB AUTO_INCREMENT=133 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user_info_map
-- ----------------------------
