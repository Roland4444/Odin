package se.roland;
import se.roland.abstractions.Doc;
import se.roland.docs.SimpleDoc;
import se.roland.docs.SuperDoc;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;


public class JHOF {
    private String marinafunc(String foo, String  bar){
        return "Σ="+(Integer.valueOf(foo)+Integer.valueOf(bar));
    };
    private String guest(String a, String b){
        return "";
    }
    public static String result(String foo, String bar, String name, Object obj) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method =obj.getClass().getDeclaredMethod(name, String.class, String.class);
        return "<h1>"+foo+"</h1><br>h1>"+bar+"</h1><br>"+method.invoke(obj, foo, bar);
    }
    public static String callmarina() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        var foo = "3";
        var bar = "7";
        return (result(foo,bar, "marinafunc", new JHOF()));
    }

    public Doc gen(String input){
        if (input.equals("S"))
            return new SuperDoc();
        if (input.equals("s"))
            return new SimpleDoc();
        return null;
    };


    public ArrayList<Doc> generate(ArrayList input){
        ArrayList result = new ArrayList();
        input.forEach(a->result.add(gen((String) a)));
        return result;
    }

    public int countSuper() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        var counter = 0;
        Class c=  JHOF.class.getClassLoader().getClass();
        System.out.println(c);
        return counter;
    };



}
