package nsu;

import nsu.testing.Person;

import java.lang.reflect.InvocationTargetException;

public class Main {
    public static void main(String[] args){
        Person person = new Person("John", 20);
        String serialized = Persistence.persist(person).toString();
        System.out.println(serialized);
//        var objectFields = PersistenceFramework.getFields(person.getClass(),person);
//        System.out.println(objectFields);

       // Object person2 = PersistenceFramework.deserializeObject(serialized, null);
        //System.out.println(person2);
    }
}
