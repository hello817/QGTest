CREATE DATABASE IF NOT EXISTS repair_system
CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
-- 创建数据库和设置数据库字符集要合并到一条语句中
USE repair_system;

-- 创建用户表 应该有id（标识），账号，密码，角色（管理员/学生），宿舍楼栋，房间号
CREATE TABLE user
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    account     VARCHAR(20) NOT NULL UNIQUE COMMENT '账号',
    password    VARCHAR(50) NOT NULL COMMENT '密码', -- 密码可以重复
    role        VARCHAR(20) NOT NULL COMMENT '角色',
    building    VARCHAR(20) COMMENT '宿舍楼栋',
    room        VARCHAR(20) COMMENT '房间号',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX       idx_account (account), -- 添加索引，便于直接，快速的查询
    INDEX       idx_role (role)
)COMMENT '用户表';

-- ---------用户表完工
-- ---------创建保修单表 应有标识（id），单号（唯一），学生id（外键），报修内容（设备类型），状态（待处理/处理中/已完成），或许可以添加优先级（？），创建时间，更新时间

CREATE TABLE repair_order(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id    VARCHAR(20) NOT NULL UNIQUE COMMENT '单号',
    student_id  BIGINT NOT NULL COMMENT '学生的用户id',
    fix_type    VARCHAR(50) NOT NULL COMMENT '报修的类型',
    description TEXT COMMENT '问题描述'
)COMMENT '保修单表';