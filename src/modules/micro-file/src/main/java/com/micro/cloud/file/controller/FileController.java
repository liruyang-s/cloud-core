package com.micro.cloud.file.controller;

import com.micro.cloud.common.core.domain.Result;
import com.micro.cloud.file.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * 文件管理
 */
@Tag(name = "文件管理")
@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @Operation(summary = "上传文件")
    @PostMapping("/upload")
    public Result<String> upload(@RequestParam("file") MultipartFile file) {
        return Result.success(fileService.upload(file));
    }

    @Operation(summary = "下载文件")
    @GetMapping("/download")
    public void download(@RequestParam String objectName, HttpServletResponse response) throws Exception {
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment;filename="
                + URLEncoder.encode(objectName.substring(objectName.lastIndexOf('/') + 1), StandardCharsets.UTF_8));
        try (InputStream in = fileService.download(objectName); OutputStream out = response.getOutputStream()) {
            in.transferTo(out);
        }
    }

    @Operation(summary = "删除文件")
    @DeleteMapping("/remove")
    public Result<Void> remove(@RequestParam String objectName) {
        fileService.remove(objectName);
        return Result.success();
    }
}
