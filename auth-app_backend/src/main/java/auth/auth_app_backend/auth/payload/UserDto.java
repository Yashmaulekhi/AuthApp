package auth.auth_app_backend.auth.payload;

import auth.auth_app_backend.auth.entities.Provider;
import lombok.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;


@Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
public class UserDto {

    private UUID uId;

    private String name;

    private String email;
    private String image;
    private String password;
    private boolean enabled=true;
    private Instant createdAt= Instant.now();
    private Instant updatedAt=  Instant.now();

    private Provider provider=Provider.Local;
    private String gender;

    private Set<RoleDto> role=new HashSet<>();

}
