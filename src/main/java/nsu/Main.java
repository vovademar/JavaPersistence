package nsu;

import nsu.framework.Persistence;
import nsu.testing.Citizen;
import nsu.testing.Person;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException, ParseException {
        Person person = new Person("John", 20);
        String serialized = Persistence.persist(person).toString();
        //System.out.println(serialized);
        List<String> stringList = new ArrayList<>();
        stringList.add("first");
        stringList.add("second");
        Citizen citizen = new Citizen("Novosibirsk", 1, person, stringList);
        String serialized1 = Persistence.persist(citizen).toString();
        System.out.println(serialized1);
//        Persistence.flush(Persistence.persist(citizen));
//        var objectFields = PersistenceFramework.getFields(person.getClass(),person);
//        System.out.println(objectFields);

        // Object person2 = PersistenceFramework.deserializeObject(serialized, null);
        //System.out.println(person2);
    }
}
