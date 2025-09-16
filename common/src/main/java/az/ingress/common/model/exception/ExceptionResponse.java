package az.ingress.common.model.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExceptionResponse {
    private String message;
    private String details;
    private String path;
    private Integer status;
    private String timestamp;
    private Map<String, String> validationErrors;
}
