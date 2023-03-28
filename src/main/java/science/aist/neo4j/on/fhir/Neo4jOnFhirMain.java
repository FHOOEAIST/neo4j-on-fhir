package science.aist.neo4j.on.fhir;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.util.BundleUtil;
import org.hl7.fhir.instance.model.api.IBaseBundle;
import org.hl7.fhir.r4.model.*;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import science.aist.neo4j.Neo4jRepository;
import science.aist.neo4j.on.fhir.domain.Address;
import science.aist.neo4j.on.fhir.domain.Patient;
import science.aist.neo4j.on.fhir.domain.fhirextension.FhirAddress;
import science.aist.neo4j.on.fhir.domain.fhirextension.FhirExtensionPatient;
import science.aist.neo4j.on.fhir.domain.processmining.PMAuditEvent;
import science.aist.neo4j.on.fhir.domain.synthea.SyntheaPatient;
import science.aist.neo4j.transaction.TransactionManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>TODO class description</p>
 *
 * @author Andreas Pointner
 * @since 1.0
 */
public class Neo4jOnFhirMain {
    private Neo4jRepository<Patient, Long> patientRepository;
    private Neo4jRepository<FhirExtensionPatient, Long> fhirExtensionPatientLongNeo4jRepository;
    private Neo4jRepository<SyntheaPatient, Long> syntheaPatientLongNeo4jRepository;
    private Neo4jRepository<PMAuditEvent, Long> pmAuditEventLongNeo4jRepository;
    private Neo4jRepository<Address, Long> addressNeo4jRepository;
    private Neo4jRepository<FhirAddress, Long> fhirAddressNeo4jRepository;

    public static void main(String[] args) {
        try (var applicationContext = new ClassPathXmlApplicationContext("config.xml");
             var tx = applicationContext.getBean(TransactionManager.class).beginTransaction()) {

            Neo4jOnFhirMain main = new Neo4jOnFhirMain();
            main.patientRepository =  applicationContext.getBean("patientRepository", Neo4jRepository.class);
            main.fhirExtensionPatientLongNeo4jRepository = applicationContext.getBean("fhirExtensionPatient", Neo4jRepository.class);
            main.syntheaPatientLongNeo4jRepository = applicationContext.getBean("syntheaPatient", Neo4jRepository.class);
            main.pmAuditEventLongNeo4jRepository = applicationContext.getBean("pmAuditEvent", Neo4jRepository.class);
            main.addressNeo4jRepository = applicationContext.getBean("address", Neo4jRepository.class);
            main.fhirAddressNeo4jRepository = applicationContext.getBean("fhirAddress", Neo4jRepository.class);

            tx.run("MATCH(x) DETACH DELETE x;");

            // Loading patients
            FhirContext fhirContext = FhirContext.forR4();
            IGenericClient iGenericClient = fhirContext.newRestfulGenericClient("http://aist-partner.projekte.fh-hagenberg.at:8082/fhir/");
            Bundle bundle = iGenericClient.search().forResource(org.hl7.fhir.r4.model.Patient.class).count(10).returnBundle(Bundle.class).execute();
            List<org.hl7.fhir.r4.model.Patient> patients = BundleUtil.toListOfResourcesOfType(fhirContext, bundle, org.hl7.fhir.r4.model.Patient.class);

            Map<String, Patient> patientCache = new HashMap<>();
            for (org.hl7.fhir.r4.model.Patient patient : patients) {
                var p = main.saveFhirPatientAsCustomPatient(patient);
                patientCache.put("Patient/"+patient.getIdPart(), p);
            }

            // loading audit events
            List<org.hl7.fhir.r4.model.AuditEvent> auditEvents = new ArrayList<>();
            bundle = iGenericClient.search().forResource(org.hl7.fhir.r4.model.AuditEvent.class).returnBundle(Bundle.class).execute();
            auditEvents.addAll(BundleUtil.toListOfResourcesOfType(fhirContext, bundle, org.hl7.fhir.r4.model.AuditEvent.class));

            while (bundle.getLink(IBaseBundle.LINK_NEXT) != null) {
                bundle = iGenericClient
                        .loadPage()
                        .next(bundle)
                        .execute();
                auditEvents.addAll(BundleUtil.toListOfResourcesOfType(fhirContext, bundle, org.hl7.fhir.r4.model.AuditEvent.class));
            }

            for (AuditEvent auditEvent : auditEvents) {
                PMAuditEvent pmAuditEvent = new PMAuditEvent();
                Patient patient = patientCache.get(((Reference)auditEvent.getExtensionByUrl("http://fhir.r5.extensions/patient").getValue()).getReference());
                if (patient != null) {
                    pmAuditEvent.setPatient(patient);
                    pmAuditEvent.setTimestamp(auditEvent.getExtensionByUrl("http://fhir.r5.extensions/occurredDateTime").getValue().primitiveValue());
                    pmAuditEvent.setAction(((Coding)auditEvent.getExtensionByUrl("http://fhir.r5.extensions/code").getValue()).getDisplay());
                    main.pmAuditEventLongNeo4jRepository.save(pmAuditEvent);
                }
            }
            tx.commit();
        }
    }

    public Patient saveFhirPatientAsCustomPatient(org.hl7.fhir.r4.model.Patient p) {
        Patient ap = new Patient(p.getNameFirstRep().getNameAsSingleString());
        ap = patientRepository.save(ap);

        SyntheaPatient sp = syntheaPatientLongNeo4jRepository.findById(ap.getId());
        sp.setDisabilityAdjustedLifeYears(Double.parseDouble(p.getExtensionByUrl("http://synthetichealth.github.io/synthea/disability-adjusted-life-years").getValue().primitiveValue()));
        sp = syntheaPatientLongNeo4jRepository.save(sp);

        FhirExtensionPatient fep = fhirExtensionPatientLongNeo4jRepository.findById(sp.getId());
        fep.setMothersMaidenName(p.getExtensionByUrl("http://hl7.org/fhir/StructureDefinition/patient-mothersMaidenName").getValue().primitiveValue());
        fep = fhirExtensionPatientLongNeo4jRepository.save(fep);

        org.hl7.fhir.r4.model.Address addressFirstRep = p.getAddressFirstRep();
        Address a = new Address(
                addressFirstRep.getLine().get(0).getValue(),
                addressFirstRep.getCity(),
                addressFirstRep.getState()
        );
        a = addressNeo4jRepository.save(a);


        FhirAddress fa = fhirAddressNeo4jRepository.findById(a.getId());
        Extension geolocation = addressFirstRep.getExtensionByUrl("http://hl7.org/fhir/StructureDefinition/geolocation");
        fa.setLatitude(Double.parseDouble(geolocation.getExtensionByUrl("latitude").getValue().primitiveValue()));
        fa.setLongitude(Double.parseDouble(geolocation.getExtensionByUrl("longitude").getValue().primitiveValue()));
        fhirAddressNeo4jRepository.save(fa);

        fep.setAddress(fa);
        fep = patientRepository.save(fep);

        return fep;
    }
}
