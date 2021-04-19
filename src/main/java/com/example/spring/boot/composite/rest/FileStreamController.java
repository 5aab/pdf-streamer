package com.example.spring.boot.composite.rest;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.UUID;


@Controller
@AllArgsConstructor
public class FileStreamController {

    @RequestMapping(value = "downloadFile", method = RequestMethod.POST)
    public StreamingResponseBody getSteamingFile(HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"demo.pdf\"");
        InputStream inputStream = new FileInputStream(new File("C:\\FAST\\ws\\pdf-streamer\\src\\main\\resources\\sample.pdf"));
        return outputStream -> {
            int nRead;
            byte[] data = new byte[1024];
            while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                outputStream.write(data, 0, nRead);
            }
        };
    }

    @ResponseBody
    @PostMapping(value = "saveFile")
    public String saveFile(){
        RestTemplate restTemplate = new RestTemplate();
        File file = restTemplate.execute("http://localhost:9095/downloadFile", HttpMethod.POST, null, clientHttpResponse -> {
            File ret = File.createTempFile("report-"+ UUID.randomUUID(), ".pdf");
            try(FileOutputStream out = new FileOutputStream(ret)) {
                StreamUtils.copy(clientHttpResponse.getBody(), out);
                return ret;
            }
        });
        String absolutePath = file.getAbsolutePath();
        return absolutePath;
    }

}
