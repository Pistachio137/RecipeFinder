package app;
import java.io.Serializable;
import java.util.*;

import javafx.collections.ObservableList;
public class RestrictionsObject implements Serializable{

    public HashMap<String, String[]> hashMap;
    public ArrayList<String> array;

    public RestrictionsObject(HashMap<String, String[]> hm, ArrayList<String> a) {
        hashMap = hm;
        array = a;
    }

    public RestrictionsObject() {}

    public HashMap<String, String[]> getHashMap () {
        return hashMap;
    }
    public ArrayList<String> getArrayList() {
        return array;
    }

}