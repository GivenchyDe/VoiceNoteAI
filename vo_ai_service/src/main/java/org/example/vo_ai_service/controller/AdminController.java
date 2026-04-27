package org.example.vo_ai_service.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.example.vo_ai_service.comm.JwtUtil;
import org.example.vo_ai_service.comm.MD5Util;
import org.example.vo_ai_service.comm.Result;
import org.example.vo_ai_service.entity.Admin;
import org.example.vo_ai_service.entity.AudioFile;
import org.example.vo_ai_service.entity.Feedback;
import org.example.vo_ai_service.entity.Notice;
import org.example.vo_ai_service.entity.Note;
import org.example.vo_ai_service.entity.RecognitionLog;
import org.example.vo_ai_service.entity.User;
import org.example.vo_ai_service.service.AdminService;
import org.example.vo_ai_service.service.AudioFileService;
import org.example.vo_ai_service.service.FeedbackService;
import org.example.vo_ai_service.service.NoticeService;
import org.example.vo_ai_service.service.NoteService;
import org.example.vo_ai_service.service.RecognitionLogService;
import org.example.vo_ai_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "管理员模块")
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;
    @Autowired
    private UserService userService;
    @Autowired
    private NoteService noteService;
    @Autowired
    private AudioFileService audioFileService;
    @Autowired
    private RecognitionLogService recognitionLogService;
    @Autowired
    private FeedbackService feedbackService;
    @Autowired
    private NoticeService noticeService;

    @Operation(summary = "管理员登录")
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");
        LambdaQueryWrapper<Admin> qw = new LambdaQueryWrapper<>();
        qw.eq(Admin::getUsername, username);
        Admin admin = adminService.getOne(qw);
        if (admin == null || !admin.getPassword().equals(MD5Util.encrypt(password))) {
            return Result.error(400, "用户名或密码错误");
        }
        if (admin.getStatus() == 0) {
            return Result.error(400, "账号已被禁用");
        }
        String token = JwtUtil.generateToken(admin.getId(), admin.getUsername(), "admin");
        Map<String, Object> map = new HashMap<>();
        map.put("token", token);
        map.put("admin", admin);
        return Result.success(map);
    }

    @Operation(summary = "仪表盘统计数据")
    @GetMapping("/dashboard")
    public Result<Map<String, Object>> dashboard() {
        Map<String, Object> map = new HashMap<>();
        long userCount = userService.count();
        long noteCount = noteService.count();
        long audioCount = audioFileService.count();
        long logCount = recognitionLogService.count();
        // 平均识别准确率
        List<RecognitionLog> logs = recognitionLogService.list();
        double avgAccuracy = logs.stream()
                .filter(l -> l.getAccuracy() != null)
                .mapToDouble(l -> l.getAccuracy().doubleValue())
                .average().orElse(0.0);
        map.put("userCount", userCount);
        map.put("noteCount", noteCount);
        map.put("audioCount", audioCount);
        map.put("logCount", logCount);
        map.put("avgAccuracy", new BigDecimal(avgAccuracy).setScale(2, BigDecimal.ROUND_HALF_UP));
        return Result.success(map);
    }

    // ---------- 用户管理 ----------
    @Operation(summary = "用户列表")
    @GetMapping("/user/list")
    public Result<Page<User>> userList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword) {
        Page<User> p = new Page<>(page, size);
        LambdaQueryWrapper<User> qw = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            qw.like(User::getUsername, keyword).or().like(User::getPhone, keyword);
        }
        qw.orderByAsc(User::getId);
        return Result.success(userService.page(p, qw));
    }

    @Operation(summary = "禁用/启用用户")
    @PutMapping("/user/status/{id}")
    public Result<String> userStatus(@PathVariable Long id, @RequestParam Integer status) {
        User user = new User();
        user.setId(id);
        user.setStatus(status);
        userService.updateById(user);
        return Result.success("操作成功");
    }

    // ---------- 反馈管理 ----------
    @Operation(summary = "反馈列表")
    @GetMapping("/feedback/list")
    public Result<Page<Feedback>> feedbackList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status) {
        Page<Feedback> p = new Page<>(page, size);
        LambdaQueryWrapper<Feedback> qw = new LambdaQueryWrapper<>();
        if (status != null) {
            qw.eq(Feedback::getStatus, status);
        }
        qw.orderByDesc(Feedback::getCreateTime);
        Page<Feedback> result = feedbackService.page(p, qw);
        for (Feedback fb : result.getRecords()) {
            User u = userService.getById(fb.getUserId());
            if (u != null) {
                fb.setUsername(u.getUsername());
            }
        }
        return Result.success(result);
    }

    @Operation(summary = "回复反馈")
    @PutMapping("/feedback/reply/{id}")
    public Result<String> feedbackReply(@PathVariable Long id, @RequestBody Map<String, String> params) {
        Feedback fb = new Feedback();
        fb.setId(id);
        String reply = params.get("reply");
        if (reply == null) reply = params.get("replyContent");
        fb.setReply(reply);
        fb.setStatus(1); // 已回复
        feedbackService.updateById(fb);
        return Result.success("回复成功");
    }

    // ---------- 公告管理 ----------
    @Operation(summary = "公告列表（管理端）")
    @GetMapping("/notice/list")
    public Result<Page<Notice>> noticeList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<Notice> p = new Page<>(page, size);
        LambdaQueryWrapper<Notice> qw = new LambdaQueryWrapper<>();
        qw.orderByDesc(Notice::getIsTop).orderByDesc(Notice::getCreateTime);
        return Result.success(noticeService.page(p, qw));
    }

    @Operation(summary = "发布公告")
    @PostMapping("/notice/add")
    public Result<String> noticeAdd(@RequestBody Notice notice, HttpServletRequest request) {
        Long adminId = (Long) request.getAttribute("userId");
        notice.setAdminId(adminId);
        noticeService.save(notice);
        return Result.success("发布成功");
    }

    @Operation(summary = "更新公告")
    @PutMapping("/notice/update")
    public Result<String> noticeUpdate(@RequestBody Notice notice) {
        noticeService.updateById(notice);
        return Result.success("更新成功");
    }

    @Operation(summary = "删除公告")
    @DeleteMapping("/notice/delete/{id}")
    public Result<String> noticeDelete(@PathVariable Long id) {
        noticeService.removeById(id);
        return Result.success("删除成功");
    }

    // ---------- 管理员账号管理 ----------
    @Operation(summary = "管理员账号列表")
    @GetMapping("/account/list")
    public Result<Page<Admin>> accountList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<Admin> p = new Page<>(page, size);
        LambdaQueryWrapper<Admin> qw = new LambdaQueryWrapper<>();
        qw.orderByAsc(Admin::getId);
        return Result.success(adminService.page(p, qw));
    }

    @Operation(summary = "新增管理员")
    @PostMapping("/account/add")
    public Result<String> accountAdd(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");
        String role = params.get("role");
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            return Result.error(400, "账号和密码不能为空");
        }
        LambdaQueryWrapper<Admin> qw = new LambdaQueryWrapper<>();
        qw.eq(Admin::getUsername, username);
        if (adminService.count(qw) > 0) {
            return Result.error(400, "账号已存在");
        }
        Admin admin = new Admin();
        admin.setUsername(username);
        admin.setPassword(MD5Util.encrypt(password));
        admin.setRole(role == null || role.isEmpty() ? "普通管理员" : role);
        admin.setStatus(1);
        adminService.save(admin);
        return Result.success("添加成功");
    }

    @Operation(summary = "编辑管理员")
    @PutMapping("/account/update")
    public Result<String> accountUpdate(@RequestBody Map<String, String> params) {
        Long id = Long.valueOf(params.get("id"));
        Admin admin = new Admin();
        admin.setId(id);
        String username = params.get("username");
        if (username != null && !username.isEmpty()) {
            admin.setUsername(username);
        }
        String role = params.get("role");
        if (role != null && !role.isEmpty()) {
            admin.setRole(role);
        }
        String password = params.get("password");
        if (password != null && !password.isEmpty()) {
            admin.setPassword(MD5Util.encrypt(password));
        }
        adminService.updateById(admin);
        return Result.success("更新成功");
    }

    @Operation(summary = "删除管理员")
    @DeleteMapping("/account/delete/{id}")
    public Result<String> accountDelete(@PathVariable Long id, HttpServletRequest request) {
        Long currentAdminId = (Long) request.getAttribute("userId");
        if (id.equals(currentAdminId)) {
            return Result.error(400, "不能删除自己");
        }
        Admin admin = adminService.getById(id);
        if (admin != null && "超级管理员".equals(admin.getRole())) {
            return Result.error(400, "不能删除超级管理员");
        }
        adminService.removeById(id);
        return Result.success("删除成功");
    }

    @Operation(summary = "禁用/启用管理员")
    @PutMapping("/account/status/{id}")
    public Result<String> accountStatus(@PathVariable Long id, @RequestParam Integer status, HttpServletRequest request) {
        Long currentAdminId = (Long) request.getAttribute("userId");
        if (id.equals(currentAdminId)) {
            return Result.error(400, "不能操作自己");
        }
        Admin admin = new Admin();
        admin.setId(id);
        admin.setStatus(status);
        adminService.updateById(admin);
        return Result.success("操作成功");
    }

    // ---------- 识别日志 ----------
    @Operation(summary = "识别日志列表")
    @GetMapping("/log/list")
    public Result<Page<RecognitionLog>> logList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long userId) {
        Page<RecognitionLog> p = new Page<>(page, size);
        LambdaQueryWrapper<RecognitionLog> qw = new LambdaQueryWrapper<>();
        if (userId != null) {
            qw.eq(RecognitionLog::getUserId, userId);
        }
        qw.orderByAsc(RecognitionLog::getId);
        Page<RecognitionLog> result = recognitionLogService.page(p, qw);
        for (RecognitionLog log : result.getRecords()) {
            User u = userService.getById(log.getUserId());
            if (u != null) {
                log.setUsername(u.getUsername());
            }
        }
        return Result.success(result);
    }
}
