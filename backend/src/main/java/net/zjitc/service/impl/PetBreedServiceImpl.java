package net.zjitc.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import net.zjitc.entity.PetBreed;
import net.zjitc.mapper.PetBreedMapper;
import net.zjitc.service.PetBreedService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PetBreedServiceImpl extends ServiceImpl<PetBreedMapper, PetBreed> implements PetBreedService {

    @Override
    public List<PetBreed> getByCategory(Long categoryId) {
        return baseMapper.selectByCategoryId(categoryId);
    }

    @Override
    public List<PetBreed> getFeatured() {
        return baseMapper.selectFeatured();
    }

    @Override
    public PetBreed getBreedDetail(Long id) {
        return getById(id);
    }
}
