package nsu.testing;

import nsu.framework.Serialize;
import nsu.framework.SerializeField;
import nsu.id.ID;

@Serialize
public class Person {

    @ID
    private long id;
    @SerializeField
    private String name;

    @SerializeField(Name = "personAge")
    private int age;

    @SerializeField(Name = "comlexTest")
    Kids kids = new Kids();

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    @Override
    public String toString() {
        return "Person{" +
                "name = '" + name + '\'' +
                ", age = " + age +
                ", kids = " + kids +
                '}';
    }
}
