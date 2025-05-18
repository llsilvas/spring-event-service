package br.dev.leandro.spring.event.entity;

import br.dev.leandro.spring.event.entity.enums.OrganizerStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

import java.time.LocalDateTime;

@Builder
@Entity
@Table(name = "organizers")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Organizer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private String userId;  // UUID do Keycloak

    @NotBlank
    @Column(nullable = false)
    private String organizationName;
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrganizerStatus status = OrganizerStatus.ACTIVE;
    @Email
    @Column(nullable = false)
    private String contactEmail;
    @Column(nullable = false)
    private String contactPhone;
    @NotBlank
    @Column(nullable = false)
    private String documentNumber;
    @CreatedBy
    private String createdBy;
    @LastModifiedBy
    private String updatedBy;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
