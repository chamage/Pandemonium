package com.sierra8;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.scenes.scene2d.ui.List;

import java.util.ArrayList;

public class Player implements RenderableEntity {

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
    private boolean facingRight = true;

    private Animation<TextureRegion> walkAnimation;
    private Texture walkSheet;
    private Animation<TextureRegion> idleAnimation;
    private Texture idleSheet;
    private float animationTimer;

    private Texture qAbility;
    private Texture eAbility;

    private TeleportListener teleportListener;
    public void setTeleportListener(TeleportListener teleportListener) {
        this.teleportListener = teleportListener;
    }

    @Override
    public float getRenderY() {
        return position.y;
    }

    @Override
    public void render(SpriteBatch batch) {
        renderPlayer(batch);
    }

    public Player(float x, float y){
        this.position = new Vector2(x, y);
        this.speed = 300f;
        this.speedSprint = 400f;
        this.rotation = 0;
        this.size = DEFAULT_SIZE;
        this.sizeBox = (float)(size*.3);
        this.bullets = new ArrayList<>();
        this.hitbox = new Rectangle(position.x-sizeBox/2-4, position.y-sizeBox/2-18, sizeBox, sizeBox*1.4f);
        this.shootCooldown = 0.4f;
        this.shootTimer = 1f;
        this.maxMana = 100f;
        this.mana = maxMana;
        this.reloadSpeed = DEFAULT_RELOAD_SPEED;
        this.enemiesKilled = 0;

        this.walkSheet = new Texture("textures/walkSheet.png");
        TextureRegion[][] walkTmp = TextureRegion.split(walkSheet,
            walkSheet.getWidth() / 8,
            walkSheet.getHeight());

        TextureRegion[] walkFrames = new TextureRegion[8];

        for (int i = 0; i < 8; i++) {
            walkFrames[i] = walkTmp[0][i];
        }

        this.idleSheet = new Texture("textures/idleSheet.png");
        TextureRegion[][] idleTmp = TextureRegion.split(idleSheet,
            idleSheet.getWidth() / 6,
            idleSheet.getHeight());

        walkAnimation = new Animation<TextureRegion>(0.1f, walkFrames);

        TextureRegion[] idleFrames = new TextureRegion[6];
        for (int i = 0; i < 6; i++) {
            idleFrames[i] = idleTmp[0][i];
        }

        idleAnimation = new Animation<TextureRegion>(0.1f, idleFrames);
        this.animationTimer = 0f;

        this.maxStamina = 100f;
        this.stamina = maxStamina;
        this.specialCooldown = 2f;
        this.specialTimer = 2f;
        this.teleportCooldown = 3f;
        this.teleportTimer = 3f;

        this.eAbility = new Texture("textures/e_ability.png");
        this.qAbility = new Texture("textures/q_ability.png");

    }

    public void setPistolShootListener(PistolShootListener pistolShootListener) {
        this.pistolShootListener = pistolShootListener;
    }

    public void update(float delta, Camera camera, ArrayList<Object> worldObjects){

        animationTimer += delta;
        handleMovement(delta, worldObjects);
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

    private void handleMovement(float delta, ArrayList<Object> worldObjects){
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
            // Proposed new position
            Vector2 proposedPosition = new Vector2(position).mulAdd(direction, delta);
            Rectangle proposedHitbox = new Rectangle(hitbox); // Create a copy for checking
            // Calculate proposed hitbox position based on player's visual center and hitbox offsets
            float proposedHitboxX = proposedPosition.x - sizeBox / 2 + (facingRight ? 6 : -6); // Adjust based on facing direction if necessary
            float proposedHitboxY = proposedPosition.y - sizeBox / 2 - 18;
            proposedHitbox.setPosition(proposedHitboxX, proposedHitboxY);


            boolean collision = false;
            for (Object obj : worldObjects) {
                if (obj.isCollidable() && obj.getHitbox() != null && obj.getHitbox().overlaps(proposedHitbox)) {
                    collision = true;
                    break;
                }
            }

            if (!collision) {
                position.set(proposedPosition);
            }
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

        rotation = (angle + 360) % 360;
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

        Vector2 oldPos = new Vector2(position);
        Vector3 mouseScreen = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        Vector3 worldCoords = camera.unproject(mouseScreen);
        if (teleportListener != null) {
            teleportListener.onTeleport(oldPos);
        }
        position.set(worldCoords.x, worldCoords.y);

    }


    private void handleHitbox(){
        if (facingRight){
            hitbox.setPosition(position.x-sizeBox/2+6, position.y-sizeBox/2-18);
        }
        else {
            hitbox.setPosition(position.x-sizeBox/2-6, position.y-sizeBox/2-18);
        }
    }

    public void renderPlayer(SpriteBatch batch){
        float playerWidth = size;
        float playerHeight = size;

        if (direction.x > 0) {
            facingRight = true;
        } else if (direction.x < 0) {
            facingRight = false;
        }


        Animation<TextureRegion> currentAnimation = direction.len2() > 0 ? walkAnimation : idleAnimation;
        TextureRegion currentFrame = currentAnimation.getKeyFrame(animationTimer, true);

        if (!facingRight && !currentFrame.isFlipX()) {
            currentFrame.flip(true, false);
        } else if (facingRight && currentFrame.isFlipX()) {
            currentFrame.flip(true, false);
        }

        batch.draw(currentFrame, position.x - playerWidth / 2, position.y - playerHeight / 2, playerWidth, playerHeight);
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
        shape.setColor(new Color(0, 0, 0, 0.5f));
        shape.rect(x, y, width, height);

        float staminaPercent = stamina / maxStamina;
        shape.setColor(new Color(80/255f,205/255f,80/255f, 0.9f));
        shape.rect(x, y, width * staminaPercent, height);
    }

    public void renderManaBar(ShapeRenderer shape){
        float width = 300;
        float height = 30;
        float x = position.x + Gdx.graphics.getWidth()/2f - 6 - width;
        float y = position.y - Gdx.graphics.getHeight()/2f + 6;;
        shape.setColor(new Color(0, 0, 0, 0.5f));
        shape.rect(x, y, width, height);

        float manaPercent = mana / maxMana;
        shape.setColor(new Color(30/255f, 30/255f, 205/255f, 0.9f));
        shape.rect(x, y, width * manaPercent, height);
    }

    public void renderAbilities(SpriteBatch batch){
        //E attack
        float size = 80;
        float eX = position.x + Gdx.graphics.getWidth()/2f - 6 - size;
        float eY = position.y + Gdx.graphics.getHeight()/2f - 6 - size;
        batch.draw(eAbility, eX, eY, size, size);

        //Q teleport
        float qX = position.x + Gdx.graphics.getWidth()/2f - 6 - size;
        float qY = position.y + Gdx.graphics.getHeight()/2f - 12 - size*2;
        batch.draw(qAbility ,qX, qY, size, size);
    }

    public void renderAbilitiesUsed(ShapeRenderer shape){
        shape.setColor(new Color(0, 0, 0, .8F));
        float size = 80;
        float eX = position.x + Gdx.graphics.getWidth()/2f - 6 - size;
        float eY = position.y + Gdx.graphics.getHeight()/2f - 6 - size;

        //Q teleport
        float qX = position.x + Gdx.graphics.getWidth()/2f - 6 - size;
        float qY = position.y + Gdx.graphics.getHeight()/2f - 12 - size*2;

        float eCooldownPercent = Math.min(1f, specialTimer / specialCooldown);
        float qCooldownPercent = Math.min(1f, teleportTimer / teleportCooldown);

        float eCooldownHeight = size * (1f - eCooldownPercent);
        float qCooldownHeight = size * (1f - qCooldownPercent);

        if (eCooldownPercent < 1f) {
            shape.rect(eX, eY + eCooldownHeight, size, size - eCooldownHeight);
        }
        if (qCooldownPercent < 1f) {
            shape.rect(qX, qY + qCooldownHeight, size, size - qCooldownHeight);
        }

    }

    public void renderBoxes(ShapeRenderer shape){
        shape.setColor(75, 60, 255, .4f);
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
