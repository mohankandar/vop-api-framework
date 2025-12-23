package com.tnl.vop.reference;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RootRedirectController {

  @GetMapping("/")
  public String root() {
    // because context-path is /reference/api, this redirects to /reference/api/swagger-ui/index.html
    return "redirect:/swagger-ui/index.html";
  }
}
