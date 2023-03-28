package nsu;

import nsu.framework.Persistence;
import nsu.testing.Citizen;
import nsu.testing.Person;
import nsu.testing.Student;
import nsu.testing.Subjects;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        Person person = new Person("John", 20);
        //String serialized = Persistence.persist(person).toString();
        //System.out.println(serialized);
        List<String> stringList = new ArrayList<>();
        stringList.add("first");
        stringList.add("second");
        Citizen citizen = new Citizen("Novosibirsk", person, stringList);
        String serialized1 = Persistence.persist(citizen).toString();
        System.out.println(serialized1);

        Citizen c = (Citizen) Persistence.deserializeObject(serialized1, null);
        assert c != null;
        System.out.println(c.getId());
        String serializedDeserialize = Persistence.persist(c).toString();
        System.out.println(serializedDeserialize);

//        ArrayList<String> days = new ArrayList<>();
//        days.add("monday");
//        days.add("friday");
//        Student ex = new Student("Alex", 21, new Subjects(2128,"Math", days));

//        //ex.setPrikol(new Subjects(1,"tmp"));
//        String res = Persistence.persist(ex).toString();
//        System.out.println(res);
//
//        Student p = (Student) Persistence.deserializeObject(res, null);
//        assert p != null;
//        System.out.println(p.getSubjects());
//        System.out.println(p.getAge());
//        System.out.println(p.getName());


//        Persistence.flush(Persistence.persist(citizen));
//        var objectFields = PersistenceFramework.getFields(person.getClass(),person);
//        System.out.println(objectFields);

        // Object person2 = PersistenceFramework.deserializeObject(serialized, null);
        //System.out.println(person2);
    }
}
