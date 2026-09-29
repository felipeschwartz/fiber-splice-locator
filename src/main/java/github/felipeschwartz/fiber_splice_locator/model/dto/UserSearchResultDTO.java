package github.felipeschwartz.fiber_splice_locator.model.dto;

// Aberta a qualquer usuário logado (agenda de colegas): por isso sem perfis nem situação da conta.
public record UserSearchResultDTO(Long id, String name, String email) {}
