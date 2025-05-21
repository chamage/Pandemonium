package com.sierra8;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.Iterator;

public class ObjectManager {
    private static ArrayList<Object> objects;
    private final float spawnCooldown;
    private float spawnTimer;
    private final int maxObjects;

    public ObjectManager(int maxObjects) {
        objects = new ArrayList<>();
        this.spawnCooldown = 0;
        this.spawnTimer = 0;
        this.maxObjects = maxObjects;
    }

    public void update(float delta, Player player) {
        spawnTimer += delta;

        Vector2 playerPos = player.getPosition();

        float despawnDistanceX = Gdx.graphics.getWidth() * 1.5f;
        float despawnDistanceY = Gdx.graphics.getHeight() * 1.5f;

        Iterator<Object> iterator = objects.iterator();
        while (iterator.hasNext()) {
            Object obj = iterator.next();
            Vector2 pos = obj.getPosition();
            if (Math.abs(pos.x - playerPos.x) > despawnDistanceX ||
                Math.abs(pos.y - playerPos.y) > despawnDistanceY) {
                obj.dispose();
                iterator.remove();
            }
            // Removed call to obj.update here:
            // else {
            //    obj.update(delta, playerPos, null);
            // }
        }

        if (spawnTimer >= spawnCooldown && objects.size() < maxObjects) {
            spawnObject(playerPos);
            spawnTimer = 0;
        }
    }

    private void spawnObject(Vector2 playerPosition) {
        final float spawnDistanceX = Gdx.graphics.getWidth();
        final float spawnDistanceY = Gdx.graphics.getHeight();
        float x, y;
        boolean positionFound = false;
        int maxAttempts = 10;
        Rectangle newObjectHitbox = new Rectangle();


        for (int attempts = 0; attempts < maxAttempts && !positionFound; attempts++) {
            float angle = MathUtils.random(360f);
            x = playerPosition.x + MathUtils.cosDeg(angle) * spawnDistanceX;
            y = playerPosition.y + MathUtils.sinDeg(angle) * spawnDistanceY;

            float tempObjectSizeForSpacing = 100f; // Using Box size for general spacing
            newObjectHitbox.setSize(tempObjectSizeForSpacing, tempObjectSizeForSpacing);
            newObjectHitbox.setCenter(x,y);

            positionFound = true;
            for (Object existingObject : objects) {
                if (existingObject.getHitbox() != null && newObjectHitbox.overlaps(existingObject.getHitbox())) {
                    positionFound = false;
                    break;
                }
            }

            if (positionFound) {
                int objectType = MathUtils.random(0, 2);
                Object newObj = null;
                switch (objectType) {
                    case 0:
                        newObj = new Box(x, y);
                        break;
                    case 1:
                        newObj = new Tree(x, y);
                        break;
                    case 2:
                        newObj = new Bush(x, y);
                        break;
                }
                if (newObj != null) {
                    if (newObj.getHitbox() == null) { // Bush
                        objects.add(newObj);
                    } else { // Box or Tree, re-check with their actual hitbox
                        boolean finalCollisionCheck = false;
                        for (Object existingObject : objects) {
                            if (existingObject.getHitbox() != null && newObj.getHitbox().overlaps(existingObject.getHitbox())) {
                                finalCollisionCheck = true;
                                break;
                            }
                        }
                        if (!finalCollisionCheck) {
                            objects.add(newObj);
                        } else {
                            positionFound = false; // Mark as not found to allow retry if possible
                            newObj.dispose();
                        }
                    }
                }
            }
        }
    }

    public void render(SpriteBatch batch) {
        for (Object object : objects) {
            object.render(batch);
        }
    }

    public void renderBoxes(ShapeRenderer shape) {
        for (Object object : objects) {
            object.renderBoxes(shape);
        }
    }

    static public ArrayList<Object> getObjects() {
        return objects;
    }

    public void disposeAll() {
        for (Object obj : objects) {
            obj.dispose();
        }
        objects.clear();
    }
}
