package github.felipeschwartz.fiber_splice_locator.service;

import github.felipeschwartz.fiber_splice_locator.mapper.CEOMapper;
import github.felipeschwartz.fiber_splice_locator.model.dto.AddressDTO;
import github.felipeschwartz.fiber_splice_locator.model.dto.CEODTO;
import github.felipeschwartz.fiber_splice_locator.model.entities.Address;
import github.felipeschwartz.fiber_splice_locator.model.entities.CEO;
import github.felipeschwartz.fiber_splice_locator.model.enums.CEOStatus;
import github.felipeschwartz.fiber_splice_locator.repository.CEORepository;
import github.felipeschwartz.fiber_splice_locator.service.exceptions.ObjectNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
        addressDTO = new AddressDTO(1L, null, "Rua", "Rua dos Andradas", "500", null, "Centro", "Porto Alegre");

        ceo = new CEO(1L, "CEO-001", "Caixa em bom estado", address, CEOStatus.STANDARDIZED);
        ceoDTO = new CEODTO(1L, "CEO-001", "Caixa em bom estado", addressDTO, CEOStatus.STANDARDIZED);
    }

    @Test
    void findAll_ReturnsListOfCEODTO() {
        Pageable pageable = PageRequest.of(0, 20);
        when(ceoRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(ceo)));
        when(ceoMapper.toDTO(ceo)).thenReturn(ceoDTO);

        Page<CEODTO> result = ceoService.findAll(pageable, null);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
    }

    @Test
    void findAll_WithStatusFilter_ReturnsOnlyMatchingCEODTO() {
        Pageable pageable = PageRequest.of(0, 20);
        List<CEOStatus> statuses = List.of(CEOStatus.DAMAGED);
        when(ceoRepository.findByStatusIn(statuses, pageable)).thenReturn(new PageImpl<>(List.of(ceo)));
        when(ceoMapper.toDTO(ceo)).thenReturn(ceoDTO);

        Page<CEODTO> result = ceoService.findAll(pageable, statuses);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(ceoRepository, never()).findAll(pageable);
    }

    @Test
    void findAll_WithMultipleStatuses_ReturnsCombinedCEODTO() {
        Pageable pageable = PageRequest.of(0, 20);
        List<CEOStatus> statuses = List.of(CEOStatus.DAMAGED, CEOStatus.UNDER_MAINTENANCE);
        when(ceoRepository.findByStatusIn(statuses, pageable)).thenReturn(new PageImpl<>(List.of(ceo)));
        when(ceoMapper.toDTO(ceo)).thenReturn(ceoDTO);

        Page<CEODTO> result = ceoService.findAll(pageable, statuses);

        assertNotNull(result);
        verify(ceoRepository).findByStatusIn(statuses, pageable);
    }

    @Test
    void findAll_SortedByStatus_UsesSeverityRanking() {
        Pageable pageable = PageRequest.of(0, 20, Sort.by(Sort.Order.asc("status")));
        Pageable plainPageable = PageRequest.of(0, 20);
        when(ceoRepository.findAllOrderByStatusSeverity(1, plainPageable)).thenReturn(new PageImpl<>(List.of(ceo)));
        when(ceoMapper.toDTO(ceo)).thenReturn(ceoDTO);

        Page<CEODTO> result = ceoService.findAll(pageable, null);

        assertNotNull(result);
        verify(ceoRepository).findAllOrderByStatusSeverity(1, plainPageable);
        verify(ceoRepository, never()).findAll(any(Pageable.class));
    }

    @Test
    void findAll_SortedByStatusDescendingWithFilter_UsesSeverityRankingWithFilter() {
        Pageable pageable = PageRequest.of(0, 20, Sort.by(Sort.Order.desc("status")));
        Pageable plainPageable = PageRequest.of(0, 20);
        List<CEOStatus> statuses = List.of(CEOStatus.DAMAGED, CEOStatus.UNDER_MAINTENANCE);
        when(ceoRepository.findByStatusInOrderByStatusSeverity(List.of("DAMAGED", "UNDER_MAINTENANCE"), -1, plainPageable))
                .thenReturn(new PageImpl<>(List.of(ceo)));
        when(ceoMapper.toDTO(ceo)).thenReturn(ceoDTO);

        Page<CEODTO> result = ceoService.findAll(pageable, statuses);

        assertNotNull(result);
        verify(ceoRepository).findByStatusInOrderByStatusSeverity(List.of("DAMAGED", "UNDER_MAINTENANCE"), -1, plainPageable);
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
        CEODTO nonExisting = new CEODTO(99L, "CEO-999", "x", addressDTO, CEOStatus.STANDARDIZED);
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