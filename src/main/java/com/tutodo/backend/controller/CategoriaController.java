package com.tutodo.backend.controller;

import com.tutodo.backend.entity.Categoria;
import com.tutodo.backend.repository.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
public class CategoriaController {

    private final CategoriaRepository categoriaRepository;

    /**
     * GET /api/categorias
     * Obtener todas las categorías
     */
    @GetMapping
    public ResponseEntity<List<Categoria>> obtenerTodasCategorias() {
        return ResponseEntity.ok(categoriaRepository.findAll());
    }

    /**
     * GET /api/categorias/{id}
     * Obtener una categoría por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Categoria> obtenerCategoriaPorId(@PathVariable Long id) {
        return categoriaRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
