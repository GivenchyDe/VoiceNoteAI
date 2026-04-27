USE voice_note_system;

-- 1. 插入管理员数据（2条）
INSERT INTO admin (username, password, role, status) VALUES
('admin', MD5('123456'), '超级管理员', 1),
('manager', MD5('123456'), '普通管理员', 1);

-- 2. 插入用户数据（5条）
INSERT INTO user (username, phone, email, password, avatar, status) VALUES
('张三', '13800138001', 'zhangsan@example.com', MD5('123456'), 'https://picsum.photos/200/1', 1),
('李四', '13800138002', 'lisi@example.com', MD5('123456'), 'https://picsum.photos/200/2', 1),
('王五', '13800138003', 'wangwu@example.com', MD5('123456'), 'https://picsum.photos/200/3', 1),
('赵六', '13800138004', 'zhaoliu@example.com', MD5('123456'), 'https://picsum.photos/200/4', 1),
('测试用户', '13800138005', 'test@example.com', MD5('123456'), 'https://picsum.photos/200/5', 1);

-- 3. 插入公告数据（2条）
INSERT INTO notice (title, content, admin_id, is_top) VALUES
('系统功能升级通知', '本次升级新增了笔记批量导出功能，优化了语音识别准确率，欢迎体验！', 1, 1),
('API额度调整通知', '讯飞语音识别API免费额度已调整为每日500次，超出部分将按次计费。', 1, 0);

-- 4. 插入音频文件数据（5条）
INSERT INTO audio_file (user_id, file_name, file_path, file_size, file_format, duration) VALUES
(1, '20240520_会议录音.mp3', '/voice/20240520_meeting.mp3', 2560000, 'mp3', 180),
(2, '20240521_学习笔记.wav', '/voice/20240521_study.wav', 1800000, 'wav', 120),
(3, '20240522_待办事项.m4a', '/voice/20240522_todo.m4a', 900000, 'm4a', 60),
(4, '20240523_会议纪要.mp3', '/voice/20240523_minutes.mp3', 3200000, 'mp3', 240),
(5, '20240524_测试录音.wav', '/voice/20240524_test.wav', 512000, 'wav', 30);

-- 5. 插入识别日志数据（5条）
INSERT INTO recognition_log (user_id, audio_id, result, duration, accuracy, engine, status) VALUES
(1, 1, '今天的会议主要讨论了项目进度，下周需要完成前端开发...', 2500, 96.50, '讯飞', 1),
(2, 2, '学习笔记：SpringBoot的自动配置原理是基于条件注解...', 1800, 95.20, '百度', 1),
(3, 3, '待办事项：明天上午9点开会，下午提交报告...', 900, 98.00, '讯飞', 1),
(4, 4, '会议纪要：本次会议确定了产品上线时间为6月1日...', 3200, 94.80, '百度', 1),
(5, 5, '测试录音：这是一条测试语音，用于验证识别功能。', 500, 99.00, '讯飞', 1);

-- 6. 插入笔记数据（8条）
INSERT INTO note (user_id, title, content, category, tags, status) VALUES
(1, '项目进度会议纪要', '今天的会议主要讨论了项目进度，下周需要完成前端开发，后端接口联调，以及测试用例编写。', '工作', '会议,项目,进度', 1),
(2, 'SpringBoot学习笔记', '学习笔记：SpringBoot的自动配置原理是基于条件注解@Conditional，通过判断类路径下是否存在指定类来决定是否加载配置。', '学习', 'SpringBoot,Java,后端', 1),
(3, '明日待办事项', '待办事项：1. 明天上午9点开会；2. 下午提交项目报告；3. 整理上周的笔记。', '生活', '待办,提醒', 1),
(4, '产品上线会议纪要', '会议纪要：本次会议确定了产品上线时间为6月1日，需要在5月28日前完成所有功能测试，5月30日完成部署。', '工作', '会议,产品,上线', 1),
(1, '技术分享会笔记', '技术分享会：今天分享了微服务架构的设计原则，包括服务拆分、负载均衡、熔断降级等。', '学习', '技术,微服务,架构', 1),
(2, '读书笔记：《深入理解Java虚拟机》', '读书笔记：Java内存区域分为堆、栈、方法区、程序计数器，其中堆是垃圾回收的主要区域。', '学习', '读书,JVM,Java', 1),
(3, '购物清单', '购物清单：牛奶、面包、鸡蛋、水果、纸巾。', '生活', '购物,清单', 1),
(5, '测试笔记', '这是一条测试笔记，用于验证笔记的创建、编辑、删除功能。', '其他', '测试', 1);

-- 7. 插入反馈数据（3条）
INSERT INTO feedback (user_id, content, attachment, status, reply) VALUES
(1, '语音识别在噪音环境下准确率有点低，希望能优化。', NULL, 2, '感谢您的反馈！我们已经在对接降噪功能，预计下周上线。'),
(2, '建议增加笔记协作功能，可以和同事一起编辑笔记。', NULL, 1, NULL),
(3, '上传音频文件时，有时候会失败，提示网络错误。', '/attachment/error_screenshot.png', 0, NULL);