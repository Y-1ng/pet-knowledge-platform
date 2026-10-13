package net.zjitc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user_moment")
public class UserMoment {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String content;
    private String images;
    private Integer likeCount;
    private Integer commentCount;
    private LocalDateTime createTime;

    // 非DB字段
    private String username;
    private String userAvatar;
    private Boolean isLiked;
}
