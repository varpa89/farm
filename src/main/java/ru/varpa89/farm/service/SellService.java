package ru.varpa89.farm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellAddress;
import org.springframework.stereotype.Service;
import ru.varpa89.farm.dto.DocumentHeader;
import ru.varpa89.farm.dto.DocumentTablePart;
import ru.varpa89.farm.dto.SingleDocument;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SellService extends AbstractXlsService {
    private static final CellAddress INVOICE_NUMBER = new CellAddress("W27");
    private static final CellAddress INVOICE_DATE = new CellAddress("AC27");
    private static final CellAddress CLIENT_INFO = new CellAddress("H13");
    private static final CellAddress PRODUCTS = new CellAddress("A33");
    private static final CellAddress ORDER_NR = new CellAddress("BB24");
    private static final CellAddress ORDER_DATE = new CellAddress("BB25");
    private static final CellAddress GLN = new CellAddress("BB26");
    private static final CellAddress ADDRESS = new CellAddress("H22");
    private static final CellAddress INDIVIDUAL_ENTREPRENEUR_INFO = new CellAddress("A7");

    private final ClientExtractor clientExtractor;

    public SingleDocument readFile(Workbook invoice) {
        final Sheet sheet = invoice.getSheetAt(0);
        log.info("Process sheet {}", sheet.getSheetName());

        final String invoiceNumber = getStringValue(sheet, INVOICE_NUMBER);
        final Date invoiceDate = getInvoiceDate(sheet, INVOICE_DATE);
        final String clientInfoValue = getStringValue(sheet, CLIENT_INFO);
        final boolean useNds = useNds(sheet, INDIVIDUAL_ENTREPRENEUR_INFO);
        log.info("Use nds: {}", useNds ? "yes" : "no");

        final ClientExtractor.ClientInfo clientInfo = clientExtractor.extractInfo(clientInfoValue);
        log.info("Address parsed: {}", clientInfo);

        final DocumentHeader documentHeader = DocumentHeader.builder()
                .clientName(clientInfo.getName())
                .kpp(clientInfo.getKpp())
                .inn(clientInfo.getInn())
                .addrName(getStringValue(sheet, ADDRESS))
                .numberTs(invoiceNumber)
                .date(getInvoiceFormattedDate(sheet, INVOICE_DATE))
                .typeRn("Продажа")
                .bonus(0)
                .promo(0)
                .firm("Мега Опт")
                .numberSf(useNds
                        ? invoiceNumber
                        : ""
                )
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

    private List<DocumentTablePart> extractDocumentTablePart(Sheet sheet) {

        List<DocumentTablePart> documentTableParts = new ArrayList<>();
        for (int i = PRODUCTS.getRow(); i < sheet.getLastRowNum(); i++) {
            if (!isProductRow(sheet, i)) {
                continue;
            }

            Row row = sheet.getRow(i);

            final double lineNumber = row.getCell(0).getNumericCellValue();
            final double amount = row.getCell(52).getNumericCellValue();
            final double price = row.getCell(39).getNumericCellValue();
            final String unit = row.getCell(19).getStringCellValue();
            final String name = row.getCell(3).getStringCellValue();
            final String ndsValue = row.getCell(45).getStringCellValue();
            final double ndsAmount = row.getCell(48).getNumericCellValue();
            BigDecimal nds = extractNds(ndsValue);
            final double priceWithNds = price + price * nds.doubleValue() / 100;


            //nomenclature - код/артикул
            //plu - пустой
            //factor - сколько единиц в одной коробке - через артикул
            //quantity - столбец (10) количество разделить на factor

            final String nomenclature = row.getCell(16).getStringCellValue();
            final Integer factor = nomenclatureToFactor.get(nomenclature);
            if (factor == null) {
                throw new RuntimeException("Неизвестная номенклатура " + nomenclature);
            }
            final int quantity = parseInteger(row.getCell(36).getNumericCellValue()) / factor;

            final DocumentTablePart documentTablePart = DocumentTablePart.builder()
                    .amount(parseBigDecimal(amount))
                    .price(parseBigDecimal(priceWithNds))
                    .quantity(parseInteger(quantity))
                    .unit(unit)
                    .name(name)
                    .line(parseInteger(lineNumber))
                    .nomenclature(nomenclature)
                    .plu("")
                    .factor(factor)
                    .quantity(quantity)
                    .nds(nds.equals(BigDecimal.ZERO) ? "Без НДС" : nds.toString())
                    .ndsAmount(BigDecimal.valueOf(ndsAmount))
                    .build();

            documentTableParts.add(documentTablePart);

        }

        return documentTableParts;
    }

    private boolean isProductRow(Sheet sheet, int rowIndex) {
        final Row row = sheet.getRow(rowIndex);

        return row.getCell(0) != null
                && row.getCell(42) != null
                && row.getCell(39) != null
                && row.getCell(19) != null
                && row.getCell(3) != null
                && row.getCell(0).getCellType().equals(CellType.NUMERIC)
                && row.getCell(42).getCellType().equals(CellType.NUMERIC)
                && row.getCell(39).getCellType().equals(CellType.NUMERIC)
                && row.getCell(19).getCellType().equals(CellType.STRING)
                && row.getCell(3).getCellType().equals(CellType.STRING);
    }

    private BigDecimal extractNds(String ndsCellValue) {
        if (ndsCellValue.equals("Без НДС")) {
            return BigDecimal.ZERO;
        } else {
            return BigDecimal.valueOf(Double.parseDouble(ndsCellValue.trim().replace("%", "")));
        }
    }
}
