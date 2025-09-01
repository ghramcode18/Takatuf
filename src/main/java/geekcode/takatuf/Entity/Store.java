package geekcode.takatuf.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Lob;

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@Table(name = "stores")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Setter
@Getter

public class Store {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    private String name;
    @Lob
    @Column(columnDefinition = "TEXT")
    private String description;
    private String status;
    private LocalDateTime createdAt;
    @Column(name = "image_url")
    private String imageUrl;

    @OneToMany(mappedBy = "store")
    private List<Product> products;

}
