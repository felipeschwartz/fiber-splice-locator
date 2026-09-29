package github.felipeschwartz.fiber_splice_locator.service;

import github.felipeschwartz.fiber_splice_locator.config.CustomUserDetails;
import github.felipeschwartz.fiber_splice_locator.model.entities.ServiceOrder;
import github.felipeschwartz.fiber_splice_locator.model.enums.UserRole;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Objects;

// Leitura de OS é livre para técnicos (histórico da CEO); alteração só pelo técnico atribuído ou por admin.
@Component
public class ServiceOrderAccessChecker {

    public void checkCanModify(ServiceOrder serviceOrder) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof CustomUserDetails user)) {
            throw new AccessDeniedException("Not authenticated");
        }

        if (user.getRoles().contains(UserRole.SUPER_ADMIN) || user.getRoles().contains(UserRole.ADMIN)) {
            return;
        }

        Long assignedTechnicianId = serviceOrder != null && serviceOrder.getUser() != null
                ? serviceOrder.getUser().getId()
                : null;
        if (!Objects.equals(assignedTechnicianId, user.getId())) {
            throw new AccessDeniedException("Only the technician assigned to this service order can change it");
        }
    }
}
