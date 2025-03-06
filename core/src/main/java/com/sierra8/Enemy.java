package com.sierra8;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class Enemy {
    private Vector2 position;
    private float speed;
    private float rotation;
    private float size;
    private boolean dead;

    public Enemy(float x, float y, float speed){
        this.position = new Vector2(x, y);
        this.speed = speed;
        this.size = 30f;
        this.dead = false;
    }

    public void update(float delta, Vector2 playerPosition){
        Vector2 target = new Vector2(playerPosition.x, playerPosition.y);
        Vector2 direction = target.sub(position);
        if (direction.len() != 0) {
            direction.nor();
        }
        position.add(direction.scl(speed * delta));
        rotation = direction.angleDeg();
    }

    public void render(ShapeRenderer shape){
        shape.begin(ShapeRenderer.ShapeType.Filled);

        float tipX   = position.x + MathUtils.cosDeg(rotation) * size;
        float tipY   = position.y + MathUtils.sinDeg(rotation) * size;
        float leftX  = position.x + MathUtils.cosDeg(rotation + 135) * size;
        float leftY  = position.y + MathUtils.sinDeg(rotation + 135) * size;
        float rightX = position.x + MathUtils.cosDeg(rotation - 135) * size;
        float rightY = position.y + MathUtils.sinDeg(rotation - 135) * size;

        shape.setColor(Color.RED);
        shape.triangle(tipX, tipY, leftX, leftY, rightX, rightY);
        shape.end();
    }

    public boolean collidesWith(Bullet bullet) {
        float collisionDistance = (size / 2) + 5f;
        return position.dst(bullet.getPosition()) < collisionDistance;
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
}
