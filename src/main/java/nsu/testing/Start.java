package nsu.testing;


import nsu.id.IdWorker;
import org.json.simple.parser.ParseException;

import java.io.IOException;

public class Start {
    public static void main(String[] args) throws IllegalAccessException, IOException, ParseException {
        TestId testId = new TestId();
        IdWorker pf = new IdWorker();
//        System.out.println(pf.getId());
        pf.procId(testId);
//        System.out.println(pf.checkId(testId));


    }
}