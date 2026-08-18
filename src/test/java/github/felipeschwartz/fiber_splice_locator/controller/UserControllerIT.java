package github.felipeschwartz.fiber_splice_locator.controller;

import github.felipeschwartz.fiber_splice_locator.model.dto.UserDTO;
import github.felipeschwartz.fiber_splice_locator.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper objectMapper;

    @MockitoBean
    private UserService userService;

    private UserDTO userDTO;

    @BeforeEach
    void setUp() {
        userDTO = new UserDTO(
                1L,
                "Felipe Schwartz",
                "felipe@example.com",
                "123456"
        );
    }

    @Test
    void findAll_ReturnsOkAndLinks() throws Exception {
        when(userService.findAll()).thenReturn(List.of(userDTO));

        mockMvc.perform(get("/api/user/v1")
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.self.href").exists());
    }

    @Test
    void findById_ReturnsOkAndUser() throws Exception {
        when(userService.findById(1L)).thenReturn(userDTO);

        mockMvc.perform(get("/api/user/v1/id/{id}", 1L)
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("felipe@example.com"));
    }

    @Test
    void create_ReturnsCreatedAndLocationHeader() throws Exception {
        when(userService.create(any(UserDTO.class))).thenReturn(userDTO);

        mockMvc.perform(post("/api/user/v1")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.name").value("Felipe Schwartz"));
    }

    @Test
    void create_WithBlankName_ReturnsBadRequest() throws Exception {
        UserDTO invalid = new UserDTO(
                null,
                "",
                "invalido@example.com",
                "123456"
        );

        mockMvc.perform(post("/api/user/v1")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void update_ReturnsOk() throws Exception {
        when(userService.update(any(UserDTO.class))).thenReturn(userDTO);

        mockMvc.perform(put("/api/user/v1/{id}", 1L)
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isOk());
    }

    @Test
    void delete_ReturnsNoContent() throws Exception {
        doNothing().when(userService).delete(1L);

        mockMvc.perform(delete("/api/user/v1/id/{id}", 1L)
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isNoContent());
    }

    @Test
    void findAll_WithWrongRole_ReturnsForbidden() throws Exception {
        mockMvc.perform(get("/api/user/v1")
                        .with(user("technician").roles("FIELD_TECHNICIAN")))
                .andExpect(status().isForbidden());
    }

    @Test
    void findAll_WithoutAuthentication_ReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/user/v1"))
                .andExpect(status().isUnauthorized());
    }
}