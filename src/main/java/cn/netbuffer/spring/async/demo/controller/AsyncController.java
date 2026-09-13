package cn.netbuffer.spring.async.demo.controller;

import cn.netbuffer.spring.async.demo.service.TaskService;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@RestController
@RequestMapping("/async")
public class AsyncController {

    private static final Logger log = LoggerFactory.getLogger(AsyncController.class);

    @Resource
    private TaskService taskService;

    @GetMapping("taskNoReturnValue")
    public void taskNoReturnValue() {
        taskService.taskNoReturnValue();
    }

    @GetMapping("taskReturnValue")
    public List<Integer> taskReturnValue(@RequestParam(value = "count", defaultValue = "10") int count) throws ExecutionException, InterruptedException {
        List<Integer> values = new ArrayList<>();
        List<Future<Integer>> valuesFuture = new ArrayList<>();
        log.debug("begin taskReturnValue, count={}", count);
        for (int i = 0; i < count; i++) {
            valuesFuture.add(taskService.taskReturnValue());
        }
        log.debug("end taskReturnValue dispatch");
        log.debug("begin get valuesFuture result");
        for (int i = 0; i < count; i++) {
            values.add(valuesFuture.get(i).get());
        }
        log.debug("end get valuesFuture result");
        return values;
    }

    @GetMapping("virtualThreadInfo")
    public Map<String, Object> getVirtualThreadInfo(@RequestParam(value = "count", defaultValue = "5") int count) throws ExecutionException, InterruptedException {
        List<CompletableFuture<Map<String, Object>>> futures = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            futures.add(taskService.taskWithThreadInfo("task-" + (i + 1)));
        }
        List<Map<String, Object>> tasks = new ArrayList<>();
        for (CompletableFuture<Map<String, Object>> future : futures) {
            tasks.add(future.get());
        }
        Map<String, Object> response = new HashMap<>();
        response.put("total", count);
        response.put("currentServerThread", Thread.currentThread().getName());
        response.put("currentServerThreadIsVirtual", Thread.currentThread().isVirtual());
        response.put("tasks", tasks);
        return response;
    }

}
