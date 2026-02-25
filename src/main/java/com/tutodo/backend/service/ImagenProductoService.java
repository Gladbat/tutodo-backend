package com.tutodo.backend.service;

import com.tutodo.backend.entity.ImagenProducto;
import com.tutodo.backend.entity.Producto;
import com.tutodo.backend.repository.ImagenProductoRepository;
import com.tutodo.backend.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ImagenProductoService {

    private final ImagenProductoRepository imagenProductoRepository;
    private final ProductoRepository productoRepository;
    private final SupabaseStorageService supabaseStorageService;

    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png", "webp");
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    /**
     * Subir imagen de producto a Supabase Storage
     */
    @Transactional
    public ImagenProducto subirImagen(Long productoId, MultipartFile file, Boolean esPrincipal, Integer orden) {
        // Validar que el producto existe
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        // Validar archivo
        validarArchivo(file);

        try {
            // Subir archivo a Supabase Storage
            String imageUrl = supabaseStorageService.uploadFile(file);

            // Si es principal, quitar el flag de las demás imágenes
            if (esPrincipal != null && esPrincipal) {
                desmarcarImagenesPrincipales(productoId);
            }

            // Crear registro en BD
            ImagenProducto imagen = new ImagenProducto();
            imagen.setProducto(producto);
            imagen.setUrlImagen(imageUrl);
            imagen.setEsPrincipal(esPrincipal != null ? esPrincipal : false);
            imagen.setOrden(orden != null ? orden : 0);
            imagen.setFechaCreacion(LocalDateTime.now());

            return imagenProductoRepository.save(imagen);

        } catch (IOException e) {
            throw new RuntimeException("Error al guardar la imagen: " + e.getMessage());
        }
    }

    /**
     * Subir múltiples imágenes
     */
    @Transactional
    public List<ImagenProducto> subirMultiplesImagenes(Long productoId, List<MultipartFile> files) {
        if (files.size() > 8) {
            throw new RuntimeException("Máximo 8 imágenes por producto");
        }

        List<ImagenProducto> imagenesSubidas = new ArrayList<>();
        List<String> errores = new ArrayList<>();

        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);
            try {
                boolean esPrincipal = (i == 0 && imagenesSubidas.isEmpty()); // La primera exitosa es principal
                ImagenProducto imagen = subirImagen(productoId, file, esPrincipal, i);
                imagenesSubidas.add(imagen);
            } catch (Exception e) {
                System.err.println("Error al subir imagen " + file.getOriginalFilename() + ": " + e.getMessage());
                errores.add(file.getOriginalFilename() + ": " + e.getMessage());
            }
        }

        // Si ninguna imagen se subió, lanzar excepción
        if (imagenesSubidas.isEmpty()) {
            throw new RuntimeException("No se pudo subir ninguna imagen. Errores: " + String.join(", ", errores));
        }

        // Si algunas fallaron, registrar pero continuar
        if (!errores.isEmpty()) {
            System.err.println("Algunas imágenes fallaron: " + String.join(", ", errores));
        }

        return imagenesSubidas;
    }

    /**
     * Obtener imágenes de un producto
     */
    public List<ImagenProducto> obtenerImagenesProducto(Long productoId) {
        return imagenProductoRepository.findByProductoIdOrderByOrdenAsc(productoId);
    }

    /**
     * Establecer imagen principal
     */
    @Transactional
    public ImagenProducto establecerImagenPrincipal(Long imagenId) {
        ImagenProducto imagen = imagenProductoRepository.findById(imagenId)
                .orElseThrow(() -> new RuntimeException("Imagen no encontrada"));

        // Desmarcar otras imágenes principales del mismo producto
        desmarcarImagenesPrincipales(imagen.getProducto().getId());

        // Marcar esta como principal
        imagen.setEsPrincipal(true);

        return imagenProductoRepository.save(imagen);
    }

    /**
     * Eliminar imagen
     */
    @Transactional
    public void eliminarImagen(Long imagenId) {
        ImagenProducto imagen = imagenProductoRepository.findById(imagenId)
                .orElseThrow(() -> new RuntimeException("Imagen no encontrada"));

        // Eliminar archivo de Supabase Storage
        try {
            supabaseStorageService.deleteFile(imagen.getUrlImagen());
        } catch (IOException e) {
            System.err.println("Error al eliminar archivo de Supabase: " + e.getMessage());
        }

        imagenProductoRepository.delete(imagen);
    }

    /**
     * Validar archivo
     */
    private void validarArchivo(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("El archivo está vacío");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new RuntimeException("El archivo excede el tamaño máximo de 5MB");
        }

        String extension = obtenerExtension(file.getOriginalFilename());
        if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new RuntimeException("Formato no permitido. Solo se aceptan: jpg, jpeg, png, webp");
        }
    }

    /**
     * Obtener extensión del archivo
     */
    private String obtenerExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            throw new RuntimeException("Archivo sin extensión");
        }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }

    /**
     * Desmarcar imágenes principales de un producto
     */
    private void desmarcarImagenesPrincipales(Long productoId) {
        List<ImagenProducto> imagenes = imagenProductoRepository.findByProductoIdOrderByOrdenAsc(productoId);
        imagenes.forEach(img -> {
            img.setEsPrincipal(false);
            imagenProductoRepository.save(img);
        });
    }
}