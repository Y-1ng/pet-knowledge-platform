package net.zjitc.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import net.zjitc.entity.PetHealthRecord;
import net.zjitc.entity.PetProfile;
import net.zjitc.mapper.PetHealthRecordMapper;
import net.zjitc.mapper.PetProfileMapper;
import net.zjitc.service.PetProfileService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class PetProfileServiceImpl extends ServiceImpl<PetProfileMapper, PetProfile> implements PetProfileService {

    private final PetHealthRecordMapper healthRecordMapper;

    public PetProfileServiceImpl(PetHealthRecordMapper healthRecordMapper) {
        this.healthRecordMapper = healthRecordMapper;
    }

    @Override
    public List<PetProfile> getUserPets(Long userId) {
        return baseMapper.selectByUserId(userId);
    }

    @Override
    public List<PetHealthRecord> getHealthRecords(Long petId) {
        return healthRecordMapper.selectByPetId(petId);
    }

    @Override
    public boolean addHealthRecord(Long petId, String recordType, String recordName, String recordDate, String notes) {
        PetHealthRecord record = new PetHealthRecord();
        record.setPetId(petId);
        record.setRecordType(recordType);
        record.setRecordName(recordName);
        record.setRecordDate(LocalDate.parse(recordDate));
        record.setNotes(notes);
        healthRecordMapper.insert(record);
        return true;
    }
}
