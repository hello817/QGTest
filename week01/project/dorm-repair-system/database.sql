CREATE DATABASE IF NOT EXISTS repair_system
CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
-- 创建数据库和设置数据库字符集要合并到一条语句中
USE repair_system;

-- 创建用户表 应该有id（标识），账号，密码，角色（管理员/学生），宿舍楼栋，房间号
CREATE TABLE user
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    account     VARCHAR(20) NOT NULL UNIQUE COMMENT '账号',
    password    VARCHAR(225) NOT NULL COMMENT '密码', -- 密码可以重复
    role        VARCHAR(20) NOT NULL COMMENT '角色',
    building    VARCHAR(20) COMMENT '宿舍楼栋',
    room        VARCHAR(20) COMMENT '房间号',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- 时间戳，可以在插入时自动维护
    INDEX       idx_account (account), -- 添加索引，便于直接，快速的查询
    INDEX       idx_role (role)
)COMMENT '用户表';

-- ---------用户表完工
-- ---------创建保修单表 应有标识（id），单号（唯一），学生id（外键），报修内容（设备类型），状态（待处理/处理中/已完成），或许可以添加优先级（？），创建时间，更新时间

CREATE TABLE repair_order(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_no    VARCHAR(20) NOT NULL UNIQUE COMMENT '单号',
    student_id  BIGINT NOT NULL COMMENT '学生的用户id',
    fix_type    VARCHAR(50) NOT NULL COMMENT '报修的类型',
    description TEXT COMMENT '问题描述',
    status      VARCHAR(20) DEFAULT 'pending' COMMENT '状态',-- 默认是在等待
    priority     VARCHAR(20) DEFAULT 'medium' COMMENT '优先级',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT NOW() ON UPDATE NOW(),
    -- 添加外键约束,order表的student id 必须和 user表有对应
    FOREIGN KEY (student_id) REFERENCES user(id),
    INDEX       id_status(status)
)COMMENT '保修单表';
-- 表单完工,添加测试账号
INSERT INTO user (account,password,role) VALUES ('0025000001','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','admin');
-- 我说哈希加密密码战未来
INSERT INTO user (account, password, role, building, room) VALUES ('2024001', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'student', 'A栋', '101');