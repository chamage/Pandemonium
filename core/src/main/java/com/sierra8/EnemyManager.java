package com.sierra8;

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

    public EnemyManager(float spawnCooldown, int maxEnemies, float enemySpeed){
        enemies = new ArrayList<>();
        this.spawnCooldown = spawnCooldown;
        this.spawnTimer = 0;
        this.maxEnemies = maxEnemies;
        this.enemySpeed = enemySpeed;
    }

    public void setPlayerDeathListener(PlayerDeathListener playerDeathListener) {
        this.playerDeathListener = playerDeathListener;
    }

    public void setEnemyDeathListener(EnemyDeathListener enemyDeathListener) {
        this.enemyDeathListener = enemyDeathListener;
    }

    public void update(float delta, Player player, ArrayList<Bullet> bullets){
        spawnTimer += delta;

        if (spawnTimer >= spawnCooldown && enemies.size() < maxEnemies) {
            spawnEnemy(player.getPosition());
            spawnTimer = 0;
        }

        // Create a temporary list to track bullets that should be removed
        List<Bullet> bulletsToRemove = null;


        for (int i = enemies.size() - 1; i >= 0; i--) {
            Enemy enemy = enemies.get(i);
            enemy.update(delta, player.getPosition(), enemies);

            // Check collision with player
            if (enemy.collidesWith(player)) {
                if (playerDeathListener != null) {
                    playerDeathListener.onPlayerDeath();
                }
            }

            // Check collision with bullets safely
            for (Bullet bullet : bullets) {
                if (enemy.collidesWith(bullet)) {
                    if (enemyDeathListener != null) {
                        enemyDeathListener.onEnemyDeath();
                    }
                    enemy.markDead();
                    if (bulletsToRemove == null) {
                        bulletsToRemove = new ArrayList<>();
                    }
                    bulletsToRemove.add(bullet);
                    break;

                }
            }

            if (enemy.isDead()) {
                enemies.remove(i);
            }
        }

        // Remove bullets after iteration to avoid concurrent modification
        if (bulletsToRemove != null) {
            bullets.removeAll(bulletsToRemove);
        }


    }

    private void spawnEnemy(Vector2 playerPosition){
        final float spawnDistance = 650f;
        float angle = MathUtils.random(360f);
        float x = playerPosition.x + MathUtils.cosDeg(angle) * spawnDistance;
        float y = playerPosition.y + MathUtils.sinDeg(angle) * spawnDistance;
        enemies.add(new Enemy(x, y, enemySpeed));
    }

    public void render(SpriteBatch batch){
        for (Enemy enemy : enemies){
            enemy.render(batch);
        }
    }

    public ArrayList<Enemy> getEnemies(){
        return enemies;
    }
}
