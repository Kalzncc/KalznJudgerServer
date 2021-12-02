package com.kalzn.judger.controller;

import com.kalzn.judger.util.JudgerHttpStatusJson;
import com.kalzn.judger.service.judgecontrol.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/Task")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @RequestMapping("/Submit")
    public String submit(@RequestParam("config-str") String configStr, String code) throws IOException {
        try {
            taskService.submit(configStr, code);
        } catch (Exception e) {
            return JudgerHttpStatusJson.getFailureJson(e.toString());
        }
        return JudgerHttpStatusJson.OK;
    }
}
