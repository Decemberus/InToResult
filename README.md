# InToResult

本项目使用ASM+JavaAgent来读取方法的一些参数并打印到前台控制台

## 效果演示

### ASM修改类

假如说我们需要监控的类如下

```java
public class ClassToBeModify {
    public static void main(String[] args) throws Exception {
        ClassToBeModify classToBeModify = new ClassToBeModify();
        String queryres = classToBeModify.query(145, 17);
        System.out.println("the query result is " + queryres + "\r\n");
    }

    public String query(int uid ,int age) throws Exception{
        return "test to be done here";
    }
}
```

经过我们的ASM+Agent处理后，类变成了

```java
import org.enjoy.asm.MethodMonitor;

public class ClassToBeModifynew {
    public ClassToBeModifynew() {
    }

    public static void main(String[] args) throws Exception {
        ClassToBeModifynew classToBeModify = new ClassToBeModifynew();
        String queryres = classToBeModify.query(145, 17);
        System.out.println("the query result is " + queryres + "\r\n");
    }

    public String query(int var1, int var2) throws Exception {
        long var3 = System.nanoTime();
        Object[] var5 = new Object[]{var1, var2};

        try {
            String var7 = "test to be done here";
            MethodMonitor.point(var3, 1, var5, var7);
            return var7;
        } catch (Exception var8) {
            MethodMonitor.point(var3, 1, var5, var8);
            throw var8;
        }
    }
}
```

这里有一个重点需要讲一下，因为我在HandlerFilter中已经过滤了项目中的类，所以说我们待测对象的类是不能够放在项目中的放在项目中会引发一系列的问题，会导致var和类名没有办法正确显示

### 输出端

理想的输出方式是这样的

```
ASM类输出路径：D:\Code_Project\Java\ASM\src\main\java\org\enjoy\modifying\NewClass.class
监控 - Begin
类名：ClassToBeModifynew
方法：query
入参类型：["I","I"]
入数[值]：[111,17]
出参类型：Ljava/lang/String;
出参[值]："test to be done here"
耗时：(s)
监控 - End

测试结果：the query result is test to be done here
```

但是生成的类无法被正常的执行，debug了半天也找不到问题，这里就先挖个坑了，目前只能显示在MyMethodVisitor中`System.out.println`的内容，没有办法去执行point()方法

，也就是在`MethodMonitor`中写的类没法被正常的执行

目前的输出情况是

```
ASM类输出路径：D:\Code_Project\Java\ASM\src\main\java\org\enjoy\modifying\NewClass.class
startTimeIdentifier：3
parameterIdentifier：5
currentLocal：7
startTimeIdentifier：0
currentLocal：3
xxxx:3
startTimeIdentifier：2
parameterIdentifier：4
currentLocal：7
xxxx:7
startTimeIdentifier：2
parameterIdentifier：4
currentLocal：6
xxxx:7
ASM类输出路径：D:\Code_Project\Java\ASM\src\main\java\org\enjoy\modifying\NewClass.class
startTimeIdentifier：1
currentLocal：3
xxxx:3
startTimeIdentifier：1
currentLocal：3
xxxx:3
ASM类输出路径：D:\Code_Project\Java\ASM\src\main\java\org\enjoy\modifying\NewClass.class
```

