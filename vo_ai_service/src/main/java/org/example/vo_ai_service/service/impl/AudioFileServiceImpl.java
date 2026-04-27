package org.example.vo_ai_service.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.vo_ai_service.entity.AudioFile;
import org.example.vo_ai_service.service.AudioFileService;
import org.example.vo_ai_service.mapper.AudioFileMapper;
import org.springframework.stereotype.Service;

/**
* @author Given
* @description 针对表【audio_file(音频文件表)】的数据库操作Service实现
* @createDate 2026-04-25 12:59:57
*/
@Service
public class AudioFileServiceImpl extends ServiceImpl<AudioFileMapper, AudioFile>
    implements AudioFileService{

}




