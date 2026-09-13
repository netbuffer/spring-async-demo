package cn.netbuffer.spring.async.demo;

import cn.netbuffer.spring.async.demo.service.TaskService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.task.AsyncTaskExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class SpringAsyncDemoApplicationTests {

    @Resource
    private TaskService taskService;

    @Resource
    @Qualifier("ate")
    private AsyncTaskExecutor asyncTaskExecutor;

    @Resource
    @Qualifier("applicationTaskExecutor")
    private AsyncTaskExecutor applicationTaskExecutor;

    @Test
    @DisplayName("验证 Spring 上下文正常加载且线程池注入成功")
    void contextLoads() {
        assertThat(taskService).isNotNull();
        assertThat(asyncTaskExecutor).isNotNull();
        assertThat(applicationTaskExecutor).isNotNull();
    }

    @Test
    @DisplayName("验证自定义线程池 ate 使用虚拟线程执行任务")
    void testCustomVirtualThreadExecutor() throws Exception {
        CompletableFuture<Boolean> isVirtualFuture = new CompletableFuture<>();
        CompletableFuture<String> threadNameFuture = new CompletableFuture<>();

        asyncTaskExecutor.execute(() -> {
            threadNameFuture.complete(Thread.currentThread().getName());
            isVirtualFuture.complete(Thread.currentThread().isVirtual());
        });

        String threadName = threadNameFuture.get(5, TimeUnit.SECONDS);
        Boolean isVirtual = isVirtualFuture.get(5, TimeUnit.SECONDS);

        assertThat(isVirtual).isTrue();
        assertThat(threadName).startsWith("ate-vt-");
    }

    @Test
    @DisplayName("验证 Spring Boot 默认 applicationTaskExecutor 启用虚拟线程")
    void testApplicationTaskExecutorIsVirtualThread() throws Exception {
        CompletableFuture<Boolean> isVirtualFuture = new CompletableFuture<>();
        CompletableFuture<String> threadNameFuture = new CompletableFuture<>();

        applicationTaskExecutor.execute(() -> {
            threadNameFuture.complete(Thread.currentThread().getName());
            isVirtualFuture.complete(Thread.currentThread().isVirtual());
        });

        String threadName = threadNameFuture.get(5, TimeUnit.SECONDS);
        Boolean isVirtual = isVirtualFuture.get(5, TimeUnit.SECONDS);

        assertThat(isVirtual).isTrue();
        assertThat(threadName).startsWith("sasync-");
    }

    @Test
    @DisplayName("验证 @Async 无返回值方法执行成功")
    void testTaskNoReturnValue() {
        taskService.taskNoReturnValue();
    }

    @Test
    @DisplayName("验证 @Async 带返回值方法能正常并发获取结果")
    void testTaskReturnValue() throws Exception {
        List<Future<Integer>> futures = new ArrayList<>();
        int count = 10;
        for (int i = 0; i < count; i++) {
            futures.add(taskService.taskReturnValue());
        }

        for (Future<Integer> future : futures) {
            Integer value = future.get(10, TimeUnit.SECONDS);
            assertThat(value).isNotNull().isBetween(0, 1000);
        }
    }

    @Test
    @DisplayName("验证 TaskService 中异步任务由虚拟线程执行 (isVirtual=true)")
    void testTaskWithThreadInfoIsVirtual() throws Exception {
        int count = 10;
        List<CompletableFuture<Map<String, Object>>> futures = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            futures.add(taskService.taskWithThreadInfo("test-task-" + i));
        }

        for (CompletableFuture<Map<String, Object>> future : futures) {
            Map<String, Object> info = future.get(10, TimeUnit.SECONDS);
            assertThat(info).isNotNull();
            assertThat(info.get("isVirtual")).isEqualTo(true);
            assertThat(info.get("threadName").toString()).startsWith("ate-vt-");
        }
    }

}
