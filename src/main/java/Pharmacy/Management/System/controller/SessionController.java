package Pharmacy.Management.System.controller;

import Pharmacy.Management.System.dto.ApiResponse;
import Pharmacy.Management.System.service.AuthService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Tells the dashboard page who is logged in and with what role, so one dashboard.html
 * can render the correct nav/widgets for whichever role is currently signed in.
 */
@RestController
@RequiredArgsConstructor
public class SessionController {

    @GetMapping("/api/session")
    public ApiResponse<Map<String, Object>> currentSession(HttpSession session) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("userId", session.getAttribute(AuthService.SESSION_USER_ID));
        data.put("role", session.getAttribute(AuthService.SESSION_ROLE));
        data.put("name", session.getAttribute(AuthService.SESSION_NAME));
        return ApiResponse.ok("Session active", data);
    }
}
