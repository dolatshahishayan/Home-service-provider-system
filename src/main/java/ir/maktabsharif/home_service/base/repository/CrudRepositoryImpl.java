package ir.maktabsharif.home_service.base.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;

import javax.swing.text.html.parser.Entity;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public abstract class CrudRepositoryImpl<T> implements CrudRepository<T> {
    private final EntityManager em;

    @Override
    public void save(T t) {
        em.persist(t);
    }

    @Override
    public void delete(T t) {
        em.remove(t);
    }

    @Override
    public void update(T t) {
        em.merge(t);
    }

    @Override
    public List<T> findAll() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> query = cb.createQuery(getEntityClass());
        Root<T> root = query.from(getEntityClass());
        query.select(root);
        return em.createQuery(query).getResultList();
    }

    @Override
    public Optional<T> findById(Integer id) {
        return Optional.ofNullable(em.find(getEntityClass(), id));
    }

    public void beginTransaction() {
        em.getTransaction().begin();
    }

    public void commitTransaction() {
        em.getTransaction().commit();
    }

    public abstract Class<T> getEntityClass();
}
