package com.tutodo.backend.service;

import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.List;

@Service
public class ModerationService {

    // Lista de palabras prohibidas (puedes ampliarla)
    private static final List<String> PALABRAS_PROHIBIDAS = Arrays.asList(
            // Groserías comunes
            "mierda", "puto", "puta", "cabrón", "cabron", "pendejo", "pendeja",
            "chingada", "verga", "coño", "joder", "gilipollas",

            // Contenido inapropiado
            "drogas", "cocaina", "marihuana", "armas", "pistola",
            "estafa", "fraude", "robado", "pirata",

            // Spam
            "compra ahora", "gratis total", "oferta limitada", "haz clic aquí",

            // Puedes agregar más palabras según necesites
            "xxx", "sexo", "porno"
    );

    /**
     * Verifica si el texto contiene palabras prohibidas
     */
    public boolean contienePalabrasProhibidas(String texto) {
        if (texto == null || texto.isBlank()) {
            return false;
        }

        String textoLower = texto.toLowerCase();

        return PALABRAS_PROHIBIDAS.stream()
                .anyMatch(textoLower::contains);
    }

    /**
     * Valida un producto completo (nombre + descripción)
     */
    public boolean validarContenidoProducto(String nombre, String descripcion) {
        return contienePalabrasProhibidas(nombre) ||
                contienePalabrasProhibidas(descripcion);
    }

    /**
     * Censura las palabras prohibidas reemplazándolas con asteriscos
     */
    public String censurarTexto(String texto) {
        if (texto == null || texto.isBlank()) {
            return texto;
        }

        String resultado = texto;

        for (String palabra : PALABRAS_PROHIBIDAS) {
            String asteriscos = "*".repeat(palabra.length());
            resultado = resultado.replaceAll(
                    "(?i)" + palabra, // (?i) = case insensitive
                    asteriscos
            );
        }

        return resultado;
    }

    /**
     * Obtiene la lista de palabras prohibidas encontradas
     */
    public List<String> encontrarPalabrasProhibidas(String texto) {
        if (texto == null || texto.isBlank()) {
            return List.of();
        }

        String textoLower = texto.toLowerCase();

        return PALABRAS_PROHIBIDAS.stream()
                .filter(textoLower::contains)
                .toList();
    }
}