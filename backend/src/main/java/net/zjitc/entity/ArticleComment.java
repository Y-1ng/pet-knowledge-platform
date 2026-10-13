package net.zjitc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("article_comment")
public class ArticleComment {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long articleId;
    private Long userId;
    private String content;
    private Long parentId;
    private Long replyToUserId;
    private String replyToUsername;
    private LocalDateTime createTime;
    private Integer isDeleted;

    // 非数据库字段（JOIN查询用）
    @TableField(exist = false)
    private String username;
    @TableField(exist = false)
    private String userAvatar;

    // 关联文章标题（查询用户评论列表时填充）
    @TableField(exist = false)
    private String articleTitle;

    // 二级评论列表（非DB字段，由服务端组装）
    @TableField(exist = false)
    private List<ArticleComment> replies;
}
