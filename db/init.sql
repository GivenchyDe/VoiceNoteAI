-- 创建数据库
CREATE DATABASE IF NOT EXISTS voice_note_system DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE voice_note_system;

-- 1. 管理员表
CREATE TABLE IF NOT EXISTS admin (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键，自增',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '管理员账号',
    password VARCHAR(100) NOT NULL COMMENT '加密存储',
    role VARCHAR(20) NOT NULL DEFAULT '普通管理员' COMMENT '角色（超级管理员/普通管理员）',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '账号状态（0-禁用，1-正常）',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员表';

-- 2. 用户表
CREATE TABLE IF NOT EXISTS user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键，自增',
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    phone VARCHAR(20) UNIQUE COMMENT '手机号，唯一',
    email VARCHAR(50) UNIQUE COMMENT '邮箱，唯一',
    password VARCHAR(100) NOT NULL COMMENT '加密存储',
    avatar VARCHAR(255) COMMENT '头像地址',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '账号状态（0-禁用，1-正常）',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_phone (phone),
    INDEX idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 3. 公告表
CREATE TABLE IF NOT EXISTS notice (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键，自增',
    title VARCHAR(100) NOT NULL COMMENT '公告标题',
    content TEXT NOT NULL COMMENT '公告内容',
    admin_id BIGINT NOT NULL COMMENT '发布管理员ID',
    is_top TINYINT NOT NULL DEFAULT 0 COMMENT '是否置顶（0-否，1-是）',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (admin_id) REFERENCES admin(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公告表';

-- 4. 音频文件表
CREATE TABLE IF NOT EXISTS audio_file (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键，自增',
    user_id BIGINT NOT NULL COMMENT '关联用户ID',
    file_name VARCHAR(100) NOT NULL COMMENT '音频文件名',
    file_path VARCHAR(255) NOT NULL COMMENT '音频文件存储路径',
    file_size BIGINT NOT NULL COMMENT '文件大小（字节）',
    file_format VARCHAR(10) NOT NULL COMMENT '文件格式（mp3/wav等）',
    duration INT NOT NULL COMMENT '音频时长（秒）',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (user_id) REFERENCES user(id),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='音频文件表';

-- 5. 识别日志表
CREATE TABLE IF NOT EXISTS recognition_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键，自增',
    user_id BIGINT NOT NULL COMMENT '关联用户ID',
    audio_id BIGINT NOT NULL COMMENT '关联音频文件ID',
    result TEXT COMMENT '识别结果文字',
    duration INT NOT NULL COMMENT '识别耗时（毫秒）',
    accuracy DECIMAL(5,2) COMMENT '识别准确率（%）',
    engine VARCHAR(20) NOT NULL DEFAULT '讯飞' COMMENT '识别引擎（讯飞/百度）',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '识别状态（0-失败，1-成功）',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (audio_id) REFERENCES audio_file(id),
    INDEX idx_user_id (user_id),
    INDEX idx_audio_id (audio_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='识别日志表';

-- 6. 笔记表
CREATE TABLE IF NOT EXISTS note (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键，自增',
    user_id BIGINT NOT NULL COMMENT '关联用户ID',
    title VARCHAR(100) NOT NULL COMMENT '笔记标题',
    content TEXT NOT NULL COMMENT '笔记内容（识别后的文字）',
    category VARCHAR(30) NOT NULL DEFAULT '其他' COMMENT '笔记分类',
    tags VARCHAR(255) COMMENT '标签，逗号分隔',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态（0-归档，1-正常）',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES user(id),
    INDEX idx_user_id (user_id),
    INDEX idx_category (category)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='笔记表';

-- 7. 反馈表
CREATE TABLE IF NOT EXISTS feedback (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键，自增',
    user_id BIGINT NOT NULL COMMENT '关联用户ID',
    content TEXT NOT NULL COMMENT '反馈内容',
    attachment VARCHAR(255) COMMENT '附件地址（截图/音频）',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '处理状态（0-未处理，1-处理中，2-已处理）',
    reply TEXT COMMENT '管理员回复',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES user(id),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='反馈表';