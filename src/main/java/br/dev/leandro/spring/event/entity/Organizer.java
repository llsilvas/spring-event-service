package br.dev.leandro.spring.event.entity;

import br.dev.leandro.spring.event.entity.enums.OrganizerStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Entity
@Table(name = "organizers")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Organizer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36,
            columnDefinition = "CHAR(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci",
            updatable = false,
            nullable = false
    )
    @JdbcTypeCode(SqlTypes.CHAR)
    private UUID id;

    @Column(name = "user_id", nullable = false, unique = true)
    private String userId;  // UUID do Keycloak

    @NotBlank
    @Column(nullable = false)
    private String organizationName;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrganizerStatus status;
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

    @Version
    @Builder.Default
    private Long version = 0L;
}
