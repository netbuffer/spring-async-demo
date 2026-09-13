package cn.netbuffer.spring.async.demo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class AsyncControllerTests {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @DisplayName("测试 GET /async/taskNoReturnValue 接口")
    void testTaskNoReturnValueEndpoint() throws Exception {
        mockMvc.perform(get("/async/taskNoReturnValue"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("测试 GET /async/taskReturnValue 接口返回数据列表")
    void testTaskReturnValueEndpoint() throws Exception {
        mockMvc.perform(get("/async/taskReturnValue").param("count", "5"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(5)));
    }

    @Test
    @DisplayName("测试 GET /async/virtualThreadInfo 接口返回虚拟线程状态")
    void testVirtualThreadInfoEndpoint() throws Exception {
        mockMvc.perform(get("/async/virtualThreadInfo").param("count", "3"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.total", is(3)))
                .andExpect(jsonPath("$.tasks", hasSize(3)))
                .andExpect(jsonPath("$.tasks[0].isVirtual", is(true)))
                .andExpect(jsonPath("$.tasks[0].threadName", startsWith("ate-vt-")));
    }

}
