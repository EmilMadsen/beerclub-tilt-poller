package dk.thebeerclub.brewhub.service;

import dk.thebeerclub.brewhub.model.Brew;
import dk.thebeerclub.brewhub.repository.BrewRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BrewService {

    private final BrewRepository brewRepository;

    public BrewService(BrewRepository brewRepository) {
        this.brewRepository = brewRepository;
    }

    public Optional<Brew> findById(Long id) {
        return brewRepository.findById(id);
    }

    public List<Brew> findAllWithTiltUrl() {
        return brewRepository.findByTiltUrlNotNull();
    }

}
