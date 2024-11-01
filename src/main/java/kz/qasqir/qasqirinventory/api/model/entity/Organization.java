package kz.qasqir.qasqirinventory.api.model.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "t_organizations")
public class Organization {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "organization_name", nullable = false)
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name)  {
        this.name = name;
    }
}
