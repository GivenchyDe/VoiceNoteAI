package org.example.vo_ai_service.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.example.vo_ai_service.comm.Result;
import org.example.vo_ai_service.entity.Notice;
import org.example.vo_ai_service.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "公告模块")
@RestController
@RequestMapping("/notice")
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    @Operation(summary = "公告列表（用户端）")
    @GetMapping("/list")
    public Result<List<Notice>> list() {
        LambdaQueryWrapper<Notice> qw = new LambdaQueryWrapper<>();
        qw.orderByDesc(Notice::getIsTop).orderByDesc(Notice::getCreateTime);
        return Result.success(noticeService.list(qw));
    }
}
