
package gui.project2.v1;


public class pair {
    private final String name;
    private final double a;
    private final double b;
    public pair(String name, double a, double b){
        this.name = name;
        this.a = a;
        this.b = b;
    }
    String getName(){
        return this.name;
    }
    double getA(){
        return this.a;
    }
    double getB(){
        return this.b;
    }
}
