package ru.varpa89.farm.dto.transportservice;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.List;

public class SingleDocument {
    @JacksonXmlProperty(isAttribute = true, localName = "N")
    private Long number;
    @JacksonXmlProperty(localName = "ШапкаДокумента")
    private DocumentHeader documentHeader = new DocumentHeader();
    @JacksonXmlProperty(localName = "ТабличнаяЧастьДокумента")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<DocumentTablePart> documentTablePart = List.of(new DocumentTablePart(), new DocumentTablePart());
}
