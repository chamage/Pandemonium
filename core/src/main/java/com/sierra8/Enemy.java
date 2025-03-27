package com.sierra8;

import com.badlogic.gdx.graphics.Color;
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

    public Enemy(float x, float y, float speed){
        this.position = new Vector2(x, y);
        this.direction = new Vector2();
        this.speed = speed;
        this.size = 30f;
        this.dead = false;
        this.hitbox = new Circle(position, size*.6f);
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

    public void render(ShapeRenderer shape){
        float rotation = direction.angleDeg();

        float tipX   = position.x + MathUtils.cosDeg(rotation) * size;
        float tipY   = position.y + MathUtils.sinDeg(rotation) * size;
        float leftX  = position.x + MathUtils.cosDeg(rotation + 135) * size;
        float leftY  = position.y + MathUtils.sinDeg(rotation + 135) * size;
        float rightX = position.x + MathUtils.cosDeg(rotation - 135) * size;
        float rightY = position.y + MathUtils.sinDeg(rotation - 135) * size;

        shape.setColor(Color.RED);
        shape.triangle(tipX, tipY, leftX, leftY, rightX, rightY);
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
