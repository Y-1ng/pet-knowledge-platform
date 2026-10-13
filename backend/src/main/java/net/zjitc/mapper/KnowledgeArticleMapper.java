package net.zjitc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import net.zjitc.entity.KnowledgeArticle;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface KnowledgeArticleMapper extends BaseMapper<KnowledgeArticle> {

    @Select("SELECT ka.*, " +
            "(SELECT COUNT(*) FROM article_comment ac WHERE ac.article_id = ka.id AND (ac.is_deleted IS NULL OR ac.is_deleted = 0) AND ac.parent_id IS NULL) AS comment_count " +
            "FROM knowledge_article ka WHERE ka.category_id = #{categoryId} ORDER BY ka.create_time DESC")
    List<KnowledgeArticle> selectByCategoryId(@Param("categoryId") Long categoryId);

    @Select("SELECT ka.*, " +
            "(SELECT COUNT(*) FROM article_comment ac WHERE ac.article_id = ka.id AND (ac.is_deleted IS NULL OR ac.is_deleted = 0) AND ac.parent_id IS NULL) AS comment_count " +
            "FROM knowledge_article ka WHERE ka.title LIKE CONCAT('%', #{keyword}, '%') OR ka.summary LIKE CONCAT('%', #{keyword}, '%') ORDER BY ka.create_time DESC")
    List<KnowledgeArticle> searchByKeyword(@Param("keyword") String keyword);

    @Select("SELECT ka.*, " +
            "(SELECT COUNT(*) FROM article_comment ac WHERE ac.article_id = ka.id AND (ac.is_deleted IS NULL OR ac.is_deleted = 0) AND ac.parent_id IS NULL) AS comment_count " +
            "FROM knowledge_article ka ORDER BY ka.view_count DESC LIMIT 10")
    List<KnowledgeArticle> selectHot();

    @Select("SELECT ka.*, " +
            "(SELECT COUNT(*) FROM article_comment ac WHERE ac.article_id = ka.id AND (ac.is_deleted IS NULL OR ac.is_deleted = 0) AND ac.parent_id IS NULL) AS comment_count " +
            "FROM knowledge_article ka ORDER BY ka.create_time DESC")
    List<KnowledgeArticle> selectAllWithCommentCount();

    @Select("SELECT ka.*, " +
            "(SELECT COUNT(*) FROM article_comment ac WHERE ac.article_id = ka.id AND (ac.is_deleted IS NULL OR ac.is_deleted = 0) AND ac.parent_id IS NULL) AS comment_count " +
            "FROM knowledge_article ka WHERE ka.id = #{id}")
    KnowledgeArticle selectByIdWithCommentCount(@Param("id") Long id);

    @Update("UPDATE knowledge_article SET view_count = view_count + 1 WHERE id = #{id}")
    void incrementViewCount(@Param("id") Long id);
}
