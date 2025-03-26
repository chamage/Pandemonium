package com.sierra8;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

public class Enemy {
    private Vector2 position;
    private float speed;
    private float rotation;
    private float size;
    private boolean dead;
    private Circle hitbox;

    public Enemy(float x, float y, float speed){
        this.position = new Vector2(x, y);
        this.speed = speed;
        this.size = 30f;
        this.dead = false;
        this.hitbox = new Circle(position, size*.6f);
    }

    public void update(float delta, Vector2 playerPosition, ArrayList<Enemy> enemies){
        Vector2 target = new Vector2(playerPosition.x, playerPosition.y);
        Vector2 direction = target.sub(position);
        if (direction.len() != 0) {
            direction.nor();
        }
        position.add(direction.scl(speed * delta));
        rotation = direction.angleDeg();

        hitbox.setPosition(position);

        for (Enemy other : enemies) {
            if (other == this) continue;
            if (position.dst(other.getPosition()) < 38f) {
                Vector2 repulsion = new Vector2(position).sub(other.getPosition()).nor();
                position.add(repulsion.scl(160f * delta));
            }
        }
    }

    public void render(ShapeRenderer shape){

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
