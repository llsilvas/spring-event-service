package br.dev.leandro.spring.event.audit;


import br.dev.leandro.spring.event.entity.User;
import br.dev.leandro.spring.event.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AuditorAwareImpl implements AuditorAware<User> {

    private final UserRepository userRepository;

    @Override
    public Optional<User> getCurrentAuditor() {
        var auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            return Optional.empty();
        }

        Object principal = auth.getPrincipal();

        if (principal instanceof Jwt jwt) {
            String email = jwt.getClaimAsString("email"); // ou preferred_username
            return userRepository.findByEmail(email);
        }

        return Optional.empty();
    }
}
