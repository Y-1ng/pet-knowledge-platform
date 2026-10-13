package net.zjitc.controller;

import net.zjitc.common.Result;
import net.zjitc.entity.PetGuide;
import net.zjitc.service.PetGuideService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/guide")
public class GuideController {

    private final PetGuideService petGuideService;

    public GuideController(PetGuideService petGuideService) {
        this.petGuideService = petGuideService;
    }

    @GetMapping("/list")
    public Result<List<PetGuide>> getList(
            @RequestParam(required = false) String type) {
        if (type != null) {
            return Result.success(petGuideService.getByType(type));
        }
        return Result.success(petGuideService.list());
    }

    @GetMapping("/{id}")
    public Result<PetGuide> getDetail(@PathVariable Long id) {
        PetGuide guide = petGuideService.getGuideDetail(id);
        if (guide == null) {
            return Result.error("指南不存在");
        }
        return Result.success(guide);
    }
}
