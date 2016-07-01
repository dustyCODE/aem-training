package com.globant.julian.cacharriando.core.utils;

import org.junit.runner.RunWith;
/*import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;*/
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.mockito.Mockito.any;

/**
 * Created by julian.herrera on 6/27/2016.
 */
//@RunWith(PowerMockRunner.class)
public class MockLog {

   // private static final Logger log = PowerMockito.mock(Logger.class);

    public static void mockLogger(Class<?> clazz) {
    //    PowerMockito.mockStatic(LoggerFactory.class);
     //   PowerMockito.when(LoggerFactory.getLogger(clazz)).thenReturn(log);
      //  PowerMockito.doNothing().when(log).info(any(String.class));
    }

}
