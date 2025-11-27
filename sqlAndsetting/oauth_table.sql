-- OAuth2 第三方账号绑定表
-- 请在 hoj 数据库中执行此 SQL

CREATE TABLE IF NOT EXISTS `user_third_auth` (
    `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `uid` varchar(32) NOT NULL COMMENT '用户ID',
    `platform` varchar(50) NOT NULL COMMENT '第三方平台类型：github, gitee, gitlab, custom等',
    `open_id` varchar(255) NOT NULL COMMENT '第三方平台的用户唯一标识',
    `username` varchar(255) DEFAULT NULL COMMENT '第三方平台的用户名',
    `nickname` varchar(255) DEFAULT NULL COMMENT '第三方平台的昵称',
    `avatar` varchar(1024) DEFAULT NULL COMMENT '第三方平台的头像',
    `email` varchar(255) DEFAULT NULL COMMENT '第三方平台的邮箱',
    `raw_user_info` text COMMENT '第三方平台返回的原始用户信息JSON',
    `access_token` varchar(1024) DEFAULT NULL COMMENT 'access_token',
    `refresh_token` varchar(1024) DEFAULT NULL COMMENT 'refresh_token',
    `expire_in` bigint(20) DEFAULT NULL COMMENT 'token过期时间(秒)',
    `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_platform_open_id` (`platform`, `open_id`),
    KEY `idx_uid` (`uid`),
    KEY `idx_platform_uid` (`platform`, `uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='第三方账号绑定表';
