package nsu.id;

import nsu.annotations.ID;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.lang.reflect.Field;


public class IdWorker {

    public static File file = new File("id.json");
    public static long idCnt;

    public IdWorker() {

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
            idCnt = 1;
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
                return obj;
            }

        }
        idCnt--;
        System.out.println("net");
        reader.close();
        System.out.println(idCnt);
        return obj;
    }

    public static long getId() throws IOException, ParseException {
        if (file.length() == 0 || !file.exists()) {
            return -1;
        }
        Reader reader = new FileReader(file);
        JSONParser parser = new JSONParser();
        JSONObject ob = new JSONObject();
        ob = (JSONObject) parser.parse(reader);
        long id = (Long) ob.get("id");
        return id;
    }

    public static long checkId(Object obj) throws IllegalAccessException {
        Class c = obj.getClass();
//        long id = -1; // tipa error
        Field[] fields = c.getDeclaredFields();
        for (Field fld : fields) {
            if (fld.isAnnotationPresent(ID.class)) {
                fld.setAccessible(true);
                if ((long) fld.get(obj) == 0) {
                    return -1;
                } else {
                    return (long) fld.get(obj);
                }
            }
        }
        return -2;

    }


}