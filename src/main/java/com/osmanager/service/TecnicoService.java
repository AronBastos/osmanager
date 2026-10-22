package com.osmanager.service;

import com.osmanager.dto.TecnicoRequestDTO;
import com.osmanager.dto.TecnicoResponseDTO;
import com.osmanager.exception.BusinessException;
import com.osmanager.exception.ResourceNotFoundException;
import com.osmanager.model.Tecnico;
import com.osmanager.repository.TecnicoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/** Business logic for technicians. */
@Service
public class TecnicoService {

    private final TecnicoRepository tecnicoRepository;

    public TecnicoService(TecnicoRepository tecnicoRepository) {
        this.tecnicoRepository = tecnicoRepository;
    }

    /** Creates a new technician. CPF must be unique (if provided). */
    @Transactional
    public TecnicoResponseDTO createTecnico(TecnicoRequestDTO dto) {
        if (dto.getCpf() != null && !dto.getCpf().isBlank()
                && tecnicoRepository.existsByCpf(dto.getCpf())) {
            throw new BusinessException("CPF already registered: " + dto.getCpf());
        }

        Tecnico saved = tecnicoRepository.save(toEntity(dto));
        return TecnicoResponseDTO.from(saved);
    }

    @Transactional(readOnly = true)
    public List<TecnicoResponseDTO> getAllActiveTecnicos() {
        return tecnicoRepository.findByActiveOrderByNameAsc(true)
                .stream()
                .map(TecnicoResponseDTO::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TecnicoResponseDTO getTecnicoById(Long id) {
        Tecnico tecnico = tecnicoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Technician not found with id: " + id));
        return TecnicoResponseDTO.from(tecnico);
    }

    private Tecnico toEntity(TecnicoRequestDTO dto) {
        Tecnico tecnico = new Tecnico();
        tecnico.setName(dto.getName());
        tecnico.setEmail(dto.getEmail());
        tecnico.setPhone(dto.getPhone());
        tecnico.setCpf(dto.getCpf());
        tecnico.setSpecialty(dto.getSpecialty());
        tecnico.setProfessionalLicense(dto.getProfessionalLicense());
        return tecnico;
    }
}
