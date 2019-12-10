package ru.varpa89.farm.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.varpa89.farm.dto.transportservice.TransportServiceDtoRoot;

import java.io.IOException;

@Controller
@RequiredArgsConstructor
public class InvoiceController {
    private final ObjectMapper objectMapper;

    @GetMapping("/")
    public String listUploadedFiles(Model model) throws IOException {
        return "uploadForm";
    }

    @RequestMapping(value = "/transport_service", produces = MediaType.APPLICATION_XML_VALUE)
    public TransportServiceDtoRoot transportServices() {
        return new TransportServiceDtoRoot();
    }

    @PostMapping(value = "/")//, produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file,
                                                     RedirectAttributes redirectAttributes) throws JsonProcessingException {

//        storageService.store(file);
//        redirectAttributes.addFlashAttribute("message",
//                "You successfully uploaded " + file.getOriginalFilename() + "!");

        final TransportServiceDtoRoot transportService = new TransportServiceDtoRoot();

//        Resource resource

        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);
        xmlMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        xmlMapper.configure( ToXmlGenerator.Feature.WRITE_XML_DECLARATION, true );

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_XML)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"invoice.xml\"")
                .body(xmlMapper.writeValueAsString(transportService));
    }
}

