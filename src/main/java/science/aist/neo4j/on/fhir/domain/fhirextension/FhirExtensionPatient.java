package science.aist.neo4j.on.fhir.domain.fhirextension;

import science.aist.neo4j.on.fhir.domain.Patient;

/**
 * <p>TODO class description</p>
 *
 * @author Andreas Pointner
 * @since 1.0
 */
public class FhirExtensionPatient extends Patient {
    private String mothersMaidenName;

    public FhirExtensionPatient() {
    }

    public FhirExtensionPatient(Patient p, String mothersMaidenName) {
        super(p);
        this.mothersMaidenName = mothersMaidenName;
    }

    public String getMothersMaidenName() {
        return mothersMaidenName;
    }

    public void setMothersMaidenName(String mothersMaidenName) {
        this.mothersMaidenName = mothersMaidenName;
    }
}
