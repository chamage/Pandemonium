package com.sierra8;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import java.util.ArrayList;

public class Player {

    private static final float DEFAULT_SIZE = 30f;
    private static final float DEFAULT_RELOAD_SPEED = 3f;

    private Vector2 position;
    private float speed;
    private float speedSprint;
    private float rotation;
    private float size;
    private ArrayList<Bullet> bullets;
    private Circle hitbox;
    private float shootCooldown;
    private float shootTimer;
    private int enemiesKilled;
    private int magSize, currentMag;
    private float reloadSpeed;
    private PistolShootListener pistolShootListener;

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
        this.magSize = 10;
        this.currentMag = magSize;
        this.reloadSpeed = DEFAULT_RELOAD_SPEED;
        this.enemiesKilled = 0;
    }

    public void setPistolShootListener(PistolShootListener pistolShootListener) {
        this.pistolShootListener = pistolShootListener;
    }

    public void update(float delta, Camera camera){

        handleMovement(delta);
        handleRotation(camera);
        handleShooting(delta);
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
        float moveSpeed = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) ? speedSprint : speed;


        if (Gdx.input.isKeyPressed(Input.Keys.W)){
            position.y += moveSpeed * delta;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)){
            position.y -= moveSpeed * delta;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)){
            position.x += moveSpeed * delta;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)){
            position.x -= moveSpeed * delta;
        }
    }

    private void handleRotation(Camera camera){
        Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(mousePos);
        Vector2 direction = new Vector2(mousePos.x - position.x, mousePos.y - position.y);
        rotation = direction.angleDeg();
    }

    private void handleShooting(float delta){
        shootTimer += delta;

        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && shootTimer >= shootCooldown) {
            if (currentMag > 0) {
                shootBullet();
                shootTimer = 0;
            }
        }

        if (Gdx.input.isKeyPressed(Input.Keys.R)){
            reload();
        }
    }

    private void shootBullet(){
        Vector2 bulletPostion = position.cpy();
        Vector2 bulletDirection = new Vector2(MathUtils.cosDeg(rotation), MathUtils.sinDeg(rotation)).nor();
        bullets.add(new Bullet(bulletPostion, bulletDirection, 500f));
        currentMag--;
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

    public void render(ShapeRenderer shape, Camera camera){

        float tipX   = position.x + MathUtils.cosDeg(rotation) * size;
        float tipY   = position.y + MathUtils.sinDeg(rotation) * size;
        float leftX  = position.x + MathUtils.cosDeg(rotation + 135) * size;
        float leftY  = position.y + MathUtils.sinDeg(rotation + 135) * size;
        float rightX = position.x + MathUtils.cosDeg(rotation - 135) * size;
        float rightY = position.y + MathUtils.sinDeg(rotation - 135) * size;

        shape.setColor(Color.BLUE);
        shape.triangle(tipX, tipY, leftX, leftY, rightX, rightY);

        shape.setColor(Color.GREEN);
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
