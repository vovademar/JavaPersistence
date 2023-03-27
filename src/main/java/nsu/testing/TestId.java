package nsu.testing;


import nsu.annotations.ID;

public class TestId {

    private long u;
    private int tmp;

    @ID
    private long id;
    public long getId() {

        return (id);
    }

    public void printSmth(){
        System.out.println("vnejf");
    }

}
