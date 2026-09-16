package com.micro.cloud.file.service;

import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.micro.cloud.common.core.exception.ServiceException;
import com.micro.cloud.common.minio.utils.MinioUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 文件服务：上传、下载、删除
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {

    private final MinioUtils minioUtils;

    /** 允许上传的扩展名白名单 */
    private static final List<String> ALLOWED_EXTENSIONS = List.of(
            "jpg", "jpeg", "png", "gif", "bmp", "webp",
            "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx",
            "txt", "csv", "zip", "rar", "7z");

    /** 上传大小上限（默认 10MB） */
    @Value("${file.max-size:10485760}")
    private long maxSize;

    /**
     * 上传文件，返回访问地址
     * <p>
     * 对象名规则：业务前缀/日期/随机名.扩展名，避免重名覆盖；
     * 校验扩展名白名单与文件大小，防止上传恶意脚本。
     */
    public String upload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ServiceException("上传文件不能为空");
        }
        if (file.getSize() > maxSize) {
            throw new ServiceException("文件大小超出限制（最大 " + maxSize / 1024 / 1024 + "MB）");
        }
        String ext = FileNameUtil.extName(file.getOriginalFilename());
        if (StrUtil.isBlank(ext) || !ALLOWED_EXTENSIONS.contains(ext.toLowerCase())) {
            throw new ServiceException("不支持的文件类型");
        }
        String objectName = "files/" + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE)
                + "/" + IdUtil.fastSimpleUUID() + "." + ext.toLowerCase();
        try (InputStream in = file.getInputStream()) {
            minioUtils.upload(objectName, in, file.getSize(), file.getContentType());
        } catch (Exception e) {
            log.error("文件上传失败", e);
            throw new ServiceException("文件上传失败");
        }
        return minioUtils.getFileUrl(objectName);
    }

    /** 下载文件流 */
    public InputStream download(String objectName) {
        return minioUtils.download(objectName);
    }

    /** 删除文件 */
    public void remove(String objectName) {
        minioUtils.remove(objectName);
    }
}
