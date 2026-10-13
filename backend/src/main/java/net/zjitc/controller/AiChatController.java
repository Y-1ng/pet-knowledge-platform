package net.zjitc.controller;

import net.zjitc.common.Result;
import net.zjitc.entity.AiChatMessage;
import net.zjitc.service.AiChatService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AiChatController {

    private final AiChatService aiChatService;

    public AiChatController(AiChatService aiChatService) {
        this.aiChatService = aiChatService;
    }

    @GetMapping("/history")
    public Result<List<AiChatMessage>> getHistory(@RequestParam String sessionId) {
        List<AiChatMessage> history = aiChatService.getHistory(sessionId);
        return Result.success(history);
    }

    @PostMapping("/chat")
    public Result<String> chat(@RequestBody Map<String, String> body) {
        String sessionId = body.get("sessionId");
        String message = body.get("message");
        if (sessionId == null || message == null) {
            return Result.error("参数不能为空");
        }
        String reply = aiChatService.chat(sessionId, message);
        return Result.success(reply);
    }

    @DeleteMapping("/history")
    public Result<String> clearHistory(@RequestParam String sessionId) {
        aiChatService.clearHistory(sessionId);
        return Result.success("已清空");
    }
}
