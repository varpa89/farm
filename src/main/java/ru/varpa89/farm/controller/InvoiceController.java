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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import ru.varpa89.farm.dto.sellservice.SellServiceDtoRoot;
import ru.varpa89.farm.dto.SingleDocument;
import ru.varpa89.farm.dto.storageservice.StorageServiceDtoRoot;
import ru.varpa89.farm.service.SellService;
import ru.varpa89.farm.service.StorageService;

import java.io.IOException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Controller
@RequiredArgsConstructor
public class InvoiceController {
    private final XmlMapper xmlMapper;
    private final SellService sellService;
    private final StorageService storageService;

    @GetMapping("/")
    public String listUploadedFiles() {
        return "uploadForm";
    }

    @PostMapping(value = "/process_sell_document", headers = "content-type=multipart/*")
    public ResponseEntity<String> handleSellFilesUpload(@RequestParam("files[]") List<MultipartFile> files) throws JsonProcessingException {

        if (files.isEmpty() || Objects.equals(files.get(0).getOriginalFilename(), "")) {
            return ResponseEntity.badRequest()
                    .body("Нужно выбрать хотябы один файл");
        }

        List<SingleDocument> documents = new ArrayList<>();
        for (MultipartFile file : files) {
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
            } catch (Exception e) {
                log.error("Can't read file", e);
                return ResponseEntity.badRequest()
                        .body("Ошибка");
            }

            final SingleDocument document = sellService.readFile(invoice);
            documents.add(document);
        }


        Date minDate = documents.stream()
                .min(Comparator.comparing(t -> t.getInvoiceDate().getTime()))
                .get().getInvoiceDate();

        Date maxDate = documents.stream()
                .max(Comparator.comparing(t -> t.getInvoiceDate().getTime()))
                .get().getInvoiceDate();

        SellServiceDtoRoot root = new SellServiceDtoRoot(documents, getInvoiceDateIso(minDate), getInvoiceDateIso(maxDate));

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_XML)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"sell_doc_" + documents.size() + "_total.xml\"")
                .body(xmlMapper.writeValueAsString(root));
    }

    @PostMapping(value = "/process_storage_document", headers = "content-type=multipart/*")
    public ResponseEntity<String> handleStorageFilesUpload(@RequestParam("files[]") List<MultipartFile> files) throws JsonProcessingException {

        if (files.isEmpty() || Objects.equals(files.get(0).getOriginalFilename(), "")) {
            return ResponseEntity.badRequest()
                    .body("Нужно выбрать хотябы один файл");
        }

        List<SingleDocument> documents = new ArrayList<>();
        for (MultipartFile file : files) {
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
            } catch (Exception e) {
                log.error("Can't read file", e);
                return ResponseEntity.badRequest()
                        .body("Ошибка");
            }

            final SingleDocument document = storageService.readFile(invoice);
            documents.add(document);
        }


        Date now = new Date();

        StorageServiceDtoRoot root = new StorageServiceDtoRoot(getDate(now), getTime(now), documents);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_XML)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"storage_doc_" + documents.size() + "_total.xml\"")
                .body(xmlMapper.writeValueAsString(root));
    }

    private String getInvoiceDateIso(Date date) {
        return DateTimeFormatter.ISO_LOCAL_DATE_TIME.withZone(ZoneId.of("Europe/Moscow")).format(date.toInstant());
    }

    private String getDate(Date date) {
        return DateTimeFormatter.ofPattern("yyyyMMdd").withZone(ZoneId.of("Europe/Moscow")).format(date.toInstant());
    }

    private String getTime(Date date) {
        return DateTimeFormatter.ofPattern("HH:mm:ss").withZone(ZoneId.of("Europe/Moscow")).format(date.toInstant());
    }
}

