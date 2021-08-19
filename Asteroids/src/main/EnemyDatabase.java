package main;

import tools.StaticTools;

public class EnemyDatabase
{
    private static final double[][] dartEnemy  = new double[][] { {0, -5, -2, -2, 2, 2, 5, 5}, 
                                                                  {12, -13, -13, -20, -20, -13, -13, -13}};
    private static final double[][] ufoEnemy   = new double[][] { {0, -5, -5, -8, -8, -5, -5, 0, 5, 5, 5},
                                                                  {15, 10, 5, 5, -5, -5, -10, -15, -10, 10, 10}};
    private static final double[][] warShip    = new double[][] { {-2, -6, -7, -11, -14, -19, -11, -2, 1, 10, 18, 13, 10, 6, 5, 1, 1},
                                                                  {3, 7, 1, 0, 9, 1, -7, -2, -2, -7, 1, 9, 0, 1, 7, 3, 3}};
    private static final double[][] warShip2   = new double[][] { {-2, -5, -8, -9, -11, -16, -17, -16, -14, -4, 3, 13, 15, 16, 15, 10, 8, 7, 4, 1, 1},
                                                                  {8, 10, 5, 9, 15, 9, 5, -1, -9, -15, -15, -9, -1, 5, 9, 15, 9, 5, 10, 8, 8}};
    private static final double[][] cruiser    = new double[][] { {-20, -20, -16, 5, -2, 19, 12, 19, -2, 5, -16},
                                                                  {0, 2, -5, -4, -13, -7, -1, 6, 12, 3, 4}};
    private static final double[][] archEnemy  = new double[][] { {0,   -10, 10},
                                                                  {12, -13, -13}};
    private static final double[][] plane      = new double[][] { {0, -6, -31, -31, -6, -3, -16, -16, 0, 16, 16, 3, 6, 31, 31, 6, 6},
                                                                  {35, 16, 5, -1, 5, -22, -28, -34, -31, -34, -28, -22, 5, -1, 5, 16, 16}};
    private static final double[][] pacman     = new double[][] { {2, 2, -12, -19, -19, -13, -3, 13, 18, 18, 14, 14},
                                                                  {20, -10, 16, 7, -6, -14, -19, -14, -6, 7, 14, 14}};
    private static final double[][] classic    = new double[][] { {0, -4, -9, -12, -14, -15, -15, -12, -7, -4, 0, 4, 7, 12, 15, 15, 14, 12, 9, 4, 4},
                                                                  {19, 18, 13, 8, 4, -4, -13, -17, -17, -12, -17, -12, -17, -17, -13, -4, 3, 8, 13, 18, 18}};
    private static final double[][] shipChris  = new double[][] { {0, 15, 15, 1, 1, 7, 0, -7, -1, -1, -15, -15},
                                                                  {-12, -5, 5, 1, 0, 3, 24, 3, 0, 1, 5, -5}};
    private static final double[][] triShip    = new double[][] { {0-20, 17-20, 17-20, 3-20, 36-20, 22-20, 22-20, 39-20, 39-20, 19.5-20, 0-20},
                                                                  {7-20, 31-20, 25-20, 0-20, 0-20, 25-20, 31-20, 7-20, 20-20, 39-20, 20-20}};
    private static final double[][] batwing    = new double[][] { {0.0, 6.0, 10.0, 20.0, 28.0, 37.0, 37.0, 28.0, 24.0, 16.0, 9.0, 6.0, 0.0, -6.0, -9.0, -16.0, -24.0, -28.0, -37.0, -37.0, -28.0, -20.0, -10.0, -6.0},
                                                                  {-12.0, -18.0, -9.0, -9.0, -17.0, -9.0, 3.0, 11.0, 3.0, 0.0, 6.0, 0.0, 18.0, 0.0, 6.0, 0.0, 3.0, 11.0, 3.0, -9.0, -17.0, -9.0, -9.0, -18.0}};
    private static final double[][] toothbrush = new double[][] { {-6.0, -4.0, -4.0, 6.0, -4.0, 6.0, -2.0, 5.0, -8.0, -8.0},
                                                                  {-30.5, -28.5, 7.5, 7.5, 12.5, 13.5, 15.5, 18.5, 18.5, -28.5}};
    private static final double[][] toothpick  = new double[][] { {-2, 0, 2, 2, 0, -2},
                                                                  {-35, -40, -35, 35, 40, 35}};
//     private static final double[][] toothpaste = new double[][] { {-20,20 ,10, 5,5, -5, -5, -10},
//                                                                   {-30, -30, 10, 10, 15, 15, 10, 10}};
//     private static final double[][] tuningfork = new double[][] { {-33.5, 30.5, 30.5, 2.5, 2.5, 33.5, 13.5, 4.5, 22.5, -1.5, -25.5, -7.5, -16.5, -36.5, -5.5, -5.5, -33.5},
//                                                                   {-35.0, -35.0, -30.0, -30.0, -25.0, 0.0, 35.0, 31.0, 0.0, -18.0, 0.0, 31.0, 35.0, 0.0, -25.0, -30.0, -30.0}};
    
    private static final double[][][] database = new double[][][] {dartEnemy, ufoEnemy, warShip, warShip2, cruiser, archEnemy, plane, pacman, classic, shipChris, triShip, batwing, toothbrush, toothpick};
    public static final double maxRadius = 41;
    
    public static double[][] randomEnemyShape(){
        return database[StaticTools.rnd.nextInt(database.length)];
    }
    
    public static int length(){
        return database.length;
    }
    
    public static double[][] get(int i){
        try{
            return database[i];
        }
        catch (ArrayIndexOutOfBoundsException e){
            return null;
        }
    }
}