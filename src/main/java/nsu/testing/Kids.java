package nsu.testing;

import nsu.annotations.Serialize;
import nsu.annotations.SerializeField;

@Serialize
public class Kids {

    private long id;
    @SerializeField
    public int age;
    public String str;

    public Kids() {
        this(1, "Bob");
    }

    public Kids(int i, String str) {
        this.age = i;
        this.str = str;
    }

    @Override
    public String toString() {
        return "Kids{" +
                "Age = " + age +
                ", Name = '" + str + '\'' +
                '}';
    }
}

