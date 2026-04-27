package org.example.vo_ai_service.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.example.vo_ai_service.comm.Result;
import org.example.vo_ai_service.entity.Note;
import org.example.vo_ai_service.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "笔记模块")
@RestController
@RequestMapping("/note")
public class NoteController {

    @Autowired
    private NoteService noteService;

    @Operation(summary = "创建笔记")
    @PostMapping("/add")
    public Result<String> add(@RequestBody Note note, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        note.setUserId(userId);
        note.setStatus(1);
        noteService.save(note);
        return Result.success("创建成功");
    }

    @Operation(summary = "笔记列表")
    @GetMapping("/list")
    public Result<Page<Note>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Page<Note> p = new Page<>(page, size);
        LambdaQueryWrapper<Note> qw = new LambdaQueryWrapper<>();
        qw.eq(Note::getUserId, userId);
        if (category != null && !category.isEmpty()) {
            qw.eq(Note::getCategory, category);
        }
        if (keyword != null && !keyword.isEmpty()) {
            qw.and(w -> w.like(Note::getTitle, keyword).or().like(Note::getContent, keyword));
        }
        qw.orderByDesc(Note::getUpdateTime);
        return Result.success(noteService.page(p, qw));
    }

    @Operation(summary = "笔记详情")
    @GetMapping("/detail/{id}")
    public Result<Note> detail(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Note note = noteService.getById(id);
        if (note == null || !note.getUserId().equals(userId)) {
            return Result.error("笔记不存在");
        }
        return Result.success(note);
    }

    @Operation(summary = "更新笔记")
    @PutMapping("/update")
    public Result<String> update(@RequestBody Note note, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Note old = noteService.getById(note.getId());
        if (old == null || !old.getUserId().equals(userId)) {
            return Result.error("无权操作");
        }
        note.setUserId(null);
        noteService.updateById(note);
        return Result.success("更新成功");
    }

    @Operation(summary = "删除笔记")
    @DeleteMapping("/delete/{id}")
    public Result<String> delete(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Note old = noteService.getById(id);
        if (old == null || !old.getUserId().equals(userId)) {
            return Result.error("无权操作");
        }
        noteService.removeById(id);
        return Result.success("删除成功");
    }

    @Operation(summary = "归档/恢复笔记")
    @PutMapping("/archive/{id}")
    public Result<String> archive(@PathVariable Long id, @RequestParam Integer status, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Note old = noteService.getById(id);
        if (old == null || !old.getUserId().equals(userId)) {
            return Result.error("无权操作");
        }
        Note note = new Note();
        note.setId(id);
        note.setStatus(status);
        noteService.updateById(note);
        return Result.success("操作成功");
    }

    @Operation(summary = "所有分类")
    @GetMapping("/categories")
    public Result<List<String>> categories(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        LambdaQueryWrapper<Note> qw = new LambdaQueryWrapper<>();
        qw.eq(Note::getUserId, userId);
        qw.select(Note::getCategory);
        qw.groupBy(Note::getCategory);
        List<Note> list = noteService.list(qw);
        return Result.success(list.stream().map(Note::getCategory).collect(Collectors.toList()));
    }

    @Operation(summary = "所有标签")
    @GetMapping("/tags")
    public Result<List<String>> tags(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        LambdaQueryWrapper<Note> qw = new LambdaQueryWrapper<>();
        qw.eq(Note::getUserId, userId);
        qw.isNotNull(Note::getTags);
        List<Note> list = noteService.list(qw);
        return Result.success(
                list.stream()
                        .flatMap(n -> Arrays.stream(n.getTags().split(",")))
                        .map(String::trim)
                        .distinct()
                        .collect(Collectors.toList())
        );
    }
}
