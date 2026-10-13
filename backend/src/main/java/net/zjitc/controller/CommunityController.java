package net.zjitc.controller;

import net.zjitc.common.PageResult;
import net.zjitc.common.Result;
import net.zjitc.entity.CommunityComment;
import net.zjitc.entity.CommunityPost;
import net.zjitc.service.CommunityService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/community")
public class CommunityController {

    private final CommunityService communityService;

    public CommunityController(CommunityService communityService) {
        this.communityService = communityService;
    }

    // ===== 帖子 =====
    @GetMapping("/posts")
    public Result<List<CommunityPost>> getPosts() {
        return Result.success(communityService.getPostList());
    }

    @GetMapping("/post/{id}")
    public Result<CommunityPost> getPost(@PathVariable Long id) {
        return Result.success(communityService.getPostDetail(id));
    }

    @PostMapping("/post")
    public Result<String> createPost(@RequestBody Map<String, String> body) {
        Long userId = Long.valueOf(body.get("userId"));
        String title = body.get("title");
        String content = body.get("content");
        String images = body.get("images");
        if (communityService.createPost(userId, title, content, images)) {
            return Result.success("发布成功");
        }
        return Result.error("发布失败");
    }

    @DeleteMapping("/post/{id}")
    public Result<String> deletePost(@PathVariable Long id, @RequestParam Long userId) {
        if (communityService.deletePost(id, userId)) {
            return Result.success("删除成功");
        }
        return Result.error("删除失败，无权操作");
    }

    // ===== 评论 =====
    @GetMapping("/comments/{postId}")
    public Result<PageResult<CommunityComment>> getComments(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size) {
        return Result.success(communityService.getPrimaryComments(postId, page, size));
    }

    @GetMapping("/comments/{postId}/replies/{parentId}")
    public Result<List<CommunityComment>> getReplies(@PathVariable Long postId, @PathVariable Long parentId) {
        return Result.success(communityService.getRepliesByParentId(parentId));
    }

    @PostMapping("/comment")
    public Result<String> addComment(@RequestBody Map<String, String> body) {
        Long postId = Long.valueOf(body.get("postId"));
        Long userId = Long.valueOf(body.get("userId"));
        String content = body.get("content");
        Long parentId = body.containsKey("parentId") && body.get("parentId") != null ? Long.valueOf(body.get("parentId")) : null;
        Long replyToUserId = body.containsKey("replyToUserId") && body.get("replyToUserId") != null ? Long.valueOf(body.get("replyToUserId")) : null;
        String replyToUsername = body.get("replyToUsername");
        if (communityService.addComment(postId, userId, content, parentId, replyToUserId, replyToUsername)) {
            return Result.success("评论成功");
        }
        return Result.error("评论失败");
    }

    @DeleteMapping("/comment/{id}")
    public Result<String> deleteComment(@PathVariable Long id, @RequestParam Long userId) {
        try {
            if (communityService.deleteComment(id, userId)) {
                return Result.success("删除成功");
            }
            return Result.error("删除失败或无权操作");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("删除异常: " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }

    // ===== 点赞 =====
    @PostMapping("/like")
    public Result<Boolean> toggleLike(@RequestBody Map<String, String> body) {
        Long postId = Long.valueOf(body.get("postId"));
        Long userId = Long.valueOf(body.get("userId"));
        boolean liked = communityService.toggleLike(postId, userId);
        return Result.success(liked);
    }

    @GetMapping("/like/check")
    public Result<Boolean> checkLike(@RequestParam Long postId, @RequestParam Long userId) {
        return Result.success(communityService.isLiked(postId, userId));
    }
}
