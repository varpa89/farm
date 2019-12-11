package ru.varpa89.farm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellAddress;
import org.springframework.stereotype.Service;
import ru.varpa89.farm.dto.transportservice.DocumentHeader;
import ru.varpa89.farm.dto.transportservice.SingleDocument;
import ru.varpa89.farm.dto.transportservice.TransportServiceDtoRoot;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransportService {
    private static final CellAddress INVOICE_NUMBER = new CellAddress("W27");
    private static final CellAddress INVOICE_DATE = new CellAddress("AC27");
    private static final CellAddress CLIENT_INFO = new CellAddress("H13");

    private final ClientExtractor clientExtractor;

    public TransportServiceDtoRoot readFile(Workbook invoice) {
        final Sheet sheet = invoice.getSheetAt(0);
        log.info("Process sheet {}", sheet.getSheetName());

        final String invoiceNumber = getStringValue(sheet, INVOICE_NUMBER);
        final String invoiceDate = getInvoiceDate(sheet);
        final String clientInfoValue = getStringValue(sheet, CLIENT_INFO);

        final ClientExtractor.ClientInfo clientInfo = clientExtractor.extractInfo(clientInfoValue);
        log.info("Address parsed: {}", clientInfo);

        final DocumentHeader documentHeader = DocumentHeader.builder()
                .gln(1L) //TODO: GLN = "9" + 6 цифр кода контрагента + 6 цифр кода адреса.
                .clientName(clientInfo.getName())
                .kpp(clientInfo.getKpp())
                .inn(clientInfo.getInn())
                .addrName(clientInfo.getAddress())
                .build();

        SingleDocument singleDocument = new SingleDocument(invoiceNumber, documentHeader, List.of());

        return new TransportServiceDtoRoot(singleDocument, invoiceDate, invoiceDate);
    }

    private String getStringValue(Sheet sheet, CellAddress cellAddress) {
        return getCell(sheet, cellAddress).getStringCellValue();
    }

    private String getInvoiceDate(Sheet sheet) {
        final Date dateCellValue = getCell(sheet, INVOICE_DATE).getDateCellValue();
        return DateTimeFormatter.ISO_LOCAL_DATE_TIME.withZone(ZoneId.of("Europe/Moscow")).format(dateCellValue.toInstant());
    }

    private Cell getCell(Sheet sheet, CellAddress address) {
        Row row = sheet.getRow(address.getRow());
        return row.getCell(address.getColumn());
    }
}
