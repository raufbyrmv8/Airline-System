package az.ingress.flightms.service.impl;


import az.ingress.common.model.exception.ApplicationException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import static az.ingress.flightms.model.enums.Exceptions.NOT_FOUND;
import static az.ingress.flightms.model.enums.Exceptions.SOMETHING_WENT_WRONG;


@Service
public class FileService {
    @Value("${file.directory}")
    private String DIRECTORY_NAME;
    @Value("${server.name}")
    private String SERVER_NAME;
    @Value("${server.servlet.context-path}")
    private String CONTEXT_PATH;
    @Value("${file.url}")
    private String FILE_URL;


    public String createAndWriteToFile(String example) {
        createDirectory();
        try {
            Path file = Files.createFile(Path.of(System.getProperty("user.dir") + "/" + DIRECTORY_NAME + File.separator + System.currentTimeMillis() + ".txt"));
            Files.write(file, example.getBytes());
            return SERVER_NAME + CONTEXT_PATH + FILE_URL +"?name="+ file.toFile().getName();
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException(SOMETHING_WENT_WRONG, example);
        }
    }


    private void createDirectory() {
        try {
            Path dir = Path.of(System.getProperty("user.dir") + "/" + DIRECTORY_NAME);
            if (Files.notExists(dir)) {
                Files.createDirectory(dir);
            }
        } catch (Exception e) {
            throw new ApplicationException(SOMETHING_WENT_WRONG, DIRECTORY_NAME);
        }
    }

    public ResponseEntity<Resource> getFile(String fileName) {
        String fileUploadPath = System.getProperty("user.dir") + DIRECTORY_NAME;
        String[] fileNames = this.getFiles();
        boolean contains = Arrays.asList(fileNames).contains(fileName);
        if (!contains) {
            throw new ApplicationException(NOT_FOUND, fileName);
        }
        String filePath = fileUploadPath + File.separator + fileName;
        File file = new File(filePath);
        try {
            InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);

        } catch (Exception e) {
            throw new ApplicationException(SOMETHING_WENT_WRONG, fileName);
        }
    }


    private String[] getFiles() {
        String folderPath = System.getProperty("user.dir") + DIRECTORY_NAME;
        File folder = new File(folderPath);
        String[] fileNames = folder.list();
        return fileNames;
    }
    public static byte[] createPdf(String content) throws IOException {
        PDDocument document = new PDDocument();
        PDPage page = new PDPage();
        document.addPage(page);

        PDPageContentStream contentStream = new PDPageContentStream(document, page);
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
        contentStream.beginText();
        contentStream.newLineAtOffset(100, 700);
        contentStream.showText(content);
        contentStream.endText();
        contentStream.close();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        document.save(outputStream);
        document.close();

        return outputStream.toByteArray();
    }
}
