package com.sierra8;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

public class Object implements RenderableEntity {
    private final Vector2 position;
    private final float size;
    private final float sizeBox;
    private final Rectangle hitbox;
    private Texture boxTexture;

    @Override
    public float getRenderY() {
        return position.y;
    }

    public Object(float x, float y){
        this.position = new Vector2(x, y);
        this.size = 100f;
        this.sizeBox = size;
        this.hitbox = new Rectangle(position.x-sizeBox/2, position.y-sizeBox/2, sizeBox, sizeBox);
        this.boxTexture = new Texture("textures/box.png");
    }

    public void update(float delta, Vector2 playerPosition, ArrayList<Enemy> enemies){
        hitbox.setPosition(position.x-sizeBox/2, position.y-sizeBox/2);
    }



    public void render(SpriteBatch batch){

        float playerWidth = size;
        float playerHeight = size;

        batch.draw(boxTexture, position.x - playerWidth / 2, position.y - playerHeight / 2, playerWidth, playerHeight);

    }

    public void renderBoxes(ShapeRenderer shape){
        shape.setColor(75, 60, 255, 1);
        shape.rect(hitbox.x, hitbox.y, hitbox.width, hitbox.height);
    }

    public boolean collidesWith(Bullet bullet) {
        return Intersector.overlaps(bullet.getHitbox(), hitbox);
    }

    public boolean collidesWith(Player player) {
        return hitbox.overlaps(player.getHitbox());
    }

    public Vector2 getPosition() {
        return position;
    }

    public Rectangle getHitbox(){
        return hitbox;
    }
}
