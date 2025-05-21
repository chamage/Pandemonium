package com.sierra8;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import java.util.ArrayList;

public abstract class Object implements RenderableEntity {
    protected Vector2 position;
    protected float size; // General size, can be used differently by subclasses
    protected Rectangle hitbox;
    protected Texture texture;
    protected boolean collidable;

    public Object(float x, float y, float size) {
        this.position = new Vector2(x, y);
        this.size = size;
        this.collidable = true;
    }

    @Override
    public float getRenderY() {
        return position.y;
    }

    @Override
    public abstract void render(SpriteBatch batch);

    public abstract void renderBoxes(ShapeRenderer shape);

    public Vector2 getPosition() {
        return position;
    }

    public Rectangle getHitbox(){
        return hitbox;
    }

    protected Texture loadTexture(String path) {
        return new Texture(path);
    }

    public boolean isCollidable() { // Getter for the flag
        return collidable;
    }

    public void dispose() {
        if (texture != null) {
            texture.dispose();
        }
    }
}
