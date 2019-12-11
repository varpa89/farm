package ru.varpa89.farm.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.OLE2NotOfficeXmlFileException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import ru.varpa89.farm.dto.transportservice.TransportServiceDtoRoot;
import ru.varpa89.farm.service.TransportService;

import java.io.IOException;

@Slf4j
@Controller
@RequiredArgsConstructor
public class InvoiceController {
    private final XmlMapper xmlMapper;
    private final TransportService transportService;

    @GetMapping("/")
    public String listUploadedFiles() {
        return "uploadForm";
    }

    @PostMapping(value = "/", headers = "content-type=multipart/*")
    public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file) throws JsonProcessingException {
        Workbook invoice;
        try {
            invoice = new XSSFWorkbook(file.getInputStream());
        } catch (OLE2NotOfficeXmlFileException e) {
            try {
                invoice = new HSSFWorkbook(file.getInputStream());
            } catch (IOException ex) {
                log.error("Can't read file", e);
                return ResponseEntity.badRequest()
                        .body("Ошибка");
            }
        } catch (IOException e) {
            log.error("Can't read file", e);
            return ResponseEntity.badRequest()
                    .body("Ошибка");
        }

        final TransportServiceDtoRoot dtoRoot = transportService.readFile(invoice);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_XML)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"invoice.xml\"")
                .body(xmlMapper.writeValueAsString(dtoRoot));
    }
}

