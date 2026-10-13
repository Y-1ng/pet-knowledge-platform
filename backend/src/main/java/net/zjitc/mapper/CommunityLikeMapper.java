package net.zjitc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import net.zjitc.entity.CommunityLike;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CommunityLikeMapper extends BaseMapper<CommunityLike> {

    @Select("SELECT COUNT(*) FROM community_like WHERE user_id = #{userId} AND post_id = #{postId}")
    int countExist(@Param("userId") Long userId, @Param("postId") Long postId);
}
