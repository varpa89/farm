package ru.varpa89.farm.dto.transportservice;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JacksonXmlRootElement(localName = "Документы")
public class TransportServiceDtoRoot {
    @JacksonXmlProperty(localName = "ОтдельныйДокумент")
    SingleDocument singleDocument = new SingleDocument();
}
