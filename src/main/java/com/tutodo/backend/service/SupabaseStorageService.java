package com.tutodo.backend.service;

import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SupabaseStorageService {

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.service.role.key}")
    private String supabaseKey;

    private static final String BUCKET_NAME = "imagenes-productos";

    private final ImageCompressionService imageCompressionService;
    private final OkHttpClient client = new OkHttpClient();

    /**
     * Subir archivo a Supabase Storage con compresión
     */
    public String uploadFile(MultipartFile file) throws IOException {
        // Comprimir imagen antes de subir
        byte[] compressedImage = imageCompressionService.compressImage(file);

        // Generar nombre único
        String extension = "jpg"; // Siempre JPG después de compresión
        String fileName = UUID.randomUUID().toString() + "." + extension;

        // URL del endpoint de Supabase Storage
        String url = supabaseUrl + "/storage/v1/object/" + BUCKET_NAME + "/" + fileName;

        // Crear request body con imagen comprimida
        RequestBody requestBody = RequestBody.create(
                compressedImage,
                MediaType.parse("image/jpeg")
        );

        // Crear request
        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "Bearer " + supabaseKey)
                .header("Content-Type", "image/jpeg")
                .post(requestBody)
                .build();

        // Ejecutar request
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Error al subir archivo a Supabase: " + response.body().string());
            }

            // Retornar URL pública del archivo
            return supabaseUrl + "/storage/v1/object/public/" + BUCKET_NAME + "/" + fileName;
        }
    }

    /**
     * Eliminar archivo de Supabase Storage
     */
    public void deleteFile(String fileUrl) throws IOException {
        // Extraer nombre del archivo de la URL
        String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);

        // URL del endpoint
        String url = supabaseUrl + "/storage/v1/object/" + BUCKET_NAME + "/" + fileName;

        // Crear request
        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "Bearer " + supabaseKey)
                .delete()
                .build();

        // Ejecutar request
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Error al eliminar archivo: " + response.body().string());
            }
        }
    }
}