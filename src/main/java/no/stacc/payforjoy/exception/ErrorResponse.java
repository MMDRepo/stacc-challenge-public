package no.stacc.payforjoy.exception;

import lombok.*;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ErrorResponse {

    private int status;
    private String message;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    private String path;

    // Additional error details for debugging
    private String errorCode;
    private String details;

    // Constructor for basic error response
    public ErrorResponse(int status, String message, LocalDateTime timestamp, String path) {
        this.status = status;
        this.message = message;
        this.timestamp = timestamp;
        this.path = path;
    }
}

