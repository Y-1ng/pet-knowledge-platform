package net.zjitc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("pet_guide")
public class PetGuide {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String type;
    private String title;
    private String cover;
    private String content;
    private Integer viewCount;
    private LocalDateTime createTime;
}
