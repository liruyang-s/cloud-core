package com.micro.cloud.file.service;

import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.IdUtil;
import com.micro.cloud.common.core.exception.ServiceException;
import com.micro.cloud.common.minio.utils.MinioUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 文件服务：上传、下载、删除
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {

    private final MinioUtils minioUtils;

    /**
     * 上传文件，返回访问地址
     * <p>
     * 对象名规则：业务前缀/日期/随机名.扩展名，避免重名覆盖。
     */
    public String upload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ServiceException("上传文件不能为空");
        }
        String ext = FileNameUtil.extName(file.getOriginalFilename());
        String objectName = "files/" + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE)
                + "/" + IdUtil.fastSimpleUUID() + "." + ext;
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
