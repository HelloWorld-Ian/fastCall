package Core;

import Annotation.FastService;
import Controller.HelloFastService;

@FastService(HelloFastService.class)
public class HelloFastServiceImpl implements HelloFastService{
    @Override
    public String hello() {
        return "Hello RPC!";
    }
}
