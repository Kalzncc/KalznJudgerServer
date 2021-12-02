package com.kalzn.judger.controller;


import com.kalzn.judger.service.judgecontrol.DataService;
import com.kalzn.judger.util.JudgerHttpStatusJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/Data")
public class DataController {

    @Autowired
    private DataService dataService;


    @RequestMapping("/UpdateData")
    public String updateData(@RequestParam("data-id")int dataId) {
        try {
            dataService.updateData(dataId);
        } catch (Exception e) {
            return JudgerHttpStatusJson.getFailureJson(e.toString());
        }
        return JudgerHttpStatusJson.OK;
    }
    @RequestMapping("/ClearDataSpace")
    public String clearDataSpace() {
        try {
            dataService.clearDataSpace();
        } catch (Exception e) {
            return JudgerHttpStatusJson.getFailureJson(e.toString());
        }
        return JudgerHttpStatusJson.OK;

    }
    @RequestMapping("/ResetData")
    public String resetData() {

        try {
            dataService.resetData();
        } catch (Exception e) {
            return JudgerHttpStatusJson.getFailureJson(e.toString());
        }
        return JudgerHttpStatusJson.OK;

    }
    @RequestMapping("/ReleaseData")
    public String releaseData(@RequestParam("data-id") int dataId) {
        try {
            dataService.releaseData(dataId);
        } catch (Exception e) {
            return JudgerHttpStatusJson.getFailureJson(e.toString());
        }
        return JudgerHttpStatusJson.OK;
    }
    @RequestMapping("/CheckManagerStatus")
    public String CheckManagerStatus() {
        Integer status = dataService.checkManagerStatus();
        return JudgerHttpStatusJson.getRejectedJson("{\"status\":\""+ status +"\"}");
    }
    @RequestMapping("/PauseService")
    public String pauseService() {
        try {
            dataService.pauseService();
        } catch (Exception e) {
            return JudgerHttpStatusJson.getFailureJson(e.toString());
        }
        return JudgerHttpStatusJson.OK;
    }
    @RequestMapping("/StartService")
    public String startService() {
        try {
            dataService.startService();
        } catch (Exception e) {
            return JudgerHttpStatusJson.getFailureJson(e.toString());
        }
        return JudgerHttpStatusJson.OK;
    }
}

