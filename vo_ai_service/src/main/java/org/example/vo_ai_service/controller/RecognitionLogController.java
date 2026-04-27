package org.example.vo_ai_service.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.example.vo_ai_service.comm.Result;
import org.example.vo_ai_service.entity.RecognitionLog;
import org.example.vo_ai_service.service.RecognitionLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "识别日志模块")
@RestController
@RequestMapping("/log")
public class RecognitionLogController {

    @Autowired
    private RecognitionLogService recognitionLogService;

    @Operation(summary = "我的识别记录")
    @GetMapping("/list")
    public Result<Page<RecognitionLog>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Page<RecognitionLog> p = new Page<>(page, size);
        LambdaQueryWrapper<RecognitionLog> qw = new LambdaQueryWrapper<>();
        qw.eq(RecognitionLog::getUserId, userId);
        qw.orderByDesc(RecognitionLog::getCreateTime);
        return Result.success(recognitionLogService.page(p, qw));
    }
}
