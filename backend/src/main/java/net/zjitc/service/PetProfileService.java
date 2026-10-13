package net.zjitc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import net.zjitc.entity.PetHealthRecord;
import net.zjitc.entity.PetProfile;

import java.util.List;

public interface PetProfileService extends IService<PetProfile> {
    List<PetProfile> getUserPets(Long userId);

    // 健康记录
    List<PetHealthRecord> getHealthRecords(Long petId);
    boolean addHealthRecord(Long petId, String recordType, String recordName, String recordDate, String notes);
}
