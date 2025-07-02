package com.poo.miapi.repository;

import com.poo.miapi.model.Usuario;
import com.poo.miapi.model.Tecnico;
import com.poo.miapi.model.Trabajador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.List;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Optional<Usuario> findByNombre(String nombre);

    // Buscar por nombre e ID (para login por ejemplo)
    Optional<Usuario> findByIdAndPassword(int id, String password);

    // Si querés filtrar por tipo de usuario (herencia)
    @Query("SELECT t FROM Tecnico t WHERE t.bloqueado = true")
    List<Tecnico> findTecnicosBloqueados();

    @Query("SELECT tr FROM Trabajador tr")
    List<Trabajador> findAllTrabajadores();
}
