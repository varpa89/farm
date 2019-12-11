package ru.varpa89.farm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellAddress;
import org.springframework.stereotype.Service;
import org.springframework.util.NumberUtils;
import ru.varpa89.farm.dto.transportservice.DocumentHeader;
import ru.varpa89.farm.dto.transportservice.DocumentTablePart;
import ru.varpa89.farm.dto.transportservice.SingleDocument;
import ru.varpa89.farm.dto.transportservice.TransportServiceDtoRoot;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransportService {
    private static final CellAddress INVOICE_NUMBER = new CellAddress("W27");
    private static final CellAddress INVOICE_DATE = new CellAddress("AC27");
    private static final CellAddress CLIENT_INFO = new CellAddress("H13");
    private static final CellAddress PRODUCTS = new CellAddress("A33");

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
                .clientName(clientInfo.getName())
                .kpp(clientInfo.getKpp())
                .inn(clientInfo.getInn())
                .addrName(clientInfo.getAddress())
                .build();

        final List<DocumentTablePart> documentTableParts = extractDocumentTablePart(sheet);

        SingleDocument singleDocument = new SingleDocument(invoiceNumber, documentHeader, documentTableParts);

        return new TransportServiceDtoRoot(singleDocument, invoiceDate, invoiceDate);
    }

    private String getStringValue(Sheet sheet, CellAddress cellAddress) {
        return getCell(sheet, cellAddress).getStringCellValue();
    }

    private String getInvoiceDate(Sheet sheet) {
        final Date dateCellValue = getCell(sheet, INVOICE_DATE).getDateCellValue();
        return DateTimeFormatter.ISO_LOCAL_DATE_TIME.withZone(ZoneId.of("Europe/Moscow")).format(dateCellValue.toInstant());
    }

    private static BigDecimal parseBigDecimal(double value) {
        if (value == 0) {
            return null;
        }
        return NumberUtils.parseNumber(String.valueOf(value), BigDecimal.class);
    }

    private static Integer parseInteger(double value) {
        final BigDecimal result = parseBigDecimal(value);
        return result == null ? null : result.intValue();
    }

    private Cell getCell(Sheet sheet, CellAddress address) {
        Row row = sheet.getRow(address.getRow());
        return row.getCell(address.getColumn());
    }

    private List<DocumentTablePart> extractDocumentTablePart(Sheet sheet) {
        int rowIndex = PRODUCTS.getRow();
        List<DocumentTablePart> documentTableParts = new ArrayList<>();
        while (sheet.getRow(rowIndex).getCell(0).getCellType().equals(CellType.NUMERIC)) {

            Row row = sheet.getRow(rowIndex);

            final double lineNumber = row.getCell(0).getNumericCellValue();
            final double amount = row.getCell(42).getNumericCellValue();
            final double price = row.getCell(39).getNumericCellValue();
            final double quantity = row.getCell(36).getNumericCellValue();
            final String unit = row.getCell(19).getStringCellValue();
            final String name = row.getCell(3).getStringCellValue();

            final DocumentTablePart documentTablePart = DocumentTablePart.builder()
                    .amount(parseBigDecimal(amount))
                    .price(parseBigDecimal(price))
                    .quantity(parseInteger(quantity))
                    .unit(unit)
                    .name(name)
                    .line(parseInteger(lineNumber))
                    .build();

            documentTableParts.add(documentTablePart);

            rowIndex++;
        }

        return documentTableParts;
    }
}
