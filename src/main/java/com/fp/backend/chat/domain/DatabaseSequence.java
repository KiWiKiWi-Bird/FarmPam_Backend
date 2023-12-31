package com.fp.backend.chat.domain;

import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "database_sequences")
public class DatabaseSequence {

    @Id
    private String id;

    private Long seq;
}
