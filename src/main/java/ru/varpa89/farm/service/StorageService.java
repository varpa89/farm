package ru.varpa89.farm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellAddress;
import org.springframework.stereotype.Service;
import ru.varpa89.farm.dto.DocumentHeader;
import ru.varpa89.farm.dto.DocumentTablePart;
import ru.varpa89.farm.dto.SingleDocument;

import java.math.BigDecimal;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class StorageService extends AbstractXlsService {
    private static final CellAddress INVOICE_NUMBER = new CellAddress("P11");
    private static final CellAddress INVOICE_DATE = new CellAddress("R11");
    private static final CellAddress PRODUCTS = new CellAddress("A22");

    public SingleDocument readFile(Workbook invoice) {
        final Sheet sheet = invoice.getSheetAt(0);
        log.info("Process sheet {}", sheet.getSheetName());

        final String invoiceNumber = getStringValue(sheet, INVOICE_NUMBER);
        final Date invoiceDate = getInvoiceDate(sheet, INVOICE_DATE);

        final DocumentHeader documentHeader = DocumentHeader.builder()
                .numberTs(invoiceNumber)
                .date(getInvoiceFormattedDate(sheet, INVOICE_DATE))
                .typeRn("Постуление")
                .firm("000000010")
                .clientName("ООО \"Порт-Холод\"")
                .addrName("Ленинградская обл., Ломоносовский р-н, Разбегаево д., Ропшинское ш., д.3,стр.1")
                .gln("4607196173820")
                .build();

        final List<DocumentTablePart> documentTableParts = extractDocumentTablePart(sheet);


        return new SingleDocument(invoiceNumber, documentHeader, documentTableParts, invoiceDate);
    }

    private List<DocumentTablePart> extractDocumentTablePart(Sheet sheet) {
        int rowIndex = PRODUCTS.getRow();
        int lineNumber = 1;
        List<DocumentTablePart> documentTableParts = new ArrayList<>();
        while (!Objects.equals(sheet.getRow(rowIndex).getCell(0), null)) {

            Row row = sheet.getRow(rowIndex);

            final double amount = row.getCell(22).getNumericCellValue();
            final double price = row.getCell(20).getNumericCellValue();
            final String unit = row.getCell(10).getStringCellValue();
            final String name = row.getCell(0).getStringCellValue();

            //nomenclature - код/артикул
            //plu - пустой
            //factor - сколько единиц в одной коробке - через артикул
            //quantity - столбец (10) количество разделить на factor
            //SummaNDS - 0
            // NDS - без ндс

            final String nomenclature = row.getCell(5).getStringCellValue();
            final Integer factor = nomenclatureToFactor.get(nomenclature);
            if (factor == null) {
                throw new RuntimeException("Неизвестная номенклатура " + nomenclature);
            }
            final int quantity = parseInteger(row.getCell(14).getNumericCellValue()) / factor;

            final DocumentTablePart documentTablePart = DocumentTablePart.builder()
                    .amount(parseBigDecimal(amount))
                    .price(parseBigDecimal(price))
                    .quantity(parseInteger(quantity))
                    .unit(unit)
                    .name(name)
                    .line(parseInteger(lineNumber))
                    .nomenclature(nomenclature)
                    .factor(factor)
                    .quantity(quantity)
                    .nds("0%")
                    .ndsAmount(BigDecimal.ZERO)
                    .build();

            documentTableParts.add(documentTablePart);

            rowIndex++;
            lineNumber++;
        }

        return documentTableParts;
    }
}
