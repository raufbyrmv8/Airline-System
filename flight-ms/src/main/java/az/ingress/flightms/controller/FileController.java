package az.ingress.flightms.controller;
import az.ingress.flightms.service.impl.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/files")
public class FileController {
    private final FileService fileService;
    @GetMapping
    public ResponseEntity<Resource> testFile(@RequestParam(value = "name") String fileName){
        return fileService.getFile(fileName);
    }
}
