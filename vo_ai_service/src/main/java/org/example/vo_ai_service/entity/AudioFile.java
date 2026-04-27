package org.example.vo_ai_service.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 音频文件表
 * @TableName audio_file
 */
@TableName(value ="audio_file")
@Data
public class AudioFile {
    /**
     * 主键，自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 关联用户ID
     */
    private Long userId;

    /**
     * 音频文件名
     */
    private String fileName;

    /**
     * 音频文件存储路径
     */
    private String filePath;

    /**
     * 文件大小（字节）
     */
    private Long fileSize;

    /**
     * 文件格式（mp3/wav等）
     */
    private String fileFormat;

    /**
     * 音频时长（秒）
     */
    private Integer duration;

    /**
     * 创建时间
     */
    private Date createTime;
}