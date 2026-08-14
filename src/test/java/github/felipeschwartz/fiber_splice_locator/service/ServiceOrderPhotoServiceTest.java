package github.felipeschwartz.fiber_splice_locator.service;

import github.felipeschwartz.fiber_splice_locator.mapper.ServiceOrderPhotoMapper;
import github.felipeschwartz.fiber_splice_locator.model.dto.ServiceOrderPhotoDTO;
import github.felipeschwartz.fiber_splice_locator.model.entities.ServiceOrder;
import github.felipeschwartz.fiber_splice_locator.model.entities.ServiceOrderPhoto;
import github.felipeschwartz.fiber_splice_locator.model.enums.ServiceOrderStatus;
import github.felipeschwartz.fiber_splice_locator.repository.ServiceOrderPhotoRepository;
import github.felipeschwartz.fiber_splice_locator.repository.ServiceOrderRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceOrderPhotoServiceTest {

    @TempDir
    Path tempDir;

    @Mock
    private ServiceOrderRepository serviceOrderRepository;

    @Mock
    private ServiceOrderPhotoRepository photoRepository;

    @Mock
    private ServiceOrderPhotoMapper photoMapper;

    private ServiceOrderPhotoService photoService;

    private ServiceOrder serviceOrder;
    private ServiceOrderPhoto photo;
    private ServiceOrderPhotoDTO photoDTO;

    @BeforeEach
    void setUp() {
        photoService = new ServiceOrderPhotoService(
                tempDir.toString(),
                serviceOrderRepository,
                photoRepository,
                photoMapper
        );

        serviceOrder = new ServiceOrder(1L, null, ServiceOrderStatus.OPEN, null, LocalDateTime.now(), null);
        photo = new ServiceOrderPhoto(1L, serviceOrder, "1/photo.jpg", "original.jpg", "photo.jpg", "image/jpeg", 1024L, 1, LocalDateTime.now());
        photoDTO = new ServiceOrderPhotoDTO(1L, 1L, "1/photo.jpg", "original.jpg", "photo.jpg", "image/jpeg", 1024L, 1, LocalDateTime.now());
    }

    @Test
    void findAllByServiceOrder_WhenServiceOrderExists_ReturnsPhotoList() {
        when(serviceOrderRepository.existsById(1L)).thenReturn(true);
        when(photoRepository.findByServiceOrder_ServiceOrderId(1L)).thenReturn(List.of(photo));
        when(photoMapper.toDTO(photo)).thenReturn(photoDTO);

        List<ServiceOrderPhotoDTO> result = photoService.findAllByServiceOrder(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void findAllByServiceOrder_WhenServiceOrderDoesNotExist_ThrowsException() {
        when(serviceOrderRepository.existsById(99L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> photoService.findAllByServiceOrder(99L));
    }

    @Test
    void findById_WhenPhotoExists_ReturnsPhotoDTO() {
        when(photoRepository.findById(1L)).thenReturn(Optional.of(photo));
        when(photoMapper.toDTO(photo)).thenReturn(photoDTO);

        ServiceOrderPhotoDTO result = photoService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void findById_WhenPhotoDoesNotExist_ThrowsException() {
        when(photoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> photoService.findById(99L));
    }

    @Test
    void savePhoto_WhenFileIsValid_SavesAndReturnsPhotoDTO() throws IOException {
        MockMultipartFile file = new MockMultipartFile(
                "file", "original.jpg", "image/jpeg", "conteudo de teste".getBytes()
        );

        when(serviceOrderRepository.findById(1L)).thenReturn(Optional.of(serviceOrder));
        when(photoRepository.countByServiceOrder_ServiceOrderId(1L)).thenReturn(0);
        when(photoRepository.save(any(ServiceOrderPhoto.class))).thenReturn(photo);
        when(photoMapper.toDTO(photo)).thenReturn(photoDTO);

        ServiceOrderPhotoDTO result = photoService.savePhoto(1L, file);

        assertNotNull(result);
        verify(photoRepository, times(1)).save(any(ServiceOrderPhoto.class));
    }

    @Test
    void savePhoto_WhenFileIsEmpty_ThrowsException() {
        MockMultipartFile emptyFile = new MockMultipartFile("file", "empty.jpg", "image/jpeg", new byte[0]);

        assertThrows(IllegalArgumentException.class, () -> photoService.savePhoto(1L, emptyFile));
    }

    @Test
    void savePhoto_WhenContentTypeIsNotAllowed_ThrowsException() {
        MockMultipartFile invalidFile = new MockMultipartFile("file", "file.txt", "text/plain", "conteudo".getBytes());

        assertThrows(IllegalArgumentException.class, () -> photoService.savePhoto(1L, invalidFile));
    }

    @Test
    void savePhoto_WhenServiceOrderDoesNotExist_ThrowsException() {
        MockMultipartFile file = new MockMultipartFile("file", "original.jpg", "image/jpeg", "conteudo".getBytes());
        when(serviceOrderRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> photoService.savePhoto(99L, file));
    }

    @Test
    void update_WhenPhotoExists_ReturnsUpdatedPhotoDTO() {
        when(photoRepository.findById(1L)).thenReturn(Optional.of(photo));
        doNothing().when(photoMapper).updateEntityFromDTO(photoDTO, photo);
        when(photoRepository.save(photo)).thenReturn(photo);
        when(photoMapper.toDTO(photo)).thenReturn(photoDTO);

        ServiceOrderPhotoDTO result = photoService.update(1L, photoDTO);

        assertNotNull(result);
    }

    @Test
    void update_WhenPhotoDoesNotExist_ThrowsException() {
        when(photoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> photoService.update(99L, photoDTO));
    }

    @Test
    void delete_WhenPhotoExists_DeletesPhoto() {
        when(photoRepository.findById(1L)).thenReturn(Optional.of(photo));

        photoService.delete(1L);

        verify(photoRepository, times(1)).delete(photo);
    }

    @Test
    void delete_WhenPhotoDoesNotExist_ThrowsException() {
        when(photoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> photoService.delete(99L));
    }
}