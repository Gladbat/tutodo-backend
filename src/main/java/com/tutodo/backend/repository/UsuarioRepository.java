package com.tutodo.backend.repository;

import com.tutodo.backend.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import com.tutodo.backend.enums.Rol;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;


@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByNumeroWhatsapp(String numeroWhatsapp);

    Optional<Usuario> findByNumeroWhatsapp(String numeroWhatsapp);
    long countByRol(Rol rol);

    List<Usuario> findByRol(Rol rol);
}