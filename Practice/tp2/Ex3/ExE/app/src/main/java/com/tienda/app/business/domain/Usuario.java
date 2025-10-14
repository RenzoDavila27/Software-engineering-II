import com.tienda.app.business.domain.BaseEntity;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Usuario extends BaseEntity<Long> {
    
    private String nombre;
    private String password;
    
}
