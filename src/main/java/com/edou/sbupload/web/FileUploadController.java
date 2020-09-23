package com.edou.sbupload.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @ClassName FileUploadController
 * @Description 文件上传控制器
 * @Author 中森明菜
 * @Date 2020/9/23 11:27
 * @Version 1.0
 */
@Controller
public class FileUploadController {

    @Value("${upload.file.path}")
    private String uploadPath;

    @GetMapping("/")
    public String index(Model model) {
        // 获取项目根路径
        String rootPath = System.getProperty("user.dir");
        String targetPath = rootPath + File.separator + uploadPath;
        File file = new File(targetPath);
        try {
            // 遍历上传目录的文件
            List<String> collect = Files
                    .walk(file.toPath(), 1)
                    .filter(path -> !path.toFile().isDirectory())
                    .map(path1 -> path1.toFile().getName())
                    .collect(Collectors.toList());
            System.out.println(collect);
            model.addAttribute("fileList",collect);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "index";
    }

    @PostMapping("/upload")
    @ResponseBody
    public ResponseEntity upload(@RequestParam("file") MultipartFile file) {
        // 获取项目根路径
        String rootPath = System.getProperty("user.dir");
        try {
            String fileName = rootPath + File.separator + uploadPath + file.getOriginalFilename();
            File allFile = new File(fileName);
            Files.copy(file.getInputStream(), allFile.toPath());
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("上传失败");
        }
        return ResponseEntity.ok().body("上传成功");
    }
}
