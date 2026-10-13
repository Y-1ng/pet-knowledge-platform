package net.zjitc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("pet_health_record")
public class PetHealthRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long petId;
    private String recordType;
    private String recordName;
    private LocalDate recordDate;
    private String notes;
    private LocalDateTime createTime;
}
