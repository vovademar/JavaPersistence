package nsu.testing;

import nsu.annotations.Serialize;
import nsu.annotations.SerializeField;

import java.util.ArrayList;
import java.util.List;

@Serialize
public class Citizen extends Person{
    @SerializeField
    private String districtName;
    private int cnt;
    @SerializeField
    Person person;
    @SerializeField
    List<String> stringList = new ArrayList<>();

    public Citizen(String districtName, int cnt, Person person, List<String> stringList) {
        super(person.getName(), person.getAge());
        this.stringList = stringList;
        this.person = person;
        this.districtName = districtName;
        this.cnt = cnt;
    }

    public Citizen(String name, int age, String districtName, int cnt) {
        super(name, age);
        this.districtName = districtName;
        this.cnt = cnt;
    }
}
