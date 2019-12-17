package ru.varpa89.farm.dto.sellservice;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class DocumentHeader {
    @JacksonXmlProperty(isAttribute = true, localName = "GLN")
    private String gln;
    @JacksonXmlProperty(isAttribute = true, localName = "ClientName")
    private String clientName;
    @JacksonXmlProperty(isAttribute = true, localName = "INN")
    private String inn;
    @JacksonXmlProperty(isAttribute = true, localName = "KPP")
    private String kpp;
    @JacksonXmlProperty(isAttribute = true, localName = "Addrname")
    private String addrName;
    @JacksonXmlProperty(isAttribute = true, localName = "Addr")
    private String addr;
    @JacksonXmlProperty(isAttribute = true, localName = "OrderDate")
    private String orderDate;
    @JacksonXmlProperty(isAttribute = true, localName = "OrderNR")
    private String orderNr;
    @JacksonXmlProperty(isAttribute = true, localName = "NumberSF")
    private String numberSf;
    @JacksonXmlProperty(isAttribute = true, localName = "Client")
    private String client;
    @JacksonXmlProperty(isAttribute = true, localName = "Firm")
    private String firm;
    @JacksonXmlProperty(isAttribute = true, localName = "TypeRN")
    private String typeRn;
    @JacksonXmlProperty(isAttribute = true, localName = "Date")
    private String date;
    @JacksonXmlProperty(isAttribute = true, localName = "NumberTS")
    private String numberTs;
    @JacksonXmlProperty(isAttribute = true, localName = "Bonus")
    private Integer bonus;
    @JacksonXmlProperty(isAttribute = true, localName = "Promo")
    private Integer promo;

}
