package net.zjitc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import net.zjitc.entity.MomentLike;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface MomentLikeMapper extends BaseMapper<MomentLike> {

    @Select("SELECT COUNT(*) FROM moment_like WHERE moment_id = #{momentId} AND user_id = #{userId}")
    int countExist(@Param("momentId") Long momentId, @Param("userId") Long userId);
}
