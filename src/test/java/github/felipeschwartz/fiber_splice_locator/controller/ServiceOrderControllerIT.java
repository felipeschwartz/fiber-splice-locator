package github.felipeschwartz.fiber_splice_locator.controller;

import github.felipeschwartz.fiber_splice_locator.model.dto.ServiceOrderDTO;
import github.felipeschwartz.fiber_splice_locator.model.enums.ServiceOrderStatus;
import github.felipeschwartz.fiber_splice_locator.service.ServiceOrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.json.JsonMapper;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ServiceOrderControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper objectMapper;

    @MockitoBean
    private ServiceOrderService serviceOrderService;

    private ServiceOrderDTO serviceOrderDTO;

    @BeforeEach
    void setUp() {
        objectMapper.registeredModules();
        serviceOrderDTO = new ServiceOrderDTO(1L, null, ServiceOrderStatus.OPEN, null, null, LocalDateTime.now(), null);
    }

    @Test
    @WithMockUser(roles = "FIELD_TECHNICIAN")
    void findAll_ReturnsOk() throws Exception {
        when(serviceOrderService.findAll()).thenReturn(List.of(serviceOrderDTO));

        mockMvc.perform(get("/api/service_orders/v1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("OPEN"));
    }

    @Test
    @WithMockUser(roles = "FIELD_TECHNICIAN")
    void findById_ReturnsOk() throws Exception {
        when(serviceOrderService.findById(1L)).thenReturn(serviceOrderDTO);

        mockMvc.perform(get("/api/service_orders/v1/id/{id}", 1L))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_ReturnsCreated() throws Exception {
        when(serviceOrderService.create(any(ServiceOrderDTO.class))).thenReturn(serviceOrderDTO);

        mockMvc.perform(post("/api/service_orders/v1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(serviceOrderDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "FIELD_TECHNICIAN")
    void update_ReturnsOk() throws Exception {
        when(serviceOrderService.update(eq(1L), any(ServiceOrderDTO.class))).thenReturn(serviceOrderDTO);

        mockMvc.perform(put("/api/service_orders/v1/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(serviceOrderDTO)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_ReturnsNoContent() throws Exception {
        doNothing().when(serviceOrderService).delete(1L);

        mockMvc.perform(delete("/api/service_orders/v1/{id}", 1L))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "FIELD_TECHNICIAN")
    void delete_WithFieldTechnicianRole_ReturnsForbidden() throws Exception {
        mockMvc.perform(delete("/api/service_orders/v1/{id}", 1L))
                .andExpect(status().isForbidden());
    }
}