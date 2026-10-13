package net.zjitc.controller;

import net.zjitc.common.Result;
import net.zjitc.entity.QaAnswer;
import net.zjitc.entity.QaQuestion;
import net.zjitc.service.QaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/qa")
public class QaController {

    private final QaService qaService;

    public QaController(QaService qaService) {
        this.qaService = qaService;
    }

    // ===== 问题 =====
    @GetMapping("/questions")
    public Result<List<QaQuestion>> getQuestions() {
        return Result.success(qaService.getQuestionList());
    }

    @GetMapping("/question/{id}")
    public Result<QaQuestion> getQuestion(@PathVariable Long id) {
        return Result.success(qaService.getQuestionDetail(id));
    }

    @PostMapping("/question")
    public Result<String> createQuestion(@RequestBody Map<String, String> body) {
        Long userId = Long.valueOf(body.get("userId"));
        String title = body.get("title");
        String description = body.get("description");
        if (qaService.createQuestion(userId, title, description)) {
            return Result.success("提问成功");
        }
        return Result.error("提问失败");
    }

    @GetMapping("/search")
    public Result<List<QaQuestion>> search(@RequestParam String keyword) {
        return Result.success(qaService.searchQuestions(keyword));
    }

    // ===== 回答 =====
    @GetMapping("/answers/{questionId}")
    public Result<List<QaAnswer>> getAnswers(@PathVariable Long questionId) {
        return Result.success(qaService.getAnswers(questionId));
    }

    @PostMapping("/answer")
    public Result<String> addAnswer(@RequestBody Map<String, String> body) {
        Long questionId = Long.valueOf(body.get("questionId"));
        Long userId = Long.valueOf(body.get("userId"));
        String content = body.get("content");
        if (qaService.addAnswer(questionId, userId, content)) {
            return Result.success("回答成功");
        }
        return Result.error("回答失败");
    }

    @PostMapping("/answer/accept")
    public Result<String> acceptAnswer(@RequestBody Map<String, String> body) {
        Long answerId = Long.valueOf(body.get("answerId"));
        Long questionId = Long.valueOf(body.get("questionId"));
        Long userId = Long.valueOf(body.get("userId"));
        if (qaService.acceptAnswer(answerId, questionId, userId)) {
            return Result.success("已采纳");
        }
        return Result.error("采纳失败，无权操作");
    }
}
