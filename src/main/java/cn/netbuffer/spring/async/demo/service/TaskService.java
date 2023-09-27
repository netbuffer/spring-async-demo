package cn.netbuffer.spring.async.demo.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class TaskService {

    @Async
    public void taskNoReturnValue() {
        log.debug("exec");
    }

    @Async("ate")
    public Future<Integer> taskReturnValue() {
        Double sleep = Math.random() * 10;
        try {
            TimeUnit.SECONDS.sleep(sleep.intValue());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Double value = Math.random() * 1000;
        log.debug("taskReturnValue={}", value);
        return new AsyncResult<>(value.intValue());
    }

}
