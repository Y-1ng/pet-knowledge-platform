package net.zjitc.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import net.zjitc.entity.KnowledgeArticle;
import net.zjitc.entity.KnowledgeCategory;
import net.zjitc.mapper.KnowledgeArticleMapper;
import net.zjitc.mapper.KnowledgeCategoryMapper;
import net.zjitc.service.KnowledgeService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KnowledgeServiceImpl extends ServiceImpl<KnowledgeArticleMapper, KnowledgeArticle> implements KnowledgeService {

    private final KnowledgeCategoryMapper categoryMapper;

    public KnowledgeServiceImpl(KnowledgeCategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    @Override
    public List<KnowledgeCategory> getAllCategories() {
        return categoryMapper.selectList(null);
    }

    @Override
    public List<KnowledgeArticle> getByCategory(Long categoryId) {
        return baseMapper.selectByCategoryId(categoryId);
    }

    @Override
    public List<KnowledgeArticle> search(String keyword) {
        return baseMapper.searchByKeyword(keyword);
    }

    @Override
    public List<KnowledgeArticle> getHot() {
        return baseMapper.selectHot();
    }

    @Override
    public KnowledgeArticle getArticleDetail(Long id) {
        baseMapper.incrementViewCount(id);
        return baseMapper.selectByIdWithCommentCount(id);
    }

    @Override
    public List<KnowledgeArticle> list() {
        return baseMapper.selectAllWithCommentCount();
    }
}
