package com.osmanager.controller;

import com.osmanager.dto.TecnicoRequestDTO;
import com.osmanager.dto.TecnicoResponseDTO;
import com.osmanager.service.TecnicoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for technicians — thin layer, same conventions as ClientController.
 *
 * POST /api/technicians       → create (201 Created)
 * GET  /api/technicians       → list active (200 OK)
 * GET  /api/technicians/{id}  → get one (200 OK or 404)
 */
@RestController
@RequestMapping("/api/technicians")
public class TecnicoController {

    private final TecnicoService tecnicoService;

    public TecnicoController(TecnicoService tecnicoService) {
        this.tecnicoService = tecnicoService;
    }

    @PostMapping
    public ResponseEntity<TecnicoResponseDTO> createTecnico(
            @Valid @RequestBody TecnicoRequestDTO dto) {
        TecnicoResponseDTO created = tecnicoService.createTecnico(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<TecnicoResponseDTO>> getAllTecnicos() {
        return ResponseEntity.ok(tecnicoService.getAllActiveTecnicos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TecnicoResponseDTO> getTecnicoById(@PathVariable Long id) {
        return ResponseEntity.ok(tecnicoService.getTecnicoById(id));
    }
}
