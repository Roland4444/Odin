package se.roland;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class JHOF {
    public  String marinafunc(String foo, String  bar){
        return "Σ="+(Integer.valueOf(foo)+Integer.valueOf(bar));
    };

    public  String guest(String a, String b){
        return "";
    }

    public static String result(String foo, String bar, String name, Object obj) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method =obj.getClass().getMethod(name, String.class, String.class);
        method.setAccessible(true);
        var result =   method.invoke(obj, foo, bar);
        return "<h1>"+foo+"</h1><br>h1>"+bar+"</h1><br>"+result;
    }

    public static String callmarina() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        var foo = "3";
        var bar = "7";
        return (result(foo,bar, "marinafunc", new JHOF()));
    }
}
