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

public class Enemy implements RenderableEntity {
    private final Vector2 position;
    private final Vector2 direction;
    private final float speed;
    private final float size;
    private final float sizeBox;
    private boolean dead;
    private final Rectangle hitbox;
    private boolean facingRight = true;

    private Animation<TextureRegion> walkAnimation;
    private Texture walkSheet;
    private float animationTimer;

    @Override
    public float getRenderY() {
        return position.y;
    }

    public Enemy(float x, float y, float speed){
        this.position = new Vector2(x, y);
        this.direction = new Vector2();
        this.speed = speed;
        this.size = 480f;
        this.sizeBox = (float)(140*.3);
        this.dead = false;
        this.hitbox = new Rectangle(position.x-sizeBox/2, position.y-sizeBox/2, sizeBox, (float)(sizeBox*1.5));

        this.walkSheet = new Texture("textures/enemySheet.png");
        TextureRegion[][] walkTmp = TextureRegion.split(walkSheet,
            walkSheet.getWidth() / 8,
            walkSheet.getHeight());

        TextureRegion[] walkFrames = new TextureRegion[8];

        for (int i = 0; i < 8; i++) {
            walkFrames[i] = walkTmp[0][i];
        }
        walkAnimation = new Animation<TextureRegion>(0.1f, walkFrames);
        this.animationTimer = 0f;
    }

    public void update(float delta, Vector2 playerPosition, ArrayList<Enemy> enemies){
        animationTimer += delta;
        direction.set(playerPosition).sub(position);

        if (direction.len2() > 0.01f) {
            direction.nor().scl(speed * delta);
            position.add(direction);
        }

        hitbox.setPosition(position.x-sizeBox/2, position.y-sizeBox/2);

        handleCollisionWithOthers(delta, enemies);
    }

    private void handleCollisionWithOthers(float delta, ArrayList<Enemy> enemies){
        for (Enemy other : enemies) {
            if (other == this) continue;

            float distance = position.dst(other.position);
            if (distance < 52f && distance > 0f) {
                Vector2 repulsion = new Vector2(position).sub(other.position).nor().scl(180f * delta);
                position.add(repulsion);
                hitbox.setPosition(position.x-sizeBox/2, position.y-sizeBox/2);
            }
        }
    }

    public void render(SpriteBatch batch){

        float playerWidth = size;
        float playerHeight = size;

        if (direction.x > 0) {
            facingRight = true;
        } else if (direction.x < 0) {
            facingRight = false;
        }

        TextureRegion currentFrame = walkAnimation.getKeyFrame(animationTimer, true);

        if (!facingRight && !currentFrame.isFlipX()) {
            currentFrame.flip(true, false);
        } else if (facingRight && currentFrame.isFlipX()) {
            currentFrame.flip(true, false);
        }

        batch.draw(currentFrame, position.x - playerWidth / 2, position.y - playerHeight / 2, playerWidth, playerHeight);

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

    public void markDead() {
        dead = true;
    }

    public boolean isDead() {
        return dead;
    }

    public Vector2 getPosition() {
        return position;
    }

    public Rectangle getHitbox(){
        return hitbox;
    }
}
