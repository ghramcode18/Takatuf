package geekcode.takatuf.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "messages")

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Setter
@Getter

public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;
    private LocalDateTime timestamp;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender;
    
    @ManyToOne
    @JoinColumn(name = "receiver_id", insertable = false, updatable = false)
    private User receiver;
}