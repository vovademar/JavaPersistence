package nsu.framework;

//import com.google.gson.JsonObject;

import javax.json.JsonObject;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class PredicateWorker<T> implements Predicate<JsonObject> {
    private final Predicate<T> predicate;
    protected List<String> paths;

    Class<T> type;

    public PredicateWorker(Predicate<T> predicate, String path, Class<T> type) {
        this.predicate = predicate;
        this.paths = Arrays.stream(path.split("/")).toList();
        this.type = type;
    }

    public static Object convert(Class type, String txt) {
        PropertyEditor editor = PropertyEditorManager.findEditor(type);
        editor.setAsText(txt);
        return editor.getValue();
    }

    @Override
    public boolean test(JsonObject jsonObject) {
        T val;
        JsonObject obj = jsonObject;
        String pth;
        String value;
        for (int i = 0; i < paths.size(); i++) {
            pth = paths.get(i);
            if (i == paths.size() - 1) {
                value = jsonObject.getString(pth);
                val = (T) convert(type, value);
                return predicate.test(val);
            }
            else {
                obj = obj.getJsonObject(pth).getJsonObject("fields");
            }
        }
        return false;
    }
}
