package com.sierra8;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class Bullet {
    private Vector2 position;
    private Vector2 velocity;
    private float distanceTraveled;
    private float maxDistance;

    public Bullet(Vector2 position, Vector2 direction, float speed){
        this.position = position;
        this.velocity = direction.scl(speed);
        this.distanceTraveled = 0;
        this.maxDistance = 800f;
    }

    public void update(float delta){
        float displacement = velocity.len() * delta;
        position.add(new Vector2(velocity).scl(delta));
        distanceTraveled += displacement;
    }

    public boolean isOutOfRange(){
        return distanceTraveled >= maxDistance;
    }

    public void render(ShapeRenderer shape){
        shape.circle(position.x, position.y, 9);
    }
}
