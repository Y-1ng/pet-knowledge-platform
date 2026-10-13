package net.zjitc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("pet_breed")
public class PetBreed {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long categoryId;
    private String name;
    private String englishName;
    private String origin;
    private String personality;
    private String lifespan;
    private String weight;
    private String height;
    private String coat;
    private String colors;
    private String description;
    private String careTips;
    private String imageUrl;
    private Boolean isFeatured;
    private Integer sortOrder;
    private LocalDateTime createTime;
}
