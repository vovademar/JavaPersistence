package nsu.framework;

import nsu.id.ID;
import nsu.id.PersFramework;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.StringReader;
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

    public static JsonValue persist(Object obj) throws IOException, ParseException {
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

        Field[] findIdField = obj.getClass().getDeclaredFields();
        boolean idDetected = false;
        for (Field fld : findIdField) {
            if (fld.isAnnotationPresent(ID.class)) {
                try {
                    PersFramework.procId(obj);
                } catch (IllegalAccessException | IOException | ParseException e) {
                    throw new RuntimeException(e);
                }
                json.add("ID", PersFramework.getId());
                idDetected = true;
            }
        }
        if (!idDetected) {
            json.add("ID", "NULL");
        }
        json.add("ClassName", className);


        if (objectFields != null) {
            Set<String> keys = objectFields.keySet();
            JsonObjectBuilder jsonFields = Json.createObjectBuilder();
            for (String key : keys) {
                try {
                    Field field = objectFields.get(key);
                    field.setAccessible(true);
                    var cls = field.getType();
                    boolean isWrapper = Integer.class.isAssignableFrom(cls) ||
                            String.class.isAssignableFrom(cls) ||
                            Double.class.isAssignableFrom(cls) ||
                            Boolean.class.isAssignableFrom(cls) ||
                            Byte.class.isAssignableFrom(cls) ||
                            Short.class.isAssignableFrom(cls) ||
                            Long.class.isAssignableFrom(cls) ||
                            Float.class.isAssignableFrom(cls);
                    if (isWrapper || cls.isPrimitive()) {
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

    private Object deserialize(JsonObject json) {
        Object obj = new Object();
        return obj;
    }

    private Object deserialize(String json) {
        JsonObject object = Json.createReader(new StringReader(json)).readObject();
        return deserialize(object);
    }
}

