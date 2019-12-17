package ru.varpa89.farm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellAddress;
import org.springframework.stereotype.Service;
import org.springframework.util.NumberUtils;
import ru.varpa89.farm.dto.sellservice.DocumentHeader;
import ru.varpa89.farm.dto.sellservice.DocumentTablePart;
import ru.varpa89.farm.dto.sellservice.SingleDocument;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class SellService {
    private static final CellAddress INVOICE_NUMBER = new CellAddress("W27");
    private static final CellAddress INVOICE_DATE = new CellAddress("AC27");
    private static final CellAddress CLIENT_INFO = new CellAddress("H13");
    private static final CellAddress PRODUCTS = new CellAddress("A33");
    private static final CellAddress ORDER_NR = new CellAddress("BB24");
    private static final CellAddress ORDER_DATE = new CellAddress("BB25");
    private static final CellAddress GLN = new CellAddress("BB26");
    private static final CellAddress ADDRESS = new CellAddress("H22");

    private final ClientExtractor clientExtractor;

    public SingleDocument readFile(Workbook invoice) {
        final Sheet sheet = invoice.getSheetAt(0);
        log.info("Process sheet {}", sheet.getSheetName());

        final String invoiceNumber = getStringValue(sheet, INVOICE_NUMBER);
        final Date invoiceDate = getInvoiceDate(sheet);
        final String clientInfoValue = getStringValue(sheet, CLIENT_INFO);

        final ClientExtractor.ClientInfo clientInfo = clientExtractor.extractInfo(clientInfoValue);
        log.info("Address parsed: {}", clientInfo);

        final DocumentHeader documentHeader = DocumentHeader.builder()
                .clientName(clientInfo.getName())
                .kpp(clientInfo.getKpp())
                .inn(clientInfo.getInn())
                .addrName(getStringValue(sheet, ADDRESS))
                .numberTs(invoiceNumber)
                .date(getInvoiceFormattedDate(sheet))
                .typeRn("Продажа")
                .bonus(0)
                .promo(0)
                .firm("Мега Опт")
                .numberSf("")
                .orderNr(getStringValue(sheet, ORDER_NR))
                .orderDate(getStringValue(sheet, ORDER_DATE))
                .gln(getStringValue(sheet, GLN))
                .build();

        //firm - мега опт
        //NumberSF - пусто
        //OrderNR - форма
        //OrderDate - форма
        //GLN - форма
        //адрес - форма

        final List<DocumentTablePart> documentTableParts = extractDocumentTablePart(sheet);

        return new SingleDocument(invoiceNumber, documentHeader, documentTableParts, invoiceDate);
    }

    private String getStringValue(Sheet sheet, CellAddress cellAddress) {
        return getCell(sheet, cellAddress).getStringCellValue();
    }

    private Date getInvoiceDate(Sheet sheet) {
        return getCell(sheet, INVOICE_DATE).getDateCellValue();
    }

    private String getInvoiceFormattedDate(Sheet sheet) {
        final Date dateCellValue = getCell(sheet, INVOICE_DATE).getDateCellValue();
        return DateTimeFormatter.ofPattern("yyyyMMdd").withZone(ZoneId.of("Europe/Moscow")).format(dateCellValue.toInstant());
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
        Map<String, Integer> nomenclatureToFactor = new HashMap<>();
        nomenclatureToFactor.put("00203", 9);
        nomenclatureToFactor.put("00212", 9);
        nomenclatureToFactor.put("00211", 9);
        nomenclatureToFactor.put("00197", 9);
        nomenclatureToFactor.put("00195", 9);
        nomenclatureToFactor.put("00204", 9);
        nomenclatureToFactor.put("00205", 9);
        nomenclatureToFactor.put("00202", 18);
        nomenclatureToFactor.put("00201", 18);
        nomenclatureToFactor.put("00199", 18);
        nomenclatureToFactor.put("00198", 18);
        nomenclatureToFactor.put("00196", 18);


        int rowIndex = PRODUCTS.getRow();
        List<DocumentTablePart> documentTableParts = new ArrayList<>();
        while (sheet.getRow(rowIndex).getCell(0).getCellType().equals(CellType.NUMERIC)) {

            Row row = sheet.getRow(rowIndex);

            final double lineNumber = row.getCell(0).getNumericCellValue();
            final double amount = row.getCell(42).getNumericCellValue();
            final double price = row.getCell(39).getNumericCellValue();
            final String unit = row.getCell(19).getStringCellValue();
            final String name = row.getCell(3).getStringCellValue();

            //nomenclature - код/артикул
            //plu - пустой
            //factor - сколько единиц в одной коробке - через артикул
            //quantity - столбец (10) количество разделить на factor
            //SummaNDS - 0
            // NDS - без ндс

            final String nomenclature = row.getCell(16).getStringCellValue();
            final Integer factor = nomenclatureToFactor.get(nomenclature);
            if (factor == null) {
                throw new RuntimeException("Неизвестная номенклатура " + nomenclature);
            }
            final int quantity = parseInteger(row.getCell(36).getNumericCellValue()) / factor;

            final DocumentTablePart documentTablePart = DocumentTablePart.builder()
                    .amount(parseBigDecimal(amount))
                    .price(parseBigDecimal(price))
                    .quantity(parseInteger(quantity))
                    .unit(unit)
                    .name(name)
                    .line(parseInteger(lineNumber))
                    .nomenclature(nomenclature)
                    .plu("")
                    .factor(factor)
                    .quantity(quantity)
                    .nds("Без НДС")
                    .ndsAmount(BigDecimal.ZERO)
                    .build();

            documentTableParts.add(documentTablePart);

            rowIndex++;
        }

        return documentTableParts;
    }
}
