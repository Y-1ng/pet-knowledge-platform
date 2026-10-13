package net.zjitc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import net.zjitc.entity.PetBreed;

import java.util.List;

public interface PetBreedService extends IService<PetBreed> {
    List<PetBreed> getByCategory(Long categoryId);
    List<PetBreed> getFeatured();
    PetBreed getBreedDetail(Long id);
}
