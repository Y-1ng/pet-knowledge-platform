package net.zjitc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import net.zjitc.common.PageResult;
import net.zjitc.entity.MomentComment;
import net.zjitc.entity.MomentLike;
import net.zjitc.entity.UserMoment;
import net.zjitc.mapper.MomentCommentMapper;
import net.zjitc.mapper.MomentLikeMapper;
import net.zjitc.mapper.UserMomentMapper;
import net.zjitc.service.MomentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MomentServiceImpl implements MomentService {

    private final UserMomentMapper momentMapper;
    private final MomentLikeMapper likeMapper;
    private final MomentCommentMapper commentMapper;

    public MomentServiceImpl(UserMomentMapper momentMapper, MomentLikeMapper likeMapper, MomentCommentMapper commentMapper) {
        this.momentMapper = momentMapper;
        this.likeMapper = likeMapper;
        this.commentMapper = commentMapper;
    }

    @Override
    public List<UserMoment> getMoments() {
        return momentMapper.selectAllWithUser();
    }

    @Override
    public List<UserMoment> getMomentsByUserId(Long userId) {
        return momentMapper.selectByUserIdWithUser(userId);
    }

    @Override
    public boolean createMoment(Long userId, String content, String images) {
        UserMoment m = new UserMoment();
        m.setUserId(userId);
        m.setContent(content);
        m.setImages(images);
        m.setLikeCount(0);
        m.setCommentCount(0);
        return momentMapper.insert(m) > 0;
    }

    @Override
    public boolean deleteMoment(Long momentId, Long userId) {
        UserMoment m = momentMapper.selectById(momentId);
        if (m != null && m.getUserId().equals(userId)) {
            momentMapper.deleteById(momentId);
            likeMapper.delete(new LambdaQueryWrapper<MomentLike>().eq(MomentLike::getMomentId, momentId));
            commentMapper.delete(new LambdaQueryWrapper<MomentComment>().eq(MomentComment::getMomentId, momentId));
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public boolean toggleLike(Long momentId, Long userId) {
        int count = likeMapper.countExist(momentId, userId);
        if (count > 0) {
            likeMapper.delete(new LambdaQueryWrapper<MomentLike>()
                    .eq(MomentLike::getMomentId, momentId)
                    .eq(MomentLike::getUserId, userId));
            momentMapper.decrementLikeCount(momentId);
            return false;
        } else {
            MomentLike like = new MomentLike();
            like.setMomentId(momentId);
            like.setUserId(userId);
            likeMapper.insert(like);
            momentMapper.incrementLikeCount(momentId);
            return true;
        }
    }

    @Override
    public boolean isLiked(Long momentId, Long userId) {
        return likeMapper.countExist(momentId, userId) > 0;
    }

    @Override
    public PageResult<MomentComment> getPrimaryComments(Long momentId, int page, int size) {
        IPage<MomentComment> pageResult = commentMapper.selectPrimaryByMomentId(
                new Page<>(page, size), momentId);

        for (MomentComment primary : pageResult.getRecords()) {
            List<MomentComment> replies = commentMapper.selectRepliesByParentId(primary.getId());
            primary.setReplies(replies);
        }

        return new PageResult<>(pageResult.getRecords(), pageResult.getTotal(), page, size);
    }

    @Override
    public List<MomentComment> getRepliesByParentId(Long parentId) {
        return commentMapper.selectRepliesByParentId(parentId);
    }

    @Override
    public boolean addComment(Long momentId, Long userId, String content, Long parentId, Long replyToUserId, String replyToUsername) {
        MomentComment c = new MomentComment();
        c.setMomentId(momentId);
        c.setUserId(userId);
        c.setContent(content);
        c.setParentId(parentId);
        c.setReplyToUserId(replyToUserId);
        c.setReplyToUsername(replyToUsername);
        c.setIsDeleted(0);
        commentMapper.insert(c);
        momentMapper.incrementCommentCount(momentId);
        return true;
    }

    @Override
    @Transactional
    public boolean deleteComment(Long commentId, Long userId) {
        MomentComment c = commentMapper.selectById(commentId);
        if (c == null || !c.getUserId().equals(userId)) {
            return false;
        }
        // 软删除评论本身
        c.setIsDeleted(1);
        commentMapper.updateById(c);

        int delta = 1;
        // 如果是一级评论，同时软删除所有二级回复
        if (c.getParentId() == null) {
            Long replyCount = commentMapper.selectCount(
                    new LambdaQueryWrapper<MomentComment>()
                            .eq(MomentComment::getParentId, commentId)
                            .ne(MomentComment::getIsDeleted, 1));
            delta += replyCount.intValue();

            MomentComment replyPlaceholder = new MomentComment();
            replyPlaceholder.setIsDeleted(1);
            commentMapper.update(replyPlaceholder,
                    new LambdaQueryWrapper<MomentComment>()
                            .eq(MomentComment::getParentId, commentId));
        }
        // 更新动态的评论计数器
        momentMapper.decrementCommentCount(c.getMomentId(), delta);
        return true;
    }
}
