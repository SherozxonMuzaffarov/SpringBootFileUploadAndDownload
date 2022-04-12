package com.example.fileuploadanddownload.controller;

import com.example.fileuploadanddownload.entity.Attachment;
import com.example.fileuploadanddownload.entity.AttachmentContent;
import com.example.fileuploadanddownload.repository.AttachmentContentRepository;
import com.example.fileuploadanddownload.repository.AttachmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/attachment")
public class AttachmentController {

    @Autowired
    AttachmentRepository attachmentRepository;

    @Autowired
    AttachmentContentRepository attachmentContentRepository;

    private static final String uploadFile="yuklanganlar"; //papka nomi

    @PostMapping("/uploadDB")
    public String uploadFileToDB(MultipartHttpServletRequest request) throws IOException {
        Iterator<String> fileNames = request.getFileNames();
        MultipartFile file = request.getFile(fileNames.next());
        if (file != null) {

            //File haqida malumot olish
            String originalFilename = file.getOriginalFilename();
            String contentType = file.getContentType();
            long size = file.getSize();

            Attachment attachment = new Attachment();
            attachment.setFileOriginalName(originalFilename);
            attachment.setContentType(contentType);
            attachment.setSize(size);
            Attachment savedAttachment = attachmentRepository.save(attachment);

            //Fileni ichidagi Contentni saqlaydi
            AttachmentContent attachmentContent = new AttachmentContent();
            attachmentContent.setMainContent(file.getBytes());
            attachmentContent.setAttachment(savedAttachment);
            attachmentContentRepository.save(attachmentContent);

            return "File saved with ID: " + savedAttachment.getId();
        }
        return "ERROR";
    }

    @PostMapping("/uploadSystem")
    public String uploadFileToFileSystem(MultipartHttpServletRequest request) throws IOException {
        Iterator<String> fileNames = request.getFileNames();
        MultipartFile file = request.getFile(fileNames.next());
        if (file != null) {
            String originalFilename = file.getOriginalFilename();

            Attachment attachment = new Attachment();
            attachment.setFileOriginalName(originalFilename);
            attachment.setContentType(request.getContentType());
            attachment.setSize(file.getSize());

            String[] split = originalFilename.split("\\.");

            String name = UUID.randomUUID().toString()+"." + split[split.length-1];

            attachment.setName(name);
            attachmentRepository.save(attachment);

            Path path = Paths.get(uploadFile+"/" + name);
            Files.copy(file.getInputStream(),path);
            return "File saved with ID:" + attachment.getId();
        }
        return "ERROR";
    }

    @GetMapping("/getFile/{id}")
    public void getFile(@PathVariable Integer id, HttpServletResponse response) throws IOException {
        Optional<Attachment> optionalAttachment = attachmentRepository.findById(id);
        if (optionalAttachment.isPresent()) {
            Attachment attachment = optionalAttachment.get();
            Optional<AttachmentContent> contentOptional = attachmentContentRepository.findByAttachmentId(id);
            if (contentOptional.isPresent()){
                AttachmentContent attachmentContent = contentOptional.get();

                //fileni nomini berish uchun
                response.setHeader("Content-Disposition",
                        "attachment;fileName=\"" + attachment.getFileOriginalName()+"\"");

                //file typeni berish uchun
                response.setContentType(attachment.getContentType());

                //file contentini berish uchun
                FileCopyUtils.copy(attachmentContent.getMainContent(), response.getOutputStream());
            }
        }

    }

    @GetMapping("/getFileFromSystem/{id}")
    public void getFileFromSystem(@PathVariable Integer id, HttpServletResponse response) throws IOException {
        Optional<Attachment> optionalAttachment = attachmentRepository.findById(id);
        if (optionalAttachment != null) {
            Attachment attachment = optionalAttachment.get();

            //fileni nomini berish uchun
            response.setHeader("Content-Disposition",
                    "attachment;fileName=\"" + attachment.getFileOriginalName()+"\"");

            //file typeni berish uchun
            response.setContentType(attachment.getContentType());

            FileInputStream fileInputStream = new FileInputStream(uploadFile+"/" + attachment.getName());

            FileCopyUtils.copy(fileInputStream, response.getOutputStream());
        }

    }
}
