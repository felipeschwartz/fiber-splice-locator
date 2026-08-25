package github.felipeschwartz.fiber_splice_locator.controller;

import github.felipeschwartz.fiber_splice_locator.model.dto.AddressDTO;
import github.felipeschwartz.fiber_splice_locator.model.dto.CEODTO;
import github.felipeschwartz.fiber_splice_locator.model.enums.CEOStatus;
import github.felipeschwartz.fiber_splice_locator.service.CEOService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.junit.jupiter.api.Assertions.assertNotNull; // Importar para as asserções

@SpringBootTest
@AutoConfigureMockMvc
class CEOControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper objectMapper; // Usado para serializar o DTO para JSON

    @MockitoBean
    private CEOService ceoService;

    private CEODTO ceoDTO;

    @BeforeEach
    void setUp() {
        AddressDTO addressDTO = new AddressDTO(
                1L, "-30.0346,-51.2177", "Street",
                "Rua dos Andradas", "500", "Próximo ao Mercado Público",
                "Centro",
                "Porto Alegre"
        );
        ceoDTO = new CEODTO(
                1L, "CEO-001", "Caixa em bom estado", addressDTO, CEOStatus.STANDARDIZED
        );
    }

    @Test
    void findAll_ReturnsOk() throws Exception {
        when(ceoService.findAll()).thenReturn(List.of(ceoDTO));

        mockMvc.perform(get("/api/ceo/v1")
                        .with(user("technician").roles("FIELD_TECHNICIAN")))
                .andExpect(status().isOk());
    }

    @Test
    void findById_ReturnsOkAndCEO() throws Exception {
        when(ceoService.findById(1L)).thenReturn(ceoDTO);

        mockMvc.perform(get("/api/ceo/v1/id/{id}", 1L)
                        .with(user("technician").roles("FIELD_TECHNICIAN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.boxNumber").value("CEO-001"));
    }

    @Test
    void create_ReturnsCreated() throws Exception {
        // Mock do serviço para retornar o ceoDTO quando create for chamado
        when(ceoService.create(any(CEODTO.class))).thenReturn(ceoDTO);

        // Realiza a requisição POST usando MockMvc
        String responseContent = mockMvc.perform(post("/api/ceo/v1")
                        .with(user("admin").roles("ADMIN")) // Simula um usuário ADMIN
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ceoDTO))) // Corpo da requisição
                .andExpect(status().isCreated()) // Espera status 201 Created
                .andReturn().getResponse().getContentAsString(); // Captura o corpo da resposta

        // Desserializa a resposta para um CEODTO
        CEODTO createdCEO = objectMapper.readValue(responseContent, CEODTO.class);

        // Realiza as asserções sobre o objeto retornado
        assertNotNull(createdCEO.getId());
        assertNotNull(createdCEO.getBoxNumber());
        assertNotNull(createdCEO.getNotes());
        assertNotNull(createdCEO.getAddress());
        assertNotNull(createdCEO.getStatus());
        // Adicione mais asserções conforme necessário para o seu CEODTO
    }

//    @Test
//    void create_WithFieldTechnicianRole_ReturnsForbidden() throws Exception {
//        mockMvc.perform(post("/api/ceo/v1")
//                        .with(user("technician").roles("FIELD_TECHNICIAN"))
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(ceoDTO)))
//                .andExpect(status().isForbidden());
//    }

    @Test
    void update_ReturnsOk() throws Exception {
        when(ceoService.update(any(CEODTO.class))).thenReturn(ceoDTO);

        mockMvc.perform(put("/api/ceo/v1/id/{id}", 1L)
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ceoDTO)))
                .andExpect(status().isOk());
    }

    @Test
    void delete_ReturnsNoContent() throws Exception {
        doNothing().when(ceoService).delete(1L);

        mockMvc.perform(delete("/api/ceo/v1/id/{id}", 1L)
                        .with(user("god").roles("GOD_ADMIN")))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_WithFieldTechnicianRole_ReturnsForbidden() throws Exception {
        mockMvc.perform(delete("/api/ceo/v1/id/{id}", 1L)
                        .with(user("technician").roles("FIELD_TECHNICIAN")))
                .andExpect(status().isForbidden());
    }
}