package space.manipulators;

import space.objects.*;

public final class Inactive
{
    public final SpaceObjectList<Bullet> bullets      = new SpaceObjectList<Bullet>();
    //public final SpaceObjectList throwStars   = new SpaceObjectList();
    public final SpaceObjectList<Missile> missiles     = new SpaceObjectList<Missile>();
    public final SpaceObjectList<GuidedMissile> gMissiles    = new SpaceObjectList<GuidedMissile>();
    public final SpaceObjectList<Asteroid> asteroids    = new SpaceObjectList<Asteroid>();
    public final SpaceObjectList<Enemy> enemies      = new SpaceObjectList<Enemy>();
    public final SpaceObjectList<ChaseEnemy> chaseEnemies = new SpaceObjectList<ChaseEnemy>();
    public final SpaceObjectList<NuclearMissile> nukMissiles  = new SpaceObjectList<NuclearMissile>();
    public final SpaceObjectList<Mine> mines        = new SpaceObjectList<Mine>();
}