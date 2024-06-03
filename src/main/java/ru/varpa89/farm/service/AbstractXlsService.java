package ru.varpa89.farm.service;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellAddress;
import org.springframework.util.NumberUtils;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractXlsService {
    protected static Map<String, Integer> nomenclatureToFactor = new HashMap<>();

    static {
        nomenclatureToFactor.put("00203", 9);
        nomenclatureToFactor.put("00212", 9);
        nomenclatureToFactor.put("00211", 9);
        nomenclatureToFactor.put("00197", 9);
        nomenclatureToFactor.put("00195", 9);
        nomenclatureToFactor.put("00204", 9);
        nomenclatureToFactor.put("00205", 9);
        nomenclatureToFactor.put("00301", 9);
        nomenclatureToFactor.put("00296", 9);
        nomenclatureToFactor.put("00295", 9);
        nomenclatureToFactor.put("00299", 9);
        nomenclatureToFactor.put("00298", 9);
        nomenclatureToFactor.put("00297", 9);
        nomenclatureToFactor.put("00300", 9);
        nomenclatureToFactor.put("00389", 9);
        nomenclatureToFactor.put("00390", 9);
        nomenclatureToFactor.put("00391", 9);
        nomenclatureToFactor.put("00392", 9);
        nomenclatureToFactor.put("00393", 9);
        nomenclatureToFactor.put("00394", 9);
        nomenclatureToFactor.put("00395", 9);
        nomenclatureToFactor.put("00218", 2);
        nomenclatureToFactor.put("00217", 2);
        nomenclatureToFactor.put("00409", 12);
        nomenclatureToFactor.put("00410", 12);
        nomenclatureToFactor.put("00411", 12);
        nomenclatureToFactor.put("00421", 12);
        nomenclatureToFactor.put("00422", 12);
        nomenclatureToFactor.put("00429", 12);
        nomenclatureToFactor.put("00457", 12);
        nomenclatureToFactor.put("00458", 12);
        nomenclatureToFactor.put("00459", 12);
        nomenclatureToFactor.put("00519", 12);
        nomenclatureToFactor.put("00490", 2);
        nomenclatureToFactor.put("00491", 2);
        nomenclatureToFactor.put("00492", 4);
        nomenclatureToFactor.put("00429", 4);
        nomenclatureToFactor.put("00493", 4);
        nomenclatureToFactor.put("00430", 4);
        nomenclatureToFactor.put("00525", 4);
        nomenclatureToFactor.put("00542", 6);
        nomenclatureToFactor.put("00540", 6);
        nomenclatureToFactor.put("00543", 6);
        nomenclatureToFactor.put("00586", 4);
        nomenclatureToFactor.put("00587", 4);
        nomenclatureToFactor.put("00588", 4);
        nomenclatureToFactor.put("00589", 4);
        nomenclatureToFactor.put("00593", 12);
        nomenclatureToFactor.put("00594", 12);
        nomenclatureToFactor.put("00595", 12);
        nomenclatureToFactor.put("00590", 6);
        nomenclatureToFactor.put("00591", 6);
        nomenclatureToFactor.put("00592", 6);
        nomenclatureToFactor.put("00619", 4);
        nomenclatureToFactor.put("00620", 4);
        nomenclatureToFactor.put("00628", 4);
        nomenclatureToFactor.put("00629", 4);
        nomenclatureToFactor.put("00630", 4);
        nomenclatureToFactor.put("00631", 4);
        nomenclatureToFactor.put("00632", 4);
        nomenclatureToFactor.put("00633", 4);
        nomenclatureToFactor.put("00634", 3);
        nomenclatureToFactor.put("00635", 3);
        nomenclatureToFactor.put("00636", 3);
        nomenclatureToFactor.put("00637", 3);
        nomenclatureToFactor.put("00638", 3);
        nomenclatureToFactor.put("00647", 15);
        nomenclatureToFactor.put("00653", 15);
        nomenclatureToFactor.put("00650", 5);
        nomenclatureToFactor.put("00649", 5);
        nomenclatureToFactor.put("00651", 5);
        nomenclatureToFactor.put("00652", 5);
        nomenclatureToFactor.put("00682", 2);
        nomenclatureToFactor.put("00684", 2);
        nomenclatureToFactor.put("00689", 4);
        nomenclatureToFactor.put("00690", 4);
        nomenclatureToFactor.put("00691", 4);
        nomenclatureToFactor.put("00692", 4);
        nomenclatureToFactor.put("00693", 4);
        nomenclatureToFactor.put("00711", 2);
        nomenclatureToFactor.put("00722", 4);
        nomenclatureToFactor.put("00723", 4);
        nomenclatureToFactor.put("00699", 4);
        nomenclatureToFactor.put("00700", 4);
        nomenclatureToFactor.put("00701", 4);
        nomenclatureToFactor.put("00702", 4);
        nomenclatureToFactor.put("00717", 4);
        nomenclatureToFactor.put("00745", 9);
        nomenclatureToFactor.put("00749", 4);
        nomenclatureToFactor.put("00762", 4);
        nomenclatureToFactor.put("00763", 4);
        nomenclatureToFactor.put("00764", 4);
        nomenclatureToFactor.put("00765", 4);
        nomenclatureToFactor.put("00753", 4);
        nomenclatureToFactor.put("00720", 4);
    }


    protected static String getStringValue(Sheet sheet, CellAddress cellAddress) {
        return getCell(sheet, cellAddress).getStringCellValue();
    }

    protected static Cell getCell(Sheet sheet, CellAddress address) {
        Row row = sheet.getRow(address.getRow());
        return row.getCell(address.getColumn());
    }

    protected static Date getInvoiceDate(Sheet sheet, CellAddress address) {
        return getCell(sheet, address).getDateCellValue();
    }

    protected static String getInvoiceFormattedDate(Sheet sheet, CellAddress address) {
        final Date dateCellValue = getCell(sheet, address).getDateCellValue();
        return DateTimeFormatter.ofPattern("yyyyMMdd").withZone(ZoneId.of("Europe/Moscow")).format(dateCellValue.toInstant());
    }

    protected static BigDecimal parseBigDecimal(double value) {
        if (value == 0) {
            return null;
        }
        return NumberUtils.parseNumber(String.valueOf(value), BigDecimal.class);
    }

    protected static Integer parseInteger(double value) {
        final BigDecimal result = parseBigDecimal(value);
        return result == null ? null : result.intValue();
    }

    protected static boolean useNds(Sheet sheet, CellAddress address) {
        return getStringValue(sheet, address).contains("Индивидуальный предприниматель Беспятов Сергей Юрьевич");
    }
}

