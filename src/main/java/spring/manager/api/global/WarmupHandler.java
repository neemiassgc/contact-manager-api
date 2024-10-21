package spring.manager.api.global;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WarmupHandler {

    @CrossOrigin("*")
    @GetMapping("/_ah/warmup")
    @ResponseStatus(HttpStatus.OK)
    public String warmup() {
        return "ok";
    }
}