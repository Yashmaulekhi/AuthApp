package auth.auth_app_backend.auth.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor


public class RoleDto {

    private UUID id=UUID.randomUUID();

    private String name;
}
