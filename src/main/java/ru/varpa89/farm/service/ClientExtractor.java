package ru.varpa89.farm.service;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class ClientExtractor {
    ClientInfo extractInfo(String data) {
        final String[] strings = data.split(",");

        String name = strings[0];
        String inn = Stream.of(strings)
                .filter(string -> string.contains(" ИНН "))
                .findFirst().map(string -> string.replace(" ИНН ", ""))
                .orElse("");

        String kpp = Stream.of(strings)
                .filter(string -> string.contains(" КПП "))
                .findFirst().map(string -> string.replace(" КПП ", ""))
                .orElse("");

        String address = data.replace(name + ", ", "")
                .replace(", ИНН " + inn, "")
                .replace(", КПП " + kpp, "");

        return ClientInfo.builder()
                .name(name)
                .address(address)
                .inn(inn)
                .kpp(kpp)
                .build();
    }

    @Builder
    @Getter
    @ToString
    static class ClientInfo {
        private String name;
        private String address;
        private String inn;
        private String kpp;
    }
}
