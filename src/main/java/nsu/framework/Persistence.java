package nsu.framework;

import nsu.id.ID;
import nsu.id.PersFramework;
import org.json.simple.parser.ParseException;

import javax.json.*;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Predicate;

public class Persistence {

    public static File file = new File("serializer.json");

    private static Stack<Object> alreadySerialized = new Stack<>();

    public static HashMap<String, Field> getFields(Class<?> cls) {

        if (cls.isAnnotationPresent(Serialize.class)) {
            Serialize an = cls.getAnnotation(Serialize.class);
            HashMap<String, Field> fields = new HashMap<>();

            Field[] declaredFields = cls.getDeclaredFields();
            if (an.serializeAll()) {
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

    public static JsonValue persist(Object obj) throws IllegalAccessException {
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
        for (Field fld : findIdField) {
            if (fld.isAnnotationPresent(ID.class)) {
                if (fld.getLong(obj) == 0) {
                    try {
                        PersFramework.procId(obj);
                    } catch (IllegalAccessException | IOException | ParseException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
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
                    boolean isWrapper = Integer.class.isAssignableFrom(cls) || String.class.isAssignableFrom(cls) || Double.class.isAssignableFrom(cls) || Boolean.class.isAssignableFrom(cls) || Byte.class.isAssignableFrom(cls) || Short.class.isAssignableFrom(cls) || Long.class.isAssignableFrom(cls) || Float.class.isAssignableFrom(cls);
                    if (isWrapper || cls.isPrimitive()) {
                        System.out.println(field.getType());
                        if (field.get(obj) != null) {
                            System.out.println(field.getName() + " " + field.get(obj) + " - obj");
                            jsonFields.add(key, field.get(obj).toString());
                        } else {
                            jsonFields.add(key, JsonValue.NULL);
                        }
                    } else if (Collection.class.isAssignableFrom(field.getType())) {
                        alreadySerialized.push(obj);
                        jsonFields.add(key, persistCollection((Collection<?>) field.get(obj)));
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

    protected static JsonValue persistCollection(Collection<?> collection) {
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
                arrBuilder.add(persistCollection((Collection<?>) element));
                alreadySerialized.pop();
            } else {
                arrBuilder.add(element.toString());
            }
        }
        json.add("array", arrBuilder.build());
        return json.build();
    }


    public static void flush(JsonValue jsonValue) throws IOException {
        if (!file.exists()) {
            file.createNewFile();
        }
        Writer writer = new java.io.FileWriter(file);
        JsonWriter jsonWriter = Json.createWriter(writer);
        jsonWriter.writeObject((JsonObject) jsonValue);
        jsonWriter.close();
        writer.close();
    }

    private static Object modifyObject(Class<?> targetType, String text) {
        PropertyEditor editor = PropertyEditorManager.findEditor(targetType);
        editor.setAsText(text);
        return editor.getValue();
    }

    public static Object deserializeObject(String jsonString, Predicate<JsonObject> predicate) throws Exception {
        JsonObject jsonObject = Json.createReader(new StringReader(jsonString)).readObject();
        if (jsonObject.containsKey("fields")) {
            if (predicate == null || predicate.test(jsonObject.getJsonObject("fields"))) {
                return deserializeFields(jsonObject);
            } else {
                return null;
            }
        } else if (jsonObject.containsKey("array")) {
            if (predicate == null) {
                return deserializeCollection(jsonObject);
            } else {
                JsonObjectBuilder filteredCollection = Json.createObjectBuilder().add("ClassName", jsonObject.getString("ClassName"));
                try {
                    filteredCollection.add("Collection type", jsonObject.getString("Collection type"));
                } catch (ClassCastException e) {
                    return deserializeCollection(jsonObject);
                }

                JsonArrayBuilder filteredArray = Json.createArrayBuilder();
                JsonArray elements = jsonObject.getJsonArray("array");

                for (int i = 0; i < elements.size(); i++) {
                    JsonObject obj = elements.getJsonObject(i);
                    if (predicate.test(obj.getJsonObject("fields"))) {
                        filteredArray.add(obj);
                    }
                }
                filteredCollection.add("array", filteredArray.build());
                return deserializeCollection(filteredCollection.build());
            }
        } else {
            return null;
        }
    }

    private static Object deserializeJsonObject(JsonObject object) throws Exception {
        String objectType = object.containsKey("fields") ? "fields" : object.containsKey("array") ? "array" : object.containsKey("ID") ? "ID" : null;

        return switch (Objects.requireNonNull(objectType)) {
            case "fields", "ID" -> deserializeFields(object);
            case "array" -> deserializeCollection(object);
            default -> null;
        };
    }

    private static Collection<?> deserializeCollection(JsonObject jsonObject) throws Exception {
        String className = jsonObject.getString("ClassName");
        Class<?> cls = Class.forName(className);
        Collection<Object> collection = (Collection<Object>) cls.getDeclaredConstructor().newInstance();

        String genericClassName;
        genericClassName = jsonObject.getString("Collection type");

        Class<?> genericClass = Class.forName(genericClassName);
        boolean isWrapper = Integer.class.isAssignableFrom(genericClass) || String.class.isAssignableFrom(genericClass) ||
                Double.class.isAssignableFrom(genericClass) || Boolean.class.isAssignableFrom(genericClass) ||
                Byte.class.isAssignableFrom(genericClass) || Short.class.isAssignableFrom(genericClass) ||
                Long.class.isAssignableFrom(genericClass) || Float.class.isAssignableFrom(genericClass);
        JsonArray jsonArray = jsonObject.getJsonArray("array");
        for (JsonValue value : jsonArray) {
            if (isWrapper || genericClass.isPrimitive()) {
                collection.add(modifyObject(genericClass, value.toString()));
            } else {
                collection.add(deserializeJsonObject((JsonObject) value));
            }
        }
        return collection;
    }


    private static Field findField(Class<?> cls, String fieldName) throws Exception {
        Field res = null;
        Field[] fields = cls.getDeclaredFields();
        Serialize an = cls.getAnnotation(Serialize.class);
        if (an == null) throw new Exception("deserializing unannotated class");
        for (Field fld : fields) {
            if (fld.getName().equals(fieldName)) {
                res = fld;
                break;
            } else if (fld.isAnnotationPresent(SerializeField.class)) {
                SerializeField serAno = fld.getAnnotation(SerializeField.class);
                if (serAno.Name().equals(fieldName)) {
                    res = fld;
                    break;
                }
            }
        }

        if (res == null && an.requiresParent()) res = findField(cls.getSuperclass(), fieldName);
        return res;
    }

    private static Object deserializeFields(JsonObject jsonObject) throws Exception {
        String className = jsonObject.getString("ClassName");
        Class<?> cls = Class.forName(className);
        System.out.println(cls);
        Object[] constructors = Arrays.stream(cls.getConstructors()).filter(c -> c.isAnnotationPresent(DeserializeConstructor.class)).toArray();
        if (constructors.length == 1) {
            Constructor<?> constructor = (Constructor<?>) constructors[0];

            Annotation[][] parameterAnnotations = constructor.getParameterAnnotations();
            ArrayList<String> constructorParams = new ArrayList<>();

            for (Annotation[] ano : parameterAnnotations) {
                for (Annotation an : ano) {
                    if (an.annotationType().equals(DeserializeField.class)) {
                        constructorParams.add(((DeserializeField) an).value());
                    }
                }
            }


            JsonObject fields = jsonObject.getJsonObject("fields");

            ArrayList<Object> constrParams = new ArrayList<>();
            HashMap<String, Object> fieldsInstance = new HashMap<>();
            Set<String> keys = fields.keySet();

            for (String fieldName : constructorParams) {
                if (keys.contains(fieldName)) {
                    JsonValue fieldValue = fields.get(fieldName);
                    if (fieldValue instanceof JsonString) {
                        constrParams.add(((JsonString) fieldValue).getString());
                    } else {
                        constrParams.add(fieldValue);
                    }
                } else {
                    throw new RuntimeException("Not enough parameters for constructor");
                }
            }

            for (String key : keys) {
                if (!constructorParams.contains(key)) {
                    JsonValue fieldValue = fields.get(key);
                    if (fieldValue instanceof JsonString) {
                        fieldsInstance.put(key, ((JsonString) fieldValue).getString());
                    } else {
                        fieldsInstance.put(key, fieldValue);
                    }
                }
            }

            Class<?>[] required = constructor.getParameterTypes();
            for (int i = 0; i < required.length; i++) {
                try {
                    constrParams.set(i, modifyObject(required[i], (String) constrParams.get(i)));
                } catch (ClassCastException e) { // complex types
                    constrParams.set(i, deserializeJsonObject((JsonObject) constrParams.get(i)));
                } catch (NullPointerException e2) {
                    constrParams.set(i, null);
                }
            }

            Set<String> fieldKeys = fieldsInstance.keySet();
            for (String key : fieldKeys) {
                Field field = findField(cls, key);
                field.setAccessible(true);
                Class<?> fieldClass = field.getType();
                try {
                    fieldsInstance.put(key, modifyObject(fieldClass, (String) fieldsInstance.get(key)));
                } catch (ClassCastException e) {
                    fieldsInstance.put(key, deserializeJsonObject((JsonObject) fieldsInstance.get(key)));
                } catch (NullPointerException e) {
                    fieldsInstance.put(key, null);
                }
            }

            Object res;
            try {
                res = constructor.newInstance(constrParams.toArray());

            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                return null;
            }
            for (String key : fieldKeys) {
                Field fld = findField(cls, key);
                fld.setAccessible(true);
                if (key.equals("ID")) {
                    fld.set(res, fieldsInstance.get(key));
                } else {
                    fld.set(res, fieldsInstance.get(key));
                }
            }
            return res;
        } else {

            constructors = Arrays.stream(cls.getConstructors()).filter(c -> c.getParameterCount() == 0).toArray();
            Object res;
            if (constructors.length != 1) {
                throw new Exception("Couldn't find constructor for the class");
            } else {
                Constructor<?> constructor = (Constructor<?>) constructors[0];
                res = constructor.newInstance();
            }
            JsonObject fields = jsonObject.getJsonObject("fields");
            Set<?> keys = fields.keySet();
            for (var key : keys) {

                Field field = findField(cls, (String) key);
                field.setAccessible(true);
                try {
                    String value = fields.getString((String) key);
                    Object updatedValue = modifyObject(field.getType(), value);
                    field.set(res, updatedValue);
                } catch (ClassCastException e) {
                    JsonObject complexValue = fields.getJsonObject((String) key);
                    Object updatedValue = deserializeJsonObject(complexValue);
                    field.set(res, updatedValue);
                }

            }
            return res;
        }
    }

    public static JsonValue findById(JsonValue jsonValue, String id) {
        if (jsonValue instanceof JsonObject jsonObject) {

            JsonObject firstLayer = jsonObject.getJsonObject("fields");
            try {
                if (firstLayer.getString("id").equals(id)) {
                    return jsonObject;
                }
            } catch (NullPointerException e) {
                return null;
            }

            if (jsonObject.containsKey("fields")) {
                JsonObject fields = jsonObject.getJsonObject("fields");
                for (String key : fields.keySet()) {
                    JsonValue value = findById(fields.get(key), id);
                    if (value != null) {
                        return value;
                    }
                }
            }
        } else if (jsonValue instanceof JsonArray jsonArray) {
            for (JsonValue value : jsonArray) {
                JsonValue result = findById(value, id);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }


}

