package com.hackerrank.files.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class XFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column
    private String fileGroup;

    @NonNull

    @Column
    private String fileName;

    @NonNull
    @Column
    @Lob
    private byte[] file;
}
