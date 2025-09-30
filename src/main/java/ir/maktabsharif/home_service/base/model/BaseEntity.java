package ir.maktabsharif.home_service.base.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
@MappedSuperclass
public class BaseEntity implements Serializable {
    @Id
    @SequenceGenerator(name = "my_entity_seq_generator", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "my_entity_seq_generator")
    private Integer id;

    public BaseEntity() {
    }


    public BaseEntity(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
