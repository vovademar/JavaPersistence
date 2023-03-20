package nsu.id;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.UUID;

public class TestId {
    @ID
    private long id;
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private long id2;
//    @Id
//    @GeneratedValue
//    private long id3;
//    private String title;


    public long getId() {

        return (id);
    }

    public void printSmth(){
        System.out.println("vnejf");
    }

}
