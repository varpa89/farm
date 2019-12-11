package ru.varpa89.farm.dto.transportservice;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Builder
@Getter
public class DocumentTablePart {
    @JacksonXmlProperty(isAttribute = true, localName = "NDS")
    private String nds;
    @JacksonXmlProperty(isAttribute = true, localName = "Summa")
    private BigDecimal sum;
    @JacksonXmlProperty(isAttribute = true, localName = "SummaNDS")
    private BigDecimal ndsSum;
    @JacksonXmlProperty(isAttribute = true, localName = "Price")
    private BigDecimal price;
    @JacksonXmlProperty(isAttribute = true, localName = "Quantity")
    private Integer quantity;
    @JacksonXmlProperty(isAttribute = true, localName = "Factor")
    private Integer factor;
    @JacksonXmlProperty(isAttribute = true, localName = "Unit")
    private String unit;
    @JacksonXmlProperty(isAttribute = true, localName = "Name")
    private String name;
    @JacksonXmlProperty(isAttribute = true, localName = "Nomenclature")
    private String nomenclature;
    @JacksonXmlProperty(isAttribute = true, localName = "Promo")
    private Integer promo;
    @JacksonXmlProperty(isAttribute = true, localName = "Line")
    private Integer line;
    @JacksonXmlProperty(isAttribute = true, localName = "PLU")
    private String plu;
}
