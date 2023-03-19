package nsu.testing;

import nsu.framework.Serialize;
import nsu.framework.SerializeField;

import java.util.ArrayList;

@Serialize
public class Kids {
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
