package github.felipeschwartz.fiber_splice_locator.service;


import github.felipeschwartz.fiber_splice_locator.controller.CEOController;
import github.felipeschwartz.fiber_splice_locator.mapper.CEOMapper;
import github.felipeschwartz.fiber_splice_locator.model.dto.CEODTO;
import github.felipeschwartz.fiber_splice_locator.model.entities.CEO;
import github.felipeschwartz.fiber_splice_locator.repository.CEORepository;
import github.felipeschwartz.fiber_splice_locator.service.exceptions.ObjectNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class CEOService {
    private Logger logger = LoggerFactory.getLogger(CEOService.class.getName());

    private final CEORepository ceoRepository;
    private final CEOMapper ceoMapper;

    public CEOService(CEORepository ceoRepository, CEOMapper ceoMapper) {
        this.ceoRepository = ceoRepository;
        this.ceoMapper = ceoMapper;
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN') or hasRole('FIELD_TECHNICIAN')")
    public List<CEODTO> findAll() {
        logger.info("Finding all CEOs!");
        List<CEODTO> ceoDTOS = ceoRepository.findAll()
                .stream()
                .map(ceoMapper::toDTO)
                .collect(Collectors.toList());
        ceoDTOS.forEach(this::addHateoasLinks);
        return ceoDTOS;
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN') or hasRole('FIELD_TECHNICIAN')")
    public CEODTO findById(Long id) {
        logger.info("Finding one CEO by ID: {}", id);
        CEO ceo = ceoRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("CEO", id));
        CEODTO ceoDTO = ceoMapper.toDTO(ceo);
        addHateoasLinks(ceoDTO);
        return ceoDTO;
    }



    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public CEODTO create(CEODTO requestDTO) {
        logger.info("Creating a CEO: {}", requestDTO.getBoxNumber());
        CEO ceo = ceoMapper.toEntity(requestDTO);
        CEO savedCEO = ceoRepository.save(ceo);
        CEODTO createdCEODTO = ceoMapper.toDTO(savedCEO);
        addHateoasLinks(createdCEODTO);
        return createdCEODTO;
    }



    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public CEODTO update(CEODTO updatedDTO) {
        logger.info("Updating CEO with ID: {}", updatedDTO.getId());

        CEO existingCEO = ceoRepository.findById(updatedDTO.getId())
                .orElseThrow(() -> new ObjectNotFoundException("CEO with ID: " + updatedDTO.getId()));
        ceoMapper.updateEntityFromDTO(updatedDTO, existingCEO);
        CEODTO updatedCEODTO = ceoMapper.toDTO(ceoRepository.save(existingCEO));
        addHateoasLinks(updatedCEODTO);
        return updatedCEODTO;
    }



    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(Long id) {
        logger.info("Deleting one CEO!");
        if (!ceoRepository.existsById(id)) {
            throw new ObjectNotFoundException("CEO", id);
        }
        ceoRepository.deleteById(id);
    }



    private void addHateoasLinks(CEODTO dto) {
        dto.add(linkTo(methodOn(CEOController.class).findById(dto.getId())).withSelfRel().withType("GET"));
        dto.add(linkTo(methodOn(CEOController.class).findAll()).withRel("findAllCEOs").withType("GET"));
        dto.add(linkTo(methodOn(CEOController.class).create(null)).withRel("createCEO").withType("POST"));
        dto.add(linkTo(methodOn(CEOController.class).update(dto)).withRel("updateCEO").withType("PUT"));
        dto.add(linkTo(methodOn(CEOController.class).delete(dto.getId())).withRel("deleteCEO").withType("DELETE"));
    }
}