package net.zjitc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import net.zjitc.entity.PetHealthRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PetHealthRecordMapper extends BaseMapper<PetHealthRecord> {

    @Select("SELECT * FROM pet_health_record WHERE pet_id = #{petId} ORDER BY record_date DESC")
    List<PetHealthRecord> selectByPetId(@Param("petId") Long petId);
}
