package com.sierra8;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import java.util.ArrayList;

public class Bush extends Object {

    private final float visualWidth;
    private final float visualHeight;

    public Bush(float x, float y) {
        super(x, y, 180f); // Visual size for a bush
        this.visualWidth = this.size;
        this.visualHeight = this.size * 0.5f; // Bush might be wider than tall, or adjust as needed
        this.hitbox = null; // No collision for bushes
        this.texture = loadTexture("textures/bush.png");
        this.collidable = false;
    }

    @Override
    public void render(SpriteBatch batch) {
        if (texture != null) {
            batch.draw(texture, position.x - visualWidth / 2, position.y - visualHeight / 2, visualWidth, visualHeight);
        }
    }

    @Override
    public void renderBoxes(ShapeRenderer shape) {
        // no hitbox to show
    }
}
