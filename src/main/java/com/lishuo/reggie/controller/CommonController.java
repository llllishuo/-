package com.lishuo.reggie.controller;


import com.lishuo.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

/**
 * 文件上传与下载
 */
@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {

    @Value("${reggie.path}")
    private String path;

    @PostMapping("/upload")
    //                               形参名必须是file
    public R<String> upload(MultipartFile file) throws IOException {
        log.info(file.toString());

        //雪花算法设置文件名防止重复
        String uuid= UUID.randomUUID().toString();


        //获取文件名
        String originalFilename = file.getOriginalFilename();
        //获取后缀
        String substring = originalFilename.substring(originalFilename.lastIndexOf("."));

        String fileName = uuid + substring;

        //判断目录是否存在
        File dir=new File(path);
        if(!dir.exists()){
            //不存在就创建
            dir.mkdirs();
        }

        file.transferTo(new File(path+fileName));



        return R.success(fileName) ;
    }

    /**
     * 文件下载
     * @param name
     * @param response
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) throws IOException {

        //输入流读取文件内容
        FileInputStream fileInputStream=new FileInputStream(new File(path+name));


        //输出流写回浏览器
        ServletOutputStream outputStream = response.getOutputStream();

        response.setContentType("image/jpeg");

        int len=0;
        byte[] bytes=new byte[1024];
        while ((len=fileInputStream.read(bytes))!=-1){
            outputStream.write(bytes,0,len);
            outputStream.flush();
        }
        outputStream.close();
        fileInputStream.close();


    }

}
