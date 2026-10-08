package Pharmacy.Management.System.config;

import Pharmacy.Management.System.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Guards role-restricted API/page routes using the session created at login.
 * /api/admin/** requires ADMIN or PHARMACY_OWNER; /dashboard/** requires any
 * authenticated user (the dashboard page itself renders role-specific content
 * client-side).
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false);
        String path = request.getRequestURI();

        boolean authenticated = session != null && session.getAttribute(AuthService.SESSION_USER_ID) != null;

        if (!authenticated) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"success\":false,\"message\":\"Not authenticated. Please log in.\"}");
            return false;
        }

        if (path.startsWith("/api/admin/")) {
            String role = (String) session.getAttribute(AuthService.SESSION_ROLE);
            if (!"ADMIN".equals(role) && !"PHARMACY_OWNER".equals(role)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json");
                response.getWriter().write("{\"success\":false,\"message\":\"Administrator or Pharmacy Owner access only.\"}");
                return false;
            }
        }

        return true;
    }
}