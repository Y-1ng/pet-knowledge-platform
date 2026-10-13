package net.zjitc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import net.zjitc.entity.QaAnswer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface QaAnswerMapper extends BaseMapper<QaAnswer> {

    @Select("SELECT * FROM qa_answer WHERE question_id = #{questionId} ORDER BY is_accepted DESC, create_time ASC")
    List<QaAnswer> selectByQuestionId(@Param("questionId") Long questionId);
}
