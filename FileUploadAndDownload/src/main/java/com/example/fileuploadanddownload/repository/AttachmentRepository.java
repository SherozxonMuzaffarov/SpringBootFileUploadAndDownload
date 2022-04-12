package com.example.fileuploadanddownload.repository;

import com.example.fileuploadanddownload.entity.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttachmentRepository extends JpaRepository<Attachment, Integer> {
}
