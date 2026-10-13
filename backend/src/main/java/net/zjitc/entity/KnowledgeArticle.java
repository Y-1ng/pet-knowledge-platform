package net.zjitc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("knowledge_article")
public class KnowledgeArticle {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long categoryId;
    private String title;
    private String cover;
    private String summary;
    private String content;
    private Integer viewCount;
    private LocalDateTime createTime;

    // 非数据库字段 — 由 LEFT JOIN 子查询填充
    @TableField(exist = false)
    private Integer commentCount;
}
