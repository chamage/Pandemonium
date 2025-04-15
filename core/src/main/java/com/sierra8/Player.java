package com.sierra8;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.*;

import java.util.ArrayList;

public class Player {

    private static final float DEFAULT_SIZE = 140f;
    private static final float DEFAULT_RELOAD_SPEED = 3f;

    private final Vector2 position;
    private final float speed;
    private final float speedSprint;
    private float rotation;
    private final float size;
    private final float sizeBox;
    private final ArrayList<Bullet> bullets;
    private final Rectangle hitbox;
    private int enemiesKilled;
    private final float reloadSpeed;
    private PistolShootListener pistolShootListener;

    private final float maxMana;
    private float mana;

    private final float maxStamina;
    private float stamina;

    private final float shootCooldown;
    private float shootTimer;

    private final float specialCooldown;
    private float specialTimer;

    private final float teleportCooldown;
    private float teleportTimer;

    private final Vector2 direction = new Vector2();
    private final Vector3 mousePos = new Vector3();
    private final Texture playerTexture;
    private boolean facingRight = true;


    public Player(float x, float y){
        this.position = new Vector2(x, y);
        this.speed = 300f;
        this.speedSprint = 400f;
        this.rotation = 0;
        this.size = DEFAULT_SIZE;
        this.sizeBox = (float)(size*.3);
        this.bullets = new ArrayList<>();
        this.hitbox = new Rectangle(position.x-sizeBox/2, position.y-sizeBox/2, sizeBox, sizeBox);
        this.shootCooldown = 0.4f;
        this.shootTimer = 1f;
        this.maxMana = 100f;
        this.mana = maxMana;
        this.reloadSpeed = DEFAULT_RELOAD_SPEED;
        this.enemiesKilled = 0;
        this.playerTexture = new Texture("textures/one.PNG");
        this.maxStamina = 100f;
        this.stamina = maxStamina;
        this.specialCooldown = 2f;
        this.specialTimer = 2f;
        this.teleportCooldown = 3f;
        this.teleportTimer = 3f;

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
        float currentSpeed;

        if (Gdx.input.isKeyPressed(Input.Keys.W)) direction.y += 1;
        if (Gdx.input.isKeyPressed(Input.Keys.S)) direction.y -= 1;
        if (Gdx.input.isKeyPressed(Input.Keys.A)) direction.x -= 1;
        if (Gdx.input.isKeyPressed(Input.Keys.D)) direction.x += 1;

        if(direction.len2() > 0) {
            if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) && stamina > 0f){
                currentSpeed = speedSprint;
                stamina -= 12f * delta;
                if (stamina < 0f) stamina = 0f;
            }
            else{
                currentSpeed = speed;
            }
            direction.nor().scl(currentSpeed);
            position.mulAdd(direction, delta);
        }

        if(stamina < maxStamina && !Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || direction.len2() <= 0f){
            stamina += 10f * delta;
            if (stamina > maxStamina) stamina = maxStamina;
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
        specialTimer += delta;
        teleportTimer += delta;

        //normal attack
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && shootTimer >= shootCooldown) {
            if (mana >= 2f) {
                shootBullet();
                shootTimer = 0;
            }
        }

        //special attack (aoe type of)
        if (Gdx.input.isKeyJustPressed(Input.Keys.E) && specialTimer >= specialCooldown) {
            if (mana >= 10f) {
                specialAttack(18);
                specialTimer = 0;
            }
        }

        //teleport spell
        if (Gdx.input.isKeyJustPressed(Input.Keys.Q) && teleportTimer >= teleportCooldown) {
            if (mana >= 15f) {
                teleportAction(camera);
                teleportTimer = 0;
            }
        }

        if(mana < maxMana){
            mana +=  2f * delta;
            if (mana > maxMana) mana = maxMana;
        }
    }

    private void shootBullet(){
        mana -= 2f;
        float bulletSpeed = 800f;
        float bulletX = position.x + MathUtils.cosDeg(rotation) * sizeBox;
        float bulletY = position.y + MathUtils.sinDeg(rotation) * sizeBox;

        Vector2 direction = new Vector2(bulletX, bulletY).sub(position).nor();

        Bullet bullet = new Bullet(new Vector2(bulletX, bulletY), direction, bulletSpeed);
        bullets.add(bullet);
        if (pistolShootListener != null) {
            pistolShootListener.onPistolShot();
        }
    }

    public void specialAttack(int bulletCount) {
        mana -= 10f;

        float bulletSpeed = 600f;
        float angleStep = 360f / bulletCount;

        for (int i = 0; i < bulletCount; i++) {
            float angle = i * angleStep;
            float bulletX = position.x + MathUtils.cosDeg(angle) * sizeBox;
            float bulletY = position.y + MathUtils.sinDeg(angle) * sizeBox;

            Vector2 direction = new Vector2(MathUtils.cosDeg(angle), MathUtils.sinDeg(angle)).nor();
            Bullet bullet = new Bullet(new Vector2(bulletX, bulletY), direction, bulletSpeed);
            bullets.add(bullet);
        }

        if (pistolShootListener != null) {
            pistolShootListener.onPistolShot();
        }
    }

    public void teleportAction(Camera camera) {
        mana -= 15f;

        Vector3 mouseScreen = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);

        Vector3 worldCoords = camera.unproject(mouseScreen);

        position.set(worldCoords.x, worldCoords.y);

    }


    private void handleHitbox(){
        hitbox.setPosition(position.x-sizeBox/2, position.y-sizeBox/2);
    }

    public void renderPlayer(SpriteBatch batch){
        float playerWidth = size;
        float playerHeight = size;

        if (direction.x > 0) {
            facingRight = true;
        } else if (direction.x < 0) {
            facingRight = false;
        }

        if (facingRight) {
            batch.draw(playerTexture, position.x - playerWidth / 2 - 6, position.y - playerHeight / 2 + 10, playerWidth, playerHeight);
        } else {
            batch.draw(playerTexture, position.x + playerWidth / 2 + 6, position.y - playerHeight / 2 + 10, -playerWidth, playerHeight);
        }

    }

    public void renderBullets(ShapeRenderer shape){

        shape.setColor(Color.RED);
        for (Bullet bullet : bullets){
            bullet.render(shape);
        }
    }

    public void renderStaminaBar(ShapeRenderer shape){
        float x = position.x - Gdx.graphics.getWidth()/2f + 6;
        float y = position.y - Gdx.graphics.getHeight()/2f + 6;;
        float width = 300;
        float height = 30;
        shape.setColor(Color.DARK_GRAY);
        shape.rect(x, y, width, height);

        float staminaPercent = stamina / maxStamina;
        shape.setColor(Color.LIME);
        shape.rect(x, y, width * staminaPercent, height);
    }

    public void renderManaBar(ShapeRenderer shape){
        float width = 300;
        float height = 30;
        float x = position.x + Gdx.graphics.getWidth()/2f - 6 - width;
        float y = position.y - Gdx.graphics.getHeight()/2f + 6;;
        shape.setColor(Color.DARK_GRAY);
        shape.rect(x, y, width, height);

        float manaPercent = mana / maxMana;
        shape.setColor(Color.ROYAL);
        shape.rect(x, y, width * manaPercent, height);
    }

    public void renderAbilities(ShapeRenderer shape){
        //E attack
        float size = 50;
        float eX = position.x + Gdx.graphics.getWidth()/2f - 6 - size;
        float eY = position.y + Gdx.graphics.getHeight()/2f - 6 - size;
        shape.setColor(Color.DARK_GRAY);
        shape.rect(eX, eY, size, size);

        //Q teleport
        float qX = position.x + Gdx.graphics.getWidth()/2f - 6 - size;
        float qY = position.y + Gdx.graphics.getHeight()/2f - 12 - size*2;
        shape.setColor(Color.DARK_GRAY);
        shape.rect(qX, qY, size, size);

        float eCooldownPercent = Math.min(1f, specialTimer / specialCooldown);
        float qCooldownPercent = Math.min(1f, teleportTimer / teleportCooldown);

        float eCooldownHeight = size * (1f - eCooldownPercent);
        float qCooldownHeight = size * (1f - qCooldownPercent);

        shape.setColor(Color.BLACK);
        if (eCooldownPercent < 1f) {
            shape.rect(eX, eY + eCooldownHeight, size, size - eCooldownHeight);
        }
        if (qCooldownPercent < 1f) {
            shape.rect(qX, qY + qCooldownHeight, size, size - qCooldownHeight);
        }

    }

    public void renderBoxes(ShapeRenderer shape){
        shape.setColor(75, 60, 255, 1f);
        shape.rect(hitbox.x, hitbox.y, hitbox.width, hitbox.height);
    }

    public Vector2 getPosition(){
        return position.cpy();
    }

    public ArrayList<Bullet> getBullets(){
        return bullets;
    }


    public Rectangle getHitbox(){
        return hitbox;
    }

    public int getEnemiesKilled(){
        return enemiesKilled;
    }
    public void enemyKilled(){
        enemiesKilled++;
    }
    public float getCurrentMana(){return mana; }
    public float getCurrentStamina(){return stamina; }

}
