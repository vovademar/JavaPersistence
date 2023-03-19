package nsu.testing;

import nsu.framework.Serialize;
import nsu.framework.SerializeField;
@Serialize
public class Citizen extends Person{
    @SerializeField
    private String districtName;
    private int cnt;
    @SerializeField
    Person person;

    public Citizen(String districtName, int cnt, Person person) {
        super(person.getName(), person.getAge());
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
