package main;

import java.awt.*;
import java.util.*;
import java.io.File;
import javax.swing.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;

import display.BFont;
import display.BFrame;
import display.CanvasMax;
import space.manipulators.Explode;
import space.manipulators.Inactive;
import space.manipulators.SpaceObjectIterator;
import space.manipulators.SpaceObjectList;
import space.objects.Ally;
import space.objects.Asteroid;
import space.objects.Bullet;
import space.objects.ChaseEnemy;
import space.objects.Enemy;
import space.objects.EnemyBullet;
import space.objects.EnemyMissile;
import space.objects.Flame;
import space.objects.GuidedMissile;
import space.objects.Mine;
import space.objects.Missile;
import space.objects.NuclearMissile;
import space.objects.Ship;
import space.objects.Star;
import space.objects.parents.SpaceObject;
import tools.StaticTools;
import sound.SoundClip;

//import java.applet.Applet;
//import java.applet.AudioClip;


public class Asteroids extends KeyAdapter implements KeyListener, Runnable
{
    // Game fields:
       private static boolean clearInactive = true, getHarder = false, cheatMode = true, superMan = false, fullScreen = false;
       private static boolean cheatModeUnlimited = true;
      // Points
        private static int pointsPerGen = 25, enemyPoints = 30, chaseEnemyPoints = 40, enemyMissilePoints = 500, enemyBulletPoints = 1000, diePenalty = 200;
        private static int moreMissilePoints = 8050, moreGmissilePoints = 9500, moreSshotPoints = 7000, moreNukMissilePoints = 90000, moreGhostPoints = 30000, moreLivesPoints = 40001, moreMinePoints = 1000;
        private static int allyAwardPoints = 50000;
      // Frame data
        private static int framesBetweenAsteroidRebirth = 200, framesBetweenEnemyBirth = 400, framesBetweenChaseEnemyBirth = 800, framesBetweenShipRebirth = 100;
        private static int missileLife = 150, bulletLife = 150, gMissileLife = 1000, nukMissileLife = 400, mineLife = 3000;
        private static int clearFrameCountdown = 3000;
      // Time data
        private static int bDelay = 80, mDelay = 95, gmDelay = 95, ssDelay = 250, pupMDelay = 250, pupGmDelay = 250, nukDelay = 300, mineDelay = 100, maxWait = 20, pupMineDelay = 10000;
        //private static final int bDelay = 5, mDelay = 5, gmDelay = 5, ssDelay = 5, pupMDelay = 5, pupGmDelay = 5, nukDelay = 5, mineDelay = 5, maxWait = 20;
      // Awards
        private static int  allyAward = 1,  sShotAward = 1,  gMissAward = 50,  missAward = 50, nukMissAward = 3, lifeAward = 1, ghostAward = 1, mineAward = 5;
      // Visuals
        private static int numOfAsteroids = 1;
        private static int numOfStars = 30, xMax = (fullScreen?1600:1300), yMax = (fullScreen?900:750), numAsteroidsAdded = 2, maxAsteroids = 18;
        private static BFont statsFont = new BFont("Arial", Font.PLAIN, 12), gameOverFont = new BFont("Arial", Font.PLAIN, 50);
        private static Color statusTextColor = Color.white, gameOverTextColor = Color.magenta;
      // Startup data
        private static int startingMiss = 150, startingGmiss = 150, startingSshot = 4, startingNukMiss = 5, startingLives = 4, startingGhosts = 3, startingMines = 20;
        private static boolean startWithAsteroids = false;
      // Other Game play data
        private static double bulletPUmultiplyer = .9, missilePUmultiplyer = .9, gMissilePUmultiplyer =.75, minePUmultiplyer = 2.5; // Powerup life multipliers for bullets, missiles, guided missiles and mines
        private static boolean useEnemies = false, useChaseEnemies = false, useAllies = false, autoInitAsteroids = false;
        private static int maxAllies = 10, maxEnemies = 100, maxChaseEnemies = 5;
        private static String filePath = "/", bgExtension = "gif";
        private boolean useStars = true, bgImage = true, useExplosions = true, useSound = false;
        private static double getHarderChance = .45;
      // Text Placement
        private static int topRight = 0, topLeft = 1, center = 2, bottomRight = 3, bottomLeft = 4, lineSpacing = 9, edgeCusion = 13;
    
    // Game Lists
      // Bullets fired by you and your allies
        private final SpaceObjectList<Bullet> bullets      = new SpaceObjectList<Bullet>();
      // Mines
        private final SpaceObjectList<Mine> mines        = new SpaceObjectList<Mine>();
      // Missiles fired by you and your allies
        private final SpaceObjectList<Missile> missiles     = new SpaceObjectList<Missile>();
      // Guided missiles fired by you and your allies
        private final SpaceObjectList<GuidedMissile> gMissiles    = new SpaceObjectList<GuidedMissile>();
      // The asteroids
        private final SpaceObjectList<Asteroid> asteroids    = new SpaceObjectList<Asteroid>();
      // The Enemies
        private final SpaceObjectList<Enemy> enemies      = new SpaceObjectList<Enemy>();
        private final SpaceObjectList<ChaseEnemy> chaseEnemies = new SpaceObjectList<ChaseEnemy>();
      // A list that holds temporary Asteroid object that may form smaller ones
        private final SpaceObjectList<Asteroid> initSmall    = new SpaceObjectList<Asteroid>();
      // A list that holds temporary NuclearMissiles that will Explode
        private final SpaceObjectList<NuclearMissile> nukeBlast    = new SpaceObjectList<NuclearMissile>();
      // Your allies
        private final SpaceObjectList<Ally> allies       = new SpaceObjectList<Ally>(maxAllies);
      // The nuclear missiles
        private final SpaceObjectList<NuclearMissile> nukMissiles  = new SpaceObjectList<NuclearMissile>();
      // A list that holds the lines of explosions
        private final Explode         explosions;
      // The stars that are drawn if the image is not found or at the user's request
        private final Star[]          stars        = new Star[numOfStars];
      // Bullets fired by the enemies
        private final SpaceObjectList<EnemyBullet> enemyBlts    = new SpaceObjectList<EnemyBullet>();
      // Missiles fired by the enemies
        private final SpaceObjectList<EnemyMissile> enemyMsls    = new SpaceObjectList<EnemyMissile>();
      // A list of anything you need, just clear it when you are done.
        private final SpaceObjectList<SpaceObject> tempList     = new SpaceObjectList<SpaceObject>();
      private final Inactive inactive = new Inactive();
    
    // Various non-final, non-changing objects
    private Ship ship;
    private JFrame frame;
    private CanvasMax canvas;
    private SoundClip nuke_Fire, die, missile_Array, fire_Missile, nuke_Explode, fire_Bullet, fire_Guided_Missile, powerup_Bullet, rock_Explode, ship_Explode;//, lay_Mine, enemy_Projectile_Explode;
    
    // Game variables
    private String name;
    private Thread runThread;
    private boolean countLoops, shipDie = false, waiting = false, revamp = false;//, shieldOn = false;
    private int loopCount, clearLoopCount = clearFrameCountdown, enemyLoopCount = framesBetweenEnemyBirth, chaseEnemyLoopCount = framesBetweenChaseEnemyBirth, rocksLeft, shipBirthLoopCount;
    private long bTime, mTime, gmTime, ssTime, puMTime, puGmTime, nukTime, mineTime, puMineTime;//, tsTime, puTsTime;
    private int moreGMiss = moreGmissilePoints, moreMiss = moreMissilePoints, moreSshot = moreSshotPoints, anotherAllyPoints = allyAwardPoints, moreNukPoints = moreNukMissilePoints, moreGhosts = moreGhostPoints, moreLives = moreLivesPoints, moreMines = moreMinePoints;
    private int nMissilesCount = startingMiss, gMissilesCount = startingGmiss, sShotCount = startingSshot, nukCount = startingNukMiss, points = 0, mineCount = startingMines;
    private int lives = startingLives, ghosts = startingGhosts, highScore, missileSide = 1, shipIndex = 0, numOfShips = -1;
    private Flame flame;
    public static boolean standAlone = false;
    
    //--------------------------------\
    // Space - bullet         0       |
    // B - missile            1       |
    // V - guided missile     2       |
    // C - powerup bullet     9       | (cost is 100% powerup bullets; all or nothing)
    // G - powerup missile    10      | (cost is 90 missiles; all or nothing)
    // F - powerup gMissile   11      | (cost is 90 gMissiles; all or nothing)
    // W - powerup Mine       21      | (cost is 150 mines; all or nothing)
    // Enter - Fire Nuke      15      |
    // D - lay mine           18      |
    //--------------------------------|
    // Left - ccw turn        3       |
    // Right - cw turn        4       |
    //--------------------------------|
    // Up - forward           5       |
    // Down - reverse         6       |
    //--------------------------------|
    // P - play/pause (run)   7       |
    //--------------------------------|
    // N - ghost              8       |
    // M - Turn on Shield     19      |
    //--------------------------------|
    // X - complete stop              |
    //--------------------------------|
    // Z - more asteroids     12      |
    // A - create enemy       13      |
    // Q - create chase enemy 16      |
    // F1 - clear inactive    14      |
    //--------------------------------|
    // F2 - toggle fill               |
    // F3 - toggle draw               |
    // F4 - toggle stars              |
    // F5 - toggle explosions         |
    // F6 - toggle sound              |
    // F7 - toggle max explosions     |
    // F8 - clear explosions  17      |
    //--------------------------------|
    // Update ship            20      |
    // Pgup - cycle ship up           |
    // Pgdn - cycle ship down         |
    //--------------------------------/
    
    private final boolean[] keysDown = new boolean[22];
    
    public synchronized static void main(String[] a){
        standAlone = true;
        Asteroids game = new Asteroids();
        game.show();
    }
    
    public Asteroids(){
        this(System.getProperty("user.name", "Player"));
    }
    
    public Asteroids(String name){
        for (int i=0; i<keysDown.length; i++)
            keysDown[i] = false;
        this.name = StaticTools.capEveryWord(name);
        initThread();
        makeFrame();
        handleFile();
        flame = new Flame(canvas);
        initShip();
        if (startWithAsteroids)
            initAsteroids();
        addListener();
        printStats();
        canvas.repaint();
        explosions = new Explode(canvas);
        if (!cheatMode)
            cheatModeUnlimited = false;
    }
    
    public void destroy(){
        System.exit(0);
    }
    
    private void handleFile(){
        File dir = new File(filePath);
        if (useStars){
            if (bgImage){
                try{
                    File[] starFiles = dir.listFiles(new MyFileFilter(bgExtension));
                    canvas.setBgImage(starFiles[0]);
                    canvas.drawBG();
                    bgImage = canvas.haveBGimage();
                    if (!bgImage)
                        throw new Throwable("Image not acceced correctly");
                }
                catch (Throwable t){
                    bgImage = false;
                    initStars();
                    canvas.shape(stars, Star.starColor, false, true);
                }
            }
            else{
                initStars();
                canvas.shape(stars, Star.starColor, false, true);
            }
        }
        
        if (useSound){
            File[] soundFiles = dir.listFiles(new MyFileFilter(new String[]{"wav", "au"}));
            for (int i=0; i<soundFiles.length; i++){
                String name = soundFiles[i].getName();
                
                SoundClip sound = new SoundClip(soundFiles[i].getPath()); //Applet.newAudioClip(soundFiles[i].toURI().toURL());
                
                if (name.equals("nuke fire.wav")){
                    nuke_Fire = sound;
                }
                else if (name.equals("die.wav")){
                    die = sound;
                }
                else if (name.equals("missile array.wav")){
                    missile_Array = sound;
                }
                else if (name.equals("fire missile.wav")){
                    fire_Missile = sound;
                }
                else if (name.equals("nuke explode.wav")){
                    nuke_Explode = sound;
                }
                else if (name.equals("fire bullet.wav")){
                    fire_Bullet = sound;
                }
                else if (name.equals("fire guided missile.wav")){
                    fire_Guided_Missile = sound;
                }
                else if (name.equals("powerup bullet.wav")){
                    powerup_Bullet = sound;
                }
                else if (name.equals("rock explode.au")){
                    rock_Explode = sound;
                }
                else if (name.equals("ship explode.au")){
                    ship_Explode = sound;
                }
            }
        }
    }
    
    private void addListener(){
        frame.addWindowListener(new WL(standAlone, this));
    }
    
    public void setRun(boolean r){
        keysDown[7] = r;
    }
    
    public void show(){
        frame.setVisible(true);
    }
    
    private void initShip(){
        ship = new Ship(canvas, xMax/2, yMax/2);
        ship.setFlame(flame);
        ship.setActive(true);
        ship.render();
        ship.draw();
        
        SpaceObjectIterator<ChaseEnemy> cen = chaseEnemies.iterator();
        while(cen.hasNext()){
            ChaseEnemy ce = cen.next();
            if (ce.active())
                ce.setTarget(ship);
        }
    }
    
    private void initAsteroids(){
        for (int i=0; i<numOfAsteroids; i++){
            double[] place = placeAlongEdge();
            Asteroid a;
            a = (Asteroid) inactive.asteroids.getInactiveObject();
            if (a == null)
                a = new Asteroid(canvas, place[0], place[1]);
            else{
                a.getBaseXs();
                a.revamp2();
                a.reHomeShape();
                a.setLocation(place[0], place[1]);
            }
            rocksLeft++;
            a.setActive(true);
            asteroids.add(a);
            a.render();
            a.draw();
        }
    }
    
    private void initStars(){
        for (int i=0; i<numOfStars; i++){
            int x = StaticTools.rnd.nextInt(xMax);
            int y = StaticTools.rnd.nextInt(yMax);
            stars[i] = new Star(x, y);
        }
    }
    
    private double[] placeAlongEdge(){
        double x = 0, y = 0;
        if (StaticTools.rnd.nextBoolean()){ // along top or bottom
            boolean top = StaticTools.rnd.nextBoolean();
            y = top?5:yMax-5;
            x = StaticTools.rnd.nextInt(xMax-10) + 5;
        }
        else{
            boolean left = StaticTools.rnd.nextBoolean();
            x = left?5:xMax-5;
            y = StaticTools.rnd.nextInt(yMax-10) + 5;
        }
        return new double[] {x, y};
    }
    
    private void createEnemy(){
        double[] place = placeAlongEdge();
        double[][] shape = EnemyDatabase.randomEnemyShape();
        Enemy e;
        e = (Enemy) inactive.enemies.getInactiveObject();
        if (e == null)
            e = new Enemy(canvas, place[0], place[1], shape);
        else
            e.init(place[0], place[1], shape);
        e.setActive(true);
        e.render();
        enemies.add(e);
    }
    
    private void createChaseEnemy(){
        double[] place = placeAlongEdge();
        double[][] shape = EnemyDatabase.randomEnemyShape();
        ChaseEnemy ce;
        ce = (ChaseEnemy) inactive.chaseEnemies.getInactiveObject();
        if (ce == null)
            ce = new ChaseEnemy(canvas, place[0], place[1], shape, ship);
        else{
            ce.init(place[0], place[1], shape);
            ce.setTarget(ship);
        }
        ce.setActive(true);
        ce.render();
        chaseEnemies.add(ce);
    }
    
    private void createAlly(){
        double x = StaticTools.rnd.nextInt(xMax);
        double y = StaticTools.rnd.nextInt(yMax);
        Ally a = new Ally(canvas, x, y);
        a.setActive(true);
        allies.replaceInactive(a);
    }
            
    public JPanel getPlayPane(){
        return canvas.getCanvas();
    }
    
    public void keyPressed(KeyEvent ke){
        eval(KeyEvent.getKeyText(ke.getKeyCode()), true);
    }
    
    public void keyReleased(KeyEvent ke){
        int keyCode = ke.getKeyCode();
        if (keyCode == KeyEvent.VK_F1){
            keysDown[14] = true;
            return;
        }
        else if (keyCode == KeyEvent.VK_F2){
            SpaceObject.fill = !SpaceObject.fill;
            if (!SpaceObject.fill && !SpaceObject.draw)
                SpaceObject.draw = true;
            return;
        }
        else if (keyCode == KeyEvent.VK_F3){
            SpaceObject.draw = !SpaceObject.draw;
            if (!SpaceObject.fill && !SpaceObject.draw)
                SpaceObject.fill = true;
            return;
        }
        else if (keyCode == KeyEvent.VK_F4){
            useStars = !useStars;
            return;
        }
        else if (keyCode == KeyEvent.VK_F5){
            useExplosions = !useExplosions;
            if (!useExplosions);
                keysDown[17] = true;
            return;
        }
        else if (keyCode == KeyEvent.VK_F6){
            useSound = !useSound;
            return;
        }
        else if (keyCode == KeyEvent.VK_F7){
            Explode.useCapacityLimit = !Explode.useCapacityLimit;
            if (Explode.useCapacityLimit)
                keysDown[17] = true;
            return;
        }
        else if (keyCode == KeyEvent.VK_F8){
            keysDown[17] = true;
            return;
        }
        else if (keyCode == KeyEvent.VK_ESCAPE){
            keysDown[7] = false;
            System.exit(0);
            return;
        }
        else if (keyCode == KeyEvent.VK_PAGE_DOWN){
            if (numOfShips == -1)
                numOfShips = EnemyDatabase.length();
            shipIndex--;
            if (shipIndex < 0)
                shipIndex = numOfShips-1;
            keysDown[20] = true;
            return;
        }
        else if (keyCode == KeyEvent.VK_PAGE_UP){
            if (numOfShips == -1)
                numOfShips = EnemyDatabase.length();
            shipIndex++;
            if (shipIndex >= numOfShips)
                shipIndex = 0;
            keysDown[20] = true;
            return;
        }
        eval(KeyEvent.getKeyText(ke.getKeyCode()), false);
    }
    
    private void eval(String key, boolean down){
        key = key.toLowerCase();
        if (key.equals("space")){
            keysDown[0] = down;
            if (down)
                keysDown[18] = keysDown[15] = keysDown[10] = keysDown[11] = keysDown[9] = keysDown[1] = keysDown[21] = keysDown[2] = false;
        }
        else if (key.equals("b")){
            keysDown[1] = down;
            if (down)
                keysDown[18] = keysDown[15] = keysDown[10] = keysDown[11] = keysDown[9] = keysDown[0] = keysDown[21] = keysDown[2] = false;
        }
        else if (key.equals("v")){
            keysDown[2] = down;
            if (down)
                keysDown[18] = keysDown[15] = keysDown[10] = keysDown[11] = keysDown[9] = keysDown[0] = keysDown[21] = keysDown[1] = false;
        }
        else if (key.equals("c")){
            keysDown[9] = down;
            if (down)
                keysDown[18] = keysDown[15] = keysDown[10] = keysDown[11] = keysDown[0] = keysDown[1] = keysDown[21] = keysDown[2] = false;
        }
        else if (key.equals("g")){
            keysDown[10] = down;
            if (down)
                keysDown[18] = keysDown[15] = keysDown[9] = keysDown[11] = keysDown[0] = keysDown[1] = keysDown[21] = keysDown[2] = false;
        }
        else if (key.equals("f")){
            keysDown[11] = down;
            if (down)
                keysDown[18] = keysDown[15] = keysDown[9] = keysDown[10] = keysDown[0] = keysDown[1] = keysDown[21] = keysDown[2] = false;
        }
        else if (key.equals("enter")){
            keysDown[15] = down;
            if (down)
                keysDown[18] = keysDown[11] = keysDown[9] = keysDown[10] = keysDown[0] = keysDown[1] = keysDown[21] =  keysDown[2] = false;
        }
        else if (key.equals("d")){
            keysDown[18] = down;
            if (down)
                keysDown[15] = keysDown[11] = keysDown[9] = keysDown[10] = keysDown[0] = keysDown[1] = keysDown[21] = keysDown[2] = false;
        }
        else if (key.equals("w")){
            keysDown[21] = down;
            if (down)
                keysDown[15] = keysDown[11] = keysDown[9] = keysDown[10] = keysDown[0] = keysDown[1] = keysDown[2] = keysDown[18] = false;
        }
        
        else if (key.equals("left")){
            keysDown[3] = down;
            if (down)
                keysDown[4] = false;
        }
        else if (key.equals("right")){
            if (down)
                keysDown[3] = false;
            keysDown[4] = down;
        }
        
        else if (key.equals("up")){
            keysDown[5] = down;
            if (ship != null)
                ship.setHolding(down);
            if (down){
                keysDown[6] = false;
                flame.forward();
            }
        }
        else if (key.equals("down")){
            if (down){
                keysDown[5] = false;
                flame.reverse();
            }
            if (ship != null)
                ship.setHolding(down);
            keysDown[6] = down;
        }
        
        else if (key.equals("p")){
            if (!down){
                keysDown[7] = !keysDown[7];
                if (keysDown[7])
                    runThread.start();
            }
        }
        
        else if (key.equals("n")){
            if (!down)
                keysDown[8] = true;
        }
        
        else if (key.equals("x")){
            if (down && ship != null)
                ship.stop();
        }
        
        else if (key.equals("z")){
            if (down)
                keysDown[12] = down;
        }
        
        else if (key.equals("a")){
            if (down)
                keysDown[13] = down;
        }
        
        else if (key.equals("q")){
            if (down)
                keysDown[16] = down;
        }
        
        else if (key.equals("s")){
            if (waiting)
                revamp = true;
        }
    }
    
    private void initThread(){
        runThread = new Thread(this);
    }
    
    private void makeFrame(){
        if (fullScreen)
            frame = new BFrame();
        else
            frame = new JFrame("Asteroids: " + name);
        frame.addKeyListener(this);
        
        canvas = new CanvasMax(frame, xMax, yMax, Color.black, !fullScreen, null);
        //JPanel cp = canvas.getCanvas();
        //cp.addKeyListener(this);
        canvas.getCanvas().addKeyListener(this);
    }
    
    public void run(){
        canvas.setFont(statsFont);
        keysDown[7] = true;
        keysDown[8] = false;
        long startTime = System.currentTimeMillis();
        while (keysDown[7]){
            //long time = System.currentTimeMillis();
            
            if (revamp)
                revamp();
            
            activatePressed();
            moveAll();
            performIFstatements();
            
            try{
                startTime += maxWait;
                Thread.sleep(Math.max(0, startTime - System.currentTimeMillis()));
            }
            catch (InterruptedException e){
                System.out.println("Ah, ha! Caught ya interrupin' ma sleep with them");
                System.out.println("dad gum method calls n what not");
            }
        }
        initThread();
    }
    
    private void activatePressed(){
        if (ship != null){
            if (keysDown[0])
                fireBullet();
            else if (keysDown[1])
                fireMissile();
            else if (keysDown[2])
                fireGmissile();
            else if (keysDown[9])
                firePowerupBullet();
            else if (keysDown[10])
                firePowerupMissile();
            else if (keysDown[11])
                firePowerupGmissile();
            else if (keysDown[15])
                fireNukMissile();
            else if (keysDown[18])
                layMine();
            else if (keysDown[21])
                layPowerupMine();
            
            if (keysDown[4])
                ship.rotate(-1);
            else if (keysDown[3])
                ship.rotate(1);
                
            if (keysDown[5])
                ship.accel();
            else if (keysDown[6])
                ship.decel();
                
            if (keysDown[8]){
                if (ghosts > 0){
                    ghosts--;
                    ship.activateGhost();
                }
                else if (cheatMode)
                    ship.activateGhost();
                keysDown[8] = false;
            }
        }
        
        if (cheatMode){
            if (keysDown[12]){
                initAsteroids();
                keysDown[12] = false;
            }
            
            
            if (keysDown[13]){
                createEnemy(); 
                keysDown[13] = false;
            }
            
            if (keysDown[16]){
                createChaseEnemy();
                keysDown[16] = false;
            }
        }
        
        if (keysDown[14]){
            clearInactiveLists();
            keysDown[14] = false;
        }
        
        if (keysDown[17]){
            explosions.clear();
            keysDown[17] = false;
        }
        
        if (keysDown[20]){
            if (ship == null)
                return;
            double[][] shape = EnemyDatabase.get(shipIndex);
            if (shape == null){
                System.out.println("Shape is null");
                return;
            }
            ship.setShape(shape);
            keysDown[20] = false;
        }
    }
    
    private void moveAll(){
        if (useStars){
            if (bgImage)
                canvas.drawBG();
            else{
                canvas.erase();
                canvas.shape(stars, Star.starColor, false, true);
            }
        }
        else
            canvas.erase();
            
        explosions.moveAll();    
        moveAsteroids();
        moveEnemies();
        moveChaseEnemies();
        moveEnemyBullets();
        moveEnemyMissiles();
        moveMines();
        moveBullets();
        moveMissiles();
        moveGmissiles();
        moveNukMissiles();
        moveAllies();
        moveShip();
        nukeBlast();
        if (superMan || !cheatMode)
            compareShip();
        initSmall();
        printStats();
        if (waiting)
            gameOver();
        canvas.repaint();
    }
    
    private void performIFstatements(){
        if (points > moreGMiss){
            moreGMiss += moreGmissilePoints;
            gMissilesCount += gMissAward;
            if (points > moreGMiss){
                int times = (int)((points - moreGMiss) / moreGmissilePoints+.0);
                moreGMiss += times*moreGmissilePoints;
                gMissilesCount += times*gMissAward;
            }
        }
        if (points > moreMiss){
            moreMiss += moreMissilePoints;
            nMissilesCount += missAward;
            if (points > moreMiss){
                int times = (int)((points - moreMiss) / moreMissilePoints+.0);
                moreMiss += times*moreMissilePoints;
                nMissilesCount += times*missAward;
            }
        }
        if (points > moreSshot){
            moreSshot += moreSshotPoints;
            sShotCount += sShotAward;
            if (points > moreSshot){
                int times = (int)((points - moreSshot) / moreSshotPoints+.0);
                moreSshot += times*moreSshotPoints;
                sShotCount += times*sShotAward;
            }
        }
        if (points > moreNukPoints){
            moreNukPoints += moreNukMissilePoints;
            nukCount += nukMissAward;
            if (points > moreNukPoints){
                int times = (int)((points - moreNukPoints) / moreNukMissilePoints+.0);
                moreNukPoints += times*moreNukMissilePoints;
                nukCount += times*nukMissAward;
            }
        }
        if (points > moreLives){
            moreLives += moreLivesPoints;
            lives += lifeAward;
            if (points > moreLives){
                int difference = points - moreLives;
                int times = (int)(difference / moreLivesPoints+.0);
                moreLives += times*moreLivesPoints;
                lives += times*lifeAward;
            }
        }
        if (points > moreGhosts){
            moreGhosts += moreGhostPoints;
            ghosts += ghostAward;
            if (points > moreGhosts){
                int times = (int)((points - moreGhosts) / moreGhostPoints+.0);
                moreGhosts += times*moreGhostPoints;
                ghosts += times*ghostAward;
            }
        }
        if (points > moreMines){
            moreMines += moreMinePoints;
            mineCount += mineAward;
            if (points > moreMines){
                int times = (int)((points - moreMines) / moreMinePoints+.0);
                moreMines += times*moreMinePoints;
                mineCount += times*mineAward;
            }
        }
        if (points > highScore){
            highScore = points;
        }
        
        if (autoInitAsteroids){
            if (countLoops && loopCount >= framesBetweenAsteroidRebirth){
                loopCount = 0;
                countLoops = false;
                if (getHarder && numOfAsteroids < maxAsteroids && Math.random() <= getHarderChance)
                    numOfAsteroids = Math.min(numOfAsteroids+numAsteroidsAdded, maxAsteroids);
                initAsteroids();
            }
            if (countLoops)
                loopCount++;
            if (rocksLeft <= 0)
                countLoops = true;
            else{
                rocksLeft = asteroids.activeSize();
                countLoops = false;
                loopCount = 0;
            }
        }
        
        if (clearInactive){
            clearLoopCount--;
            if (clearLoopCount <= 0){
                clearLoopCount = clearFrameCountdown;
                clearInactiveLists();
            }
        }
        
        if (useEnemies){
            enemyLoopCount--;
            if (enemyLoopCount <= 0){
                enemyLoopCount = framesBetweenEnemyBirth;
                if (enemies.activeSize() < maxEnemies)
                    createEnemy();
            }
        }
        if (useChaseEnemies){
            chaseEnemyLoopCount--;
            if (chaseEnemyLoopCount <= 0){
                chaseEnemyLoopCount = framesBetweenChaseEnemyBirth;
                if (chaseEnemies.activeSize() < maxChaseEnemies)
                    createChaseEnemy();
            }
        }
        
        
        if (useAllies && points > anotherAllyPoints){
            int allyCount = allies.size();
            if (allyCount < maxAllies){
                anotherAllyPoints += allyAwardPoints;
                allyAwardPoints += allyAwardPoints / 10;
                for (int i=0; i<allyAward && allyCount < maxAllies; i++){
                    createAlly();
                    allyCount++;
                }
            }
        }
        
        if (shipDie){
            shipBirthLoopCount--;
            if (shipBirthLoopCount <= 0){
                if (lives > 0){
                    points = Math.max(points-diePenalty, 0);
                    initShip();
                    lives--;
                    ship.startupGhost();
                    shipDie = false;
                }
                else{
                    waiting = true;
                    shipDie = false;
                }
            }
        }
    }
    
    private void printStats(){
        String halfSpace = "   ", space = halfSpace + halfSpace;
        String highS = "High Score: " + highScore;
        
        String[] name_mode = new String[2];
        name_mode[0] = name + ": " + points;
        name_mode[1] = "" + halfSpace;
        if (cheatMode){
            name_mode[1] = "(Cheat mode";
            if (cheatModeUnlimited)
                name_mode[1] += " + unlmtd. ammo";
            if (superMan)
                name_mode[1] += " & superman mode";
            name_mode[1] += ")";
        }
        
        String[] shipInfo = new String[4];
        shipInfo[0] = "Lives: " + lives;
        shipInfo[1] = "Ghosts Left: " + ghosts;
        if (ship != null && ship.ghost())
            shipInfo[1] += space + "Ghost Countdown: " + ship.getGhostCountdown();
        double speed = 0;
        int[] pos = new int[2];
        double angle = 0;
        
        if (ship != null){
            speed = round(ship.getSpeed(), 2);
            double[] pos2 = ship.getPosition();
            pos[0] = (int)Math.round(pos2[0]);
            pos[1] = (int)Math.round(pos2[1]);
            angle = round(ship.getAngle(), 2);
        }
        
        shipInfo[2] = "Ship speed:" + speed + space + "Angle:" + angle;
        shipInfo[3] = "Ship position (" + pos[0] + ", " + pos[1] + ")";
        
        String[] ammoInfo = new String[5];
        ammoInfo[0] = "Missiles left: " + nMissilesCount;
        ammoInfo[1] = "Guided missiles left: " + gMissilesCount;
        ammoInfo[2] = "Powerup Bullets " + (sShotCount*20) + "% Charged";
        ammoInfo[3] = "Nukes Left:" + nukCount;
        ammoInfo[4] = "Mines Left:" + mineCount;
        
        printText(new String[] {highS}, topRight, statsFont, statusTextColor);
        printText(name_mode, topLeft, statsFont, statusTextColor);
        printText(shipInfo, bottomLeft, statsFont, statusTextColor);
        printText(ammoInfo, bottomRight, statsFont, statusTextColor);
        
//         canvas.setFont(statsFont);
//         canvas.drawString(pointsString, Color.white, 20, 20);
//         canvas.drawString(playerInfo, Color.white, 20, yMax-60);
//         canvas.drawString(shot, Color.white, 20, yMax-40);
//         canvas.drawString(shot2, Color.white, 20, yMax-20);
//         canvas.drawString(highS, Color.white, xMax-200, 20);
    }
    
    private double round(double x, int decimalPlaces){
        return Math.round(x*(10*decimalPlaces))/(10*decimalPlaces);
    }
    
    private void printText(String[] text, int position, BFont f, Color c){
        if (text.length == 0)
            return;
        for (int i=0; i<text.length; i++)
            if (text[i] == null)
                text[i] = "";
        //topRight, topLeft, center, bottomRight, bottomLeft, lineSpacing, edgeCusion
        //Rectangle2D r2d =  fm.getStringBounds(String str, Graphics context)
        Rectangle2D r2d;
        Graphics g2d = canvas.getG2D();
        FontMetrics fm = f.getFontMetrics();
        int[] xyPos = new int[2];
        if (position == topLeft){
            xyPos[0] = edgeCusion;
            xyPos[1] = edgeCusion;
        }
        else if (position == topRight){
            int maxWidth = (int)Math.round(fm.getStringBounds(text[0], g2d).getWidth());
            for (int i=1; i<text.length; i++){
                int width = (int)Math.round(fm.getStringBounds(text[i], g2d).getWidth());
                if (width > maxWidth)
                    maxWidth = width;
            }
            maxWidth += edgeCusion;
            xyPos[0] = xMax - maxWidth;
            xyPos[1] = edgeCusion;
        }
        else if (position == bottomLeft){
            int height = (int)Math.round(fm.getStringBounds(text[0], g2d).getHeight());
            for (int i=1; i<text.length; i++)
                height += lineSpacing + (int)Math.round(fm.getStringBounds(text[i], g2d).getHeight());
            height += edgeCusion;
            xyPos[0] = edgeCusion;
            xyPos[1] = yMax - height;
        }
        else if (position == bottomRight){
            r2d =  fm.getStringBounds(text[0], g2d);
            int maxWidth = (int)Math.round(r2d.getWidth());
            int height = (int)Math.round(r2d.getHeight());
            for (int i=1; i<text.length; i++){
                r2d = fm.getStringBounds(text[i], g2d);
                height += lineSpacing + (int)Math.round(r2d.getHeight());
                int width = (int)Math.round(r2d.getWidth());
                if (width > maxWidth)
                    maxWidth = width;
            }
            maxWidth += edgeCusion;
            height += edgeCusion;
            xyPos[0] = xMax - maxWidth;
            xyPos[1] = yMax - height;
        }
        else if (position == center){
            int height = (int)Math.round(fm.getStringBounds(text[0], g2d).getHeight());
            for (int i=1; i<text.length; i++)
                height += lineSpacing + (int)Math.round(fm.getStringBounds(text[i], g2d).getHeight());
            xyPos[1] = (yMax/2) - (height/2);
            canvas.setFont(f);
            for (int i=0; i<text.length; i++){
                r2d = fm.getStringBounds(text[i], g2d);
                xyPos[0] = (int)Math.round((xMax/2) - (r2d.getWidth()/2));
                canvas.drawString(text[i], c, xyPos[0], xyPos[1]);
                xyPos[1] += lineSpacing + (int)Math.round(r2d.getHeight());
            }
            return;
        }
        canvas.setFont(f);
        for (int i=0; i<text.length; i++){
            canvas.drawString(text[i], c, xyPos[0], xyPos[1]);
            xyPos[1] += lineSpacing + (int)Math.round(fm.getStringBounds(text[i], g2d).getHeight());
        }
    }
    
    private SpaceObject findTarget(){
        tempList.clear();
        tempList.add(enemies.randomSpaceObject());
        tempList.add(chaseEnemies.randomSpaceObject());
        tempList.add(asteroids.randomSpaceObject());
        tempList.add(enemyMsls.randomSpaceObject());
        tempList.add(enemyBlts.randomSpaceObject());
        SpaceObject target = tempList.randomSpaceObject();
        tempList.clear();
        return target;
    }
    
    private void initSmall(){
        SpaceObjectIterator<Asteroid> it = initSmall.iterator();
        boolean clear = false;
        SpaceObjectList<Asteroid> tempA = new SpaceObjectList<Asteroid>();
        while (it.hasNext()){
            Asteroid a = it.next();
            initSmall(a, tempA);
            if (useExplosions)
                explosions.explode(a);
            clear = true;
        }
        asteroids.add(tempA);
        if (clear){
            initSmall.clear();
            if (useSound)
                rock_Explode.play();
        }
    }
    
    private void initSmall(Asteroid a, SpaceObjectList<Asteroid> list){
        Asteroid[] as = a.explode();
        rocksLeft += as.length;
        list.add(as);
    }
    
    private void nukeBlast(){
        ArrayList<Asteroid> iAsteroids = new ArrayList<Asteroid>();
        ArrayList<Enemy> iEnemies = new ArrayList<Enemy>();
        ArrayList<ChaseEnemy> iCEnemies = new ArrayList<ChaseEnemy>();
        ArrayList<EnemyBullet> ieBullets = new ArrayList<EnemyBullet>();
        ArrayList<EnemyMissile> ieMissiles = new ArrayList<EnemyMissile>();
        ArrayList<NuclearMissile> iNukes = new ArrayList<NuclearMissile>();
        SpaceObjectIterator<NuclearMissile> nml = nukeBlast.iterator();
        boolean clear = false;
        double rockAnihilationProbability = 1;
        SpaceObjectList<Asteroid> tempA = new SpaceObjectList<Asteroid>();
        int nukeBlastReach = NuclearMissile.reach;
        while(nml.hasNext()){
            NuclearMissile nm = nml.next();
            double[] p1 = nm.getPosition();
            clear = true;
            
            SpaceObjectIterator<Asteroid> as = asteroids.iterator();
            while(as.hasNext()){
                Asteroid a = as.next();
                if (a.active() && StaticTools.getDistance(p1, a.getPosition()) <= nukeBlastReach){
                    if (StaticTools.rnd.nextDouble() >= rockAnihilationProbability)
                        initSmall(a, tempA);
                    else
                        a.setActive(false);
                    iAsteroids.add(a);
                    if (useExplosions)
                        explosions.explode(a);
                    addPoints(a.getGen()*pointsPerGen);
                    rocksLeft--;
                }
            }
            
            SpaceObjectIterator<Enemy> en = enemies.iterator();
            while(en.hasNext()){
                Enemy e = en.next();
                if (e.active() && StaticTools.getDistance(p1, e.getPosition()) <= nukeBlastReach){
                    e.setActive(false);
                    if (useExplosions)
                        explosions.explode(e);
                    iEnemies.add(e);
                    addPoints(enemyPoints);
                }
            }
            
            SpaceObjectIterator<ChaseEnemy> cen = chaseEnemies.iterator();
            while(cen.hasNext()){
                ChaseEnemy ce = cen.next();
                if (ce.active() && StaticTools.getDistance(p1, ce.getPosition()) <= nukeBlastReach){
                    ce.setActive(false);
                    if (useExplosions)
                        explosions.explode(ce);
                    iCEnemies.add(ce);
                    addPoints(chaseEnemyPoints);
                }
            }
        
            SpaceObjectIterator<EnemyBullet> ebs = enemyBlts.iterator();
            while (ebs.hasNext()){
                EnemyBullet eb = ebs.next();
                if (eb.active() && StaticTools.getDistance(p1, eb.getPosition()) <= nukeBlastReach){
                    eb.setActive(false);
                    ieBullets.add(eb);
                    addPoints(enemyBulletPoints);
                }
            }
            
            SpaceObjectIterator<EnemyMissile> ems = enemyMsls.iterator();
            while (ems.hasNext()){
                EnemyMissile em = ems.next();
                if (em.active() && StaticTools.getDistance(p1, em.getPosition()) <= nukeBlastReach){
                    em.setActive(false);
                    ieMissiles.add(em);
                    addPoints(enemyMissilePoints);
                }
            }
            iNukes.add(nm);
        }
        
        asteroids.add(tempA);
        if (clear){
            nukeBlast.clear();
            if (useSound)
                nuke_Explode.play();
        }
        inactive.nukMissiles.add(nukMissiles.remove(iNukes));
        inactive.asteroids.add(asteroids.remove(iAsteroids));
        inactive.enemies.add(enemies.remove(iEnemies));
        inactive.chaseEnemies.add(chaseEnemies.remove(iCEnemies));
        enemyBlts.remove(ieBullets);
        enemyMsls.remove(ieMissiles);
    }
    
    public boolean haveActiveTargets(){
        return asteroids.haveActiveObjects() || enemies.haveActiveObjects() || chaseEnemies.haveActiveObjects() || enemyBlts.haveActiveObjects() || enemyMsls.haveActiveObjects();
    }
    
    private void clearInactiveLists(){
        inactive.missiles.clearInactive();
        inactive.gMissiles.clearInactive();
        inactive.bullets.clearInactive();
        inactive.asteroids.clearInactive();
        inactive.enemies.clearInactive();
        inactive.chaseEnemies.clearInactive();
        inactive.mines.clearInactive();
        explosions.clearInactive();
    }
    
    private void emptyLists(){
        missiles.clear();
        gMissiles.clear();
        bullets.clear();
        asteroids.clear();
        enemies.clear();
        chaseEnemies.clear();
        enemyBlts.clear();
        enemyMsls.clear();
        explosions.clear();
        mines.clear();
        clearInactiveLists();
    }
    
    private void compareShip(){
        if (ship == null || ship.ghost())
            return;
        double[] loc = ship.getPosition();
        double maxRadiusB = Math.max(Bullet.maxRadius, Ship.maxRadius);
        double maxRadiusM = Math.max(Missile.maxRadius, Ship.maxRadius);
        double maxRadiusA = Math.max(Asteroid.maxRadius, Ship.maxRadius);
        double maxRadiusE = Math.max(EnemyDatabase.maxRadius, Ship.maxRadius);
        
        SpaceObjectIterator<Enemy> enms = enemies.iterator();
        while (enms.hasNext()){
            Enemy enmy = enms.next();
            if (enmy.active() && StaticTools.getDistance(loc, enmy.getPosition()) <= maxRadiusE && ship.makesContactWith(enmy)){
                enmy.setActive(false);
                if (useSound){
                    ship_Explode.play();
                    if (!cheatMode)
                        die.play();
                }
                if (useExplosions){
                    if (!cheatMode)
                        explosions.explode(ship);
                    explosions.explode(enmy);
                }
                if (!cheatMode){
                    shipDie = true;
                    ship.setActive(false);
                    shipBirthLoopCount = framesBetweenShipRebirth;
                    ship = null;
                }
                return;
            }
        }
        
        SpaceObjectIterator<ChaseEnemy> cenms = chaseEnemies.iterator();
        while (cenms.hasNext()){
            ChaseEnemy cenmy = cenms.next();
            if (cenmy.active() && StaticTools.getDistance(loc, cenmy.getPosition()) <= maxRadiusE && ship.makesContactWith(cenmy)){
                cenmy.setActive(false);
                if (useSound){
                    ship_Explode.play();
                    if (!cheatMode)
                        die.play();
                }
                if (useExplosions){
                    if (!cheatMode)
                        explosions.explode(ship);
                    explosions.explode(cenmy);
                }
                if (!cheatMode){
                    shipDie = true;
                    ship.setActive(false);
                    shipBirthLoopCount = framesBetweenShipRebirth;
                    ship = null;
                }
                return;
            }
        }
        
        SpaceObjectIterator<EnemyBullet> ebs = enemyBlts.iterator();
        while (ebs.hasNext()){
            EnemyBullet eb = ebs.next();
            if (eb.active() && StaticTools.getDistance(loc, eb.getPosition()) <= maxRadiusB && ship.makesContactWith(eb)){
                eb.setActive(false);
                if (useSound){
                    if (!cheatMode){
                        ship_Explode.play();
                        die.play();
                    }
                }
                if (!cheatMode){
                    shipDie = true;
                    ship.setActive(false);
                    shipBirthLoopCount = framesBetweenShipRebirth;
                    if (useExplosions)
                        explosions.explode(ship);
                    ship = null;
                }
                return;
            }
        }
        
        SpaceObjectIterator<EnemyMissile> ems = enemyMsls.iterator();
        while (ems.hasNext()){
            EnemyMissile em = ems.next();
            if (em.active() && StaticTools.getDistance(loc, em.getPosition()) <= maxRadiusM && ship.makesContactWith(em)){
                em.setActive(false);
                if (useSound){
                    if (!cheatMode){
                        ship_Explode.play();
                        die.play();
                    }
                }
                if (!cheatMode){
                    shipDie = true;
                    ship.setActive(false);
                    shipBirthLoopCount = framesBetweenShipRebirth;
                    if (useExplosions)
                        explosions.explode(ship);
                    ship = null;
                }
                return;
            }
        }
        
        SpaceObjectIterator<Asteroid> asts = asteroids.iterator();
        while (asts.hasNext()){
            Asteroid a = asts.next();
            if (a.active() && StaticTools.getDistance(loc, a.getPosition()) <= maxRadiusA && ship.makesContactWith(a)){

                a.setActive(false);
                if (useSound){
                    rock_Explode.play();
                    if (!cheatMode){
                        ship_Explode.play();
                        die.play();
                    }
                }

                initSmall.add(a);
                if (useExplosions){
                    if (!cheatMode)
                        explosions.explode(ship);
                    explosions.explode(a);
                }
                if (!cheatMode){
                    shipDie = true;
                    ship.setActive(false);
                    shipBirthLoopCount = framesBetweenShipRebirth;
                    ship = null;
                }
                return;
            }
        }
    }
    
    private void fireBullet(){
        long time = System.currentTimeMillis();
        if (time < bTime + bDelay)
            return;
        bTime = time;
        double[] loc = ship.getPosition();
        double angle = ship.getAngle();
        double xS = -Bullet.speed * Math.sin(Math.toRadians(angle));
        double yS = Bullet.speed * Math.cos(Math.toRadians(angle));
        
        Bullet b;
        b = (Bullet) inactive.bullets.getInactiveObject();
        if (b == null){
            b = new Bullet(canvas, loc[0], loc[1]);
            b.setSpeed(xS, yS);
            
        }
        else
            b.revamp(xS, yS, loc[0], loc[1], angle, true);
        bullets.add(b);
        b.setActive(true);
        b.setLife(bulletLife);
        if (useSound)
            fire_Bullet.play();
    }
    
    private void fireMissile(){
        if (!cheatModeUnlimited)
            if (nMissilesCount <= 0)
                return;
        long time = System.currentTimeMillis();
        if (time < mTime + mDelay)
            return;
        if (!cheatModeUnlimited)
            nMissilesCount--;
        mTime = time;
        double[] loc = ship.getPosition();
        double angle = ship.getAngle();
        double tA = angle - (150*missileSide);
        missileSide++;// *= -1;
        double dist = Ship.half_width*1.5;
        loc[0] = -dist * Math.sin(Math.toRadians(tA)) + loc[0];
        loc[1] = dist * Math.cos(Math.toRadians(tA)) + loc[1];
        double xS = -Missile.missileSpeed * Math.sin(Math.toRadians(angle));
        double yS = Missile.missileSpeed * Math.cos(Math.toRadians(angle));
        
        Missile m;
        m = (Missile) inactive.missiles.getInactiveObject();
        if (m == null){
            m = new Missile(canvas, loc[0], loc[1]);
            m.setSpeed(xS, yS);
            m.setAngle(angle, false, true);
        }
        else
            m.revamp(xS, yS, loc[0], loc[1], angle, true);
        missiles.add(m);
        m.setLife(missileLife);
        m.setActive(true);
        if (useSound)
            fire_Missile.play();
    }
    
    private void fireGmissile(){
        if (!cheatModeUnlimited)
            if (gMissilesCount <= 0)
                return;
        long time = System.currentTimeMillis();
        if (time < gmTime + gmDelay)
            return;
        if (!cheatModeUnlimited)
            gMissilesCount--;
        gmTime = time;
        double[] loc = ship.getPosition();
        double angle = ship.getAngle();
        double tA = angle - (150*missileSide);
        missileSide *= -1;
        double dist = Ship.half_width*1.5;
        loc[0] = -dist * Math.sin(Math.toRadians(tA)) + loc[0];
        loc[1] = dist * Math.cos(Math.toRadians(tA)) + loc[1];
        double xS = -Missile.missileSpeed * Math.sin(Math.toRadians(angle));
        double yS = Missile.missileSpeed * Math.cos(Math.toRadians(angle));
        
        GuidedMissile gm;
        gm = (GuidedMissile) inactive.gMissiles.getInactiveObject();
        if (gm == null){
            gm = new GuidedMissile(canvas, loc[0], loc[1], findTarget());

            gm.setSpeed(xS, yS);
            gm.setAngle(angle, false, true);
        }
        else{
            gm.revamp(xS, yS, loc[0], loc[1], angle, true);
            gm.setTarget(findTarget());
        }
        gMissiles.add(gm);
        gm.setLife(gMissileLife);
        gm.setActive(true);
        if (useSound)
            fire_Guided_Missile.play();
    }
    
    private void firePowerupBullet(){
        if (!cheatModeUnlimited)
            if (sShotCount < 5)
                return;
        long time = System.currentTimeMillis();
        if (time < ssTime + ssDelay)
            return;
        if (!cheatModeUnlimited)
            sShotCount-=5;
        ssTime = time;
        double angle = ship.getAngle();
        double[] loc = ship.getPosition();
        for (int i=0; i<100; i++, angle+=3.6){
            double xS = -Bullet.speed * Math.sin(Math.toRadians(angle));
            double yS = Bullet.speed * Math.cos(Math.toRadians(angle));
            
            Bullet b;
            b = inactive.bullets.getInactiveObject();
            if (b == null){
                b = new Bullet(canvas, loc[0], loc[1]);
                b.setSpeed(xS, yS);
                b.setAngle(angle, false, true);
                
            }
            else
                b.revamp(xS, yS, loc[0], loc[1], angle, true);
            bullets.add(b);
            b.setActive(true);
            b.setLife((int)Math.round(bulletLife*bulletPUmultiplyer));
        }
        if (useSound)
            powerup_Bullet.play();
    }
    
    private void firePowerupMissile(){
        if (!cheatModeUnlimited)
            if (nMissilesCount < 90)
                return;
        long time = System.currentTimeMillis();
        if (time < puMTime + pupMDelay)
            return;
        if (!cheatModeUnlimited)
            nMissilesCount-=90;
        puMTime = time;
        double[] loc = ship.getPosition();
        double angle = ship.getAngle()-45;
        for (int i=0; i<90; i++, angle++){
            double xS = -Missile.missileSpeed * Math.sin(Math.toRadians(angle));
            double yS = Missile.missileSpeed * Math.cos(Math.toRadians(angle));
            
            Missile m;
            m = (Missile) inactive.missiles.getInactiveObject();
            if (m == null){
                m = new Missile(canvas, loc[0], loc[1]);
                m.setSpeed(xS, yS);
                m.setAngle(angle, false, true);
                
            }
            else
                m.revamp(xS, yS, loc[0], loc[1], angle, true);
            missiles.add(m);
            m.setActive(true);
            m.setLife((int)Math.round(missileLife*missilePUmultiplyer));
        }
        if (useSound)
            missile_Array.play();
    }
    
    private void firePowerupGmissile(){
        if (!cheatModeUnlimited)
            if (gMissilesCount < 90)
                return;
        long time = System.currentTimeMillis();
        if (time < puGmTime + pupGmDelay)
            return;
        if (!cheatModeUnlimited)
            gMissilesCount-=90;
        puGmTime = time;
        double[] loc = ship.getPosition();
        double angle = ship.getAngle()-45;
        for (int i=0; i<90; i++, angle++){
            double xS = -Missile.missileSpeed * Math.sin(Math.toRadians(angle));
            double yS = Missile.missileSpeed * Math.cos(Math.toRadians(angle));
            
            GuidedMissile gm;
            gm = (GuidedMissile) inactive.gMissiles.getInactiveObject();
            if (gm == null){
                gm = new GuidedMissile(canvas, loc[0], loc[1], findTarget());
                gm.setSpeed(xS, yS);
                gm.setAngle(angle, false, true);
            }
            else{
                gm.revamp(xS, yS, loc[0], loc[1], angle, true);
                gm.setTarget(findTarget());
            }
            gMissiles.add(gm);
            gm.setActive(true);
            gm.setLife((int)Math.round(gMissileLife*gMissilePUmultiplyer));
        }
        if (useSound)
            fire_Guided_Missile.play();
    }
    
    private void fireNukMissile(){
        if (!cheatModeUnlimited)
            if (nukCount <= 0)
                return;
        long time = System.currentTimeMillis();
        if (time < nukTime + nukDelay)
            return;
        if (!cheatModeUnlimited)
            nukCount--;
        nukTime = time;
        double[] loc = ship.getPosition();
        double angle = ship.getAngle();
        double tA = angle - (150*missileSide);
        missileSide *= -1;
        double dist = Ship.half_width*1.5;
        loc[0] = -dist * Math.sin(Math.toRadians(tA)) + loc[0];
        loc[1] = dist * Math.cos(Math.toRadians(tA)) + loc[1];
        double xS = -Missile.missileSpeed * Math.sin(Math.toRadians(angle));
        double yS = Missile.missileSpeed * Math.cos(Math.toRadians(angle));
        
        NuclearMissile nm;
        nm = (NuclearMissile) inactive.nukMissiles.getInactiveObject();
        if (nm == null){
            nm = new NuclearMissile(canvas, loc[0], loc[1]);
            nm.setActive(true);
            nm.setSpeed(xS, yS);
            nm.setAngle(angle, false, true);
            
        }
        else
            nm.revamp(xS, yS, loc[0], loc[1], angle, true);
        nukMissiles.add(nm);
        nm.setLife(nukMissileLife);
        if (useSound)
            nuke_Fire.play();
    }
    
    private void layMine(){
        if (!cheatModeUnlimited)
            if (mineCount <= 0)
                return;
        long time = System.currentTimeMillis();
        if (time < mineTime + mineDelay)
            return;
        if (!cheatModeUnlimited)
            mineCount--;
        mineTime = time;
        
        double angle = ship.getAngle()+180;
        double[] loc = ship.getPosition();
        double[] loc2 = new double[2];
        double distance = Ship.maxRadius+10;
        loc2[0] = -distance * Math.sin(Math.toRadians(angle)) + loc[0];
        loc2[1] = distance * Math.cos(Math.toRadians(angle)) + loc[1];
        
        Mine m;
        m = (Mine) inactive.mines.getInactiveObject();
        if (m == null){
            m = new Mine(canvas, loc2[0], loc2[1]);
            m.setAngle(angle, false, false);
        }
        else{
            m.setLocation(loc2[0], loc2[1]);
            m.setAngle(angle, false, false);
            m.setdefault();
        }
        m.setActive(true);
        mines.add(m);
        m.setLife(mineLife);
//         if (useSound)
//             lay_Mine.play();
    }
    
    private void layPowerupMine(){
        if (!cheatModeUnlimited)
            if (mineCount < 150)
                return;
        long time = System.currentTimeMillis();
        if (time < puMineTime + pupMineDelay)
            return;
        if (!cheatModeUnlimited)
            mineCount -= 150;
        puMineTime = time;
        double[] loc = ship.getPosition(),
                 loc2 = new double[2];
        double distance = Ship.maxRadius+10;
        for (int angle=0; angle<360; angle+=3){
            loc2[0] = -(distance + (angle%2==1?0:30)) * Math.sin(Math.toRadians(angle)) + loc[0];
            loc2[1] = (distance + (angle%2==1?0:30)) * Math.cos(Math.toRadians(angle)) + loc[1];
            
            Mine m;
            m = (Mine) inactive.mines.getInactiveObject();
            if (m == null){
                m = new Mine(canvas, loc2[0], loc2[1]);
                m.setAngle((2*angle) + (angle%2==1?0:90), false, false);
            }
            else{
                m.setLocation(loc2[0], loc2[1]);
                m.setAngle((2*angle) + (angle%2==1?0:90), false, false);
                m.setdefault();
            }
            m.setActive(true);
            mines.add(m);
            m.setLife((int) Math.round(mineLife*minePUmultiplyer));
        }
    }

    private void enemyFire(SpaceObject fireObject){
        if (fireObject != null){
            if (fireObject instanceof EnemyMissile){
                fireObject.setLife(missileLife);
                enemyMsls.add((EnemyMissile)fireObject);
            }
            else if (fireObject instanceof EnemyBullet){
                fireObject.setLife(bulletLife);
                enemyBlts.add((EnemyBullet)fireObject);
            }
        }
    }

    private void allyFire(SpaceObject fireObject){
        if (fireObject != null){
            if (fireObject instanceof Bullet){
                 fireObject.setLife(bulletLife);
                 bullets.add((Bullet)fireObject);
            }
            else if (fireObject instanceof GuidedMissile){
                GuidedMissile gm = (GuidedMissile) fireObject;
                gm.setLife(gMissileLife);
                gm.setTarget(findTarget());
                gMissiles.add((GuidedMissile)gm);
            }
            else if (fireObject instanceof Missile){
                fireObject.setLife(missileLife);
                missiles.add((Missile)fireObject);
            }
        }
    }
    
    
    private void moveBullets(){
        ArrayList<Bullet> iBullets = new ArrayList<Bullet>();
        SpaceObjectIterator<Bullet> it = bullets.iterator();
        while (it.hasNext()){
            Bullet b = it.next();
            if (b.active())
                b.move();
            else
                iBullets.add(b);
        }
        inactive.bullets.add(bullets.remove(iBullets));
    }
    
    private void moveMissiles(){
        ArrayList<Missile> iMissiles = new ArrayList<Missile>();
        SpaceObjectIterator<Missile> it = missiles.iterator();
        while (it.hasNext()){
            Missile m = it.next();
            if (m.active())
                m.move();
            else
                iMissiles.add(m);
        }
        inactive.missiles.add(missiles.remove(iMissiles));
    }
    
    private void moveGmissiles(){
        ArrayList<GuidedMissile> iGMissiles = new ArrayList<GuidedMissile>();
        SpaceObjectIterator<GuidedMissile> it = gMissiles.iterator();
        while (it.hasNext()){
            GuidedMissile gm = it.next();
            if (gm.active()){
                if (!gm.purse() && haveActiveTargets())
                    gm.setTarget(findTarget());
                gm.move();
            }
            else
                iGMissiles.add(gm);
        }
        inactive.gMissiles.add(gMissiles.remove(iGMissiles));
    }
    
    private void moveMines(){
        ArrayList<Mine> iMines = new ArrayList<Mine>();
        SpaceObjectIterator<Mine> it = mines.iterator();
        while (it.hasNext()){
            Mine m = it.next();
            if (m.active()){
                m.move();
            }
            else
                iMines.add(m);
        }
        inactive.mines.add(mines.remove(iMines));
    }
    
    private void moveNukMissiles(){
        ArrayList<NuclearMissile> iNukes = new ArrayList<NuclearMissile>();
        SpaceObjectIterator<NuclearMissile> it = nukMissiles.iterator();
        double maxRadiusA = (Asteroid.maxRadius + Missile.maxRadius) + 10;
        double maxRadiusE = (EnemyDatabase.maxRadius + Missile.maxRadius) + 10;
        double maxRadiusM = (Missile.maxRadius * 2) + 10;
        double maxRadiusB = (Bullet.maxRadius + Missile.maxRadius) + 10;
        while (it.hasNext()){
            NuclearMissile nm = it.next();
            if (nm.active()){
                boolean die = false;
                double[] p1 = nm.getPosition();
                nm.move();
                nm.inc();
                
                SpaceObjectIterator<Asteroid> as = asteroids.iterator();
                while(as.hasNext()){
                    Asteroid a = as.next();
                    if (!a.active() || StaticTools.getDistance(p1, a.getPosition()) > maxRadiusA)
                        continue;
                    if (nm.makesContactWith(a)){
                        nm.setActive(false);
                        die = true;
                        break;
                    }
                }
                
                SpaceObjectIterator<Enemy> en = enemies.iterator();
                while(en.hasNext() && !die){
                    Enemy e = en.next();
                    if (!e.active() || StaticTools.getDistance(p1, e.getPosition()) > maxRadiusE)
                        continue;
                    if (nm.makesContactWith(e)){
                        nm.setActive(false);
                        die = true;
                        break;
                    }
                }
                
                SpaceObjectIterator<ChaseEnemy> cen = chaseEnemies.iterator();
                while(cen.hasNext() && !die){
                    ChaseEnemy ce = cen.next();
                    if (!ce.active() || StaticTools.getDistance(p1, ce.getPosition()) > maxRadiusE)
                        continue;
                    if (nm.makesContactWith(ce)){
                        nm.setActive(false);
                        die = true;
                        break;
                    }
                }
                
                SpaceObjectIterator<EnemyBullet> ebs = enemyBlts.iterator();
                while (ebs.hasNext() && !die){
                    EnemyBullet eb = ebs.next();
                    if (!eb.active() || StaticTools.getDistance(p1, eb.getPosition()) > maxRadiusB)
                        continue;
                    if (nm.makesContactWith(eb)){
                        nm.setActive(false);
                        die = true;
                        break;
                    }
                }
                
                SpaceObjectIterator<EnemyMissile> ems = enemyMsls.iterator();
                while (ems.hasNext() && !die){
                    EnemyMissile em = ems.next();
                    if (!em.active() || StaticTools.getDistance(p1, em.getPosition()) > maxRadiusM)
                        continue;
                    if (nm.makesContactWith(em)){
                        nm.setActive(false);
                        die = true;
                        break;
                    }
                }
                
                if (die){
                    nukeBlast.add(nm);
                    iNukes.add(nm);
                }
            }
            else
                iNukes.add(nm);
        }
        inactive.nukMissiles.add(nukMissiles.remove(iNukes));
    }
    
    private void moveAsteroids(){
        ArrayList<Asteroid> iAsteroids = new ArrayList<Asteroid>();
        ArrayList<Bullet> iBullets = new ArrayList<Bullet>();
        ArrayList<Missile> iMissiles = new ArrayList<Missile>();
        ArrayList<GuidedMissile> iGMissiles = new ArrayList<GuidedMissile>();
        ArrayList<Mine> iMines = new ArrayList<Mine>();
        SpaceObjectIterator<Asteroid> it = asteroids.iterator();
        double maxRadius = (Asteroid.maxRadius + Bullet.maxRadius) + 10;
        boolean soundExplode = false, willInitSmall = false;
        while (it.hasNext()){
            Asteroid a = it.next();
            if (a.active()){
                boolean die = false;
                boolean add = true;
                double[] p1 = a.getPosition();
                a.move();
                
                SpaceObjectIterator<Bullet> bu = bullets.iterator();
                while(bu.hasNext()){
                    Bullet b = bu.next();
                    if (!b.active() || StaticTools.getDistance(p1, b.getPosition()) > maxRadius)
                        continue;
                    if (a.makesContactWith(b)){
                        b.setActive(false);
                        iBullets.add(b);
                        die = true;
                        willInitSmall = true;
                        break;
                    }
                }
                
                SpaceObjectIterator<Missile> mi = missiles.iterator();
                while(mi.hasNext() && !die){
                    Missile m = mi.next();
                    if (!m.active() || StaticTools.getDistance(p1, m.getPosition()) > maxRadius)
                        continue;
                    if (a.makesContactWith(m)){
                        m.setActive(false);
                        iMissiles.add(m);
                        a.setActive(false);
                        if (useExplosions)
                            explosions.explode(a);
                        soundExplode = true;
                        die = true;
                        add = false;
                        break;
                    }
                }
                
                SpaceObjectIterator<GuidedMissile> guMi = gMissiles.iterator();
                while(guMi.hasNext() && !die){
                    GuidedMissile gm = guMi.next();
                    if (!gm.active() || StaticTools.getDistance(p1, gm.getPosition()) > maxRadius)
                        continue;
                    if (a.makesContactWith(gm)){
                        gm.setActive(false);
                        iGMissiles.add(gm);
                        die = true;
                        willInitSmall = true;
                        break;
                    }
                }
                
                SpaceObjectIterator<Mine> mnes = mines.iterator();
                while(mnes.hasNext() && !die){
                    Mine mn = mnes.next();
                    if (!mn.active() || StaticTools.getDistance(p1, mn.getPosition()) > maxRadius)
                        continue;
                    if (a.makesContactWith(mn)){
                        mn.setActive(false);
                        a.setActive(false);
                        iMines.add(mn);
                        die = true;
                        break;
                    }
                }
                if (die){
                    rocksLeft--;
                    iAsteroids.add(a);
                    if (add)
                        initSmall.add(a);
                    addPoints(a.getGen() * pointsPerGen);
                }
            }
            else
                iAsteroids.add(a);
        }
        if (soundExplode && !willInitSmall && useSound)
            rock_Explode.play();
        inactive.bullets.add(bullets.remove(iBullets));
        inactive.missiles.add(missiles.remove(iMissiles));
        inactive.gMissiles.add(gMissiles.remove(iGMissiles));
        inactive.asteroids.add(asteroids.remove(iAsteroids));
        inactive.mines.add(mines.remove(iMines));
    }
    
    private void moveEnemies(){
        ArrayList<Enemy> iEnemies = new ArrayList<Enemy>();
        ArrayList<Bullet> iBullets = new ArrayList<Bullet>();
        ArrayList<Missile> iMissiles = new ArrayList<Missile>();
        ArrayList<GuidedMissile> iGMissiles = new ArrayList<GuidedMissile>();
        ArrayList<Mine> iMines = new ArrayList<Mine>();
        SpaceObjectIterator<Enemy> it = enemies.iterator();
        double maxRadiusB = (EnemyDatabase.maxRadius + Bullet.maxRadius) + 10;
        double maxRadiusM = (EnemyDatabase.maxRadius + Missile.maxRadius) + 10;
        boolean playSound = false;
        while (it.hasNext()){
            Enemy e = it.next();
            if (e.active()){
                boolean die = false;
                double[] p1 = e.getPosition();
                SpaceObject fire = e.moveAndFire();
                enemyFire(fire);
                
                SpaceObjectIterator<Bullet> bu = bullets.iterator();
                while(bu.hasNext()){
                    Bullet b = bu.next();
                    if (!b.active() || StaticTools.getDistance(p1, b.getPosition()) > maxRadiusB)
                        continue;
                    if (e.makesContactWith(b)){
                        b.setActive(false);
                        e.setActive(false);
                        iBullets.add(b);
                        playSound = true;
                        die = true;
                        break;
                    }
                }
                
                SpaceObjectIterator<Missile> mi = missiles.iterator();
                while(mi.hasNext() && !die){
                    Missile m = mi.next();
                    if (!m.active() || StaticTools.getDistance(p1, m.getPosition()) > maxRadiusM)
                        continue;
                    if (e.makesContactWith(m)){
                        m.setActive(false);
                        e.setActive(false);
                        iMissiles.add(m);
                        playSound = true;
                        die = true;
                        break;
                    }
                }
                
                SpaceObjectIterator<GuidedMissile> guMi = gMissiles.iterator();
                while(guMi.hasNext() && !die){
                    GuidedMissile gm = guMi.next();
                    if (!gm.active() || StaticTools.getDistance(p1, gm.getPosition()) > maxRadiusM)
                        continue;
                    if (e.makesContactWith(gm)){
                        gm.setActive(false);
                        e.setActive(false);
                        iGMissiles.add(gm);
                        playSound = true;
                        die = true;
                        break;
                    }
                }
                
                SpaceObjectIterator<Mine> mnes = mines.iterator();
                while(mnes.hasNext() && !die){
                    Mine mn = mnes.next();
                    if (!mn.active() || StaticTools.getDistance(p1, mn.getPosition()) > maxRadiusB)
                        continue;
                    if (e.makesContactWith(mn)){
                        mn.setActive(false);
                        e.setActive(false);
                        iMines.add(mn);
                        playSound = true;
                        die = true;
                        break;
                    }
                }
                if (die){
                    addPoints(enemyPoints);
                    iEnemies.add(e);
                    if (useExplosions)
                        explosions.explode(e);
                }
            }
            else
                iEnemies.add(e);
        }
        if (useSound && playSound)
            ship_Explode.play();
        inactive.bullets.add(bullets.remove(iBullets));
        inactive.missiles.add(missiles.remove(iMissiles));
        inactive.gMissiles.add(gMissiles.remove(iGMissiles));
        inactive.enemies.add(enemies.remove(iEnemies));
        inactive.mines.add(mines.remove(iMines));
    }
    
    private void moveChaseEnemies(){
        ArrayList<ChaseEnemy> iCEnemies = new ArrayList<ChaseEnemy>();
        ArrayList<Bullet> iBullets = new ArrayList<Bullet>();
        ArrayList<Missile> iMissiles = new ArrayList<Missile>();
        ArrayList<GuidedMissile> iGMissiles = new ArrayList<GuidedMissile>();
        ArrayList<Mine> iMines = new ArrayList<Mine>();
        SpaceObjectIterator<ChaseEnemy> it = chaseEnemies.iterator();
        double maxRadiusB = (EnemyDatabase.maxRadius + Bullet.maxRadius) + 10;
        double maxRadiusM = (EnemyDatabase.maxRadius + Missile.maxRadius) + 10;
        boolean playSound = false;
        while (it.hasNext()){
            ChaseEnemy ce = it.next();
            if (ce.active()){
                boolean die = false;
                double[] p1 = ce.getPosition();
                SpaceObject fire = ce.moveAndFire();
                enemyFire(fire);
                
                SpaceObjectIterator<Bullet> bu = bullets.iterator();
                while(bu.hasNext()){
                    Bullet b = bu.next();
                    if (!b.active() || StaticTools.getDistance(p1, b.getPosition()) > maxRadiusB)
                        continue;
                    if (ce.makesContactWith(b)){
                        b.setActive(false);
                        ce.setActive(false);
                        iBullets.add(b);
                        playSound = true;
                        die = true;
                        break;
                    }
                }
                
                SpaceObjectIterator<Missile> mi = missiles.iterator();
                while(mi.hasNext() && !die){
                    Missile m = mi.next();
                    if (!m.active() || StaticTools.getDistance(p1, m.getPosition()) > maxRadiusM)
                        continue;
                    if (ce.makesContactWith(m)){
                        m.setActive(false);
                        ce.setActive(false);
                        iMissiles.add(m);
                        playSound = true;
                        die = true;
                        break;
                    }
                }
                
                SpaceObjectIterator<GuidedMissile> guMi = gMissiles.iterator();
                while(guMi.hasNext() && !die){
                    GuidedMissile gm = guMi.next();
                    if (!gm.active() || StaticTools.getDistance(p1, gm.getPosition()) > maxRadiusM)
                        continue;
                    if (ce.makesContactWith(gm)){
                        gm.setActive(false);
                        ce.setActive(false);
                        iGMissiles.add(gm);
                        playSound = true;
                        die = true;
                        break;
                    }
                }
                
                SpaceObjectIterator<Mine> mnes = mines.iterator();
                while(mnes.hasNext() && !die){
                    Mine mn = mnes.next();
                    if (!mn.active() || StaticTools.getDistance(p1, mn.getPosition()) > maxRadiusB)
                        continue;
                    if (ce.makesContactWith(mn)){
                        mn.setActive(false);
                        ce.setActive(false);
                        iMines.add(mn);
                        playSound = true;
                        die = true;
                        break;
                    }
                }
                if (die){
                    addPoints(enemyPoints);
                    iCEnemies.add(ce);
                    if (useExplosions)
                        explosions.explode(ce);
                }
            }
            else
                iCEnemies.add(ce);
        }
        if (useSound && playSound)
            ship_Explode.play();
        inactive.bullets.add(bullets.remove(iBullets));
        inactive.missiles.add(missiles.remove(iMissiles));
        inactive.gMissiles.add(gMissiles.remove(iGMissiles));
        inactive.chaseEnemies.add(chaseEnemies.remove(iCEnemies));
        inactive.mines.add(mines.remove(iMines));
    }
    
    private void moveAllies(){
        //ArrayList<Ally> iAllies = new ArrayList<Ally>();
        boolean move = haveActiveTargets();
        SpaceObjectIterator<Ally> it = allies.iterator();
        while (it.hasNext()){
            Ally a = it.next();
            if (a.active()){
                if (!move){
                    a.draw();
                    continue;
                }
                SpaceObject so = a.moveAndFire();
                allyFire(so);
            }
        }
    }
    
    private void moveEnemyBullets(){
        ArrayList<EnemyBullet> ieBullets = new ArrayList<EnemyBullet>();
        ArrayList<Bullet> iBullets = new ArrayList<Bullet>();
        ArrayList<Missile> iMissiles = new ArrayList<Missile>();
        ArrayList<GuidedMissile> iGMissiles = new ArrayList<GuidedMissile>();
        ArrayList<Mine> iMines = new ArrayList<Mine>();
        SpaceObjectIterator<EnemyBullet> it = enemyBlts.iterator();
        double maxRadius = (Bullet.maxRadius + Missile.maxRadius) + 10;
        while (it.hasNext()){
            EnemyBullet eb = it.next();
            if (eb.active()){
                boolean die = false;
                double[] p1 = eb.getPosition();
                eb.move();
                
                SpaceObjectIterator<Bullet> bu = bullets.iterator();
                while(bu.hasNext()){
                    Bullet b = bu.next();
                    if (!b.active() || StaticTools.getDistance(p1, b.getPosition()) > maxRadius)
                        continue;
                    if (eb.makesContactWith(b)){
                        b.setActive(false);
                        eb.setActive(false);
                        iBullets.add(b);
                        die = true;
                        break;
                    }
                }
                
                SpaceObjectIterator<Missile> mi = missiles.iterator();
                while(mi.hasNext() && !die){
                    Missile m = mi.next();
                    if (!m.active() || StaticTools.getDistance(p1, m.getPosition()) > maxRadius)
                        continue;
                    if (eb.makesContactWith(m)){
                        m.setActive(false);
                        eb.setActive(false);
                        iMissiles.add(m);
                        die = true;
                        break;
                    }
                }
                
                SpaceObjectIterator<GuidedMissile> guMi = gMissiles.iterator();
                while(guMi.hasNext() && !die){
                    GuidedMissile gm = guMi.next();
                    if (!gm.active() || StaticTools.getDistance(p1, gm.getPosition()) > maxRadius)
                        continue;
                    if (eb.makesContactWith(gm)){
                        gm.setActive(false);
                        eb.setActive(false);
                        iGMissiles.add(gm);
                        die = true;
                        break;
                    }
                }
                
                SpaceObjectIterator<Mine> mnes = mines.iterator();
                while(mnes.hasNext() && !die){
                    Mine mn = mnes.next();
                    if (!mn.active() || StaticTools.getDistance(p1, mn.getPosition()) > maxRadius)
                        continue;
                    if (eb.makesContactWith(mn)){
                        mn.setActive(false);
                        eb.setActive(false);
                        iMines.add(mn);
                        die = true;
                        break;
                    }
                }
                if (die){
                    addPoints(enemyBulletPoints);
                    ieBullets.add(eb);
                }
            }
            else
                ieBullets.add(eb);
        }
        inactive.bullets.add(bullets.remove(iBullets));
        inactive.missiles.add(missiles.remove(iMissiles));
        inactive.gMissiles.add(gMissiles.remove(iGMissiles));
        inactive.mines.add(mines.remove(iMines));
        enemyBlts.remove(ieBullets);
    }
    
    private void moveEnemyMissiles(){
    	ArrayList<Bullet> iBullets = new ArrayList<Bullet>();
    	ArrayList<Missile> iMissiles = new ArrayList<Missile>();
    	ArrayList<GuidedMissile> iGMissiles = new ArrayList<GuidedMissile>();
        ArrayList<EnemyMissile> ieMissiles = new ArrayList<EnemyMissile>();
        ArrayList<Mine> iMines = new ArrayList<Mine>();
        SpaceObjectIterator<EnemyMissile> it = enemyMsls.iterator();
        double maxRadius = (Missile.maxRadius + Bullet.maxRadius) + 10;
        while (it.hasNext()){
            EnemyMissile em = it.next();
            if (em.active()){
                boolean die = false;
                double[] p1 = em.getPosition();
                em.move();
                
                SpaceObjectIterator<Bullet> bu = bullets.iterator();
                while(bu.hasNext()){
                    Bullet b = bu.next();
                    if (!b.active() || StaticTools.getDistance(p1, b.getPosition()) > maxRadius)
                        continue;
                    if (em.makesContactWith(b)){
                        b.setActive(false);
                        em.setActive(false);
                        iBullets.add(b);
                        die = true;
                        break;
                    }
                }
                
                SpaceObjectIterator<Missile> mi = missiles.iterator();
                while(mi.hasNext() && !die){
                    Missile m = mi.next();
                    if (!m.active() || StaticTools.getDistance(p1, m.getPosition()) > maxRadius)
                        continue;
                    if (em.makesContactWith(m)){
                        m.setActive(false);
                        em.setActive(false);
                        iMissiles.add(m);
                        die = true;
                        break;
                    }
                }
                
                SpaceObjectIterator<GuidedMissile> guMi = gMissiles.iterator();
                while(guMi.hasNext() && !die){
                    GuidedMissile gm = guMi.next();
                    if (!gm.active() || StaticTools.getDistance(p1, gm.getPosition()) > maxRadius)
                        continue;
                    if (em.makesContactWith(gm)){
                        gm.setActive(false);
                        em.setActive(false);
                        iGMissiles.add(gm);
                        die = true;
                        break;
                    }
                }
                
                SpaceObjectIterator<Mine> mnes = mines.iterator();
                while(mnes.hasNext() && !die){
                    Mine mn = mnes.next();
                    if (!mn.active() || StaticTools.getDistance(p1, mn.getPosition()) > maxRadius)
                        continue;
                    if (em.makesContactWith(mn)){
                        mn.setActive(false);
                        em.setActive(false);
                        iMines.add(mn);
                        die = true;
                        break;
                    }
                }
                if (die){
                    addPoints(enemyMissilePoints);
                    ieMissiles.add(em);
                }
            }
            else
                ieMissiles.add(em);
        }
        inactive.bullets.add(bullets.remove(iBullets));
        inactive.missiles.add(missiles.remove(iMissiles));
        inactive.gMissiles.add(gMissiles.remove(iGMissiles));
        inactive.mines.add(mines.remove(iMines));
        enemyMsls.remove(ieMissiles);
    }
    
    private void moveShip(){
        if (ship != null && ship.active())
            ship.move();
    }
    
    private void gameOver(){
        String[] goText = new String[2];
        goText[0] = "Game Over";
        goText[1] = "Press \"S\" to start";
        printText(goText, center, gameOverFont, gameOverTextColor);
    }
    
    private void revamp(){
        emptyLists();
        allies.clear();
        canvas.erase();
        initShip();
        rocksLeft = 0;
        if (startWithAsteroids)
            initAsteroids();
        points = 0;
        lives = startingLives;
        ghosts = startingGhosts;
        nMissilesCount = startingMiss;
        gMissilesCount = startingGmiss;
        sShotCount = startingSshot;
        nukCount = startingNukMiss;
        mineCount = startingMines;
        waiting = false;
        revamp = false;
    }
    
    private void addPoints(int p){
        if (!waiting)
            points += p;
    }
}