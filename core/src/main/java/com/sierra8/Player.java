package com.sierra8;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class Player {
    private Vector2 position;
    private float speed;
    private  float rotation;

    public Player(float x, float y){
        this.position = new Vector2(x, y);
        this.speed = 200f;
    }

    public void update(float delta, Camera camera){

        if (Gdx.input.isKeyPressed(Input.Keys.W)){
            position.y += speed * delta;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)){
            position.y -= speed * delta;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)){
            position.x += speed * delta;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)){
            position.x -= speed * delta;
        }

        Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(mousePos);
        Vector2 direction = new Vector2(mousePos.x - position.x, mousePos.y - position.y);
        rotation = direction.angleDeg();
    }

    public void render(ShapeRenderer shape, Camera camera){
        shape.begin(ShapeRenderer.ShapeType.Filled);

        float size = 20f;
        float tipX   = position.x + MathUtils.cosDeg(rotation) * size;
        float tipY   = position.y + MathUtils.sinDeg(rotation) * size;
        float leftX  = position.x + MathUtils.cosDeg(rotation + 140) * size;
        float leftY  = position.y + MathUtils.sinDeg(rotation + 140) * size;
        float rightX = position.x + MathUtils.cosDeg(rotation - 140) * size;
        float rightY = position.y + MathUtils.sinDeg(rotation - 140) * size;

        shape.setColor(Color.BLUE);
        shape.triangle(tipX, tipY, leftX, leftY, rightX, rightY);

        shape.end();

    }

    public Vector2 getPosition(){
        return position;
    }
}
