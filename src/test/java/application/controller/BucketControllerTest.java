package application.controller;

import application.util.BaseTest;
import application.util.Payload;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@EnabledIfEnvironmentVariable(named = "SPRING_PROFILES_ACTIVE", matches = "gungnir,test")
class BucketControllerTest extends BaseTest {
    @Test
    void buckets() throws Exception {
        mockMvc.perform(Payload.BUCKETS_PUT.getRequest(Payload.buildProgram("code", "file", "upload")))
                .andExpect(status().isOk());
        mockMvc.perform(Payload.BUCKETS_SEARCH.getRequest(Payload.buildBucket("file", "upload")))
                .andExpect(jsonPath("$.file._embedded").value("code"))
                .andExpect(status().isOk());
        mockMvc.perform(Payload.BUCKETS_DELETE.getRequest(Payload.buildBucket("file", "upload")))
                .andExpect(status().isOk());
    }

    @Test
    void deploy() throws Exception {
        String name = new DeployControllerTest(mockMvc).deploy("echo TEST");
        mockMvc.perform(Payload.BUCKETS_SEARCH.getRequest(Payload.buildBucket(name, "logger")))
                .andExpect(jsonPath(String.format("$.%s._embedded", name)).value("TEST\n"))
                .andExpect(status().isOk());
        mockMvc.perform(Payload.BUCKETS_DELETE.getRequest(Payload.buildBucket(name, "deploy")))
                .andExpect(status().isOk());
        mockMvc.perform(Payload.BUCKETS_DELETE.getRequest(Payload.buildBucket(name, "logger")))
                .andExpect(status().isOk());
    }
}