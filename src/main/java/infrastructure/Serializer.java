package infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;

public class Serializer {
    private static ObjectMapper mapper = new ObjectMapper();
    private static final String AGGREGATE_ID = "aggregateId";

    public static SerializedEvent serialize(Event evt) {
        ObjectNode json = mapper.valueToTree(evt);
        json.remove(AGGREGATE_ID);
        return new SerializedEvent(evt.getAggregateId(), evt.getClass(), json.toString());
    }

    public static Event deserialize(SerializedEvent sEvt) {
        try {
            ObjectNode payloadJson = (ObjectNode) mapper.readTree(sEvt.getPayload());
            if (payloadJson == null) {
                payloadJson = mapper.createObjectNode();
            }
            payloadJson.put(AGGREGATE_ID, sEvt.getAggregateId().toString());
            Class<?> eventType = Class.forName(sEvt.getEventType());
            return (Event) mapper.readValue(payloadJson.toString(), eventType);
        } catch (IOException ex) {
            throw new RuntimeException("Failed to deserialize event " + sEvt.getId() + ", payload " + sEvt.getPayload(), ex);
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException("Event " + sEvt.getId() + " type " + sEvt.getEventType() + " not found", ex);
        }
    }
}
