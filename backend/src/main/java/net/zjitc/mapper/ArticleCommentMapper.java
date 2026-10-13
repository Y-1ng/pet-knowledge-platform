package net.zjitc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import net.zjitc.entity.ArticleComment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ArticleCommentMapper extends BaseMapper<ArticleComment> {

    @Select("SELECT ac.*, u.nickname AS username, u.avatar AS user_avatar " +
            "FROM article_comment ac LEFT JOIN user u ON ac.user_id = u.id " +
            "WHERE ac.article_id = #{articleId} AND ac.parent_id IS NULL AND (ac.is_deleted IS NULL OR ac.is_deleted = 0) " +
            "ORDER BY ac.create_time DESC")
    IPage<ArticleComment> selectPrimaryByArticleId(IPage<ArticleComment> page, @Param("articleId") Long articleId);

    @Select("SELECT ac.*, u.nickname AS username, u.avatar AS user_avatar " +
            "FROM article_comment ac LEFT JOIN user u ON ac.user_id = u.id " +
            "WHERE ac.parent_id = #{parentId} AND (ac.is_deleted IS NULL OR ac.is_deleted = 0) " +
            "ORDER BY ac.create_time ASC")
    List<ArticleComment> selectRepliesByParentId(@Param("parentId") Long parentId);

    @Select("SELECT ac.*, u.nickname AS username, u.avatar AS user_avatar, ka.title AS article_title " +
            "FROM article_comment ac " +
            "LEFT JOIN user u ON ac.user_id = u.id " +
            "LEFT JOIN knowledge_article ka ON ac.article_id = ka.id " +
            "WHERE ac.user_id = #{userId} AND (ac.is_deleted IS NULL OR ac.is_deleted = 0) " +
            "ORDER BY ac.create_time DESC")
    IPage<ArticleComment> selectByUserId(IPage<ArticleComment> page, @Param("userId") Long userId);
}
