package org.com.inkagob.procedureservice.infrastructure.files.cloudinary.storage.service;


import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CloudinaryFileStorageService {
    private final Cloudinary cloudinary;

    public String uploadFile(MultipartFile file, ObjectId procedureId) throws IOException {
        String publicId = "procedures/" + procedureId + "/" + file.getOriginalFilename();

        Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                ObjectUtils.asMap(
                        "public_id", publicId,
                        "resource_type", "auto" // detecta automáticamente tipo (imagen/pdf/etc)
                )
        );

        return (String) uploadResult.get("secure_url");
    }

    public void deleteFile(String fileUrl) throws IOException {
        String publicId = extractPublicIdFromUrl(fileUrl);
        cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
    }

    private String extractPublicIdFromUrl(String url) {
        // Extrae el public_id desde la URL de Cloudinary
        String[] parts = url.split("/");
        String fileName = parts[parts.length - 1];
        return fileName.substring(0, fileName.lastIndexOf('.'));
    }
}
