package com.tutodo.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FavoritoDTO {
    private Long id;
    private Long usuarioId;
    private Long productoId;
    private ProductoDTO producto; // Información completa del producto
    private LocalDateTime fechaCreacion;
}