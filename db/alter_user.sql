-- 用户表新增字段（按顺序执行）
USE voice_note_system;

ALTER TABLE user
    ADD COLUMN IF NOT EXISTS avatar VARCHAR(255) COMMENT '头像地址',
    ADD COLUMN IF NOT EXISTS gender TINYINT DEFAULT 0 COMMENT '性别（0-未知，1-男，2-女）',
    ADD COLUMN IF NOT EXISTS birthday DATE COMMENT '出生年月日',
    ADD COLUMN IF NOT EXISTS bio VARCHAR(500) COMMENT '个人简介';
