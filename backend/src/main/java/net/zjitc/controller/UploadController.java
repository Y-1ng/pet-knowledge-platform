package net.zjitc.controller;

import net.zjitc.common.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class UploadController {

    @Value("${file.upload.path}")
    private String uploadPath;

    @PostMapping("/upload")
    public Result<Map<String, String>> upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("文件不能为空");
        }

        try {
            String originalFilename = file.getOriginalFilename();
            String suffix = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String filename = UUID.randomUUID().toString().replace("-", "") + suffix;

            File dir = new File(uploadPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            File dest = new File(uploadPath + filename);
            file.transferTo(dest);

            Map<String, String> data = new HashMap<>();
            data.put("url", "/api/download/" + filename);
            return Result.success(data);
        } catch (IOException e) {
            return Result.error("上传失败");
        }
    }

    @GetMapping("/download/{filename}")
    public void download(@PathVariable String filename, HttpServletResponse response) {
        try {
            Path path = Paths.get(uploadPath + filename);
            if (!Files.exists(path)) {
                response.setStatus(404);
                return;
            }

            String contentType = Files.probeContentType(path);
            if (contentType != null && contentType.startsWith("image/")) {
                response.setContentType(contentType);
            } else {
                response.setContentType("application/octet-stream");
            }

            try (InputStream is = Files.newInputStream(path);
                 OutputStream os = response.getOutputStream()) {
                byte[] buffer = new byte[8192];
                int len;
                while ((len = is.read(buffer)) != -1) {
                    os.write(buffer, 0, len);
                }
            }
        } catch (IOException e) {
            response.setStatus(500);
        }
    }
}
