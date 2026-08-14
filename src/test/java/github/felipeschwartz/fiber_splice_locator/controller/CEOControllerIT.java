package github.felipeschwartz.fiber_splice_locator.controller;

import github.felipeschwartz.fiber_splice_locator.model.dto.AddressDTO;
import github.felipeschwartz.fiber_splice_locator.model.dto.CEODTO;
import github.felipeschwartz.fiber_splice_locator.service.CEOService;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CEOControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper objectMapper;

    @MockitoBean
    private CEOService ceoService;

    private CEODTO ceoDTO;

    @BeforeEach
    void setUp() {
        AddressDTO addressDTO = new AddressDTO("Rua dos Andradas", "500", null, "Porto Alegre", "RS", "90020-000", "Brasil");
        ceoDTO = new CEODTO(1L, "CEO-001", "Caixa em bom estado", addressDTO);
    }

    @Test
    @WithMockUser(roles = "FIELD_TECHNICIAN")
    void findAll_ReturnsOk() throws Exception {
        when(ceoService.findAll()).thenReturn(List.of(ceoDTO));

        mockMvc.perform(get("/api/ceo/v1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "FIELD_TECHNICIAN")
    void findById_ReturnsOkAndCEO() throws Exception {
        when(ceoService.findById(1L)).thenReturn(ceoDTO);

        mockMvc.perform(get("/api/ceo/v1/id/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.boxNumber").value("CEO-001"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_ReturnsCreated() throws Exception {
        when(ceoService.create(any(CEODTO.class))).thenReturn(ceoDTO);

        mockMvc.perform(post("/api/ceo/v1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ceoDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "FIELD_TECHNICIAN")
    void create_WithFieldTechnicianRole_ReturnsForbidden() throws Exception {
        mockMvc.perform(post("/api/ceo/v1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ceoDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void update_ReturnsOk() throws Exception {
        when(ceoService.update(any(CEODTO.class))).thenReturn(ceoDTO);

        mockMvc.perform(put("/api/ceo/v1/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ceoDTO)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_ReturnsNoContent() throws Exception {
        doNothing().when(ceoService).delete(1L);

        mockMvc.perform(delete("/api/ceo/v1/{id}", 1L))
                .andExpect(status().isNoContent());
    }
}