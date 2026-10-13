package net.zjitc.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import net.zjitc.entity.PetGuide;
import net.zjitc.mapper.PetGuideMapper;
import net.zjitc.service.PetGuideService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PetGuideServiceImpl extends ServiceImpl<PetGuideMapper, PetGuide> implements PetGuideService {

    @Override
    public List<PetGuide> getByType(String type) {
        return baseMapper.selectByType(type);
    }

    @Override
    public PetGuide getGuideDetail(Long id) {
        baseMapper.incrementViewCount(id);
        return getById(id);
    }
}
