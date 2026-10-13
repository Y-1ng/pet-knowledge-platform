package net.zjitc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import net.zjitc.entity.PetProfile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PetProfileMapper extends BaseMapper<PetProfile> {

    @Select("SELECT * FROM pet_profile WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<PetProfile> selectByUserId(@Param("userId") Long userId);
}
