package org.example.vo_ai_service.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.example.vo_ai_service.comm.JwtUtil;
import org.example.vo_ai_service.comm.MD5Util;
import org.example.vo_ai_service.comm.Result;
import org.example.vo_ai_service.entity.User;
import org.example.vo_ai_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "用户模块")
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Result<String> register(@RequestBody User user) {
        // 检查用户名/手机号/邮箱是否已存在
        LambdaQueryWrapper<User> qw = new LambdaQueryWrapper<>();
        qw.eq(User::getPhone, user.getPhone()).or().eq(User::getEmail, user.getEmail());
        if (userService.count(qw) > 0) {
            return Result.error("手机号或邮箱已被注册");
        }
        user.setPassword(MD5Util.encrypt(user.getPassword()));
        user.setStatus(1);
        userService.save(user);
        return Result.success("注册成功");
    }

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> params) {
        String phone = params.get("phone");
        String password = params.get("password");
        LambdaQueryWrapper<User> qw = new LambdaQueryWrapper<>();
        qw.eq(User::getPhone, phone);
        User user = userService.getOne(qw);
        if (user == null || !user.getPassword().equals(MD5Util.encrypt(password))) {
            return Result.error(400, "手机号或密码错误");
        }
        if (user.getStatus() == 0) {
            return Result.error(400, "账号已被禁用");
        }
        String token = JwtUtil.generateToken(user.getId(), user.getUsername(), "user");
        Map<String, Object> map = new HashMap<>();
        map.put("token", token);
        map.put("user", user);
        return Result.success(map);
    }

    @Operation(summary = "获取当前用户信息")
    @GetMapping("/info")
    public Result<User> info(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        User user = userService.getById(userId);
        if (user != null) {
            user.setPassword(null);
        }
        return Result.success(user);
    }

    @Operation(summary = "更新个人信息")
    @PutMapping("/update")
    public Result<String> update(@RequestBody User user, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        user.setId(userId);
        user.setPassword(null); // 不允许在此接口修改密码
        userService.updateById(user);
        return Result.success("更新成功");
    }

    @Operation(summary = "修改密码")
    @PutMapping("/password")
    public Result<String> password(@RequestBody Map<String, String> params, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        String oldPwd = params.get("oldPassword");
        String newPwd = params.get("newPassword");
        User user = userService.getById(userId);
        if (!user.getPassword().equals(MD5Util.encrypt(oldPwd))) {
            return Result.error("原密码错误");
        }
        user.setPassword(MD5Util.encrypt(newPwd));
        userService.updateById(user);
        return Result.success("密码修改成功");
    }

    @Operation(summary = "上传头像")
    @PostMapping("/avatar")
    public Result<Map<String, String>> uploadAvatar(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (file.isEmpty()) {
            return Result.error(400, "请选择图片");
        }
        // 检查文件类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return Result.error(400, "仅支持图片格式");
        }
        // 生成唯一文件名
        String originalName = file.getOriginalFilename();
        String ext = "";
        if (originalName != null && originalName.contains(".")) {
            ext = originalName.substring(originalName.lastIndexOf("."));
        }
        String fileName = "avatar_" + userId + "_" + UUID.randomUUID().toString().substring(0, 8) + ext;
        // 保存到 upload/avatar 目录
        String uploadDir = System.getProperty("user.dir") + "/upload/avatar";
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        try {
            File dest = new File(dir, fileName);
            file.transferTo(dest);
            // 更新用户头像字段
            String avatarUrl = "/avatar/" + fileName;
            User user = new User();
            user.setId(userId);
            user.setAvatar(avatarUrl);
            userService.updateById(user);
            Map<String, String> result = new HashMap<>();
            result.put("avatar", avatarUrl);
            return Result.success(result);
        } catch (IOException e) {
            return Result.error(500, "头像上传失败");
        }
    }
}
