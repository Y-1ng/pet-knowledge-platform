package net.zjitc.controller;

import net.zjitc.common.Result;
import net.zjitc.entity.PetBreed;
import net.zjitc.service.PetBreedService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/breed")
public class PetBreedController {

    private final PetBreedService petBreedService;

    public PetBreedController(PetBreedService petBreedService) {
        this.petBreedService = petBreedService;
    }

    @GetMapping("/list/{categoryId}")
    public Result<List<PetBreed>> getByCategory(@PathVariable Long categoryId) {
        return Result.success(petBreedService.getByCategory(categoryId));
    }

    @GetMapping("/featured")
    public Result<List<PetBreed>> getFeatured() {
        return Result.success(petBreedService.getFeatured());
    }

    @GetMapping("/{id}")
    public Result<PetBreed> getDetail(@PathVariable Long id) {
        PetBreed breed = petBreedService.getBreedDetail(id);
        if (breed == null) {
            return Result.error("品种不存在");
        }
        return Result.success(breed);
    }
}
