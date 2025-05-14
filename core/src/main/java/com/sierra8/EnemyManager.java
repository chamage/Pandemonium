package com.sierra8;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

public class EnemyManager {
    private final ArrayList<Enemy> enemies;
    private final float spawnCooldown;
    private float spawnTimer;
    private final int maxEnemies;
    private final float enemySpeed;
    private PlayerDeathListener playerDeathListener;
    private EnemyDeathListener enemyDeathListener;

    private boolean delayTargeting = false;
    private float delayTimer = 0f;
    private Vector2 delayedTargetPos = null;

    private final float despawnRadius;

    public EnemyManager(float spawnCooldown, int maxEnemies, float enemySpeed){
        enemies = new ArrayList<>();
        this.spawnCooldown = spawnCooldown;
        this.spawnTimer = 0;
        this.maxEnemies = maxEnemies;
        this.enemySpeed = enemySpeed;
        this.despawnRadius = Gdx.graphics.getWidth()*2f;
    }

    public void setPlayerDeathListener(PlayerDeathListener playerDeathListener) {
        this.playerDeathListener = playerDeathListener;
    }

    public void setEnemyDeathListener(EnemyDeathListener enemyDeathListener) {
        this.enemyDeathListener = enemyDeathListener;
    }

    public void startTargetDelay(Vector2 oldPos, float delayTime) {
        this.delayTargeting = true;
        this.delayedTargetPos = oldPos;
        this.delayTimer = delayTime;
    }

    public void update(float delta, Player player, ArrayList<Bullet> bullets){
        Vector2 playerPosition = player.getPosition();
        if (delayTargeting) {
            delayTimer -= delta;
            if (delayTimer <= 0f) {
                delayTargeting = false;
                delayedTargetPos = null;
            }
        }
        spawnTimer += delta;

        if (spawnTimer >= spawnCooldown && enemies.size() < maxEnemies) {
            spawnEnemy(player.getPosition());
            spawnTimer = 0;
        }

        // Create a temporary list to track bullets that should be removed
        List<Bullet> bulletsToRemove = null;
        for (int i = enemies.size() - 1; i >= 0; i--) {
            Enemy enemy = enemies.get(i);

            // Despawn check
            if (enemy.getPosition().dst(playerPosition) > despawnRadius) {
                enemies.remove(i);
                continue; // Skip further processing for this despawned enemy
            }

            Vector2 targetPos = delayTargeting ? delayedTargetPos : playerPosition;
            // Pass worldObjects to enemy's update method
            enemy.update(delta, targetPos, enemies, ObjectManager.getObjects());


            if (enemy.collidesWith(player)) {
                if (playerDeathListener != null) {
                    playerDeathListener.onPlayerDeath();
                }
            }

            for (Bullet bullet : bullets) {
                if (enemy.collidesWith(bullet)) {
                    if (enemyDeathListener != null) {
                        enemyDeathListener.onEnemyDeath();
                    }
                    enemy.markDead(); // Enemy will be removed in the next check if dead
                    if (bulletsToRemove == null) {
                        bulletsToRemove = new ArrayList<>();
                    }
                    bulletsToRemove.add(bullet);
                    // Important: Do not break here if a bullet can hit multiple enemies.
                    // If a bullet should only hit one, then break. Current logic seems to allow one hit per enemy.
                }
            }

            if (enemy.isDead()) {
                enemies.remove(i);
            }
        }

        if (bulletsToRemove != null) {
            bullets.removeAll(bulletsToRemove);
        }
    }

    private void spawnEnemy(Vector2 playerPosition){
        final float spawnDistanceX = Gdx.graphics.getWidth();
        final float spawnDistanceY = Gdx.graphics.getHeight();
        float angle = MathUtils.random(360f);
        float x = playerPosition.x + MathUtils.cosDeg(angle) * spawnDistanceX;
        float y = playerPosition.y + MathUtils.sinDeg(angle) * spawnDistanceY;
        enemies.add(new Enemy(x, y, enemySpeed));
    }

    public void render(SpriteBatch batch){
        for (Enemy enemy : enemies){
            enemy.render(batch);
        }
    }

    public void renderBoxes(ShapeRenderer shape){
        for (Enemy enemy : enemies){
            enemy.renderBoxes(shape);
        }
    }

    public ArrayList<Enemy> getEnemies(){
        return enemies;
    }

    public int getStateCount(String state){
        int chasingCount = 0;
        int wanderingCount = 0;
        int retreatCount = 0;
        int groupCount = 0;
        for (Enemy enemy : enemies){
            if (enemy.getChasing() == 0) wanderingCount++;
            else if (enemy.getChasing() == 1) chasingCount++;
            else if (enemy.getChasing() == 2) groupCount++;
            else if (enemy.getChasing() == 3) retreatCount++;
        }

        switch (state){
            case "wandering":
                return wanderingCount;
            case "chasing":
                return chasingCount;
            case "group":
                 return groupCount;
            case "retreat":
                 return retreatCount;
            default:
                 return 0;
        }
    }
}
