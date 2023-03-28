package nsu.testing;

import nsu.annotations.Serialize;

import java.util.ArrayList;

@Serialize(serializeAll = true)
public class Subjects {
    public int roomNumber = 0;
    public String subjectName = "Some string";
    public ArrayList<String> days = new ArrayList<>();

    public ArrayList<String> getDays() {
        return days;
    }

    public Subjects() {
    }

    public Subjects(int roomNum, String subjectName) {
        this.roomNumber = roomNum;
        this.subjectName = subjectName;
    }

    public Subjects(int i, String subjectName, ArrayList<String> collection) {
        this.roomNumber = i;
        this.subjectName = subjectName;
        this.days = collection;
    }

    @Override
    public String toString() {
        return "subjects:{" +
                "i=" + roomNumber +
                ", str='" + subjectName + '\'' +
                ", coll=" + days +
                '}';
    }
}

