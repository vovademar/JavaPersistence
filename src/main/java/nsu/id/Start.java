package nsu.id;

import org.json.simple.parser.ParseException;

import javax.persistence.Id;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.Annotation;

public class Start {
    public static void main(String[] args) throws IllegalAccessException, IOException, ParseException {
        TestId testId = new TestId();
        PersFramework pf = new PersFramework();
        pf.procId(testId);


    }
}
