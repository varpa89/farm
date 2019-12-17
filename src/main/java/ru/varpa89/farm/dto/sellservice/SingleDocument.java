package ru.varpa89.farm.dto.sellservice;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SingleDocument {
    @JacksonXmlProperty(isAttribute = true, localName = "N")
    private String number;
    @JacksonXmlProperty(localName = "ШапкаДокумента")
    private DocumentHeader documentHeader;
    @JacksonXmlProperty(localName = "ТабличнаяЧастьДокумента")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<DocumentTablePart> documentTablePart;
}
