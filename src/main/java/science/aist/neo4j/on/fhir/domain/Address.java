package science.aist.neo4j.on.fhir.domain;

import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

/**
 * <p>TODO class description</p>
 *
 * @author Andreas Pointner
 * @since 1.0
 */
@NodeEntity
public class Address {
    @Id
    private Long id;
    private String street;
    private String city;
    private String state;

    public Address() {
    }

    public Address(String street, String city, String state) {
        this.street = street;
        this.city = city;
        this.state = state;
    }

    public Long getId() {
        return id;
    }
}
