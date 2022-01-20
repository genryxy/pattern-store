package aakrasnov.diploma.service.service;

import aakrasnov.diploma.service.domain.Pattern;
import aakrasnov.diploma.service.domain.User;
import aakrasnov.diploma.service.repo.PatternRepo;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PatternService {
    private final PatternRepo ptrnRepo;

    @Autowired
    public PatternService(final PatternRepo ptrnRepo) {
        this.ptrnRepo = ptrnRepo;
    }

    public Optional<Pattern> getById(final String id) {
        return ptrnRepo.findById(id);
    }

    public List<Pattern> findByAuthor(final User author) {
        return ptrnRepo.findPatternByAuthor(author);
    }

    public Pattern save(final Pattern pattern) {
        return ptrnRepo.save(pattern);
    }
}
