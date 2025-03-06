package com.sierra8;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

public class EnemyManager {
    private ArrayList<Enemy> enemies;
    private float spawnCooldown;
    private float spawnTimer;
    private int maxEnemies;
    private float enemySpeed;

    public EnemyManager(float spawnCooldown, int maxEnemies, float enemySpeed){
        enemies = new ArrayList<Enemy>();
        this.spawnCooldown = spawnCooldown;
        this.spawnTimer = 0;
        this.maxEnemies = maxEnemies;
        this.enemySpeed = enemySpeed;
    }

    public void update(float delta, Vector2 playerPosition, ArrayList<Bullet> bullets){
        spawnTimer += delta;
        if (spawnTimer >= spawnCooldown && enemies.size() < maxEnemies){
            spawnTimer = 0;
            spawnEnemy(playerPosition);
        }

        for(int i = enemies.size() - 1; i >= 0; i--){
            Enemy enemy = enemies.get(i);
            enemy.update(delta, new Vector2(playerPosition));

            for (Bullet bullet : bullets){
                if (enemy.collidesWith(bullet)){
                    enemy.markDead();
                    bullets.remove(bullet);
                    break;
                }
            }

            if (enemy.isDead()){
                enemies.remove(i);
            }
        }
    }

    private void spawnEnemy(Vector2 playerPosition){
        float spawnDistance = 500f;
        float angle = MathUtils.random(0, 360);
        float x = playerPosition.x + MathUtils.cosDeg(angle) * spawnDistance;
        float y = playerPosition.y + MathUtils.sinDeg(angle) * spawnDistance;
        enemies.add(new Enemy(x, y, enemySpeed));
    }

    public void render(ShapeRenderer shape){
        for (Enemy enemy : enemies){
            enemy.render(shape);
        }
    }

    public ArrayList<Enemy> getEnemies(){
        return enemies;
    }
}
