package net.zjitc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import net.zjitc.entity.MomentComment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MomentCommentMapper extends BaseMapper<MomentComment> {

    @Select("SELECT mc.*, u.nickname AS username, u.avatar AS user_avatar " +
            "FROM moment_comment mc LEFT JOIN user u ON mc.user_id = u.id " +
            "WHERE mc.moment_id = #{momentId} AND mc.parent_id IS NULL AND (mc.is_deleted IS NULL OR mc.is_deleted = 0) " +
            "ORDER BY mc.create_time DESC")
    IPage<MomentComment> selectPrimaryByMomentId(IPage<MomentComment> page, @Param("momentId") Long momentId);

    @Select("SELECT mc.*, u.nickname AS username, u.avatar AS user_avatar " +
            "FROM moment_comment mc LEFT JOIN user u ON mc.user_id = u.id " +
            "WHERE mc.parent_id = #{parentId} AND (mc.is_deleted IS NULL OR mc.is_deleted = 0) " +
            "ORDER BY mc.create_time ASC")
    List<MomentComment> selectRepliesByParentId(@Param("parentId") Long parentId);
}
