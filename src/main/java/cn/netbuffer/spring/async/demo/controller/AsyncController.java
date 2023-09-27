package cn.netbuffer.spring.async.demo.controller;

import cn.netbuffer.spring.async.demo.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Slf4j
@RestController
@RequestMapping("/async")
public class AsyncController {

    @Resource
    private TaskService taskService;

    @GetMapping("taskNoReturnValue")
    public void taskNoReturnValue() {
        taskService.taskNoReturnValue();
    }

    @GetMapping("taskReturnValue")
    public List<Integer> taskReturnValue() throws ExecutionException, InterruptedException {
        List<Integer> values = new ArrayList<>();
        List<Future<Integer>> valuesFuture = new ArrayList<>();
        log.debug("begin taskReturnValue");
        for (int i = 0; i < 100; i++) {
            valuesFuture.add(taskService.taskReturnValue());
        }
        log.debug("end taskReturnValue");
        log.debug("begin get valuesFuture result");
        for (int i = 0; i < 100; i++) {
            values.add(valuesFuture.get(i).get());
        }
        log.debug("end get valuesFuture result");
        return values;
    }

}
