package com.kalzn.judger.controller;


import com.kalzn.judger.util.JudgerHttpStatusJson;
import com.kalzn.judger.config.JudgerServerConfig;
import com.kalzn.judger.service.judgecontrol.ConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/Config")
public class ConfigController {

    @Autowired
    private JudgerServerConfig serverConfig;
    @Autowired
    private ConfigService configService;

    private Logger logger = LoggerFactory.getLogger(ConfigController.class);

    @RequestMapping("/SetLeader")
    public String configServer(String scheme, String ip, String port, @RequestParam("server-id") String serverId)  {
        try {
            configService.configServer(scheme, ip, Integer.parseInt(port), Integer.parseInt(serverId) );
        } catch (Exception e) {
            return JudgerHttpStatusJson.getFailureJson(e.toString());
        }
        return JudgerHttpStatusJson.OK;
    }

}
