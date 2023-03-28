package nsu.framework;

import nsu.annotations.DeserializeConstructor;
import nsu.annotations.Serialize;
import nsu.annotations.SerializeField;
import nsu.annotations.ID;
import nsu.testing.Citizen;
import nsu.testing.Person;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.json.JsonObject;
import javax.json.JsonValue;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PersistenceTest {
    @Test
    void getFields() throws IOException, ParseException, NoSuchFieldException {
        HashMap<String, Field> fields = Persistence.getFields(Person.class);
        assertNotNull(fields);
        assertEquals(4, fields.size());
        assertEquals("name", fields.get("name").getName());
        assertEquals("age", fields.get("personAge").getName());
        assertEquals("kids", fields.get("comlexTest").getName());
    }

    @Test
    void persistSimpleObject() throws Exception {
        Person person = new Person("Alex", 20);
        String serialized = Persistence.persist(person).toString();
        assertNotNull(serialized);
        Person deserialized = (Person) Persistence.deserializeObject(serialized, null);
        assertNotNull(deserialized);
        assertEquals("Alex", deserialized.getName());
        assertEquals(20, deserialized.getAge());
        assertEquals(person.toString(), deserialized.toString());

    }

    @Test
    void persistComplexObject() throws Exception {
        Person person = new Person("Alex", 20);
        List<String> stringList = new ArrayList<>();
        stringList.add("first");
        stringList.add("second");
        Citizen citizen = new Citizen("Novosibirsk", person, stringList);
        String serialized = Persistence.persist(citizen).toString();
        assertNotNull(serialized);
        Citizen deserialized = (Citizen) Persistence.deserializeObject(serialized, null);
//        System.out.println(citizen.getStringList());
        assertNotNull(deserialized);
        assertEquals("Novosibirsk", deserialized.getDistrictName());
        assertEquals("Alex", deserialized.getName());
        assertEquals(20, deserialized.getAge());
        assertEquals(person.toString(), deserialized.getPerson().toString());

        assertEquals(citizen.toString(), deserialized.toString());

    }

    @Test
    void testPersist() throws IllegalAccessException {
        JsonValue jsonValue = Persistence.persist(sampleObject);
        assertNotNull(jsonValue);
        assertTrue(jsonValue instanceof JsonObject);
    }

    @Test
    void testPersistCollection() throws IllegalAccessException {
        JsonValue jsonValue = Persistence.persist(sampleCollectionObject);
        assertNotNull(jsonValue);
        assertTrue(jsonValue instanceof JsonObject);
    }

    @Test
    void testFlush() throws IllegalAccessException, IOException {
        JsonValue jsonValue = Persistence.persist(sampleObject);
        Persistence.flush(jsonValue);
        File file = new File("serializer.json");
        assertTrue(file.exists());
    }

    @Test
    void testDeserializeObject() throws Exception {
        Person person = new Person("Alex", 20);
        String serialized = Persistence.persist(person).toString();
        Person deserialized = (Person) Persistence.deserializeObject(serialized, null);
        assertNotNull(deserialized);
        assertEquals(person.getName(), deserialized.getName());
        assertEquals(person.getAge(), deserialized.getAge());
    }

    @Test
    @DisplayName("should return JsonValue.NULL when the input collection is null")
    void deserializeCollectionWhenCollectionIsNull() {
        assertEquals(JsonValue.NULL, Persistence.persistCollection(null));
    }

    @Test
    @DisplayName(
            "should return a JsonValue representing the collection when the input collection is not null")
    void deserializeCollectionWhenCollectionIsNotNull() throws IllegalAccessException {
        JsonValue jsonValue = Persistence.persist(sampleCollectionObject);
        assertNotNull(jsonValue);
    }


    @Serialize
    static class SampleClass {
        @ID
        long id;
        @SerializeField
        String name;
        @SerializeField
        int age;

        public SampleClass(String name, int age) {
            this.name = name;
            this.age = age;
        }
    }

    @Serialize
    static class SampleCollectionClass {
        @ID
        long id;
        @SerializeField
        ArrayList<String> names;

        @DeserializeConstructor
        public SampleCollectionClass(ArrayList<String> names) {
            this.names = names;
        }
    }

    private SampleClass sampleObject;
    private SampleCollectionClass sampleCollectionObject;

    @BeforeEach
    void setUp() {
        sampleObject = new SampleClass("John Doe", 30);
        ArrayList<String> names = new ArrayList<>();
        names.add("Alice");
        names.add("Bob");
        sampleCollectionObject = new SampleCollectionClass(names);
    }

    @Test
    @DisplayName("should return null if the object with the given id is not found")
    void findByIdReturnsNullWhenIdNotFound() throws Exception {
        Person person = new Person("Alex", 20);
        List<String> stringList = new ArrayList<>();
        stringList.add("first");
        stringList.add("second");
        Citizen citizen = new Citizen("Novosibirsk", person, stringList);
        JsonValue serialized = Persistence.persist(citizen);

        assertNull(Persistence.findById(serialized, "2"));
    }

}