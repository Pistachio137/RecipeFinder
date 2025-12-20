package app;
import java.io.Serializable;

public class SortedRecipes implements Comparable<SortedRecipes>, Serializable{
    public static final long serialIUD = (long)2;
    String name;
    int times;
    public SortedRecipes(String name, int times) {
        this.name = name;
        this.times = times;
    }
    public void incrementTimes() {
        times += 1;
    }
    public String getName() {
        return name;
    }
    public String toString() {
        return (name + ": " + times);
    }
    public int getTimes() {
        return times;
    }

    @Override
    public int compareTo(SortedRecipes otherRecipe) {
        int val = times - otherRecipe.getTimes();
        return val;
    }
}
