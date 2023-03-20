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
////        ID an = testId.getClass().getAnnotation(ID.class);
//        Class c = testId.getClass();
//        Field[] field = c.getDeclaredFields();
//        for (Field fl : field) {
//            if (fl.isAnnotationPresent(ID.class)) {
//                System.out.println("ect");
//            } else {
//                System.out.println("net");
//            }
////            System.out.println(fl.toString());
//        }
        pf.procId(testId);


    }
}
