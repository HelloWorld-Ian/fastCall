package Core;

import Annotation.FastService;

@FastService(HelloFastService.class)
public class HelloFastServiceImpl implements HelloFastService{
    @Override
    public String hello() {
        return "Hello RPC!";
    }
}
