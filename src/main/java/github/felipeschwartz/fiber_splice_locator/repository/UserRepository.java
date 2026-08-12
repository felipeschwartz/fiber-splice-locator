package github.felipeschwartz.fiber_splice_locator.repository;

import github.felipeschwartz.fiber_splice_locator.model.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}