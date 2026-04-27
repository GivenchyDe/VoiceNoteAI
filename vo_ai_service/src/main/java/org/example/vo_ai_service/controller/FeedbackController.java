package org.example.vo_ai_service.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.example.vo_ai_service.comm.Result;
import org.example.vo_ai_service.entity.Feedback;
import org.example.vo_ai_service.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "反馈模块")
@RestController
@RequestMapping("/feedback")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @Operation(summary = "提交反馈")
    @PostMapping("/add")
    public Result<String> add(@RequestBody Feedback feedback, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        feedback.setUserId(userId);
        feedback.setStatus(0);
        feedbackService.save(feedback);
        return Result.success("提交成功");
    }

    @Operation(summary = "我的反馈列表")
    @GetMapping("/list")
    public Result<Page<Feedback>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Page<Feedback> p = new Page<>(page, size);
        LambdaQueryWrapper<Feedback> qw = new LambdaQueryWrapper<>();
        qw.eq(Feedback::getUserId, userId);
        qw.orderByDesc(Feedback::getCreateTime);
        return Result.success(feedbackService.page(p, qw));
    }
}
