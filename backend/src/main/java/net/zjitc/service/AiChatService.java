package net.zjitc.service;

import net.zjitc.entity.AiChatMessage;

import java.util.List;

public interface AiChatService {
    List<AiChatMessage> getHistory(String sessionId);
    String chat(String sessionId, String message);
    void clearHistory(String sessionId);
}
