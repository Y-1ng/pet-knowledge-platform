package net.zjitc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import net.zjitc.entity.CommunityPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface CommunityPostMapper extends BaseMapper<CommunityPost> {

    @Update("UPDATE community_post SET like_count = like_count + 1 WHERE id = #{id}")
    void incrementLikeCount(@Param("id") Long id);

    @Update("UPDATE community_post SET like_count = GREATEST(like_count - 1, 0) WHERE id = #{id}")
    void decrementLikeCount(@Param("id") Long id);

    @Update("UPDATE community_post SET comment_count = comment_count + 1 WHERE id = #{id}")
    void incrementCommentCount(@Param("id") Long id);

    @Update("UPDATE community_post SET comment_count = GREATEST(comment_count - #{delta}, 0) WHERE id = #{id}")
    void decrementCommentCount(@Param("id") Long id, @Param("delta") int delta);
}
