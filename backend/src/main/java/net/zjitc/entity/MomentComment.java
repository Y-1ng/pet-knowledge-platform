package net.zjitc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("moment_comment")
public class MomentComment {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long momentId;
    private Long userId;
    private String content;
    private Long parentId;
    private Long replyToUserId;
    private String replyToUsername;
    private LocalDateTime createTime;
    private Integer isDeleted;

    // 非DB字段
    @TableField(exist = false)
    private String username;
    @TableField(exist = false)
    private String userAvatar;

    // 二级评论列表（非DB字段，由服务端组装）
    @TableField(exist = false)
    private List<MomentComment> replies;
}
