package nsu.framework;

import nsu.annotations.Serialize;
import nsu.annotations.SerializeField;

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
            System.out.println(cls.getCanonicalName());
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
                    if (!Collection.class.isAssignableFrom(field.getType())) {
                        System.out.println(field.getType());
                        if (field.get(obj) != null) {
                            jsonFields.add(key, field.get(obj).toString());
                        } else {
                            jsonFields.add(key, JsonValue.NULL);
                        }
                    } else if (Collection.class.isAssignableFrom(field.getType())) {
                        alreadySerialized.push(obj);
                        jsonFields.add(key, serializeCollection((Collection<?>) field.get(obj)));
                        alreadySerialized.pop();
                    } else {
                        alreadySerialized.push(obj);
                        jsonFields.add(key, persist(field.get(obj)));
                        alreadySerialized.pop();
                    }
                } catch (IllegalAccessException e) {
                    System.out.println("illegal access???");
                }
            }
            json.add("fields", jsonFields);
        }
        return json.build();
    }

    protected static JsonValue serializeCollection(Collection<?> collection) {
        if (collection == null) {
            return JsonValue.NULL;
        }
        JsonObjectBuilder json = Json.createObjectBuilder();
        json.add("ClassName", collection.getClass().getName());
        if (collection.size() == 0) {
            json.add("Collection type", JsonValue.NULL);
        } else {
            json.add("Collection type", collection.iterator().next().getClass().getName());
        }
        JsonArrayBuilder arrBuilder = Json.createArrayBuilder();

        for (var element : collection) {
            if (element == null) {
                arrBuilder.add(JsonValue.NULL);
            } else if (Collection.class.isAssignableFrom(element.getClass())) {
                alreadySerialized.push(collection);
                arrBuilder.add(serializeCollection((Collection<?>) element));
                alreadySerialized.pop();
            } else {
                arrBuilder.add(element.toString());
            }
        }
        json.add("values", arrBuilder.build());
        return json.build();
    }

}

