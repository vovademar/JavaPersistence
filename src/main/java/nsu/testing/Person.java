package nsu.testing;

import nsu.Serialize;
import nsu.SerializeField;

@Serialize()
public class Person {
    @SerializeField()
    private String name;
    @SerializeField(Name = "personAge")
    private int age;

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
}
