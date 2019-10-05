import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.w3c.dom.ls.LSException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class temp {

    public static void main (String[] args){
     List<String> listtemp = new ArrayList<>();
     listtemp.add("afdafa");

     HashMap<String, String> hashtemp  = new HashMap<>();
     hashtemp.put("1234","1234");
     hashtemp.put("2345","2345");

     System.out.println(listtemp);
     System.out.println(hashtemp);
     listtemp.clear();
     hashtemp.remove("2345");

     System.out.println(listtemp);
        System.out.println(hashtemp);

    }
}
