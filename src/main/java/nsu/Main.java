package nsu;

import nsu.framework.Persistence;
import nsu.testing.Citizen;
import nsu.testing.Person;

public class Main {
    public static void main(String[] args) {
        Person person = new Person("John", 20);
        String serialized = Persistence.persist(person).toString();
        //System.out.println(serialized);
        Citizen citizen = new Citizen("Novosibirsk", 1, person);
        String serialized1 = Persistence.persist(citizen).toString();
        System.out.println(serialized1);
//        var objectFields = PersistenceFramework.getFields(person.getClass(),person);
//        System.out.println(objectFields);

        // Object person2 = PersistenceFramework.deserializeObject(serialized, null);
        //System.out.println(person2);
    }
}
