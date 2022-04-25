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
        nomenclatureToFactor.put("00492", 5);
        nomenclatureToFactor.put("00494", 5);
        nomenclatureToFactor.put("00430", 4);
        nomenclatureToFactor.put("00493", 4);
        nomenclatureToFactor.put("00525", 4);

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
}
