package com.sierra8;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

public class ObjectManager {
    private static ArrayList<Object> objects;
    private final float spawnCooldown;
    private float spawnTimer;
    private final int maxObjects;
    private final float minSpawn = 10f;

    public ObjectManager(float spawnCooldown, int maxObjects){
        objects = new ArrayList<>();
        this.spawnCooldown = spawnCooldown;
        this.spawnTimer = 0;
        this.maxObjects = maxObjects;
    }


    public void update(float delta, Player player){
        spawnTimer += delta;

        Vector2 playerPos = player.getPosition();

        // Despawn objects that are far away
        float despawnDistanceX = Gdx.graphics.getWidth() * 1.5f;
        float despawnDistanceY = Gdx.graphics.getHeight() * 1.5f;

        objects.removeIf(obj -> {
            Vector2 pos = obj.getPosition();
            return Math.abs(pos.x - playerPos.x) > despawnDistanceX ||
                Math.abs(pos.y - playerPos.y) > despawnDistanceY;
        });

        // Spawn new objects
        if (spawnTimer >= spawnCooldown && objects.size() < maxObjects) {
            spawnObject(playerPos);
            spawnTimer = 0;
        }
    }

    private void spawnObject(Vector2 playerPosition){
        final float spawnDistanceX = Gdx.graphics.getWidth();
        final float spawnDistanceY = Gdx.graphics.getHeight();
        float x, y;
        boolean positionFound = false;
        int maxAttempts = 10; // Limit attempts to find a non-overlapping position
        Rectangle newObjectHitbox = new Rectangle();
        // Assuming object size is 100f as defined in Object.java
        float objectSize = 100f;
        newObjectHitbox.setSize(objectSize);


        for (int attempts = 0; attempts < maxAttempts && !positionFound; attempts++) {
            float angle = MathUtils.random(360f);
            x = playerPosition.x + MathUtils.cosDeg(angle) * spawnDistanceX;
            y = playerPosition.y + MathUtils.sinDeg(angle) * spawnDistanceY;
            newObjectHitbox.setCenter(x,y);

            positionFound = true; // Assume position is valid until proven otherwise
            for (Object existingObject : objects) {
                // Add a small buffer (minSpawnAbstand) to the check
                if (newObjectHitbox.overlaps(existingObject.getHitbox())) {
                    positionFound = false;
                    break;
                }
            }

            if (positionFound) {
                objects.add(new Object(x, y));
            }
        }
    }

    public void render(SpriteBatch batch){
        for (Object object : objects){
            object.render(batch);
        }
    }

    public void renderBoxes(ShapeRenderer shape){
        for (Object object : objects){
            object.renderBoxes(shape);
        }
    }

    static public ArrayList<Object> getObjects(){
        return objects;
    }
}
