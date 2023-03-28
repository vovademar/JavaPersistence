package nsu.testing;

import nsu.framework.DeserializeField;
import nsu.framework.DeserializeConstructor;
import nsu.framework.Serialize;
import nsu.framework.SerializeField;
import nsu.id.ID;


@Serialize()
public class Student {

    @ID
    private long id;
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
