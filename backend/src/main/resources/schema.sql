-- ============================================================
-- 宠物科普知识平台 - 数据库建表脚本
-- ============================================================

-- 1. 用户表
CREATE TABLE IF NOT EXISTS `user` (
  `id`          BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username`    VARCHAR(50)  NOT NULL COMMENT '用户名',
  `password`    VARCHAR(100) NOT NULL COMMENT '密码（加密存储）',
  `email`       VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
  `nickname`    VARCHAR(50)  DEFAULT NULL COMMENT '昵称',
  `avatar`      VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
  `bio`         VARCHAR(255) DEFAULT NULL COMMENT '个人简介',
  `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 2. AI聊天记录表
CREATE TABLE IF NOT EXISTS `ai_chat_message` (
  `id`          BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT '消息ID',
  `session_id`  VARCHAR(64)  NOT NULL COMMENT '会话ID',
  `role`        VARCHAR(16)  NOT NULL COMMENT 'user 或 assistant',
  `content`     TEXT         NOT NULL COMMENT '消息内容',
  `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_session_id` (`session_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI聊天记录表';

-- 3. 知识分类表
CREATE TABLE IF NOT EXISTS `knowledge_category` (
  `id`          BIGINT(20)  NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name`        VARCHAR(50) NOT NULL COMMENT '分类名称',
  `icon`        VARCHAR(255) DEFAULT NULL COMMENT '分类图标',
  `sort`        INT(11)     DEFAULT 0 COMMENT '排序',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='知识分类表';

-- 4. 科普文章表
CREATE TABLE IF NOT EXISTS `knowledge_article` (
  `id`          BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT '文章ID',
  `category_id` BIGINT(20)   NOT NULL COMMENT '分类ID',
  `title`       VARCHAR(100) NOT NULL COMMENT '文章标题',
  `cover`       VARCHAR(255) DEFAULT NULL COMMENT '封面图',
  `summary`     VARCHAR(255) DEFAULT NULL COMMENT '摘要',
  `content`     TEXT         NOT NULL COMMENT '文章内容',
  `view_count`  INT(11)      DEFAULT 0 COMMENT '浏览量',
  `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
  PRIMARY KEY (`id`),
  KEY `idx_category` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='科普文章表';

-- 5. 养宠指南表
CREATE TABLE IF NOT EXISTS `pet_guide` (
  `id`          BIGINT(20)   NOT NULL AUTO_INCREMENT,
  `type`        VARCHAR(20)  NOT NULL COMMENT '类型：feeding/health/training',
  `title`       VARCHAR(100) NOT NULL COMMENT '标题',
  `cover`       VARCHAR(255) DEFAULT NULL COMMENT '封面图',
  `content`     TEXT         NOT NULL COMMENT '内容',
  `view_count`  INT(11)      DEFAULT 0 COMMENT '浏览量',
  `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_type` (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='养宠指南表';

-- 6. 用户收藏表
CREATE TABLE IF NOT EXISTS `user_favorite` (
  `id`          BIGINT(20)  NOT NULL AUTO_INCREMENT,
  `user_id`     BIGINT(20)  NOT NULL,
  `target_type` VARCHAR(20) NOT NULL COMMENT '收藏类型：guide/knowledge',
  `target_id`   BIGINT(20)  NOT NULL,
  `create_time` DATETIME    DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_target` (`user_id`, `target_type`, `target_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户收藏表';

-- 7. 社区帖子表
CREATE TABLE IF NOT EXISTS `community_post` (
  `id`            BIGINT(20)   NOT NULL AUTO_INCREMENT,
  `user_id`       BIGINT(20)   NOT NULL COMMENT '发帖人',
  `title`         VARCHAR(100) NOT NULL COMMENT '标题',
  `content`       TEXT         NOT NULL COMMENT '内容',
  `images`        TEXT         DEFAULT NULL COMMENT '图片URL列表',
  `like_count`    INT(11)      DEFAULT 0 COMMENT '点赞数',
  `comment_count` INT(11)      DEFAULT 0 COMMENT '评论数',
  `create_time`   DATETIME     DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='社区帖子表';

-- 8. 社区评论表（支持二级评论，含软删除标记）
CREATE TABLE IF NOT EXISTS `community_comment` (
  `id`                BIGINT(20)   NOT NULL AUTO_INCREMENT,
  `post_id`           BIGINT(20)   NOT NULL,
  `user_id`           BIGINT(20)   NOT NULL,
  `content`           VARCHAR(500) NOT NULL,
  `parent_id`         BIGINT(20)   DEFAULT NULL COMMENT '父评论ID',
  `reply_to_user_id`  BIGINT(20)   DEFAULT NULL COMMENT '回复目标用户ID',
  `reply_to_username` VARCHAR(50)  DEFAULT NULL COMMENT '回复目标用户名',
  `is_deleted`        TINYINT(1)   DEFAULT 0 COMMENT '删除标记 0=未删 1=已删',
  `create_time`       DATETIME     DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_post` (`post_id`),
  KEY `idx_parent` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='社区评论表';

-- 9. 点赞表
CREATE TABLE IF NOT EXISTS `community_like` (
  `id`          BIGINT(20) NOT NULL AUTO_INCREMENT,
  `user_id`     BIGINT(20) NOT NULL,
  `post_id`     BIGINT(20) NOT NULL,
  `create_time` DATETIME   DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_post` (`user_id`, `post_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='点赞表';

-- 10. 宠物档案表
CREATE TABLE IF NOT EXISTS `pet_profile` (
  `id`          BIGINT(20)   NOT NULL AUTO_INCREMENT,
  `user_id`     BIGINT(20)   NOT NULL,
  `name`        VARCHAR(50)  NOT NULL COMMENT '宠物名字',
  `species`     VARCHAR(20)  NOT NULL COMMENT '物种：cat/dog/rabbit...',
  `breed`       VARCHAR(50)  DEFAULT NULL COMMENT '品种',
  `birthday`    DATE         DEFAULT NULL COMMENT '生日',
  `gender`      CHAR(1)      DEFAULT NULL COMMENT 'M/F',
  `weight`      DECIMAL(5,2) DEFAULT NULL COMMENT '体重(kg)',
  `avatar`      VARCHAR(255) DEFAULT NULL COMMENT '宠物头像',
  `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='宠物档案表';

-- 11. 健康记录表
CREATE TABLE IF NOT EXISTS `pet_health_record` (
  `id`          BIGINT(20)   NOT NULL AUTO_INCREMENT,
  `pet_id`      BIGINT(20)   NOT NULL,
  `record_type` VARCHAR(20)  NOT NULL COMMENT 'vaccine/deworm/checkup/illness',
  `record_name` VARCHAR(100) NOT NULL COMMENT '记录名称',
  `record_date` DATE         NOT NULL COMMENT '发生日期',
  `notes`       TEXT         DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_pet` (`pet_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='健康记录表';

-- 12. 问答问题表
CREATE TABLE IF NOT EXISTS `qa_question` (
  `id`            BIGINT(20)   NOT NULL AUTO_INCREMENT,
  `user_id`       BIGINT(20)   NOT NULL,
  `title`         VARCHAR(100) NOT NULL COMMENT '问题标题',
  `description`   TEXT         DEFAULT NULL COMMENT '补充描述',
  `answer_count`  INT(11)      DEFAULT 0 COMMENT '回答数',
  `view_count`    INT(11)      DEFAULT 0 COMMENT '浏览数',
  `create_time`   DATETIME     DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='问答问题表';

-- 13. 回答表
CREATE TABLE IF NOT EXISTS `qa_answer` (
  `id`          BIGINT(20)  NOT NULL AUTO_INCREMENT,
  `question_id` BIGINT(20)  NOT NULL,
  `user_id`     BIGINT(20)  NOT NULL,
  `content`     TEXT        NOT NULL,
  `is_accepted` TINYINT(1)  DEFAULT 0 COMMENT '是否采纳',
  `create_time` DATETIME    DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_question` (`question_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='回答表';

-- 14. 宠物品种表
CREATE TABLE IF NOT EXISTS `pet_breed` (
  `id`          BIGINT(20)   NOT NULL AUTO_INCREMENT,
  `category_id` BIGINT(20)   NOT NULL COMMENT '关联知识分类ID',
  `name`        VARCHAR(100) NOT NULL COMMENT '品种名称',
  `english_name` VARCHAR(100) DEFAULT NULL COMMENT '英文名称',
  `origin`      VARCHAR(100) DEFAULT NULL COMMENT '原产地',
  `personality` VARCHAR(255) DEFAULT NULL COMMENT '性格特点',
  `lifespan`    VARCHAR(50)  DEFAULT NULL COMMENT '寿命',
  `weight`      VARCHAR(50)  DEFAULT NULL COMMENT '体重范围',
  `height`      VARCHAR(50)  DEFAULT NULL COMMENT '身高范围',
  `coat`        VARCHAR(255) DEFAULT NULL COMMENT '毛发特征',
  `colors`      VARCHAR(255) DEFAULT NULL COMMENT '常见毛色',
  `description` TEXT         DEFAULT NULL COMMENT '品种简介',
  `care_tips`   TEXT         DEFAULT NULL COMMENT '饲养要点',
  `image_url`   VARCHAR(255) DEFAULT NULL COMMENT '图片路径',
  `is_featured` TINYINT(1)   DEFAULT 0 COMMENT '是否推荐 1=推荐',
  `sort_order`  INT(11)      DEFAULT 0 COMMENT '排序',
  `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_category` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='宠物品种表';

-- 15. 文章评论表（支持二级评论，含软删除标记）
CREATE TABLE IF NOT EXISTS `article_comment` (
  `id`                BIGINT(20)   NOT NULL AUTO_INCREMENT,
  `article_id`        BIGINT(20)   NOT NULL COMMENT '文章ID',
  `user_id`           BIGINT(20)   NOT NULL COMMENT '评论人',
  `content`           VARCHAR(500) NOT NULL COMMENT '评论内容',
  `parent_id`         BIGINT(20)   DEFAULT NULL COMMENT '父评论ID，NULL为一级评论',
  `reply_to_user_id`  BIGINT(20)   DEFAULT NULL COMMENT '回复目标用户ID',
  `reply_to_username` VARCHAR(50)  DEFAULT NULL COMMENT '回复目标用户名',
  `is_deleted`        TINYINT(1)   DEFAULT 0 COMMENT '删除标记 0=未删 1=已删',
  `create_time`       DATETIME     DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_article` (`article_id`),
  KEY `idx_parent` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章评论表';


-- 16. 动态表
CREATE TABLE IF NOT EXISTS `user_moment` (
  `id`          BIGINT(20)   NOT NULL AUTO_INCREMENT,
  `user_id`     BIGINT(20)   NOT NULL COMMENT '发布人',
  `content`     TEXT         NOT NULL COMMENT '动态内容',
  `images`      TEXT         DEFAULT NULL COMMENT '图片URL列表',
  `like_count`  INT(11)      DEFAULT 0 COMMENT '点赞数',
  `comment_count` INT(11)   DEFAULT 0 COMMENT '评论数',
  `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user` (`user_id`),
  KEY `idx_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户动态表';

-- 17. 动态点赞表
CREATE TABLE IF NOT EXISTS `moment_like` (
  `id`          BIGINT(20) NOT NULL AUTO_INCREMENT,
  `moment_id`   BIGINT(20) NOT NULL,
  `user_id`     BIGINT(20) NOT NULL,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_moment_user` (`moment_id`, `user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='动态点赞表';

-- 18. 动态评论表（支持二级评论，含软删除标记）
CREATE TABLE IF NOT EXISTS `moment_comment` (
  `id`                BIGINT(20)   NOT NULL AUTO_INCREMENT,
  `moment_id`         BIGINT(20)   NOT NULL,
  `user_id`           BIGINT(20)   NOT NULL,
  `content`           VARCHAR(500) NOT NULL,
  `parent_id`         BIGINT(20)   DEFAULT NULL,
  `reply_to_user_id`  BIGINT(20)   DEFAULT NULL,
  `reply_to_username` VARCHAR(50)  DEFAULT NULL,
  `is_deleted`        TINYINT(1)   DEFAULT 0 COMMENT '删除标记 0=未删 1=已删',
  `create_time`       DATETIME     DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_moment` (`moment_id`),
  KEY `idx_parent` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='动态评论表';

-- ============================================================
-- 初始数据
-- ============================================================

-- 默认测试用户（密码: 123456）
INSERT IGNORE INTO `user` (`id`, `username`, `password`, `email`, `nickname`, `bio`) VALUES
(1, 'test', 'e10adc3949ba59abbe56e057f20f883e', 'test@pet.com', '测试用户', '欢迎体验宠知平台 🐾'),
(2, 'admin', '0192023a7bbd73250516f069df18b500', 'admin@pet.com', '管理员', '宠知平台管理员');

-- 知识分类初始数据
INSERT IGNORE INTO `knowledge_category` (`id`, `name`, `icon`, `sort`) VALUES
(1, '猫咪', '🐱', 1),
(2, '狗狗', '🐶', 2),
(3, '兔子', '🐰', 3),
(4, '鸟类', '🐦', 4),
(5, '鱼类', '🐟', 5),
(6, '爬宠', '🦎', 6);

-- 示例科普文章
INSERT IGNORE INTO `knowledge_article` (`id`, `category_id`, `title`, `summary`, `content`, `view_count`) VALUES
(1, 1, '猫咪不能吃的7种常见食物', '养猫必看！这些食物对猫咪有致命危险。', '# 猫咪不能吃的食物\n\n1. **巧克力** — 含有可可碱，对猫有毒\n2. **洋葱和大蒜** — 破坏红细胞\n3. **葡萄和葡萄干** — 导致肾衰竭\n4. **牛奶** — 多数猫乳糖不耐受\n5. **生鸡蛋** — 可能含沙门氏菌\n6. **酒精** — 少量即可中毒\n7. **咖啡因** — 影响心脏和神经系统\n\n> 如果猫咪误食了以上食物，请立即联系兽医！', 128);

INSERT IGNORE INTO `knowledge_article` (`id`, `category_id`, `title`, `summary`, `content`, `view_count`) VALUES
(2, 2, '新手养狗指南：从零开始', '第一次养狗？这份指南帮你做好全面准备。', '# 新手养狗指南\n\n## 接狗前的准备\n- 狗碗、狗粮、狗窝、牵引绳\n- 选择一个靠谱的宠物医院\n\n## 接狗后的注意事项\n1. **不要马上洗澡** — 适应新环境后再洗\n2. **定时喂食** — 幼犬少食多餐\n3. **疫苗和驱虫** — 按医生建议进行\n\n## 基础训练\n- 定点大小便\n- 坐下、卧下等基础指令\n\n> 💡 耐心和爱心是养好狗狗的关键！', 256);

-- 示例养宠指南
INSERT IGNORE INTO `pet_guide` (`id`, `type`, `title`, `content`, `view_count`) VALUES
(1, 'feeding', '幼猫科学喂养指南', '# 幼猫喂养指南\n\n## 0-4周\n- 母乳或专用猫奶粉\n- 每2-3小时喂一次\n\n## 4-8周\n- 开始引入湿粮\n- 逐渐减少喂奶次数\n\n## 8周后\n- 可以吃幼猫粮\n- 每日3-4餐\n\n> 注意：不要给幼猫喝牛奶！', 89);

INSERT IGNORE INTO `pet_guide` (`id`, `type`, `content`, `title`, `view_count`) VALUES
(2, 'health', '狗狗疫苗注射时间表', '# 狗狗疫苗注射时间表\n\n## 核心疫苗\n| 年龄 | 疫苗 |\n|------|------|\n| 6-8周 | 第一针联合疫苗 |\n| 10-12周 | 第二针联合疫苗 |\n| 14-16周 | 第三针联合疫苗 + 狂犬疫苗 |\n| 每年 | 加强针 |\n\n> 💡 建议在宠物医院建立完整的疫苗档案。', 156);

-- 初始社区帖子
INSERT IGNORE INTO `community_post` (`id`, `user_id`, `title`, `content`, `like_count`, `comment_count`) VALUES
(1, 1, '我家猫咪学会了开门！', '今天发现我家橘猫居然自己学会了开门把手，太聪明了😂\n视频在附件～', 15, 3);

-- 初始问答
INSERT IGNORE INTO `qa_question` (`id`, `user_id`, `title`, `description`, `view_count`) VALUES
(1, 1, '猫咪为什么喜欢踩奶？', '我家猫经常在我肚子上踩来踩去，这是为什么？', 45);

INSERT IGNORE INTO `qa_answer` (`id`, `question_id`, `user_id`, `content`, `is_accepted`) VALUES
(1, 1, 1, '踩奶是猫咪从幼猫时期遗留下来的本能行为，表示它们感到安全和舒适。当猫咪在你身上踩奶，说明它很信任你哦！🐱', 1);

-- ============================================================
-- is_deleted 列已通过手动 ALTER TABLE 添加（MySQL 5.6）
-- 新部署时需执行：
--   ALTER TABLE article_comment  ADD COLUMN is_deleted TINYINT(1) DEFAULT 0;
--   ALTER TABLE community_comment ADD COLUMN is_deleted TINYINT(1) DEFAULT 0;
--   ALTER TABLE moment_comment    ADD COLUMN is_deleted TINYINT(1) DEFAULT 0;
-- ============================================================
