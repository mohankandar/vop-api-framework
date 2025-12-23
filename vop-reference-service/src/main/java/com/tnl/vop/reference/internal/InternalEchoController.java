package com.tnl.vop.reference.internal;

import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InternalEchoController {

  @GetMapping(path = "/internal/echo", produces = MediaType.APPLICATION_JSON_VALUE)
  public Map<String, Object> echo() {
    return Map.of("hello", "world");
  }
}
