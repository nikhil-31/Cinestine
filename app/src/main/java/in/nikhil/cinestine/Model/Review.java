package in.nikhil.cinestine.Model;

/**
 * Created by nikhil on 27-08-2016.
 */
public class Review {
  private String id;
  private String author;
  private String content;

  public Review() {

  }

  public void setId(String Id) {
    id = Id;
  }

  public String getId() {
    return id;
  }

  public void setAuthor(String Author) {
    author = Author;
  }

  public String getAuthor() {
    return author;
  }

  public void setContent(String Content) {
    content = Content;
  }

  public String getContent() {
    return content;
  }

}
