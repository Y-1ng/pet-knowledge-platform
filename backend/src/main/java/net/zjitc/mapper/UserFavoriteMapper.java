package net.zjitc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import net.zjitc.entity.UserFavorite;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserFavoriteMapper extends BaseMapper<UserFavorite> {

    @Select("SELECT * FROM user_favorite WHERE user_id = #{userId} AND target_type = #{targetType} ORDER BY create_time DESC")
    List<UserFavorite> selectByUserAndType(@Param("userId") Long userId, @Param("targetType") String targetType);

    @Select("SELECT COUNT(*) FROM user_favorite WHERE user_id = #{userId} AND target_type = #{targetType} AND target_id = #{targetId}")
    int countExist(@Param("userId") Long userId, @Param("targetType") String targetType, @Param("targetId") Long targetId);
}
