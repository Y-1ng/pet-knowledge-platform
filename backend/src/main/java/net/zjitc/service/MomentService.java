package net.zjitc.service;

import net.zjitc.common.PageResult;
import net.zjitc.entity.MomentComment;
import net.zjitc.entity.UserMoment;

import java.util.List;

public interface MomentService {
    List<UserMoment> getMoments();
    List<UserMoment> getMomentsByUserId(Long userId);
    boolean createMoment(Long userId, String content, String images);
    boolean deleteMoment(Long momentId, Long userId);

    // 点赞
    boolean toggleLike(Long momentId, Long userId);
    boolean isLiked(Long momentId, Long userId);

    // 评论（分页）
    PageResult<MomentComment> getPrimaryComments(Long momentId, int page, int size);
    List<MomentComment> getRepliesByParentId(Long parentId);
    boolean addComment(Long momentId, Long userId, String content, Long parentId, Long replyToUserId, String replyToUsername);
    boolean deleteComment(Long commentId, Long userId);
}
