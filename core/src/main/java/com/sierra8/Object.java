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

    @Override
    public void renderShadow(SpriteBatch batch, Texture shadowTexture) {
        if (texture != null && shadowTexture != null) { // Only render shadow if object has a texture
            float shadowScaleFactor = 1.4f; // How much of the object's base size the shadow should be
            float shadowWidth = (this instanceof Bush) ? size * 1.1f : size * shadowScaleFactor; // Bushes might want wider shadows
            shadowWidth = (this instanceof Tree) ? ((Tree)this).getTrunkWidth() * 3.5f : shadowWidth; // Trees shadow based on trunk
            shadowWidth = (this instanceof Box) ? size * 1.1f : shadowWidth;


            float shadowHeight = shadowWidth * 0.4f; // Keep shadow aspect ratio consistent

            float shadowOffsetX = -shadowWidth / 2f;

            batch.setColor(1, 1, 1, 0.6f); // Semi-transparent shadow
            float shadowOffsetY;
            if (this instanceof Tree) {
                shadowOffsetY = -((Tree)this).getVisualHeight() / 2f - 10;
                batch.draw(shadowTexture,
                    position.x + shadowOffsetX + 5,
                    position.y + shadowOffsetY,
                    shadowWidth,
                    shadowHeight);
            } else if (this instanceof Box) {
                shadowOffsetY = -size / 2f - 25;
                batch.draw(shadowTexture,
                    position.x + shadowOffsetX,
                    position.y + shadowOffsetY,
                    shadowWidth,
                    shadowHeight);
            } else if (this instanceof Bush) {
                shadowOffsetY = -((Bush)this).getVisualHeight() / 2f - 40;
                batch.draw(shadowTexture,
                    position.x + shadowOffsetX,
                    position.y + shadowOffsetY,
                    shadowWidth,
                    shadowHeight);
            }
            else {
                shadowOffsetY = -size / 2f; // Default for other potential objects
                batch.draw(shadowTexture,
                    position.x + shadowOffsetX,
                    position.y + shadowOffsetY,
                    shadowWidth,
                    shadowHeight);
            }

            batch.setColor(1, 1, 1, 1); // Reset color
        }
    }

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
