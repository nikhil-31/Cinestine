package in.nikhil.cinestine;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class ExampleUnitTest {

  @Test
  public void exampleTest1() {

    Assert.assertEquals(2, 1 + 1);


  }

  @Test
  public void exampleTest2(){
    int a = 15;
    int b = 15;
    Assert.assertTrue((a == b));
  }

}
