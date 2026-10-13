package net.zjitc.controller;

import net.zjitc.common.PageResult;
import net.zjitc.common.Result;
import net.zjitc.dto.ArticleCommentDto;
import net.zjitc.service.ArticleCommentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/article-comment")
public class ArticleCommentController {

    private final ArticleCommentService articleCommentService;

    public ArticleCommentController(ArticleCommentService articleCommentService) {
        this.articleCommentService = articleCommentService;
    }

    /**
     * 分页查询一级评论（每项含二级回复列表）
     */
    @GetMapping("/{articleId}")
    public Result<PageResult<ArticleCommentDto>> getComments(
            @PathVariable Long articleId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size) {
        return Result.success(articleCommentService.getPrimaryComments(articleId, page, size));
    }

    /**
     * 查询指定一级评论下的所有二级回复
     */
    @GetMapping("/{articleId}/replies/{parentId}")
    public Result<List<ArticleCommentDto>> getReplies(
            @PathVariable Long articleId,
            @PathVariable Long parentId) {
        return Result.success(articleCommentService.getRepliesByParentId(parentId));
    }

    /**
     * 新增评论（一级或二级）
     * 请求体: { "articleId": 1, "userId": 1, "content": "xxx", "parentId": null, "replyToUserId": null, "replyToUsername": null }
     */
    @PostMapping
    public Result<String> addComment(@RequestBody Map<String, Object> body) {
        Long articleId = Long.valueOf(body.get("articleId").toString());
        Long userId = Long.valueOf(body.get("userId").toString());
        String content = (String) body.get("content");
        Long parentId = body.get("parentId") != null ? Long.valueOf(body.get("parentId").toString()) : null;
        Long replyToUserId = body.get("replyToUserId") != null ? Long.valueOf(body.get("replyToUserId").toString()) : null;
        String replyToUsername = (String) body.get("replyToUsername");

        if (articleCommentService.addComment(articleId, userId, content, parentId, replyToUserId, replyToUsername)) {
            return Result.success("评论成功");
        }
        return Result.error("评论失败");
    }

    /**
     * 删除评论（需校验 userId 为评论者本人）
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteComment(@PathVariable Long id, @RequestParam Long userId) {
        try {
            if (articleCommentService.deleteComment(id, userId)) {
                return Result.success("删除成功");
            }
            return Result.error("删除失败或无权操作");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("删除异常: " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }

    /**
     * 分页查询指定用户的评论列表（携带文章标题）
     */
    @GetMapping("/user/{userId}")
    public Result<PageResult<ArticleCommentDto>> getUserComments(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(articleCommentService.getCommentsByUserId(userId, page, size));
    }
}
