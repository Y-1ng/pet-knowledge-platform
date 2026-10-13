package net.zjitc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import net.zjitc.entity.AiChatMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AiChatMessageMapper extends BaseMapper<AiChatMessage> {

    @Select("SELECT * FROM ai_chat_message WHERE session_id = #{sessionId} ORDER BY create_time ASC")
    List<AiChatMessage> selectBySessionId(@Param("sessionId") String sessionId);

    @Select("DELETE FROM ai_chat_message WHERE session_id = #{sessionId}")
    void deleteBySessionId(@Param("sessionId") String sessionId);
}
