package Pharmacy.Management.System.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Convenience redirects so the URLs the client asked for work without the .html suffix:
 * /customer/login -> /customer/login.html, /manager/login -> /manager/login.html
 */
@Controller
public class PageController {

    @GetMapping("/customer/login")
    public String customerLogin() {
        return "redirect:/customer/login.html";
    }

    @GetMapping("/manager/login")
    public String managerLogin() {
        return "redirect:/manager/login.html";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "redirect:/dashboard/dashboard.html";
    }
}
