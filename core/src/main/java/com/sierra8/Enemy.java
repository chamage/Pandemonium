package com.sierra8;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

public class Enemy {
    private final Vector2 position;
    private final Vector2 direction;
    private final float speed;
    private final float size;
    private boolean dead;
    private final Circle hitbox;
    private Texture playerTexture;

    public Enemy(float x, float y, float speed){
        this.position = new Vector2(x, y);
        this.direction = new Vector2();
        this.speed = speed;
        this.size = 40f;
        this.dead = false;
        this.hitbox = new Circle(position, size*.6f);
        this.playerTexture = new Texture("textures/enemy.png");
    }

    public void update(float delta, Vector2 playerPosition, ArrayList<Enemy> enemies){
        direction.set(playerPosition).sub(position);

        if (direction.len2() > 0.01f) {
            direction.nor().scl(speed * delta);
            position.add(direction);
        }

        hitbox.setPosition(position);

        handleCollisionWithOthers(delta, enemies);
    }

    private void handleCollisionWithOthers(float delta, ArrayList<Enemy> enemies){
        for (Enemy other : enemies) {
            if (other == this) continue;

            float distance = position.dst(other.position);
            if (distance < 38f && distance > 0f) {
                Vector2 repulsion = new Vector2(position).sub(other.position).nor().scl(160f * delta);
                position.add(repulsion);
                hitbox.setPosition(position);
            }
        }
    }

    public void render(SpriteBatch batch){
        float rawAngle = direction.angleDeg();

        float playerWidth = 140;
        float playerHeight = 140;
        if (direction.x>0){
            batch.draw(playerTexture, position.x - playerWidth/2 - 4, position.y - playerHeight/2 + 6, playerWidth, playerHeight);
        }
        else {
            batch.draw(playerTexture, position.x + playerWidth/2 + 4, position.y - playerHeight/2 + 6, -playerWidth, playerHeight);
        }
    }

    public boolean collidesWith(Bullet bullet) {
        return hitbox.overlaps(bullet.getHitbox());
    }

    public boolean collidesWith(Player player) {
        return hitbox.overlaps(player.getHitbox());
    }

    public void markDead() {
        dead = true;
    }

    public boolean isDead() {
        return dead;
    }

    public Vector2 getPosition() {
        return position;
    }

    public Circle getHitbox(){
        return hitbox;
    }
}
