package nsu.testing;

import nsu.annotations.DeserializeField;
import nsu.annotations.DeserializeConstructor;
import nsu.annotations.Serialize;
import nsu.annotations.SerializeField;
import nsu.annotations.ID;


@Serialize()
public class Student {
    @SerializeField
    @ID
    public long id;
    @SerializeField()
    private String name;
    @SerializeField(Name = "personAge")
    private int age;

    @SerializeField()
    public Subjects subjects;

    public long getId() {
        return id;
    }

    @DeserializeConstructor
    public Student(@DeserializeField("name") String name, @DeserializeField("personAge") int age,
                   @DeserializeField("subjects") Subjects subjects) {
        this.name = name;
        this.age = age;
        this.subjects = subjects;
    }

    public int getAge() {
        return age;
    }

    public String getName() {
        return name;
    }

    public Subjects getSubjects() {
        return subjects;
    }

    @Override
    public String toString() {
        return "test.classes.Person{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", complexField=" + subjects +
                '}';
    }
}
