package net.zjitc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import net.zjitc.entity.PetGuide;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface PetGuideMapper extends BaseMapper<PetGuide> {

    @Select("SELECT * FROM pet_guide WHERE type = #{type} ORDER BY create_time DESC")
    List<PetGuide> selectByType(@Param("type") String type);

    @Update("UPDATE pet_guide SET view_count = view_count + 1 WHERE id = #{id}")
    void incrementViewCount(@Param("id") Long id);
}
