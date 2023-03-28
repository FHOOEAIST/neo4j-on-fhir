package science.aist.neo4j.on.fhir.domain.synthea;

import org.neo4j.ogm.annotation.NodeEntity;
import science.aist.neo4j.on.fhir.domain.Patient;

/**
 * <p>TODO class description</p>
 *
 * @author Andreas Pointner
 * @since 1.0
 */
@NodeEntity
public class SyntheaPatient extends Patient {
    private double disabilityAdjustedLifeYears;

    public SyntheaPatient() {
    }

    public SyntheaPatient(Patient p, double disabilityAdjustedLifeYears) {
        super(p);
        this.disabilityAdjustedLifeYears = disabilityAdjustedLifeYears;
    }

    public double getDisabilityAdjustedLifeYears() {
        return disabilityAdjustedLifeYears;
    }

    public void setDisabilityAdjustedLifeYears(double disabilityAdjustedLifeYears) {
        this.disabilityAdjustedLifeYears = disabilityAdjustedLifeYears;
    }
}
