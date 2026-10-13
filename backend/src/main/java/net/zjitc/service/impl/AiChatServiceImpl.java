package net.zjitc.service.impl;

import net.zjitc.entity.AiChatMessage;
import net.zjitc.mapper.AiChatMessageMapper;
import net.zjitc.service.AiChatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.messages.*;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AiChatServiceImpl implements AiChatService {

    private static final Logger log = LoggerFactory.getLogger(AiChatServiceImpl.class);

    private final OpenAiChatModel chatModel;
    private final AiChatMessageMapper messageMapper;

    public AiChatServiceImpl(ObjectProvider<OpenAiChatModel> chatModelProvider, AiChatMessageMapper messageMapper) {
        this.chatModel = chatModelProvider.getIfAvailable();
        this.messageMapper = messageMapper;
    }

    private static final String SYSTEM_PROMPT =
            "你是\"宠知\"平台的AI宠物顾问，名字叫\"小宠\"。\n\n" +
            "你的职责：\n" +
            "- 回答用户关于宠物饲养、健康、行为等方面的科普问题\n" +
            "- 用通俗易懂的语言解释宠物知识\n" +
            "- 在遇到疑似紧急医疗情况时，建议用户及时就医\n\n" +
            "你的沟通风格：\n" +
            "- 亲切、耐心、有爱心\n" +
            "- 用 🐾 等表情增加亲和力\n" +
            "- 回答简洁明了，重点突出\n\n" +
            "注意事项：\n" +
            "- 遇到疑似中毒、严重外伤等紧急情况，立即建议去宠物医院\n" +
            "- 不提供替代专业兽医的建议\n" +
            "- 不确定的问题要诚实说明，不编造信息";

    @Override
    public List<AiChatMessage> getHistory(String sessionId) {
        return messageMapper.selectBySessionId(sessionId);
    }

    @Override
    public String chat(String sessionId, String userInput) {
        // 1. 保存用户消息
        AiChatMessage userMsg = new AiChatMessage();
        userMsg.setSessionId(sessionId);
        userMsg.setRole("user");
        userMsg.setContent(userInput);
        messageMapper.insert(userMsg);

        // 2. 获取历史消息
        List<AiChatMessage> history = messageMapper.selectBySessionId(sessionId);

        // 3. 拼装消息列表
        List<Message> messages = new ArrayList<>();
        messages.add(new SystemMessage(SYSTEM_PROMPT));
        for (AiChatMessage msg : history) {
            if ("user".equals(msg.getRole())) {
                messages.add(new UserMessage(msg.getContent()));
            } else {
                messages.add(new AssistantMessage(msg.getContent()));
            }
        }

        // 4. 调用AI
        String aiContent;
        try {
            log.info("调用AI接口，sessionId={}, 消息数={}", sessionId, messages.size());
            ChatResponse response = chatModel.call(new Prompt(messages));
            aiContent = response.getResult().getOutput().getContent();
            log.info("AI回复成功，长度={}", aiContent.length());
        } catch (Exception e) {
            log.error("AI调用失败: {}", e.getMessage(), e);
            aiContent = "抱歉，小宠暂时无法回复，请稍后再试。🐾\n错误信息：" + e.getMessage();
        }

        // 5. 保存AI回复
        AiChatMessage aiMsg = new AiChatMessage();
        aiMsg.setSessionId(sessionId);
        aiMsg.setRole("assistant");
        aiMsg.setContent(aiContent);
        messageMapper.insert(aiMsg);

        // 6. 返回
        return aiContent;
    }

    @Override
    public void clearHistory(String sessionId) {
        messageMapper.deleteBySessionId(sessionId);
    }
}
