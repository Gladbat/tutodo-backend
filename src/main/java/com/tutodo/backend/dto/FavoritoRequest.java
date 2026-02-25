package com.tutodo.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FavoritoRequest {

    @NotNull(message = "El usuario es obligatorio")
    private Long usuarioId;

    @NotNull(message = "El producto es obligatorio")
    private Long productoId;
}