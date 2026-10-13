package net.zjitc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import net.zjitc.entity.PetBreed;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PetBreedMapper extends BaseMapper<PetBreed> {

    @Select("SELECT * FROM pet_breed WHERE category_id = #{categoryId} ORDER BY sort_order ASC, id ASC")
    List<PetBreed> selectByCategoryId(@Param("categoryId") Long categoryId);

    @Select("SELECT * FROM pet_breed WHERE is_featured = 1 ORDER BY sort_order ASC, id ASC LIMIT 8")
    List<PetBreed> selectFeatured();
}
