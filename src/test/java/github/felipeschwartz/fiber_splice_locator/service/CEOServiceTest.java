package github.felipeschwartz.fiber_splice_locator.service;

import github.felipeschwartz.fiber_splice_locator.mapper.CEOMapper;
import github.felipeschwartz.fiber_splice_locator.model.dto.AddressDTO;
import github.felipeschwartz.fiber_splice_locator.model.dto.CEODTO;
import github.felipeschwartz.fiber_splice_locator.model.entities.Address;
import github.felipeschwartz.fiber_splice_locator.model.entities.CEO;
import github.felipeschwartz.fiber_splice_locator.repository.CEORepository;
import github.felipeschwartz.fiber_splice_locator.service.exceptions.ObjectNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CEOServiceTest {

    @Mock
    private CEORepository ceoRepository;

    @Mock
    private CEOMapper ceoMapper;

    @InjectMocks
    private CEOService ceoService;

    private CEO ceo;
    private CEODTO ceoDTO;
    private Address address;
    private AddressDTO addressDTO;

    @BeforeEach
    void setUp() {
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));

        address = new Address(1L, null, "Rua", "Rua dos Andradas", "500", null, "Centro", "Porto Alegre");
        addressDTO = new AddressDTO("Rua dos Andradas", "500", null, "Porto Alegre", "RS", "90020-000", "Brasil");

        ceo = new CEO(1L, "CEO-001", "Caixa em bom estado", address);
        ceoDTO = new CEODTO(1L, "CEO-001", "Caixa em bom estado", addressDTO);
    }

    @Test
    void findAll_ReturnsListOfCEODTO() {
        when(ceoRepository.findAll()).thenReturn(List.of(ceo));
        when(ceoMapper.toDTO(ceo)).thenReturn(ceoDTO);

        List<CEODTO> result = ceoService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void findById_WhenCEOExists_ReturnsCEODTO() {
        when(ceoRepository.findById(1L)).thenReturn(Optional.of(ceo));
        when(ceoMapper.toDTO(ceo)).thenReturn(ceoDTO);

        CEODTO result = ceoService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void findById_WhenCEODoesNotExist_ThrowsException() {
        when(ceoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> ceoService.findById(99L));
    }

    @Test
    void create_ReturnsCreatedCEODTO() {
        when(ceoMapper.toEntity(ceoDTO)).thenReturn(ceo);
        when(ceoRepository.save(ceo)).thenReturn(ceo);
        when(ceoMapper.toDTO(ceo)).thenReturn(ceoDTO);

        CEODTO result = ceoService.create(ceoDTO);

        assertNotNull(result);
        verify(ceoRepository, times(1)).save(ceo);
    }

    @Test
    void update_WhenCEOExists_ReturnsUpdatedCEODTO() {
        when(ceoRepository.findById(1L)).thenReturn(Optional.of(ceo));
        doNothing().when(ceoMapper).updateEntityFromDTO(ceoDTO, ceo);
        when(ceoRepository.save(ceo)).thenReturn(ceo);
        when(ceoMapper.toDTO(ceo)).thenReturn(ceoDTO);

        CEODTO result = ceoService.update(ceoDTO);

        assertNotNull(result);
    }

    @Test
    void update_WhenCEODoesNotExist_ThrowsException() {
        CEODTO nonExisting = new CEODTO(99L, "CEO-999", "x", addressDTO);
        when(ceoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> ceoService.update(nonExisting));
    }

    @Test
    void delete_WhenCEOExists_DeletesCEO() {
        when(ceoRepository.existsById(1L)).thenReturn(true);

        ceoService.delete(1L);

        verify(ceoRepository, times(1)).deleteById(1L);
    }

    @Test
    void delete_WhenCEODoesNotExist_ThrowsException() {
        when(ceoRepository.existsById(99L)).thenReturn(false);

        assertThrows(ObjectNotFoundException.class, () -> ceoService.delete(99L));
    }
}