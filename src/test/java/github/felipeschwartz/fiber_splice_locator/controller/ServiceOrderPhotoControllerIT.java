package github.felipeschwartz.fiber_splice_locator.controller;

import github.felipeschwartz.fiber_splice_locator.model.dto.ServiceOrderPhotoDTO;
import github.felipeschwartz.fiber_splice_locator.service.ServiceOrderPhotoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ServiceOrderPhotoControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ServiceOrderPhotoService photoService;

    private ServiceOrderPhotoDTO photoDTO;

    @BeforeEach
    void setUp() {
        photoDTO = new ServiceOrderPhotoDTO(1L, 1L, "1/photo.jpg", "original.jpg", "photo.jpg", "image/jpeg", 1024L, 1, LocalDateTime.now());
    }

    @Test
    @WithMockUser(roles = "FIELD_TECHNICIAN")
    void findAllByServiceOrder_ReturnsOk() throws Exception {
        when(photoService.findAllByServiceOrder(1L)).thenReturn(List.of(photoDTO));

        mockMvc.perform(get("/api/service_order_photos/v1/service-order/{serviceOrderId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].originalFilename").value("original.jpg"));
    }

    @Test
    @WithMockUser(roles = "FIELD_TECHNICIAN")
    void upload_ReturnsCreated() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "original.jpg", "image/jpeg", "conteudo de teste".getBytes()
        );

        when(photoService.savePhoto(eq(1L), any())).thenReturn(photoDTO);

        mockMvc.perform(multipart("/api/service_order_photos/v1/service-order/{serviceOrderId}", 1L)
                        .file(file))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_ReturnsNoContent() throws Exception {
        doNothing().when(photoService).delete(1L);

        mockMvc.perform(delete("/api/service_order_photos/v1/{id}", 1L))
                .andExpect(status().isNoContent());
    }
}