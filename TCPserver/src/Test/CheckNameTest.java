package Test;

import com.company.ClientThread;
import org.junit.runner.RunWith;


public class CheckNameTest extends junit.framework.TestCase  {


  public void testCheckName() throws Exception {

      boolean aBoolean = new ClientThread().checkName("Hejmeddig-");
      assertEquals(true,aBoolean);
  }
    public void testCheckName1() throws Exception {

        boolean aBoolean = new ClientThread().checkName("Hejmed111_");
        assertEquals(true,aBoolean);
    }
    public void testCheckName2() throws Exception {

        boolean aBoolean = new ClientThread().checkName("hhhhhhhhhh4444444222");
        assertEquals(true,aBoolean);
    }
    public void testCheckName3() throws Exception {

        boolean aBoolean = new ClientThread().checkName("Hejmedd...");
        assertEquals(false,aBoolean);
    }
    public void testCheckNameWithWhitespace() throws Exception {

        boolean aBoolean = new ClientThread().checkName("Sebastian I");
        assertEquals(true,aBoolean);
    }

}
