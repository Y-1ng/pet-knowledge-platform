package net.zjitc.controller;

import net.zjitc.common.Result;
import net.zjitc.entity.UserFavorite;
import net.zjitc.service.FavoriteService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/favorite")
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @PostMapping("/toggle")
    public Result<Boolean> toggle(@RequestBody Map<String, String> body) {
        Long userId = Long.valueOf(body.get("userId"));
        String targetType = body.get("targetType");
        Long targetId = Long.valueOf(body.get("targetId"));
        boolean isFavorited = favoriteService.toggleFavorite(userId, targetType, targetId);
        return Result.success(isFavorited);
    }

    @GetMapping("/list")
    public Result<List<UserFavorite>> list(
            @RequestParam Long userId,
            @RequestParam String targetType) {
        return Result.success(favoriteService.getUserFavorites(userId, targetType));
    }

    @GetMapping("/check")
    public Result<Boolean> check(
            @RequestParam Long userId,
            @RequestParam String targetType,
            @RequestParam Long targetId) {
        return Result.success(favoriteService.isFavorited(userId, targetType, targetId));
    }
}
