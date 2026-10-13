package net.zjitc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import net.zjitc.entity.QaQuestion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface QaQuestionMapper extends BaseMapper<QaQuestion> {

    @Update("UPDATE qa_question SET view_count = view_count + 1 WHERE id = #{id}")
    void incrementViewCount(@Param("id") Long id);

    @Update("UPDATE qa_question SET answer_count = answer_count + 1 WHERE id = #{id}")
    void incrementAnswerCount(@Param("id") Long id);

    @Select("SELECT * FROM qa_question WHERE title LIKE CONCAT('%', #{keyword}, '%') ORDER BY create_time DESC")
    List<QaQuestion> searchByKeyword(@Param("keyword") String keyword);
}
