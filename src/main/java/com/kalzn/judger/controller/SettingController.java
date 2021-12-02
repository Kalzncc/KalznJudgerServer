package com.kalzn.judger.controller;


import com.kalzn.judger.service.judgecontrol.SettingService;
import com.kalzn.judger.util.JudgerHttpStatusJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/Setting")
public class SettingController {

    @Autowired
    private SettingService settingService;
    @Autowired
    @RequestMapping("/Hello")
    public String hello() {
        return JudgerHttpStatusJson.getOkJson(settingService.hello());
    }
    @RequestMapping("/CheckStatus")
    public String checkStatus() {
        Integer status = settingService.checkManagerStatus();
        return JudgerHttpStatusJson.getOkJson("{\"status\":\""+status+"\"}");
    }
    @RequestMapping("/RegisterData")
    public String registerData(@RequestParam("data-id") int dataId) {
        try {
            settingService.registerData(dataId);
        } catch (Exception e) {
            return JudgerHttpStatusJson.getFailureJson(e.toString());
        }
        return JudgerHttpStatusJson.OK;
    }
    @RequestMapping("/PauseService")
    public String pauseService() {
        try {
            settingService.pauseService();
        } catch (Exception e) {
            return JudgerHttpStatusJson.getFailureJson(e.toString());
        }
        return JudgerHttpStatusJson.OK;
    }
    @RequestMapping("/StartService")
    public String startService() {
        try {
            settingService.startService();
        } catch (Exception e) {
            return JudgerHttpStatusJson.getFailureJson(e.toString());
        }
        return JudgerHttpStatusJson.OK;
    }
}
