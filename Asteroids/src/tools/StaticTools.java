package tools;
public final class StaticTools{
    public static final java.util.Random rnd = new java.util.Random(System.currentTimeMillis());
    
    public static double[] getMidpoint(double x1, double y1, double x2, double y2){
        double midX = (x2+x1)/2.0;
        double midY = (y2+y1)/2.0;
        return new double[] {midX, midY};
    }
    
    public static double getDistance(double x1, double y1, double x2, double y2){
        return Math.sqrt( Math.pow(x1-x2,2) + Math.pow(y1-y2,2) );
    }
    
    public static double getDistance(double[] p1, double[] p2){
        return Math.sqrt( Math.pow(p1[0]-p2[0], 2) + Math.pow(p1[1]-p2[1], 2) );
    }
    
    public static int randomPol(){
        return rnd.nextInt(2)==0?1:-1;
    }
    
    public static int nextInt(int minValue, int maxValue){
        return rnd.nextInt(maxValue - minValue) + minValue;
    }
    
    public static double nextDouble(double minValue, double maxValue){
        return (rnd.nextDouble()*(maxValue-minValue)) + minValue;
    }
    
    public static String capEveryWord(String a){
        String value = "";
        String[] tokens = a.split(" ");
        int length;
        for (int i=0; i<tokens.length; i++){
            length = tokens[i].length();
            if (length == 1)
                value += tokens[i].toUpperCase() + " ";
            else
                value += tokens[i].substring(0, 1).toUpperCase() + tokens[i].substring(1).toLowerCase() + " ";
        }
        return value.trim();
    }
}