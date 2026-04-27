package org.example.vo_ai_service.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.vo_ai_service.entity.Feedback;
import org.example.vo_ai_service.service.FeedbackService;
import org.example.vo_ai_service.mapper.FeedbackMapper;
import org.springframework.stereotype.Service;

/**
* @author Given
* @description 针对表【feedback(反馈表)】的数据库操作Service实现
* @createDate 2026-04-25 12:59:57
*/
@Service
public class FeedbackServiceImpl extends ServiceImpl<FeedbackMapper, Feedback>
    implements FeedbackService{

}




