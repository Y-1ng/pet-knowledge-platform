package net.zjitc.dto;

import lombok.Data;

import java.util.List;

/**
 * 文章评论DTO — 返回给前端时携带用户信息（昵称、头像）和格式化时间
 */
@Data
public class ArticleCommentDto {
    private Long id;
    private Long articleId;
    private Long userId;
    private String content;
    private Long parentId;
    private Long replyToUserId;
    private String replyToUsername;
    private String createTime;   // yyyy-MM-dd HH:mm:ss
    private Integer isDeleted;

    // 用户补充信息（由 JOIN 或 Service 层填充）
    private String username;
    private String userAvatar;

    // 关联文章标题（查询用户评论列表时填充）
    private String articleTitle;

    // 二级回复列表（仅在查询一级评论时携带）
    private List<ArticleCommentDto> replies;
}
