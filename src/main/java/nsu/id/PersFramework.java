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

    public static File file = new File("id.json");
    public static long idCnt;

    public PersFramework() {

    }

    public static Object procId(Object obj) throws IllegalAccessException, IOException, ParseException {


        if (!file.exists()) {
            file.createNewFile();
        }

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
                Writer writer = new FileWriter(file);
                writer.write(ob.toJSONString());
                writer.close();
                break;
            } else {
                idCnt--;
                System.out.println("net");
                return null;
            }
        }
        reader.close();

        return obj;




    }
}