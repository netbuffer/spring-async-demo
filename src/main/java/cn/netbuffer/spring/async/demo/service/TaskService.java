package cn.netbuffer.spring.async.demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Service
public class TaskService {

    private static final Logger log = LoggerFactory.getLogger(TaskService.class);

    @Async
    public void taskNoReturnValue() {
        log.debug("exec default async task on thread: {}, isVirtual: {}",
                Thread.currentThread().getName(), Thread.currentThread().isVirtual());
    }

    @Async("ate")
    public Future<Integer> taskReturnValue() {
        Double sleep = Math.random() * 2;
        try {
            TimeUnit.SECONDS.sleep(sleep.intValue());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
        Double value = Math.random() * 1000;
        log.debug("taskReturnValue={}, thread={}, isVirtual={}",
                value, Thread.currentThread().getName(), Thread.currentThread().isVirtual());
        return CompletableFuture.completedFuture(value.intValue());
    }

    @Async("ate")
    public CompletableFuture<Map<String, Object>> taskWithThreadInfo(String taskId) {
        try {
            TimeUnit.MILLISECONDS.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
        Map<String, Object> info = new HashMap<>();
        info.put("taskId", taskId);
        info.put("threadName", Thread.currentThread().getName());
        info.put("isVirtual", Thread.currentThread().isVirtual());
        log.debug("taskWithThreadInfo executed: {}", info);
        return CompletableFuture.completedFuture(info);
    }

}
