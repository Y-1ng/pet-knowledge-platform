package net.zjitc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import net.zjitc.entity.UserFavorite;

import java.util.List;

public interface FavoriteService extends IService<UserFavorite> {
    boolean toggleFavorite(Long userId, String targetType, Long targetId);
    List<UserFavorite> getUserFavorites(Long userId, String targetType);
    boolean isFavorited(Long userId, String targetType, Long targetId);
}
