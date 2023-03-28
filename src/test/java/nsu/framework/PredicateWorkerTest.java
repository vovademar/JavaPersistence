package nsu.framework;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.json.Json;
import javax.json.JsonObject;

import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

class PredicateWorkerTest {

    private PredicateWorker<Integer> predicateWorker;

    @BeforeEach
    void setUp() {
        Predicate<Integer> predicate = value -> value > 10;
        predicateWorker = new PredicateWorker<>(predicate, "data/value", Integer.class);
    }

    @Test
    void constructorTest() {
        assertNotNull(predicateWorker);
    }

    @Test
    void convertTest() {
        String txt = "15";
        Integer expectedValue = 15;
        Integer actualValue = (Integer) PredicateWorker.convert(Integer.class, txt);
        assertEquals(expectedValue, actualValue);
    }

    @Test
    void testMethod() {
        JsonObject jsonObject = Json.createObjectBuilder()
                .add("data", Json.createObjectBuilder()
                        .add("value", "12"))
                .build();

        assertTrue(predicateWorker.test(jsonObject));

        jsonObject = Json.createObjectBuilder()
                .add("data", Json.createObjectBuilder()
                        .add("value", "8"))
                .build();

        assertFalse(predicateWorker.test(jsonObject));
    }
}