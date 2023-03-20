package nsu.id;

public class Topic {
    private Long id;
    private String title;


    private TestId category;

    public TestId getCategory() {
        return category;
    }


    public void setCategory(TestId category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
