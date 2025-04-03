package com.my;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.my.controller.ExceptionHandlerController;
import com.my.service.UserManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.util.UriComponentsBuilder;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

@SpringBootTest
@Testcontainers
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public abstract class AbstractTestContainer extends TestData {
    @Container
    private static final PostgreSQLContainer<?> postgresContainer =
            new PostgreSQLContainer<>("postgres:17.4")
                    .withInitScript("init.sql")
                    .waitingFor(Wait.forListeningPort());

    protected static ObjectMapper objectMapper;
    protected MockMvc mockMvc;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
        registry.add("spring.liquibase.contexts", () -> "test");
    }

    @BeforeAll
    static void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

    @AfterEach
    public void tearDown() {
        mockMvc = null;
        UserManager.setLoggedInUser(null);
    }

    protected void setUpMockMvc(Object controller, ExceptionHandlerController exceptionHandlerController) {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(exceptionHandlerController)
                .build();
    }

    protected MockHttpServletResponse performRequest(HttpMethod method, String url, HttpStatus expectedStatus) throws Exception {
        return performRequest(method, url, null, expectedStatus, null);
    }

    protected MockHttpServletResponse performRequest(HttpMethod method, String url, Object content, HttpStatus expectedStatus) throws Exception {
        return performRequest(method, url, content, expectedStatus, null);
    }

    protected MockHttpServletResponse performRequest(HttpMethod method, String url, HttpStatus expectedStatus, Map<String, String> pathVariables) throws Exception {
        return performRequest(method, url, null, expectedStatus, pathVariables);
    }

    protected MockHttpServletResponse performRequest(HttpMethod method, String url, Object content, HttpStatus expectedStatus, Map<String, String> pathVariables) throws Exception {
        MockHttpServletRequestBuilder requestBuilder;

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(url);
        if (pathVariables != null) {
            for (Map.Entry<String, String> entry : pathVariables.entrySet()) {
                url = uriBuilder.queryParam(entry.getKey(), entry.getValue()).toUriString();
            }
        }
        if (method == HttpMethod.GET) {
            requestBuilder = get(url);
        } else if (method == HttpMethod.POST) {
            requestBuilder = post(url).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(content));
        } else if (method == HttpMethod.PATCH) {
            requestBuilder = patch(url).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(content));
        } else if (method == HttpMethod.DELETE) {
            requestBuilder = delete(url);
        } else if (method == HttpMethod.PUT) {
            requestBuilder = put(url).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(content));
        } else {
            throw new IllegalArgumentException("Unsupported method: " + method);
        }
        var response = mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().is(expectedStatus.value()))
                .andReturn()
                .getResponse();
        response.setCharacterEncoding("UTF-8");
        return response;
    }

    protected static <T> T fromResponse(MockHttpServletResponse response, Class<T> clazz) throws JsonProcessingException, UnsupportedEncodingException {
        return objectMapper.readValue(response.getContentAsString(), clazz);
    }

    protected static <T> T fromResponse(MockHttpServletResponse response, TypeReference<T> typeReference) throws JsonProcessingException, UnsupportedEncodingException {
        return objectMapper.readValue(response.getContentAsString(), typeReference);
    }
}
