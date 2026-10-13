package net.zjitc.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import net.zjitc.entity.UserFavorite;
import net.zjitc.mapper.UserFavoriteMapper;
import net.zjitc.service.FavoriteService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FavoriteServiceImpl extends ServiceImpl<UserFavoriteMapper, UserFavorite> implements FavoriteService {

    @Override
    public boolean toggleFavorite(Long userId, String targetType, Long targetId) {
        int count = baseMapper.countExist(userId, targetType, targetId);
        if (count > 0) {
            // 已收藏 → 取消
            baseMapper.delete(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<UserFavorite>()
                    .eq(UserFavorite::getUserId, userId)
                    .eq(UserFavorite::getTargetType, targetType)
                    .eq(UserFavorite::getTargetId, targetId));
            return false; // 取消收藏
        } else {
            // 未收藏 → 添加
            UserFavorite fav = new UserFavorite();
            fav.setUserId(userId);
            fav.setTargetType(targetType);
            fav.setTargetId(targetId);
            save(fav);
            return true; // 添加收藏
        }
    }

    @Override
    public List<UserFavorite> getUserFavorites(Long userId, String targetType) {
        return baseMapper.selectByUserAndType(userId, targetType);
    }

    @Override
    public boolean isFavorited(Long userId, String targetType, Long targetId) {
        return baseMapper.countExist(userId, targetType, targetId) > 0;
    }
}
