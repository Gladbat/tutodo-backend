package com.tutodo.backend.service;

import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
public class ImageCompressionService {

    private static final int MAX_WIDTH = 1200;  // Ancho máximo
    private static final int MAX_HEIGHT = 1200; // Alto máximo
    private static final float QUALITY = 0.85f; // Calidad de compresión (85%)

    /**
     * Comprimir y redimensionar imagen
     */
    public byte[] compressImage(MultipartFile file) throws IOException {
        try {
            // Leer la imagen original
            BufferedImage originalImage = ImageIO.read(file.getInputStream());

            if (originalImage == null) {
                throw new IOException("No se pudo leer la imagen. Formato no soportado o archivo corrupto: " + file.getOriginalFilename());
            }

            // Obtener dimensiones originales
            int originalWidth = originalImage.getWidth();
            int originalHeight = originalImage.getHeight();

            // Si la imagen es pequeña, no la redimensionamos
            if (originalWidth <= MAX_WIDTH && originalHeight <= MAX_HEIGHT) {
                return compressOnly(file, originalImage);
            }

            // Calcular nuevas dimensiones manteniendo aspecto
            double aspectRatio = (double) originalWidth / originalHeight;
            int newWidth;
            int newHeight;

            if (aspectRatio > 1) {
                // Imagen horizontal
                newWidth = Math.min(originalWidth, MAX_WIDTH);
                newHeight = (int) (newWidth / aspectRatio);
            } else {
                // Imagen vertical
                newHeight = Math.min(originalHeight, MAX_HEIGHT);
                newWidth = (int) (newHeight * aspectRatio);
            }

            // Comprimir y redimensionar
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            Thumbnails.of(originalImage)
                    .size(newWidth, newHeight)
                    .outputQuality(QUALITY)
                    .outputFormat("jpg")
                    .toOutputStream(outputStream);

            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new IOException("Error al procesar imagen " + file.getOriginalFilename() + ": " + e.getMessage(), e);
        }
    }

    /**
     * Solo comprimir sin redimensionar
     */
    private byte[] compressOnly(MultipartFile file, BufferedImage originalImage) throws IOException {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            Thumbnails.of(originalImage)
                    .scale(1.0) // Mantener tamaño original
                    .outputQuality(QUALITY)
                    .outputFormat("jpg")
                    .toOutputStream(outputStream);

            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new IOException("Error al comprimir imagen " + file.getOriginalFilename() + ": " + e.getMessage(), e);
        }
    }

    /**
     * Crear miniatura (thumbnail) para vistas previas
     */
    public byte[] createThumbnail(MultipartFile file, int size) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        Thumbnails.of(file.getInputStream())
                .size(size, size)
                .outputQuality(0.75f)
                .outputFormat("jpg")
                .toOutputStream(outputStream);

        return outputStream.toByteArray();
    }

    /**
     * Obtener tamaño de archivo en bytes
     */
    public String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        char pre = "KMGTPE".charAt(exp - 1);
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), pre);
    }
}