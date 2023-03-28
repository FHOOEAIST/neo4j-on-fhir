package science.aist.neo4j.on.fhir.domain.processmining;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import science.aist.neo4j.on.fhir.domain.AuditEvent;
import science.aist.neo4j.on.fhir.domain.Patient;

import java.util.Objects;

/**
 * <p>TODO class description</p>
 *
 * @author Andreas Pointner
 * @since 1.0
 */
@NodeEntity
public class PMAuditEvent extends AuditEvent {
    @Relationship(type = Relationship.OUTGOING)
    private Patient patient;
    private String timestamp;
    private String action;

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = Objects.requireNonNull(patient);
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
