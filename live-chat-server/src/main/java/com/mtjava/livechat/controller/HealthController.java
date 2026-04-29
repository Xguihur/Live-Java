package com.mtjava.livechat.controller;

import com.mtjava.livechat.common.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 健康检查接口，便于部署和联调时快速确认服务是否可用。
 */
@RestController
@RequestMapping("/api/health")
public class HealthController {

    /**
     * 返回一个简单的存活状态。
     */
    @GetMapping
    public ApiResponse<Map<String, String>> health() {
        return ApiResponse.success(Map.of("status", "ok"));
    }
}
