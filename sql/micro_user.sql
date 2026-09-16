-- ----------------------------
-- micro-cloud-archetype 系统数据库初始化脚本
-- 数据库：micro_user（权限核心服务专属库，一服务一库）
-- ----------------------------
CREATE DATABASE IF NOT EXISTS `micro_user` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `micro_user`;

-- ----------------------------
-- 1. 用户表
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `user_id`     BIGINT       NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `dept_id`     BIGINT       DEFAULT NULL COMMENT '部门ID',
  `user_name`   VARCHAR(30)  NOT NULL COMMENT '用户账号',
  `nick_name`   VARCHAR(30)  NOT NULL COMMENT '用户昵称',
  `email`       VARCHAR(50)  DEFAULT '' COMMENT '用户邮箱',
  `phonenumber` VARCHAR(11)  DEFAULT '' COMMENT '手机号码',
  `sex`         CHAR(1)      DEFAULT '0' COMMENT '用户性别（0男 1女 2未知）',
  `avatar`      VARCHAR(100) DEFAULT '' COMMENT '头像地址',
  `password`    VARCHAR(100) DEFAULT '' COMMENT '密码（BCrypt）',
  `status`      CHAR(1)      DEFAULT '0' COMMENT '帐号状态（0正常 1停用）',
  `login_ip`    VARCHAR(128) DEFAULT '' COMMENT '最后登录IP',
  `login_date`  DATETIME     DEFAULT NULL COMMENT '最后登录时间',
  `create_by`   VARCHAR(64)  DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME     DEFAULT NULL COMMENT '创建时间',
  `update_by`   VARCHAR(64)  DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME     DEFAULT NULL COMMENT '更新时间',
  `remark`      VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `del_flag`    CHAR(1)      DEFAULT '0' COMMENT '删除标志（0存在 2删除）',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `uk_user_name` (`user_name`)
) ENGINE=InnoDB AUTO_INCREMENT=100 COMMENT='用户信息表';

-- ----------------------------
-- 2. 角色表
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `role_id`             BIGINT      NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name`           VARCHAR(30) NOT NULL COMMENT '角色名称',
  `role_key`            VARCHAR(100) NOT NULL COMMENT '角色权限字符串',
  `role_sort`           INT         NOT NULL COMMENT '显示顺序',
  `data_scope`          CHAR(1)     DEFAULT '1' COMMENT '数据范围（1全部 2自定义 3本部门 4本部门及以下 5仅本人）',
  `status`              CHAR(1)     NOT NULL COMMENT '角色状态（0正常 1停用）',
  `create_by`           VARCHAR(64) DEFAULT '' COMMENT '创建者',
  `create_time`         DATETIME    DEFAULT NULL COMMENT '创建时间',
  `update_by`           VARCHAR(64) DEFAULT '' COMMENT '更新者',
  `update_time`         DATETIME    DEFAULT NULL COMMENT '更新时间',
  `remark`              VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `del_flag`            CHAR(1)     DEFAULT '0' COMMENT '删除标志（0存在 2删除）',
  PRIMARY KEY (`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=100 COMMENT='角色信息表';

-- ----------------------------
-- 3. 用户角色关联表
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `role_id` BIGINT NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`user_id`, `role_id`)
) ENGINE=InnoDB COMMENT='用户和角色关联表';

-- ----------------------------
-- 4. 菜单权限表
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu` (
  `menu_id`     BIGINT      NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
  `menu_name`   VARCHAR(50) NOT NULL COMMENT '菜单名称',
  `parent_id`   BIGINT      DEFAULT 0 COMMENT '父菜单ID',
  `order_num`   INT         DEFAULT 0 COMMENT '显示顺序',
  `path`        VARCHAR(200) DEFAULT '' COMMENT '路由地址',
  `component`   VARCHAR(255) DEFAULT NULL COMMENT '组件路径',
  `query`       VARCHAR(255) DEFAULT NULL COMMENT '路由参数',
  `is_frame`    INT         DEFAULT 1 COMMENT '是否为外链（0是 1否）',
  `is_cache`    INT         DEFAULT 0 COMMENT '是否缓存（0缓存 1不缓存）',
  `menu_type`   CHAR(1)     DEFAULT '' COMMENT '菜单类型（M目录 C菜单 F按钮）',
  `visible`     CHAR(1)     DEFAULT '0' COMMENT '菜单状态（0显示 1隐藏）',
  `status`      CHAR(1)     DEFAULT '0' COMMENT '菜单状态（0正常 1停用）',
  `perms`       VARCHAR(100) DEFAULT NULL COMMENT '权限标识',
  `icon`        VARCHAR(100) DEFAULT '#' COMMENT '菜单图标',
  `create_by`   VARCHAR(64) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME    DEFAULT NULL COMMENT '创建时间',
  `update_by`   VARCHAR(64) DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME    DEFAULT NULL COMMENT '更新时间',
  `remark`      VARCHAR(500) DEFAULT '' COMMENT '备注',
  PRIMARY KEY (`menu_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1000 COMMENT='菜单权限表';

-- ----------------------------
-- 5. 角色菜单关联表
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu` (
  `role_id` BIGINT NOT NULL COMMENT '角色ID',
  `menu_id` BIGINT NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (`role_id`, `menu_id`)
) ENGINE=InnoDB COMMENT='角色和菜单关联表';

-- ----------------------------
-- 6. 角色部门关联表（自定义部门数据权限依赖）
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_dept`;
CREATE TABLE `sys_role_dept` (
  `role_id` BIGINT NOT NULL COMMENT '角色ID',
  `dept_id` BIGINT NOT NULL COMMENT '部门ID',
  PRIMARY KEY (`role_id`, `dept_id`)
) ENGINE=InnoDB COMMENT='角色和部门关联表';

-- ----------------------------
-- 7. 部门表
-- ----------------------------
DROP TABLE IF EXISTS `sys_dept`;
CREATE TABLE `sys_dept` (
  `dept_id`     BIGINT      NOT NULL AUTO_INCREMENT COMMENT '部门ID',
  `parent_id`   BIGINT      DEFAULT 0 COMMENT '父部门ID',
  `ancestors`   VARCHAR(50) DEFAULT '' COMMENT '祖级列表',
  `dept_name`   VARCHAR(30) DEFAULT '' COMMENT '部门名称',
  `order_num`   INT         DEFAULT 0 COMMENT '显示顺序',
  `leader`      VARCHAR(20) DEFAULT NULL COMMENT '负责人',
  `phone`       VARCHAR(11) DEFAULT NULL COMMENT '联系电话',
  `email`       VARCHAR(50) DEFAULT NULL COMMENT '邮箱',
  `status`      CHAR(1)     DEFAULT '0' COMMENT '部门状态（0正常 1停用）',
  `create_by`   VARCHAR(64) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME    DEFAULT NULL COMMENT '创建时间',
  `update_by`   VARCHAR(64) DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME    DEFAULT NULL COMMENT '更新时间',
  `del_flag`    CHAR(1)     DEFAULT '0' COMMENT '删除标志（0存在 2删除）',
  PRIMARY KEY (`dept_id`)
) ENGINE=InnoDB AUTO_INCREMENT=200 COMMENT='部门表';

-- ----------------------------
-- 8. 岗位表
-- ----------------------------
DROP TABLE IF EXISTS `sys_post`;
CREATE TABLE `sys_post` (
  `post_id`     BIGINT      NOT NULL AUTO_INCREMENT COMMENT '岗位ID',
  `post_code`   VARCHAR(64) NOT NULL COMMENT '岗位编码',
  `post_name`   VARCHAR(50) NOT NULL COMMENT '岗位名称',
  `post_sort`   INT         NOT NULL COMMENT '显示顺序',
  `status`      CHAR(1)     NOT NULL COMMENT '状态（0正常 1停用）',
  `create_by`   VARCHAR(64) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME    DEFAULT NULL COMMENT '创建时间',
  `update_by`   VARCHAR(64) DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME    DEFAULT NULL COMMENT '更新时间',
  `remark`      VARCHAR(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`post_id`)
) ENGINE=InnoDB COMMENT='岗位信息表';

-- ----------------------------
-- 9. 用户岗位关联表
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_post`;
CREATE TABLE `sys_user_post` (
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `post_id` BIGINT NOT NULL COMMENT '岗位ID',
  PRIMARY KEY (`user_id`, `post_id`)
) ENGINE=InnoDB COMMENT='用户与岗位关联表';

-- ----------------------------
-- 10. 字典类型表
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_type`;
CREATE TABLE `sys_dict_type` (
  `dict_id`     BIGINT      NOT NULL AUTO_INCREMENT COMMENT '字典主键',
  `dict_name`   VARCHAR(100) DEFAULT '' COMMENT '字典名称',
  `dict_type`   VARCHAR(100) DEFAULT '' COMMENT '字典类型',
  `status`      CHAR(1)     DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_by`   VARCHAR(64) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME    DEFAULT NULL COMMENT '创建时间',
  `update_by`   VARCHAR(64) DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME    DEFAULT NULL COMMENT '更新时间',
  `remark`      VARCHAR(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`dict_id`),
  UNIQUE KEY `uk_dict_type` (`dict_type`)
) ENGINE=InnoDB AUTO_INCREMENT=100 COMMENT='字典类型表';

-- ----------------------------
-- 11. 字典数据表
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_data`;
CREATE TABLE `sys_dict_data` (
  `dict_code`   BIGINT      NOT NULL AUTO_INCREMENT COMMENT '字典编码',
  `dict_sort`   INT         DEFAULT 0 COMMENT '字典排序',
  `dict_label`  VARCHAR(100) DEFAULT '' COMMENT '字典标签',
  `dict_value`  VARCHAR(100) DEFAULT '' COMMENT '字典键值',
  `dict_type`   VARCHAR(100) DEFAULT '' COMMENT '字典类型',
  `css_class`   VARCHAR(100) DEFAULT NULL COMMENT '样式属性',
  `list_class`  VARCHAR(100) DEFAULT NULL COMMENT '表格回显样式',
  `is_default`  CHAR(1)     DEFAULT 'N' COMMENT '是否默认（Y是 N否）',
  `status`      CHAR(1)     DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_by`   VARCHAR(64) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME    DEFAULT NULL COMMENT '创建时间',
  `update_by`   VARCHAR(64) DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME    DEFAULT NULL COMMENT '更新时间',
  `remark`      VARCHAR(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`dict_code`)
) ENGINE=InnoDB AUTO_INCREMENT=100 COMMENT='字典数据表';

-- ----------------------------
-- 12. 参数配置表
-- ----------------------------
DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config` (
  `config_id`    INT          NOT NULL AUTO_INCREMENT COMMENT '参数主键',
  `config_name`  VARCHAR(100) DEFAULT '' COMMENT '参数名称',
  `config_key`   VARCHAR(100) DEFAULT '' COMMENT '参数键名',
  `config_value` VARCHAR(500) DEFAULT '' COMMENT '参数键值',
  `config_type`  CHAR(1)      DEFAULT 'N' COMMENT '系统内置（Y是 N否）',
  `create_by`    VARCHAR(64)  DEFAULT '' COMMENT '创建者',
  `create_time`  DATETIME     DEFAULT NULL COMMENT '创建时间',
  `update_by`    VARCHAR(64)  DEFAULT '' COMMENT '更新者',
  `update_time`  DATETIME     DEFAULT NULL COMMENT '更新时间',
  `remark`       VARCHAR(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`config_id`),
  UNIQUE KEY `uk_config_key` (`config_key`)
) ENGINE=InnoDB AUTO_INCREMENT=100 COMMENT='参数配置表';

-- ----------------------------
-- 13. 通知公告表
-- ----------------------------
DROP TABLE IF EXISTS `sys_notice`;
CREATE TABLE `sys_notice` (
  `notice_id`      INT          NOT NULL AUTO_INCREMENT COMMENT '公告ID',
  `notice_title`   VARCHAR(50)  NOT NULL COMMENT '公告标题',
  `notice_type`    CHAR(1)      NOT NULL COMMENT '公告类型（1通知 2公告）',
  `notice_content` LONGTEXT     DEFAULT NULL COMMENT '公告内容',
  `status`         CHAR(1)      DEFAULT '0' COMMENT '公告状态（0正常 1关闭）',
  `create_by`      VARCHAR(64)  DEFAULT '' COMMENT '创建者',
  `create_time`    DATETIME     DEFAULT NULL COMMENT '创建时间',
  `update_by`      VARCHAR(64)  DEFAULT '' COMMENT '更新者',
  `update_time`    DATETIME     DEFAULT NULL COMMENT '更新时间',
  `remark`         VARCHAR(255) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`notice_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 COMMENT='通知公告表';

-- ----------------------------
-- 14. 操作日志表
-- ----------------------------
DROP TABLE IF EXISTS `sys_oper_log`;
CREATE TABLE `sys_oper_log` (
  `oper_id`        BIGINT       NOT NULL AUTO_INCREMENT COMMENT '日志主键',
  `title`          VARCHAR(50)  DEFAULT '' COMMENT '模块标题',
  `business_type`  INT          DEFAULT 0 COMMENT '业务类型（0其它 1新增 2修改 3删除）',
  `method`         VARCHAR(100) DEFAULT '' COMMENT '方法名称',
  `request_method` VARCHAR(10)  DEFAULT '' COMMENT '请求方式',
  `operator_type`  INT          DEFAULT 0 COMMENT '操作类别（0其它 1后台 2手机端）',
  `oper_name`      VARCHAR(50)  DEFAULT '' COMMENT '操作人员',
  `dept_name`      VARCHAR(50)  DEFAULT '' COMMENT '部门名称',
  `oper_url`       VARCHAR(255) DEFAULT '' COMMENT '请求URL',
  `oper_ip`        VARCHAR(128) DEFAULT '' COMMENT '主机地址',
  `oper_param`     VARCHAR(2000) DEFAULT '' COMMENT '请求参数',
  `json_result`    VARCHAR(2000) DEFAULT '' COMMENT '返回参数',
  `status`         INT          DEFAULT 0 COMMENT '操作状态（0正常 1异常）',
  `error_msg`      VARCHAR(2000) DEFAULT '' COMMENT '错误消息',
  `oper_time`      DATETIME     DEFAULT NULL COMMENT '操作时间',
  `trace_id`       VARCHAR(64)  DEFAULT '' COMMENT '链路追踪ID',
  PRIMARY KEY (`oper_id`),
  KEY `idx_oper_time` (`oper_time`)
) ENGINE=InnoDB AUTO_INCREMENT=100 COMMENT='操作日志记录';

-- ----------------------------
-- 15. 登录日志表
-- ----------------------------
DROP TABLE IF EXISTS `sys_login_log`;
CREATE TABLE `sys_login_log` (
  `info_id`    BIGINT      NOT NULL AUTO_INCREMENT COMMENT '访问ID',
  `user_name`  VARCHAR(50) DEFAULT '' COMMENT '用户账号',
  `ipaddr`     VARCHAR(128) DEFAULT '' COMMENT '登录IP地址',
  `status`     CHAR(1)     DEFAULT '0' COMMENT '登录状态（0成功 1失败）',
  `msg`        VARCHAR(255) DEFAULT '' COMMENT '提示信息',
  `login_time` DATETIME    DEFAULT NULL COMMENT '访问时间',
  `trace_id`   VARCHAR(64) DEFAULT '' COMMENT '链路追踪ID',
  PRIMARY KEY (`info_id`),
  KEY `idx_login_time` (`login_time`)
) ENGINE=InnoDB AUTO_INCREMENT=100 COMMENT='系统访问记录';

-- ----------------------------
-- 初始化数据
-- ----------------------------
-- 默认部门
INSERT INTO `sys_dept` VALUES (100, 0, '0', '微云科技', 0, 'admin', '15888888888', 'admin@micro.cloud', '0', 'admin', NOW(), '', NULL, '0');
INSERT INTO `sys_dept` VALUES (101, 100, '0,100', '研发部门', 1, '', '', '', '0', 'admin', NOW(), '', NULL, '0');
INSERT INTO `sys_dept` VALUES (102, 100, '0,100', '运维部门', 2, '', '', '', '0', 'admin', NOW(), '', NULL, '0');

-- 默认岗位
INSERT INTO `sys_post` VALUES (1, 'ceo', '董事长', 1, '0', 'admin', NOW(), '', NULL, '');
INSERT INTO `sys_post` VALUES (2, 'se', '项目经理', 2, '0', 'admin', NOW(), '', NULL, '');
INSERT INTO `sys_post` VALUES (3, 'dev', '开发工程师', 3, '0', 'admin', NOW(), '', NULL, '');

-- 默认角色
INSERT INTO `sys_role` VALUES (1, '超级管理员', 'admin', 1, '1', '0', 'admin', NOW(), '', NULL, '超级管理员', '0');
INSERT INTO `sys_role` VALUES (2, '普通角色', 'common', 2, '2', '0', 'admin', NOW(), '', NULL, '普通角色', '0');

-- 默认菜单
INSERT INTO `sys_menu` VALUES (1, '系统管理', 0, 1, 'system', NULL, '', 1, 0, 'M', '0', '0', '', 'system', 'admin', NOW(), '', NULL, '系统管理目录');
INSERT INTO `sys_menu` VALUES (100, '用户管理', 1, 1, 'user', 'system/user/index', '', 1, 0, 'C', '0', '0', 'system:user:list', 'user', 'admin', NOW(), '', NULL, '用户管理菜单');
INSERT INTO `sys_menu` VALUES (101, '角色管理', 1, 2, 'role', 'system/role/index', '', 1, 0, 'C', '0', '0', 'system:role:list', 'peoples', 'admin', NOW(), '', NULL, '角色管理菜单');
INSERT INTO `sys_menu` VALUES (102, '菜单管理', 1, 3, 'menu', 'system/menu/index', '', 1, 0, 'C', '0', '0', 'system:menu:list', 'tree-table', 'admin', NOW(), '', NULL, '菜单管理菜单');
INSERT INTO `sys_menu` VALUES (103, '部门管理', 1, 4, 'dept', 'system/dept/index', '', 1, 0, 'C', '0', '0', 'system:dept:list', 'tree', 'admin', NOW(), '', NULL, '部门管理菜单');
INSERT INTO `sys_menu` VALUES (104, '岗位管理', 1, 5, 'post', 'system/post/index', '', 1, 0, 'C', '0', '0', 'system:post:list', 'post', 'admin', NOW(), '', NULL, '岗位管理菜单');
INSERT INTO `sys_menu` VALUES (105, '字典管理', 1, 6, 'dict', 'system/dict/index', '', 1, 0, 'C', '0', '0', 'system:dict:list', 'dict', 'admin', NOW(), '', NULL, '字典管理菜单');
INSERT INTO `sys_menu` VALUES (1000, '用户查询', 100, 1, '', '', '', 1, 0, 'F', '0', '0', 'system:user:query', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO `sys_menu` VALUES (1001, '用户新增', 100, 2, '', '', '', 1, 0, 'F', '0', '0', 'system:user:add', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO `sys_menu` VALUES (1002, '用户修改', 100, 3, '', '', '', 1, 0, 'F', '0', '0', 'system:user:edit', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO `sys_menu` VALUES (1003, '用户删除', 100, 4, '', '', '', 1, 0, 'F', '0', '0', 'system:user:remove', '#', 'admin', NOW(), '', NULL, '');

-- 默认用户（密码 admin123，BCrypt 加密）
INSERT INTO `sys_user` VALUES (1, 101, 'admin', '超级管理员', 'admin@micro.cloud', '15888888888', '0', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '127.0.0.1', NOW(), 'admin', NOW(), '', NULL, '管理员', '0');
INSERT INTO `sys_user` VALUES (2, 102, 'micro', '普通用户', 'micro@micro.cloud', '15666666666', '0', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '127.0.0.1', NOW(), 'admin', NOW(), '', NULL, '测试员', '0');

-- 用户角色关联
INSERT INTO `sys_user_role` VALUES (1, 1);
INSERT INTO `sys_user_role` VALUES (2, 2);

-- 角色菜单关联（管理员全部菜单，普通角色仅用户查询）
INSERT INTO `sys_role_menu` VALUES (1, 1), (1, 100), (1, 101), (1, 102), (1, 103), (1, 104), (1, 105), (1, 1000), (1, 1001), (1, 1002), (1, 1003);
INSERT INTO `sys_role_menu` VALUES (2, 1), (2, 100), (2, 1000);

-- 角色部门关联（普通角色自定义数据权限：仅可访问研发部门）
INSERT INTO `sys_role_dept` VALUES (2, 101);

-- 默认字典
INSERT INTO `sys_dict_type` VALUES (1, '用户性别', 'sys_user_sex', '0', 'admin', NOW(), '', NULL, '用户性别列表');
INSERT INTO `sys_dict_type` VALUES (2, '系统开关', 'sys_normal_disable', '0', 'admin', NOW(), '', NULL, '系统开关列表');
INSERT INTO `sys_dict_data` VALUES (1, 1, '男', '0', 'sys_user_sex', '', '', 'Y', '0', 'admin', NOW(), '', NULL, '性别男');
INSERT INTO `sys_dict_data` VALUES (2, 2, '女', '1', 'sys_user_sex', '', '', 'N', '0', 'admin', NOW(), '', NULL, '性别女');
INSERT INTO `sys_dict_data` VALUES (3, 1, '正常', '0', 'sys_normal_disable', '', '', 'Y', '0', 'admin', NOW(), '', NULL, '正常状态');
INSERT INTO `sys_dict_data` VALUES (4, 2, '停用', '1', 'sys_normal_disable', '', '', 'N', '0', 'admin', NOW(), '', NULL, '停用状态');

-- 默认参数
INSERT INTO `sys_config` VALUES (1, '用户初始密码', 'sys.user.initPassword', '123456', 'Y', 'admin', NOW(), '', NULL, '初始化密码 123456');
INSERT INTO `sys_config` VALUES (2, '账号自助-验证码开关', 'sys.account.captchaEnabled', 'true', 'Y', 'admin', NOW(), '', NULL, '是否开启验证码功能（true开启，false关闭）');
