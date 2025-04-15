package com.sierra8;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;

public class Bullet {
    private final Vector2 position;
    private final Vector2 velocity;
    private float distanceTraveled;
    private final float maxDistance;
    private final Circle hitbox;

    private final Vector2 tempVelocity = new Vector2();

    public Bullet(Vector2 position, Vector2 direction, float speed){
        this.position = new Vector2(position);
        this.velocity = new Vector2(direction).nor().scl(speed);
        this.distanceTraveled = 0;
        this.maxDistance = Gdx.graphics.getWidth() + 50f;
        this.hitbox = new Circle(position, 7);
    }

    public void update(float delta){
        tempVelocity.set(velocity).scl(delta);
        position.add(tempVelocity);
        distanceTraveled += tempVelocity.len();
        hitbox.setPosition(position);
    }

    public boolean isOutOfRange(){
        return distanceTraveled >= maxDistance;
    }

    public void render(ShapeRenderer shape){
        shape.setColor(Color.RED);
        shape.circle(position.x, position.y, hitbox.radius);
    }

    public Vector2 getPosition() {
        return position.cpy();
    }

    public Circle getHitbox(){
        return hitbox;
    }
}
