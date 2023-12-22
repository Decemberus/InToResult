import org.junit.Test;
import org.objectweb.asm.Type;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class test_of_desc {

    @Test
    public void testdesc() {
        String desc = "(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/String;IJ[I[[Ljava/lang/Object;Lorg/itstack/test/Req;)Ljava/lang/String;";

        Matcher m = Pattern.compile("(L.*?;|\\[{0,2}L.*?;|[ZCBSIFJD]|\\[{0,2}[ZCBSIFJD]{1})").matcher(desc.substring(0, desc.lastIndexOf(')') + 1));

        while (m.find()) {
            String block = m.group(1);
            System.out.println(block);
        }
    }

    @Test
    public void asmwhat(){
        System.out.println(Type.getInternalName(System.class));
        System.out.println(Type.getInternalName(Exception.class));
    }
}

