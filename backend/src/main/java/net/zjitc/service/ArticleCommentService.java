package net.zjitc.service;

import net.zjitc.common.PageResult;
import net.zjitc.dto.ArticleCommentDto;

import java.util.List;

public interface ArticleCommentService {
    /** 分页查询一级评论（每项含二级回复列表） */
    PageResult<ArticleCommentDto> getPrimaryComments(Long articleId, int page, int size);

    /** 查询指定一级评论下的所有二级回复 */
    List<ArticleCommentDto> getRepliesByParentId(Long parentId);

    /** 新增评论（一级或二级） */
    boolean addComment(Long articleId, Long userId, String content, Long parentId,
                       Long replyToUserId, String replyToUsername);

    /** 删除评论（级联删除二级评论），需校验userId为评论者本人 */
    boolean deleteComment(Long commentId, Long userId);

    /** 分页查询指定用户的评论列表（携带文章标题） */
    PageResult<ArticleCommentDto> getCommentsByUserId(Long userId, int page, int size);
}
