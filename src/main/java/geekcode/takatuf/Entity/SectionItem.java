package geekcode.takatuf.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "section_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SectionItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long targetId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id")
    private Section section;
}
