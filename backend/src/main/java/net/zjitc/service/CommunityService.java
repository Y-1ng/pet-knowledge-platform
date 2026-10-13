package net.zjitc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import net.zjitc.common.PageResult;
import net.zjitc.entity.CommunityComment;
import net.zjitc.entity.CommunityPost;

import java.util.List;

public interface CommunityService extends IService<CommunityPost> {
    // 帖子
    List<CommunityPost> getPostList();
    CommunityPost getPostDetail(Long id);
    boolean createPost(Long userId, String title, String content, String images);
    boolean deletePost(Long postId, Long userId);

    // 评论（分页）
    PageResult<CommunityComment> getPrimaryComments(Long postId, int page, int size);
    List<CommunityComment> getRepliesByParentId(Long parentId);
    boolean addComment(Long postId, Long userId, String content, Long parentId, Long replyToUserId, String replyToUsername);
    boolean deleteComment(Long commentId, Long userId);

    // 点赞
    boolean toggleLike(Long postId, Long userId);
    boolean isLiked(Long postId, Long userId);
}
