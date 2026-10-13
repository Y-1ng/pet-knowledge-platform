package net.zjitc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import net.zjitc.entity.KnowledgeArticle;
import net.zjitc.entity.KnowledgeCategory;

import java.util.List;

public interface KnowledgeService extends IService<KnowledgeArticle> {
    List<KnowledgeCategory> getAllCategories();
    List<KnowledgeArticle> getByCategory(Long categoryId);
    List<KnowledgeArticle> search(String keyword);
    List<KnowledgeArticle> getHot();
    KnowledgeArticle getArticleDetail(Long id);
}
