package com.sierra8;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import java.util.ArrayList;

public class Box extends Object {
    private final float sizeBox;

    public Box(float x, float y) {
        super(x, y, 100f);
        this.sizeBox = this.size;
        this.hitbox = new Rectangle(position.x - sizeBox / 2, position.y - sizeBox / 2, sizeBox, sizeBox);
        this.texture = loadTexture("textures/box.png"); //
    }

    @Override
    public void render(SpriteBatch batch) {
        if (texture != null) {
            batch.draw(texture, position.x - size / 2, position.y - size / 2, size, size); //
        }
    }

    @Override
    public void renderBoxes(ShapeRenderer shape) {
        if (hitbox != null) {
            shape.setColor(75/255f, 60/255f, 255/255f, 1); //
            shape.rect(hitbox.x, hitbox.y, hitbox.width, hitbox.height); //
        }
    }
}
