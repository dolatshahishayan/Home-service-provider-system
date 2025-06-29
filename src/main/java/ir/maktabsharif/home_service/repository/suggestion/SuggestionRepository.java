package ir.maktabsharif.home_service.repository.suggestion;

import ir.maktabsharif.home_service.base.repository.CrudRepository;
import ir.maktabsharif.home_service.model.suggestion.Suggestion;

import java.util.List;

public interface SuggestionRepository extends CrudRepository<Suggestion> {
    List<Suggestion> findAllByExpertId(Integer expertId);
}
