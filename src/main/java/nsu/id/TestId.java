package nsu.id;


import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.UUID;

public class TestId {
    @ID
    private long id;

    public long getId() {

        return (id);
    }

    public void printSmth(){
        System.out.println("vnejf");
    }

}
