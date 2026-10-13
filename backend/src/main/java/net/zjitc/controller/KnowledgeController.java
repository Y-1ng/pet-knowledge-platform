package net.zjitc.controller;

import net.zjitc.common.Result;
import net.zjitc.entity.KnowledgeArticle;
import net.zjitc.entity.KnowledgeCategory;
import net.zjitc.service.KnowledgeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/knowledge")
public class KnowledgeController {

    private final KnowledgeService knowledgeService;

    public KnowledgeController(KnowledgeService knowledgeService) {
        this.knowledgeService = knowledgeService;
    }

    @GetMapping("/categories")
    public Result<List<KnowledgeCategory>> getCategories() {
        return Result.success(knowledgeService.getAllCategories());
    }

    @GetMapping("/list")
    public Result<List<KnowledgeArticle>> getList(
            @RequestParam(required = false) Long categoryId) {
        if (categoryId != null) {
            return Result.success(knowledgeService.getByCategory(categoryId));
        }
        return Result.success(knowledgeService.list());
    }

    @GetMapping("/search")
    public Result<List<KnowledgeArticle>> search(@RequestParam String keyword) {
        return Result.success(knowledgeService.search(keyword));
    }

    @GetMapping("/hot")
    public Result<List<KnowledgeArticle>> getHot() {
        return Result.success(knowledgeService.getHot());
    }

    @GetMapping("/{id}")
    public Result<KnowledgeArticle> getDetail(@PathVariable Long id) {
        KnowledgeArticle article = knowledgeService.getArticleDetail(id);
        if (article == null) {
            return Result.error("文章不存在");
        }
        return Result.success(article);
    }
}
