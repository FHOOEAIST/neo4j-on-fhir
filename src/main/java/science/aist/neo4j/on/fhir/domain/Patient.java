package science.aist.neo4j.on.fhir.domain;

import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

/**
 * <p>TODO class description</p>
 *
 * @author Andreas Pointner
 * @since 1.0
 */
@NodeEntity
public class Patient {
    @Id
    private Long id;

    private String name;

    @Relationship
    private Address address;

    public Patient() {
    }

    protected Patient(Patient old) {
        this.id = old.id;
        this.name = old.name;
    }

    public Patient(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
}
