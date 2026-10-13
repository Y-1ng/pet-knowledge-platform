package net.zjitc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import net.zjitc.common.PageResult;
import net.zjitc.entity.CommunityComment;
import net.zjitc.entity.CommunityLike;
import net.zjitc.entity.CommunityPost;
import net.zjitc.mapper.CommunityCommentMapper;
import net.zjitc.mapper.CommunityLikeMapper;
import net.zjitc.mapper.CommunityPostMapper;
import net.zjitc.service.CommunityService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CommunityServiceImpl extends ServiceImpl<CommunityPostMapper, CommunityPost> implements CommunityService {

    private final CommunityCommentMapper commentMapper;
    private final CommunityLikeMapper likeMapper;

    public CommunityServiceImpl(CommunityCommentMapper commentMapper, CommunityLikeMapper likeMapper) {
        this.commentMapper = commentMapper;
        this.likeMapper = likeMapper;
    }

    @Override
    public List<CommunityPost> getPostList() {
        LambdaQueryWrapper<CommunityPost> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(CommunityPost::getCreateTime);
        return list(wrapper);
    }

    @Override
    public CommunityPost getPostDetail(Long id) {
        return getById(id);
    }

    @Override
    public boolean createPost(Long userId, String title, String content, String images) {
        CommunityPost post = new CommunityPost();
        post.setUserId(userId);
        post.setTitle(title);
        post.setContent(content);
        post.setImages(images);
        post.setLikeCount(0);
        post.setCommentCount(0);
        return save(post);
    }

    @Override
    public boolean deletePost(Long postId, Long userId) {
        CommunityPost post = getById(postId);
        if (post == null || !post.getUserId().equals(userId)) {
            return false;
        }
        // 删除帖子相关的评论和点赞
        commentMapper.delete(new LambdaQueryWrapper<CommunityComment>().eq(CommunityComment::getPostId, postId));
        likeMapper.delete(new LambdaQueryWrapper<CommunityLike>().eq(CommunityLike::getPostId, postId));
        return removeById(postId);
    }

    @Override
    public PageResult<CommunityComment> getPrimaryComments(Long postId, int page, int size) {
        IPage<CommunityComment> pageResult = commentMapper.selectPrimaryByPostId(
                new Page<>(page, size), postId);

        for (CommunityComment primary : pageResult.getRecords()) {
            List<CommunityComment> replies = commentMapper.selectRepliesByParentId(primary.getId());
            primary.setReplies(replies);
        }

        return new PageResult<>(pageResult.getRecords(), pageResult.getTotal(), page, size);
    }

    @Override
    public List<CommunityComment> getRepliesByParentId(Long parentId) {
        return commentMapper.selectRepliesByParentId(parentId);
    }

    @Override
    public boolean addComment(Long postId, Long userId, String content, Long parentId, Long replyToUserId, String replyToUsername) {
        CommunityComment comment = new CommunityComment();
        comment.setPostId(postId);
        comment.setUserId(userId);
        comment.setContent(content);
        comment.setParentId(parentId);
        comment.setReplyToUserId(replyToUserId);
        comment.setReplyToUsername(replyToUsername);
        comment.setIsDeleted(0);
        commentMapper.insert(comment);
        baseMapper.incrementCommentCount(postId);
        return true;
    }

    @Override
    @Transactional
    public boolean deleteComment(Long commentId, Long userId) {
        CommunityComment c = commentMapper.selectById(commentId);
        if (c == null || !c.getUserId().equals(userId)) {
            return false;
        }
        // 软删除评论本身
        c.setIsDeleted(1);
        commentMapper.updateById(c);

        int delta = 1; // 删除一条评论
        // 如果是一级评论，同时软删除所有二级回复
        if (c.getParentId() == null) {
            // 先统计有多少活跃的回复
            Long replyCount = commentMapper.selectCount(
                    new LambdaQueryWrapper<CommunityComment>()
                            .eq(CommunityComment::getParentId, commentId)
                            .ne(CommunityComment::getIsDeleted, 1));
            delta += replyCount.intValue();

            CommunityComment replyPlaceholder = new CommunityComment();
            replyPlaceholder.setIsDeleted(1);
            commentMapper.update(replyPlaceholder,
                    new LambdaQueryWrapper<CommunityComment>()
                            .eq(CommunityComment::getParentId, commentId));
        }
        // 更新帖子的评论计数器
        baseMapper.decrementCommentCount(c.getPostId(), delta);
        return true;
    }

    @Override
    @Transactional
    public boolean toggleLike(Long postId, Long userId) {
        int count = likeMapper.countExist(userId, postId);
        if (count > 0) {
            likeMapper.delete(new LambdaQueryWrapper<CommunityLike>()
                    .eq(CommunityLike::getUserId, userId)
                    .eq(CommunityLike::getPostId, postId));
            baseMapper.decrementLikeCount(postId);
            return false;
        } else {
            CommunityLike like = new CommunityLike();
            like.setUserId(userId);
            like.setPostId(postId);
            likeMapper.insert(like);
            baseMapper.incrementLikeCount(postId);
            return true;
        }
    }

    @Override
    public boolean isLiked(Long postId, Long userId) {
        return likeMapper.countExist(userId, postId) > 0;
    }
}
