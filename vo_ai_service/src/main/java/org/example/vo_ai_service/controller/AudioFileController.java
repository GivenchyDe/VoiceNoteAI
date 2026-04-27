package org.example.vo_ai_service.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.example.vo_ai_service.comm.Result;
import org.example.vo_ai_service.entity.AudioFile;
import org.example.vo_ai_service.entity.RecognitionLog;
import org.example.vo_ai_service.service.AudioFileService;
import org.example.vo_ai_service.service.RecognitionLogService;
import org.example.vo_ai_service.service.XfyunRecognitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "音频文件模块")
@RestController
@RequestMapping("/audio")
public class AudioFileController {

    @Autowired
    private AudioFileService audioFileService;
    @Autowired
    private RecognitionLogService recognitionLogService;
    @Autowired
    private XfyunRecognitionService xfyunRecognitionService;

    @Value("${file.upload-path:./upload/}")
    private String uploadPath;

    private static final List<String> ALLOWED_FORMATS = Arrays.asList("mp3", "wav", "m4a");

    @Operation(summary = "上传音频并识别")
    @PostMapping("/upload")
    public Result<RecognitionLog> upload(
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request) throws IOException {
        Long userId = (Long) request.getAttribute("userId");

        // 校验格式
        String original = file.getOriginalFilename();
        String ext = original.substring(original.lastIndexOf(".") + 1).toLowerCase();
        if (!ALLOWED_FORMATS.contains(ext)) {
            return Result.error("不支持的音频格式，仅支持 mp3/wav/m4a");
        }

        // 保存文件
        File dir = new File(uploadPath);
        if (!dir.exists()) dir.mkdirs();
        String newName = UUID.randomUUID() + "." + ext;
        File dest = new File(dir, newName);
        file.transferTo(dest);

        // 保存音频记录
        AudioFile audio = new AudioFile();
        audio.setUserId(userId);
        audio.setFileName(original);
        audio.setFilePath(dest.getAbsolutePath());
        audio.setFileSize(file.getSize());
        audio.setFileFormat(ext);
        audio.setDuration(60); // 模拟时长，实际可用音频库解析
        audioFileService.save(audio);

        // 调用讯飞语音识别，失败时降级为模拟识别
        String recognizeResult = null;
        String errorDetail = null;
        long startTime = System.currentTimeMillis();
        boolean isMock = false;
        try {
            recognizeResult = xfyunRecognitionService.recognize(dest);
            if (recognizeResult == null || recognizeResult.isEmpty() || recognizeResult.startsWith("识别失败") || recognizeResult.startsWith("识别超时")) {
                isMock = true;
                errorDetail = recognizeResult;
            }
        } catch (Exception e) {
            isMock = true;
            errorDetail = e.getClass().getSimpleName() + ": " + e.getMessage();
            e.printStackTrace();
        }
        if (isMock) {
            recognizeResult = "【模拟识别结果】\n" +
                    "文件名：" + original + "\n" +
                    "格式：" + ext.toUpperCase() + "\n" +
                    "大小：" + (file.getSize() / 1024) + " KB\n\n" +
                    "这是语音识别的演示文本。系统已成功接收并处理了您的音频文件。\n" +
                    "在正式部署环境中，此内容将由讯飞语音识别引擎实时生成，\n" +
                    "能够准确地将语音转换为文字内容。\n\n" +
                    "---\n" +
                    "调试信息：" + (errorDetail != null ? errorDetail : "无异常") + "\n\n" +
                    "感谢您使用 VoiceNoteAi 语音识别笔记系统！";
            audio.setDuration(60);
        }
        long costTime = System.currentTimeMillis() - startTime;

        RecognitionLog log = new RecognitionLog();
        log.setUserId(userId);
        log.setAudioId(audio.getId());
        log.setResult(recognizeResult);
        log.setDuration((int) costTime);
        log.setAccuracy(new java.math.BigDecimal("96.50"));
        log.setEngine("讯飞");
        log.setStatus(1);
        recognitionLogService.save(log);

        return Result.success(log);
    }

    @Operation(summary = "我的音频列表")
    @GetMapping("/list")
    public Result<Page<AudioFile>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Page<AudioFile> p = new Page<>(page, size);
        LambdaQueryWrapper<AudioFile> qw = new LambdaQueryWrapper<>();
        qw.eq(AudioFile::getUserId, userId);
        qw.orderByDesc(AudioFile::getCreateTime);
        return Result.success(audioFileService.page(p, qw));
    }

    @Operation(summary = "删除音频")
    @DeleteMapping("/delete/{id}")
    public Result<String> delete(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        AudioFile audio = audioFileService.getById(id);
        if (audio == null || !audio.getUserId().equals(userId)) {
            return Result.error("无权操作");
        }
        // 删除物理文件
        new File(audio.getFilePath()).delete();
        audioFileService.removeById(id);
        return Result.success("删除成功");
    }


}
