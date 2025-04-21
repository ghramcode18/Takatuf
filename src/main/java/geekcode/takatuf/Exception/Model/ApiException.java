package geekcode.takatuf.Exception.Model;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiException {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String localizedMessage;
    private String path;
    private String errorCode;
}