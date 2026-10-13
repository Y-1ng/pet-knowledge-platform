package net.zjitc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import net.zjitc.common.PageResult;
import net.zjitc.dto.ArticleCommentDto;
import net.zjitc.entity.ArticleComment;
import net.zjitc.mapper.ArticleCommentMapper;
import net.zjitc.service.ArticleCommentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ArticleCommentServiceImpl implements ArticleCommentService {

    private final ArticleCommentMapper commentMapper;
    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public ArticleCommentServiceImpl(ArticleCommentMapper commentMapper) {
        this.commentMapper = commentMapper;
    }

    @Override
    public PageResult<ArticleCommentDto> getPrimaryComments(Long articleId, int page, int size) {
        // 分页查询一级评论（Mapper 已 JOIN 用户表填充 username / userAvatar）
        IPage<ArticleComment> pageResult = commentMapper.selectPrimaryByArticleId(
                new Page<>(page, size), articleId);

        // 为每个一级评论查询对应的二级回复，并转换为 DTO
        List<ArticleCommentDto> dtoList = new ArrayList<>();
        for (ArticleComment primary : pageResult.getRecords()) {
            ArticleCommentDto dto = toDto(primary);
            List<ArticleComment> replies = commentMapper.selectRepliesByParentId(primary.getId());
            dto.setReplies(replies.stream().map(this::toDto).collect(Collectors.toList()));
            dtoList.add(dto);
        }

        return new PageResult<>(dtoList, pageResult.getTotal(), page, size);
    }

    @Override
    public List<ArticleCommentDto> getRepliesByParentId(Long parentId) {
        List<ArticleComment> replies = commentMapper.selectRepliesByParentId(parentId);
        return replies.stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public boolean addComment(Long articleId, Long userId, String content,
                              Long parentId, Long replyToUserId, String replyToUsername) {
        ArticleComment comment = new ArticleComment();
        comment.setArticleId(articleId);
        comment.setUserId(userId);
        comment.setContent(content);
        comment.setParentId(parentId);
        comment.setReplyToUserId(replyToUserId);
        comment.setReplyToUsername(replyToUsername);
        comment.setIsDeleted(0);
        return commentMapper.insert(comment) > 0;
    }

    @Override
    @Transactional
    public boolean deleteComment(Long commentId, Long userId) {
        ArticleComment c = commentMapper.selectById(commentId);
        if (c == null || !c.getUserId().equals(userId)) {
            return false;
        }
        // 软删除：标记为已删除
        c.setIsDeleted(1);
        commentMapper.updateById(c);
        // 如果是一级评论，同时软删除所有二级回复
        if (c.getParentId() == null) {
            ArticleComment replyPlaceholder = new ArticleComment();
            replyPlaceholder.setIsDeleted(1);
            commentMapper.update(replyPlaceholder,
                    new LambdaQueryWrapper<ArticleComment>()
                            .eq(ArticleComment::getParentId, commentId));
        }
        return true;
    }

    /**
     * 将实体转换为 DTO，填充格式化时间
     */
    private ArticleCommentDto toDto(ArticleComment entity) {
        ArticleCommentDto dto = new ArticleCommentDto();
        dto.setId(entity.getId());
        dto.setArticleId(entity.getArticleId());
        dto.setUserId(entity.getUserId());
        dto.setContent(entity.getContent());
        dto.setParentId(entity.getParentId());
        dto.setReplyToUserId(entity.getReplyToUserId());
        dto.setReplyToUsername(entity.getReplyToUsername());
        dto.setIsDeleted(entity.getIsDeleted());
        dto.setUsername(entity.getUsername());
        dto.setUserAvatar(entity.getUserAvatar());
        dto.setArticleTitle(entity.getArticleTitle());
        if (entity.getCreateTime() != null) {
            dto.setCreateTime(entity.getCreateTime().format(DTF));
        }
        return dto;
    }

    @Override
    public PageResult<ArticleCommentDto> getCommentsByUserId(Long userId, int page, int size) {
        IPage<ArticleComment> pageResult = commentMapper.selectByUserId(
                new Page<>(page, size), userId);
        List<ArticleCommentDto> dtoList = pageResult.getRecords().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        return new PageResult<>(dtoList, pageResult.getTotal(), page, size);
    }
}
