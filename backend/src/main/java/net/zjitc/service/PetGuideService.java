package net.zjitc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import net.zjitc.entity.PetGuide;

import java.util.List;

public interface PetGuideService extends IService<PetGuide> {
    List<PetGuide> getByType(String type);
    PetGuide getGuideDetail(Long id);
}
