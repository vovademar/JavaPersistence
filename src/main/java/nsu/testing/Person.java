package nsu.testing;

import nsu.annotations.DeserializeField;
import nsu.annotations.DeserializeConstructor;
import nsu.annotations.Serialize;
import nsu.annotations.SerializeField;
import nsu.annotations.ID;

@Serialize
public class Person {
    @SerializeField
    @ID
    public long id;
    @SerializeField
    private String name;

    @SerializeField(Name = "personAge")
    private int age;

    @SerializeField(Name = "comlexTest")
    Kids kids = new Kids();

    @DeserializeConstructor
    public Person(@DeserializeField("name") String name, @DeserializeField("personAge") int age) {
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
