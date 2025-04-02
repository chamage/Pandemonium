package com.sierra8;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import java.util.ArrayList;

public class Player {

    private static final float DEFAULT_SIZE = 40f;
    private static final float DEFAULT_RELOAD_SPEED = 3f;

    private final Vector2 position;
    private final float speed;
    private final float speedSprint;
    private float rotation;
    private final float size;
    private final ArrayList<Bullet> bullets;
    private final Circle hitbox;
    private final float shootCooldown;
    private float shootTimer;
    private int enemiesKilled;
    private final int magSize;
    private int currentMag;
    private final float reloadSpeed;
    private PistolShootListener pistolShootListener;

    private final Vector2 direction = new Vector2();
    private final Vector3 mousePos = new Vector3();
    private final Texture playerTexture;

    public Player(float x, float y){
        this.position = new Vector2(x, y);
        this.speed = 300f;
        this.speedSprint = 400f;
        this.rotation = 0;
        this.size = DEFAULT_SIZE;
        this.bullets = new ArrayList<>();
        this.hitbox = new Circle(position, size*.6f);
        this.shootCooldown = 0.6f;
        this.shootTimer = 1f;
        this.magSize = 12;
        this.currentMag = magSize;
        this.reloadSpeed = DEFAULT_RELOAD_SPEED;
        this.enemiesKilled = 0;
        this.playerTexture = new Texture("textures/one.PNG");
    }

    public void setPistolShootListener(PistolShootListener pistolShootListener) {
        this.pistolShootListener = pistolShootListener;
    }

    public void update(float delta, Camera camera){

        handleMovement(delta);
        handleRotation(camera);
        handleShooting(delta, camera);
        handleHitbox();


        for (int i = bullets.size() - 1; i >= 0; i--) {
            Bullet bullet = bullets.get(i);
            bullet.update(delta);
            if (bullet.isOutOfRange()) {
                bullets.remove(i);
            }
        }
    }

    private void handleMovement(float delta){
        direction.setZero();

        if (Gdx.input.isKeyPressed(Input.Keys.W)) direction.y += 1;
        if (Gdx.input.isKeyPressed(Input.Keys.S)) direction.y -= 1;
        if (Gdx.input.isKeyPressed(Input.Keys.A)) direction.x -= 1;
        if (Gdx.input.isKeyPressed(Input.Keys.D)) direction.x += 1;

        if(direction.len2() > 0) {
            direction.nor().scl(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) ? speedSprint : speed);
            position.mulAdd(direction, delta);
        }

    }

    private void handleRotation(Camera camera){
        mousePos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(mousePos);
        float angle = MathUtils.atan2(mousePos.y - position.y, mousePos.x - position.x) * MathUtils.radiansToDegrees;

        angle = (angle + 360) % 360;

        rotation = angle;
    }

    private void handleShooting(float delta, Camera camera){
        shootTimer += delta;

        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && shootTimer >= shootCooldown) {
            if (currentMag > 0) {
                shootBullet(camera);
                shootTimer = 0;
            }
        }

        if (Gdx.input.isKeyPressed(Input.Keys.R) && currentMag < magSize){
            reload();
        }
    }

    private void shootBullet(Camera camera){
        currentMag--;
        float bulletSpeed = 800f;
        float bulletX = position.x + MathUtils.cosDeg(rotation) * size;
        float bulletY = position.y + MathUtils.sinDeg(rotation) * size;

        Vector2 direction = new Vector2(bulletX, bulletY).sub(position).nor();


        Bullet bullet = new Bullet(new Vector2(bulletX, bulletY), direction, bulletSpeed);
        bullets.add(bullet);
        if (pistolShootListener != null) {
            pistolShootListener.onPistolShot();
        }

    }

    private void reload(){
        currentMag = magSize;
    }

    private void handleHitbox(){
        hitbox.setPosition(position);
    }

    public void renderPlayer(SpriteBatch batch){
        float playerWidth = 140;
        float playerHeight = 140;
        if (direction.x>0){
            batch.draw(playerTexture, position.x - playerWidth/2 - 6, position.y - playerHeight/2 + 10, playerWidth, playerHeight);
        }
        else {
            batch.draw(playerTexture, position.x + playerWidth/2 + 6, position.y - playerHeight/2 + 10, -playerWidth, playerHeight);
        }
    }

    public void renderBullets(ShapeRenderer shape){

        shape.setColor(Color.RED);
        for (Bullet bullet : bullets){
            bullet.render(shape);
        }
    }

    public Vector2 getPosition(){
        return position.cpy();
    }

    public ArrayList<Bullet> getBullets(){
        return bullets;
    }


    public Circle getHitbox(){
        return hitbox;
    }

    public int getEnemiesKilled(){
        return enemiesKilled;
    }
    public void enemyKilled(){
        enemiesKilled++;
    }
    public int getCurrentMag(){return currentMag; }

}
