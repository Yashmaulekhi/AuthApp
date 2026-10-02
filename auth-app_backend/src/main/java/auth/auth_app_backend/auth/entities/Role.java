package auth.auth_app_backend.auth.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

@Entity

@Table(name="Role")
public class Role {
    @Id
    private UUID id=UUID.randomUUID();
    @Column(unique=true,nullable=false)
    private String name;
}
