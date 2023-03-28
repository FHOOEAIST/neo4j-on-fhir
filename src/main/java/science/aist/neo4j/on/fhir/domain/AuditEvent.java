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
public class AuditEvent {
    @Id
    private Long id;
}
