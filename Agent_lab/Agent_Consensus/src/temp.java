import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import org.w3c.dom.ls.LSException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class temp {


    public static void main (String[] args){
        String data;
        String index;

        Gson gson = new Gson();
        JsonObject object = new JsonObject();
        object.addProperty("data","data1234");
        object.addProperty("index", "index123124");

        String json = gson.toJson(object);

        System.out.println(json);

        List<String> tempList = new ArrayList<>();
        tempList.add(json);
        System.out.println(tempList);
        String temp = gson.toJson((tempList));
        //System.out.println(gson.toJson(tempList));
        //String[] tempSplit = temp.replace("\\[\\]", "");
        String tempSplit = temp.replaceAll("\\[","");
        String tempSplit2 = tempSplit.replaceAll("\\]","");

        //System.out.println(tempSplit);
        //System.out.println(tempSplit2);

        //https://crunchify.com/in-java-how-to-convert-arraylist-to-jsonobject/




        JsonParser parser = new JsonParser();
        //JsonElement element = parser.parse(tempSplit2);

        //data = element.getAsJsonObject().get("data").getAsString();
        //index = element.getAsJsonObject().get("index").getAsString();

        //.out.println(data);
        //System.out.println(index);





    }
}
