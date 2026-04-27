package org.example.vo_ai_service.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.vo_ai_service.entity.Notice;
import org.example.vo_ai_service.service.NoticeService;
import org.example.vo_ai_service.mapper.NoticeMapper;
import org.springframework.stereotype.Service;

/**
* @author Given
* @description 针对表【notice(公告表)】的数据库操作Service实现
* @createDate 2026-04-25 12:59:57
*/
@Service
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Notice>
    implements NoticeService{

}




