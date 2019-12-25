package ru.varpa89.farm.dto.storageservice;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.*;
import ru.varpa89.farm.dto.SingleDocument;

import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
@JacksonXmlRootElement(localName = "Data")
public class StorageServiceDtoRoot {
    @JacksonXmlProperty(isAttribute = true, localName = "NameBaseOut")
    private final String nameBaseOut = "SAP";
    @JacksonXmlProperty(isAttribute = true, localName = "NameBaseIn")
    private final String nameBaseIn = "OPTIMA";
    @JacksonXmlProperty(isAttribute = true, localName = "Date")
    private final String date;
    @JacksonXmlProperty(isAttribute = true, localName = "Time")
    private final String time;

    @JacksonXmlProperty(localName = "ОтдельныйДокумент")
    @JacksonXmlElementWrapper(localName = "Документы")
    private final List<SingleDocument> singleDocuments;
}
