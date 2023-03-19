package nsu.framework;

import java.lang.reflect.Field;
import java.util.*;
import javax.json.*;

public class Persistence {

    private static Stack<Object> alreadySerialized = new Stack<>();

    public static HashMap<String, Field> getFields(Class<?> cls) {

        if (cls.isAnnotationPresent(Serialize.class)) {
            Serialize an = cls.getAnnotation(Serialize.class);
            HashMap<String, Field> fields = new HashMap<>();

            if (an.requiresParent()) {
                fields.putAll(getFields(cls.getSuperclass()));
            }
            Field[] declaredFields = cls.getDeclaredFields();
            if (an.allFields()) {
                for (Field fld : declaredFields) {
                    fields.put(fld.getName(), fld);
                }
            } else {
                for (Field fld : declaredFields) {
                    if (fld.isAnnotationPresent(SerializeField.class)) {
                        SerializeField serializedAnnotation = fld.getAnnotation(SerializeField.class);
                        if (!Objects.equals(serializedAnnotation.Name(), "")) {
                            fields.put(serializedAnnotation.Name(), fld);
                        } else {
                            fields.put(fld.getName(), fld);
                        }
                    }
                }
            }
            return fields;
        } else {
            System.out.println("not annotated");
            return null;
        }
    }

    public static JsonValue persist(Object obj) {
        if (obj == null) {
            return JsonValue.NULL;
        }
        if (alreadySerialized.contains(obj)) {
            throw new IllegalArgumentException("Cyclic reference detected");
        }
        alreadySerialized.push(obj);
        Class<?> objClass = obj.getClass();
        String className = objClass.getName();
        HashMap<String, Field> objectFields = getFields(objClass);
        JsonObjectBuilder json = Json.createObjectBuilder();
        json.add("ClassName", className);

        if (objectFields != null) {
            Set<String> keys = objectFields.keySet();
            JsonObjectBuilder jsonFields = Json.createObjectBuilder();
            for (String key : keys) {
                try {
                    Field field = objectFields.get(key);
                    field.setAccessible(true);
                    if (field.get(obj) != null) {
                        jsonFields.add(key, field.get(obj).toString());
                    } else {
                        jsonFields.add(key, JsonValue.NULL);
                    }
                } catch (IllegalAccessException e) {
                    System.out.println("illegal access???");
                }
            }
            json.add("fields", jsonFields);
        }
        return json.build();
    }
}

