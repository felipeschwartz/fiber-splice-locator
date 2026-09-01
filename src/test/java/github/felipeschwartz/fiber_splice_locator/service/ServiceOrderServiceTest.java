package github.felipeschwartz.fiber_splice_locator.service;

import github.felipeschwartz.fiber_splice_locator.mapper.ServiceOrderMapper;
import github.felipeschwartz.fiber_splice_locator.model.dto.CEODTO;
import github.felipeschwartz.fiber_splice_locator.model.dto.ServiceOrderDTO;
import github.felipeschwartz.fiber_splice_locator.model.dto.UserDTO;
import github.felipeschwartz.fiber_splice_locator.model.entities.CEO;
import github.felipeschwartz.fiber_splice_locator.model.entities.ServiceOrder;
import github.felipeschwartz.fiber_splice_locator.model.entities.User;
import github.felipeschwartz.fiber_splice_locator.model.enums.CEOStatus;
import github.felipeschwartz.fiber_splice_locator.model.enums.ServiceOrderStatus;
import github.felipeschwartz.fiber_splice_locator.repository.CEORepository;
import github.felipeschwartz.fiber_splice_locator.repository.ServiceOrderRepository;
import github.felipeschwartz.fiber_splice_locator.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceOrderServiceTest  {

    @Mock
    private ServiceOrderRepository serviceOrderRepository;

    @Mock
    private ServiceOrderMapper serviceOrderMapper;

    @Mock
    private CEORepository ceoRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ServiceOrderService serviceOrderService;

    private ServiceOrder serviceOrder;
    private ServiceOrderDTO serviceOrderDTO;

    @BeforeEach
    void setUp() {
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));

        CEO ceo = new CEO(1L, "CEO-001", "Observação", null, CEOStatus.STANDARDIZED);
        CEODTO ceoDTO = new CEODTO(1L, "CEO-001", "Observação", null, CEOStatus.STANDARDIZED);

        User user = new User(1L, "Felipe Schwartz", "felipe@example.com", "encodedPassword", true);
        UserDTO userDTO = new UserDTO(1L, "Felipe Schwartz", "felipe@example.com", "123456", true);

        serviceOrder = new ServiceOrder(1L, ceo, ServiceOrderStatus.OPEN, user, LocalDateTime.now(), null);
        serviceOrderDTO = new ServiceOrderDTO(1L, ceoDTO, ServiceOrderStatus.OPEN, userDTO, new HashSet<>(), LocalDateTime.now(), null);
    }

    @Test
    void findAll_ReturnsListOfServiceOrderDTO() {
        when(serviceOrderRepository.findAll()).thenReturn(List.of(serviceOrder));
        when(serviceOrderMapper.toDTO(serviceOrder)).thenReturn(serviceOrderDTO);

        List<ServiceOrderDTO> result = serviceOrderService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void findById_WhenServiceOrderExists_ReturnsServiceOrderDTO() {
        when(serviceOrderRepository.findById(1L)).thenReturn(Optional.of(serviceOrder));
        when(serviceOrderMapper.toDTO(serviceOrder)).thenReturn(serviceOrderDTO);

        ServiceOrderDTO result = serviceOrderService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getServiceOrderId());
    }

    @Test
    void findById_WhenServiceOrderDoesNotExist_ThrowsException() {
        when(serviceOrderRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> serviceOrderService.findById(99L));
    }

    @Test
    void create_ReturnsCreatedServiceOrderDTO() {
        when(ceoRepository.findById(1L)).thenReturn(Optional.of(serviceOrder.getCeo()));
        when(userRepository.findById(1L)).thenReturn(Optional.of(serviceOrder.getUser()));
        when(serviceOrderRepository.save(any(ServiceOrder.class))).thenReturn(serviceOrder);
        when(serviceOrderMapper.toDTO(serviceOrder)).thenReturn(serviceOrderDTO);

        ServiceOrderDTO result = serviceOrderService.create(serviceOrderDTO);

        assertNotNull(result);
        verify(serviceOrderRepository, times(1)).save(any(ServiceOrder.class));
    }

    @Test
    void update_WhenServiceOrderExists_ReturnsUpdatedServiceOrderDTO() {
        when(serviceOrderRepository.findById(1L)).thenReturn(Optional.of(serviceOrder));
        doNothing().when(serviceOrderMapper).updateEntityFromDTO(serviceOrderDTO, serviceOrder);
        when(serviceOrderRepository.save(serviceOrder)).thenReturn(serviceOrder);
        when(serviceOrderMapper.toDTO(serviceOrder)).thenReturn(serviceOrderDTO);

        ServiceOrderDTO result = serviceOrderService.update(1L, serviceOrderDTO);

        assertNotNull(result);
    }

    @Test
    void update_WhenServiceOrderDoesNotExist_ThrowsException() {
        when(serviceOrderRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> serviceOrderService.update(99L, serviceOrderDTO));
    }

    @Test
    void delete_WhenServiceOrderExists_DeletesServiceOrder() {
        when(serviceOrderRepository.existsById(1L)).thenReturn(true);

        serviceOrderService.delete(1L);

        verify(serviceOrderRepository, times(1)).deleteById(1L);
    }

    @Test
    void delete_WhenServiceOrderDoesNotExist_ThrowsException() {
        when(serviceOrderRepository.existsById(99L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> serviceOrderService.delete(99L));
    }
}