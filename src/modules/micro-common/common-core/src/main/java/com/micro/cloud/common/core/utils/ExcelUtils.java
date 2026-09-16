package com.micro.cloud.common.core.utils;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Excel 导入导出通用工具（基于 EasyExcel）
 * <p>
 * 底座提供通用能力，业务方定义带 {@code @ExcelProperty} 注解的 DTO 后直接调用。
 */
@Slf4j
public class ExcelUtils {

    private ExcelUtils() {
    }

    /**
     * 导出到 HttpServletResponse（浏览器下载）
     *
     * @param response HTTP 响应
     * @param fileName 文件名（不含扩展名）
     * @param clazz    数据模型类（带 @ExcelProperty 注解）
     * @param data     数据列表
     */
    public static <T> void export(HttpServletResponse response, String fileName,
                                  Class<T> clazz, List<T> data) {
        try {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            String encodedName = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replace("+", "%20");
            response.setHeader("Content-Disposition", "attachment;filename*=utf-8''" + encodedName + ".xlsx");
            EasyExcel.write(response.getOutputStream(), clazz)
                    .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                    .sheet("Sheet1")
                    .doWrite(data);
        } catch (IOException e) {
            log.error("Excel 导出失败: {}", fileName, e);
            throw new RuntimeException("Excel 导出失败", e);
        }
    }

    /**
     * 从输入流导入
     *
     * @param inputStream 输入流
     * @param clazz       数据模型类
     * @return 数据列表
     */
    public static <T> List<T> importExcel(InputStream inputStream, Class<T> clazz) {
        return EasyExcel.read(inputStream)
                .head(clazz)
                .sheet()
                .doReadSync();
    }
}
