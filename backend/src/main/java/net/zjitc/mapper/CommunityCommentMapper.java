package net.zjitc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import net.zjitc.entity.CommunityComment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CommunityCommentMapper extends BaseMapper<CommunityComment> {

    @Select("SELECT cc.*, u.nickname AS username, u.avatar AS user_avatar " +
            "FROM community_comment cc LEFT JOIN user u ON cc.user_id = u.id " +
            "WHERE cc.post_id = #{postId} AND cc.parent_id IS NULL AND (cc.is_deleted IS NULL OR cc.is_deleted = 0) " +
            "ORDER BY cc.create_time DESC")
    IPage<CommunityComment> selectPrimaryByPostId(IPage<CommunityComment> page, @Param("postId") Long postId);

    @Select("SELECT cc.*, u.nickname AS username, u.avatar AS user_avatar " +
            "FROM community_comment cc LEFT JOIN user u ON cc.user_id = u.id " +
            "WHERE cc.parent_id = #{parentId} AND (cc.is_deleted IS NULL OR cc.is_deleted = 0) " +
            "ORDER BY cc.create_time ASC")
    List<CommunityComment> selectRepliesByParentId(@Param("parentId") Long parentId);
}
