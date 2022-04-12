package com.example.fileuploadanddownload.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Attachment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String fileOriginalName;  // tdtu.jpg, tdtu.pdf
    private String contentType;  // application/pdf, image/png  google->file content type
    private Long size;  //fileni necha baytligi
    private String name; // systemga yuklaganda qanday nom bilan papkaga joylash 2ta bir xil nom bob qolmasligi uchun

}
