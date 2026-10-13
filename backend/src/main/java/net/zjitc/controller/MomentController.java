package net.zjitc.controller;

import net.zjitc.common.PageResult;
import net.zjitc.common.Result;
import net.zjitc.entity.MomentComment;
import net.zjitc.entity.UserMoment;
import net.zjitc.service.MomentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/moment")
public class MomentController {

    private final MomentService momentService;

    public MomentController(MomentService momentService) {
        this.momentService = momentService;
    }

    @GetMapping("/list")
    public Result<List<UserMoment>> getList() {
        return Result.success(momentService.getMoments());
    }

    @GetMapping("/list/{userId}")
    public Result<List<UserMoment>> getByUserId(@PathVariable Long userId) {
        return Result.success(momentService.getMomentsByUserId(userId));
    }

    @PostMapping("/create")
    public Result<String> create(@RequestBody Map<String, String> body) {
        Long userId = Long.valueOf(body.get("userId"));
        String content = body.get("content");
        String images = body.get("images");
        if (momentService.createMoment(userId, content, images)) {
            return Result.success("发布成功");
        }
        return Result.error("发布失败");
    }

    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id, @RequestParam Long userId) {
        if (momentService.deleteMoment(id, userId)) {
            return Result.success("删除成功");
        }
        return Result.error("删除失败");
    }

    // 点赞
    @PostMapping("/like")
    public Result<Boolean> toggleLike(@RequestBody Map<String, String> body) {
        Long momentId = Long.valueOf(body.get("momentId"));
        Long userId = Long.valueOf(body.get("userId"));
        return Result.success(momentService.toggleLike(momentId, userId));
    }

    @GetMapping("/like/check")
    public Result<Boolean> checkLike(@RequestParam Long momentId, @RequestParam Long userId) {
        return Result.success(momentService.isLiked(momentId, userId));
    }

    // 评论（分页）
    @GetMapping("/comments/{momentId}")
    public Result<PageResult<MomentComment>> getComments(
            @PathVariable Long momentId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size) {
        return Result.success(momentService.getPrimaryComments(momentId, page, size));
    }

    @GetMapping("/comments/{momentId}/replies/{parentId}")
    public Result<List<MomentComment>> getReplies(@PathVariable Long momentId, @PathVariable Long parentId) {
        return Result.success(momentService.getRepliesByParentId(parentId));
    }

    @PostMapping("/comment")
    public Result<String> addComment(@RequestBody Map<String, String> body) {
        Long momentId = Long.valueOf(body.get("momentId"));
        Long userId = Long.valueOf(body.get("userId"));
        String content = body.get("content");
        Long parentId = body.containsKey("parentId") && body.get("parentId") != null ? Long.valueOf(body.get("parentId")) : null;
        Long replyToUserId = body.containsKey("replyToUserId") && body.get("replyToUserId") != null ? Long.valueOf(body.get("replyToUserId")) : null;
        String replyToUsername = body.get("replyToUsername");
        if (momentService.addComment(momentId, userId, content, parentId, replyToUserId, replyToUsername)) {
            return Result.success("评论成功");
        }
        return Result.error("评论失败");
    }

    @DeleteMapping("/comment/{id}")
    public Result<String> deleteComment(@PathVariable Long id, @RequestParam Long userId) {
        try {
            if (momentService.deleteComment(id, userId)) {
                return Result.success("删除成功");
            }
            return Result.error("删除失败或无权操作");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("删除异常: " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }
}
