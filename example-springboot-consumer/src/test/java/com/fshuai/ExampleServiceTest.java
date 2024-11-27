package com.fshuai;


import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
public class ExampleServiceTest {

    @Resource
    private ExampleServiceImpl exampleService;

    @Test
    public void test1(){
        exampleService.test();
    }

}
