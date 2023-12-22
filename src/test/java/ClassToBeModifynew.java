import org.enjoy.modifying.ClassToBeModify;

public class ClassToBeModifynew {

    public static void main(String[] args) throws Exception {
        ClassToBeModifynew classToBeModify = new ClassToBeModifynew();
        String queryres = classToBeModify.query(145, 17);
        System.out.println("the query result is " + queryres + "\r\n");
    }

    public String query(int uid ,int age) throws Exception{
        return "test to be done here";
    }
}
