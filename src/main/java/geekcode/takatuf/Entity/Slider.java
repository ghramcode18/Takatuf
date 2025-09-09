package geekcode.takatuf.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "sliders")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class Slider {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;

    private String imageUrl;
    private String targetUrl;
    private String type; // "STORE", "PRODUCT", "LINK", "NONE"

    private boolean active;
    private Integer priority;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "store_id")
    private Store store;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    private String linkUrl;

}
