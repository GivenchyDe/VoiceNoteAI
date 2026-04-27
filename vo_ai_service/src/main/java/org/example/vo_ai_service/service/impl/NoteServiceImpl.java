package org.example.vo_ai_service.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.vo_ai_service.entity.Note;
import org.example.vo_ai_service.service.NoteService;
import org.example.vo_ai_service.mapper.NoteMapper;
import org.springframework.stereotype.Service;

/**
* @author Given
* @description 针对表【note(笔记表)】的数据库操作Service实现
* @createDate 2026-04-25 12:59:57
*/
@Service
public class NoteServiceImpl extends ServiceImpl<NoteMapper, Note>
    implements NoteService{

}




