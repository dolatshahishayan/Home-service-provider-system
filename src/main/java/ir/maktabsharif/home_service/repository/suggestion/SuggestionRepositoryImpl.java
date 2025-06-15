package ir.maktabsharif.home_service.repository.suggestion;

import ir.maktabsharif.home_service.base.repository.CrudRepositoryImpl;
import ir.maktabsharif.home_service.model.suggestion.Suggestion;
import jakarta.persistence.EntityManager;

public class SuggestionRepositoryImpl extends CrudRepositoryImpl<Suggestion> implements SuggestionRepository {
    public SuggestionRepositoryImpl(EntityManager em) {
        super(em);
    }

    @Override
    public Class<Suggestion> getEntityClass() {
        return Suggestion.class;
    }
}
