package ru.varpa89.farm.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Date;
import java.util.List;

@Getter
@AllArgsConstructor
public class SingleDocument {
    @JacksonXmlProperty(isAttribute = true, localName = "N")
    private String number;
    @JacksonXmlProperty(localName = "ШапкаДокумента")
    private DocumentHeader documentHeader;
    @JacksonXmlProperty(localName = "ТабличнаяЧастьДокумента")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<DocumentTablePart> documentTablePart;

    @JsonIgnore
    private Date invoiceDate;
}
