package com.diegogonzalez.devsu;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {MicroserviceApplication.class})
class MicroserviceApplicationTests {

    @Test
    void contextLoads() {
        MicroserviceApplication.main(new String[]{});
        Assertions.assertTrue(true);
    }

}
