package github.felipeschwartz.fiber_splice_locator.controller;

import github.felipeschwartz.fiber_splice_locator.model.dto.ServiceOrderPhotoDTO;
import github.felipeschwartz.fiber_splice_locator.service.ServiceOrderPhotoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.json.JsonMapper;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ServiceOrderPhotoControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper objectMapper;

    @MockitoBean
    private ServiceOrderPhotoService photoService;

    private ServiceOrderPhotoDTO photoDTO;

    @BeforeEach
    void setUp() {
        photoDTO = new ServiceOrderPhotoDTO(
                1L,
                1L,
                "1/photo.jpg",
                "original.jpg",
                "photo.jpg",
                "image/jpeg",
                1024L,
                1,
                LocalDateTime.now()
        );
    }

    @Test
    void findAllByServiceOrder_ReturnsOk() throws Exception {
        when(photoService.findAllByServiceOrder(1L)).thenReturn(List.of(photoDTO));

        mockMvc.perform(get("/api/service_order_photos/v1/service-order/{serviceOrderId}", 1L)
                        .with(user("technician").roles("FIELD_TECHNICIAN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].originalFilename").value("original.jpg"));
    }

    @Test
    void upload_ReturnsCreated() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "original.jpg",
                "image/jpeg",
                "conteudo de teste".getBytes()
        );

        when(photoService.savePhoto(eq(1L), any())).thenReturn(photoDTO);

        mockMvc.perform(multipart("/api/service_order_photos/v1/service-order/{serviceOrderId}", 1L)
                        .file(file)
                        .with(user("technician").roles("FIELD_TECHNICIAN")))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));
    }

    @Test
    void delete_ReturnsNoContent() throws Exception {
        doNothing().when(photoService).delete(1L);

        mockMvc.perform(delete("/api/service_order_photos/v1/{id}", 1L)
                        .with(user("god").roles("GOD_ADMIN")))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_WithFieldTechnicianRole_ReturnsForbidden() throws Exception {
        mockMvc.perform(delete("/api/service_order_photos/v1/{id}", 1L)
                        .with(user("technician").roles("FIELD_TECHNICIAN")))
                .andExpect(status().isForbidden());
    }

    @Test
    void findById_ReturnsOk() throws Exception {
        when(photoService.findById(1L)).thenReturn(photoDTO);

        mockMvc.perform(get("/api/service_order_photos/v1/{id}", 1L)
                        .with(user("technician").roles("FIELD_TECHNICIAN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.originalFilename").value("original.jpg"));
    }

    @Test
    void update_ReturnsOk() throws Exception {
        when(photoService.update(eq(1L), any(ServiceOrderPhotoDTO.class)))
                .thenReturn(photoDTO);

        mockMvc.perform(put("/api/service_order_photos/v1/{id}", 1L)
                        .with(user("technician").roles("FIELD_TECHNICIAN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(photoDTO)))
                .andExpect(status().isOk());
    }
}