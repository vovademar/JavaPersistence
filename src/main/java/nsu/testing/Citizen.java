package nsu.testing;

import nsu.annotations.DeserializeField;
import nsu.annotations.DeserializeConstructor;
import nsu.annotations.Serialize;
import nsu.annotations.SerializeField;
import nsu.annotations.ID;

import java.util.ArrayList;
import java.util.List;

@Serialize
public class Citizen extends Person {
    @SerializeField
    private String districtName;
    @SerializeField
    @ID
    public long id;
    private int cnt;
    @SerializeField
    Person person;
    @SerializeField
    List<String> stringList = new ArrayList<>();

    @DeserializeConstructor
    public Citizen(@DeserializeField("districtName") String districtName,
                   @DeserializeField("person") Person person, @DeserializeField("stringList") List<String> stringList) {
        super(person.getName(), person.getAge());
        this.stringList = stringList;
        this.person = person;
        this.districtName = districtName;
    }

    public Citizen(String name, int age, String districtName, int cnt) {
        super(name, age);
        this.districtName = districtName;
        this.cnt = cnt;
    }

    public long getId() {
        return id;
    }

    public String getDistrictName() {
        return districtName;
    }

    public Object getPerson() {
        return person;
    }
}