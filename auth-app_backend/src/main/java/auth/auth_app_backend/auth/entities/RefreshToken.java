package auth.auth_app_backend.auth.entities;

import jakarta.persistence.*;
import lombok.*;
import auth.auth_app_backend.auth.entities.User;
import java.time.Instant;
import java.util.UUID;
    @Entity
    @Table(name="refresh Token",indexes={
            @Index(name="refresh_tokens_jti_idx",columnList = "jti",unique = true),
            @Index(name="refresh_tokens_user_id_idx",columnList = "user_id")

    })

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
    public class RefreshToken{
        @Id
        @GeneratedValue(strategy = GenerationType.UUID)
        private UUID id;
        @Column(name="jti",unique = true,nullable = false,updatable = false)
        private String jti;
        @ManyToOne(optional = false,fetch  =FetchType.LAZY)
        @JoinColumn(name="user_id",nullable=false,updatable = false)

        private User user;
        @Column(nullable=false,updatable = false)
        private Instant createdAt;
        @Column(nullable=false)
        private Instant expiresAt;

        @Column(nullable = false)
        private boolean revoked;
        private String replacedByToken;
    }

