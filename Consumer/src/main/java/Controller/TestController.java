package Controller;

import Annotation.FastConsumer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @FastConsumer
    HelloFastService test;

    @GetMapping("/hello")
    public String helloTest(){
        return test.hello();
    }
}
