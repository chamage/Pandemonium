package com.sierra8;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public interface RenderableEntity {
    float getRenderY();
    void render(SpriteBatch batch);
    void renderShadow(SpriteBatch batch, Texture shadowTexture);
}
