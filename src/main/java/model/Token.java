package model;

import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

public class Token {

    @Getter
    @Setter
    private String value;

    @Getter
    private Set<String> documentList;

    @Getter
    @Setter
    private int frequency;

    public Token() {
        this.value = "";
        this.documentList = new HashSet<>();
        this.frequency = 0;
    }

    public void addDocument(String document) {
        this.documentList.add(document);
    }

}
