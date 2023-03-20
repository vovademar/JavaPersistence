package nsu.id;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import netscape.javascript.JSObject;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.lang.reflect.Field;
import java.util.HashMap;
import javax.json.*;


public class PersFramework {


    public static long idCnt;
    //    private long id = 0;
    public PersFramework() {

    }

    public static void procId(Object obj) throws IllegalAccessException, IOException, ParseException {
        File file = new File("id.json");
        Reader reader = new FileReader(file);
        Class c = obj.getClass();
        Field[] fields = c.getDeclaredFields();
        JSONParser parser = new JSONParser();
        JSONObject ob = new JSONObject();

        if (file.length() == 0) {
            idCnt = 0;

        } else {
            ob = (JSONObject) parser.parse(reader);   // тута через simple json
            idCnt = (Long) ob.get("id");
            idCnt++;
        }
        for (Field fld : fields) {
            if (fld.isAnnotationPresent(ID.class)) {

                fld.setAccessible(true);
                fld.set(obj, idCnt);
                ob.put("id", idCnt);
                System.out.println(fld.get(obj));
            } else {
                idCnt--;
                System.out.println("net");
            }
        }



        Writer writer = new FileWriter(file);
        writer.write(ob.toJSONString());
        writer.close();
        reader.close();





//        JsonObject a = g.fromJson(reader, JsonObject.class);
//        JsonElement num1 = a.get("id");
////                                                                            // тута gson гугловский, выглядит хуже, но зато хухл
//        idCnt = Integer.parseInt(String.valueOf(num1));
//        idCnt++;
//        System.out.println(num1);
//        System.out.println(idCnt);
//        a.add("id", idCnt);
    }

}
