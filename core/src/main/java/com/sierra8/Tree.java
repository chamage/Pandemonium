package com.sierra8;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import java.util.ArrayList;

public class Tree extends Object {
    private final float trunkWidth;
    private final float trunkHeight;
    private final float visualWidth;
    private final float visualHeight;

    public Tree(float x, float y) {
        super(x, y, 250f); // Visual size for a tree
        this.visualWidth = this.size;
        this.visualHeight = this.size * 1.5f; // Assuming tree is taller than it is wide

        this.trunkWidth = this.size * 0.1f; // Trunk is 10% of the visual width
        this.trunkHeight = this.size * 0.6f; // Trunk height
        // Position the hitbox at the base of the visual tree
        this.hitbox = new Rectangle(
            position.x - trunkWidth / 2,
            position.y - visualHeight / 2, // Align bottom of trunk with bottom of visual
            trunkWidth,
            trunkHeight
        );
        this.texture = loadTexture("textures/tree.png"); // Placeholder: you'll need a tree texture
    }

    @Override
    public void render(SpriteBatch batch) {
        if (texture != null) {
            // Render the tree visually centered, hitbox is at its base
            batch.draw(texture, position.x - visualWidth / 2, position.y - visualHeight / 2, visualWidth, visualHeight);
        }
    }

    @Override
    public void renderBoxes(ShapeRenderer shape) {
        if (hitbox != null) {
            shape.setColor(0/255f, 100/255f, 0/255f, 1); // Dark green for tree trunk hitbox
            shape.rect(hitbox.x, hitbox.y, hitbox.width, hitbox.height);
        }
    }
}
