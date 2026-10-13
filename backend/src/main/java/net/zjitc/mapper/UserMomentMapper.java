package net.zjitc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import net.zjitc.entity.UserMoment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface UserMomentMapper extends BaseMapper<UserMoment> {

    @Select("SELECT um.*, u.nickname AS username, u.avatar AS user_avatar " +
            "FROM user_moment um LEFT JOIN user u ON um.user_id = u.id " +
            "ORDER BY um.create_time DESC")
    List<UserMoment> selectAllWithUser();

    @Update("UPDATE user_moment SET like_count = like_count + 1 WHERE id = #{id}")
    void incrementLikeCount(@Param("id") Long id);

    @Update("UPDATE user_moment SET like_count = GREATEST(like_count - 1, 0) WHERE id = #{id}")
    void decrementLikeCount(@Param("id") Long id);

    @Update("UPDATE user_moment SET comment_count = comment_count + 1 WHERE id = #{id}")
    void incrementCommentCount(@Param("id") Long id);

    @Update("UPDATE user_moment SET comment_count = GREATEST(comment_count - #{delta}, 0) WHERE id = #{id}")
    void decrementCommentCount(@Param("id") Long id, @Param("delta") int delta);

    @Select("SELECT um.*, u.nickname AS username, u.avatar AS user_avatar " +
            "FROM user_moment um LEFT JOIN user u ON um.user_id = u.id " +
            "WHERE um.user_id = #{userId} " +
            "ORDER BY um.create_time DESC")
    List<UserMoment> selectByUserIdWithUser(@Param("userId") Long userId);
}
