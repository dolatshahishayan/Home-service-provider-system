package ir.maktabsharif.home_service.repository.expert;

import ir.maktabsharif.home_service.base.repository.CrudRepository;
import ir.maktabsharif.home_service.base.repository.CrudRepositoryImpl;
import ir.maktabsharif.home_service.model.user.Expert;

public interface ExpertRepository extends CrudRepository<Expert> {
    Expert findByEmailAndPassword(String email,String password);
}
