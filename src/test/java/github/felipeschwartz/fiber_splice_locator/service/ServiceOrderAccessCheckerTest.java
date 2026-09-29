package github.felipeschwartz.fiber_splice_locator.service;

import github.felipeschwartz.fiber_splice_locator.config.CustomUserDetails;
import github.felipeschwartz.fiber_splice_locator.model.entities.ServiceOrder;
import github.felipeschwartz.fiber_splice_locator.model.entities.User;
import github.felipeschwartz.fiber_splice_locator.model.enums.ServiceOrderStatus;
import github.felipeschwartz.fiber_splice_locator.model.enums.UserRole;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ServiceOrderAccessCheckerTest {

    private final ServiceOrderAccessChecker checker = new ServiceOrderAccessChecker();

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void checkCanModify_WhenTechnicianIsAssigned_Allows() {
        User technician = user(10L, UserRole.FIELD_TECHNICIAN);
        loginAs(technician);

        assertDoesNotThrow(() -> checker.checkCanModify(serviceOrderAssignedTo(technician)));
    }

    @Test
    void checkCanModify_WhenTechnicianIsNotAssigned_Denies() {
        loginAs(user(10L, UserRole.FIELD_TECHNICIAN));
        ServiceOrder othersOrder = serviceOrderAssignedTo(user(20L, UserRole.FIELD_TECHNICIAN));

        assertThrows(AccessDeniedException.class, () -> checker.checkCanModify(othersOrder));
    }

    @Test
    void checkCanModify_WhenAdmin_AllowsAnyServiceOrder() {
        loginAs(user(1L, UserRole.ADMIN));

        assertDoesNotThrow(() -> checker.checkCanModify(serviceOrderAssignedTo(user(20L, UserRole.FIELD_TECHNICIAN))));
    }

    @Test
    void checkCanModify_WhenSuperAdmin_AllowsAnyServiceOrder() {
        loginAs(user(1L, UserRole.SUPER_ADMIN));

        assertDoesNotThrow(() -> checker.checkCanModify(serviceOrderAssignedTo(user(20L, UserRole.FIELD_TECHNICIAN))));
    }

    @Test
    void checkCanModify_WhenTechnicianAndNoServiceOrder_Denies() {
        loginAs(user(10L, UserRole.FIELD_TECHNICIAN));

        assertThrows(AccessDeniedException.class, () -> checker.checkCanModify(null));
    }

    @Test
    void checkCanModify_WhenNotAuthenticated_Denies() {
        ServiceOrder order = serviceOrderAssignedTo(user(10L, UserRole.FIELD_TECHNICIAN));

        assertThrows(AccessDeniedException.class, () -> checker.checkCanModify(order));
    }

    private static User user(Long id, UserRole role) {
        User user = new User(id, "User " + id, "user" + id + "@example.com", "encodedPassword", true);
        user.setRoles(Set.of(role));
        return user;
    }

    private static ServiceOrder serviceOrderAssignedTo(User technician) {
        return new ServiceOrder(1L, null, ServiceOrderStatus.OPEN, technician, LocalDateTime.now(), null);
    }

    private static void loginAs(User user) {
        CustomUserDetails principal = new CustomUserDetails(user);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));
    }
}
