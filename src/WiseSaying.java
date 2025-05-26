public class WiseSaying {
    public WiseSaying(int id, String contents, String writer) {
        this.id = id;
        this.contents = contents;
        this.writer = writer;
    }

    private int id;
    private String contents;
    private String writer;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }
}
