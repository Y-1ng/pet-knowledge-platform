package net.zjitc.controller;

import net.zjitc.common.Result;
import net.zjitc.entity.PetHealthRecord;
import net.zjitc.entity.PetProfile;
import net.zjitc.service.PetProfileService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pet")
public class PetProfileController {

    private final PetProfileService petProfileService;

    public PetProfileController(PetProfileService petProfileService) {
        this.petProfileService = petProfileService;
    }

    // ===== 宠物档案 =====
    @GetMapping("/list")
    public Result<List<PetProfile>> getList(@RequestParam Long userId) {
        return Result.success(petProfileService.getUserPets(userId));
    }

    @GetMapping("/{id}")
    public Result<PetProfile> getDetail(@PathVariable Long id) {
        PetProfile pet = petProfileService.getById(id);
        if (pet == null) {
            return Result.error("宠物不存在");
        }
        return Result.success(pet);
    }

    @PostMapping("/save")
    public Result<String> save(@RequestBody PetProfile petProfile) {
        petProfileService.saveOrUpdate(petProfile);
        return Result.success("保存成功");
    }

    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        petProfileService.removeById(id);
        return Result.success("删除成功");
    }

    // ===== 健康记录 =====
    @GetMapping("/health/{petId}")
    public Result<List<PetHealthRecord>> getHealthRecords(@PathVariable Long petId) {
        return Result.success(petProfileService.getHealthRecords(petId));
    }

    @PostMapping("/health")
    public Result<String> addHealthRecord(@RequestBody Map<String, String> body) {
        Long petId = Long.valueOf(body.get("petId"));
        String recordType = body.get("recordType");
        String recordName = body.get("recordName");
        String recordDate = body.get("recordDate");
        String notes = body.get("notes");
        if (petProfileService.addHealthRecord(petId, recordType, recordName, recordDate, notes)) {
            return Result.success("记录成功");
        }
        return Result.error("记录失败");
    }
}
