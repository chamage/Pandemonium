package com.sierra8;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

public class Enemy implements RenderableEntity {

    // State machine
    private enum EnemyState {
        WANDERING,
        CHASING,
        RETREATING,
        FORMING_GROUP
    }

    // Entity data
    private final Vector2 position;
    private final Vector2 currentVelocity;
    private final float baseSpeed;
    private float currentSpeed;
    private final float size;
    private final float sizeBox;
    private final Rectangle hitbox;
    private boolean dead;
    private boolean facingRight = true;

    // Entity animation
    private Animation<TextureRegion> walkAnimation;
    private Texture walkSheet;
    private float animationTimer;

    // Entity state
    private EnemyState currentState;
    private final Vector2 wanderTargetPosition;
    private float stateTimer;

    // Wandering
    private final float wanderRadius = 200f;
    private final float maxWanderTime = 5f;
    private final float minWanderTime = 2f;

    // Engagement Radius
    private final float detectionRadius = 700f;
    private final float aggroRadius = 450f;
    private final float attackRange = 70f;
    private final float deAggroRadius = 1000f;
    private final float contactAggroRadius = 70f;

    // Weaving (in CHASING state)
    private final float timeToNextWeave = 1.0f;
    private float weaveFactor;
    private final float maxWeaveFactor = 1.0f;
    private final float weaveInfluence = 0.3f;

    // Retreating
    private static final float RETREAT_DURATION = 1.5f;
    private static final float POST_RETREAT_COOLDOWN = 2.0f;
    private float postRetreatCooldownTimer;

    // Grouping
    private final Vector2 rallyPoint;
    private static final float GROUPING_TIMEOUT = 8.0f;
    private static final int GROUP_SIZE_THRESHOLD = 2;
    private static final float ALLY_SCAN_RADIUS = 300f;
    private static final float GROUPING_RALLY_RADIUS = 100f;

    // Attacking / Lunging
    private boolean isCommittingAttack = false;
    private float lungeTimer = 0f;
    private static final float LUNGE_DURATION = 0.35f;
    private static final float LUNGE_SPEED_MULTIPLIER = 1.9f;
    private static final float COMMIT_ATTACK_CHANCE = 0.8f;

    @Override
    public float getRenderY() {
        return position.y;
    }

    public Enemy(float x, float y, float speed) {
        this.position = new Vector2(x, y);
        this.currentVelocity = new Vector2();
        this.baseSpeed = speed;
        this.currentSpeed = speed;
        this.size = 480f;
        this.sizeBox = (float) (140 * .3);
        this.dead = false;
        this.hitbox = new Rectangle(x - sizeBox / 2, y - sizeBox / 2, sizeBox, (float) (sizeBox * 1.5));

        this.walkSheet = new Texture("textures/enemySheet.png");
        TextureRegion[][] walkTmp = TextureRegion.split(walkSheet,
            walkSheet.getWidth() / 8,
            walkSheet.getHeight());
        TextureRegion[] walkFrames = new TextureRegion[8];
        for (int i = 0; i < 8; i++) {
            walkFrames[i] = walkTmp[0][i];
        }
        this.walkAnimation = new Animation<>(0.1f, walkFrames);
        this.animationTimer = 0f;

        this.currentState = EnemyState.WANDERING;
        this.stateTimer = MathUtils.random(minWanderTime, maxWanderTime);

        this.wanderTargetPosition = new Vector2();
        setNewWanderTarget();

        this.weaveFactor = 0f;
        this.postRetreatCooldownTimer = 0f;
        this.rallyPoint = new Vector2();
    }

    public void update(float delta, Vector2 playerPosition, ArrayList<Enemy> otherEnemies, List<Object> worldObjects) {
        if (dead) return;
        animationTimer += delta;
        stateTimer -= delta;

        float distanceToPlayer = position.dst(playerPosition);
        Vector2 targetDirection = new Vector2();
        this.currentSpeed = this.baseSpeed;

        switch (currentState) {
            case WANDERING:
                currentSpeed = baseSpeed * 0.8f;
                if (distanceToPlayer < detectionRadius) {
                    if (MathUtils.randomBoolean(0.4f) && GROUP_SIZE_THRESHOLD > 0) {
                        currentState = EnemyState.FORMING_GROUP;
                        stateTimer = GROUPING_TIMEOUT;
                        rallyPoint.set(position).lerp(playerPosition, 0.3f);
                    } else {
                        currentState = EnemyState.CHASING;
                        stateTimer = MathUtils.random(0.5f * timeToNextWeave, 1.2f * timeToNextWeave);
                    }
                } else {
                    if (position.dst(wanderTargetPosition) < 20f || stateTimer <= 0) {
                        setNewWanderTarget();
                        stateTimer = MathUtils.random(minWanderTime, maxWanderTime);
                    }
                    if (wanderTargetPosition.dst2(position) > 1f) {
                        targetDirection.set(wanderTargetPosition).sub(position).nor();
                    } else {
                        targetDirection.set(0, 0);
                    }
                }
                break;

            case FORMING_GROUP:
                currentSpeed = baseSpeed * 0.6f;
                if (distanceToPlayer > detectionRadius * 1.2f || stateTimer <= 0) {
                    currentState = EnemyState.WANDERING;
                    setNewWanderTarget();
                    stateTimer = MathUtils.random(minWanderTime, maxWanderTime);
                    break;
                }
                if (distanceToPlayer < aggroRadius) {
                    currentState = EnemyState.CHASING;
                    stateTimer = MathUtils.random(0.5f * timeToNextWeave, 1.2f * timeToNextWeave);
                    break;
                }

                int alliesNearby = 0;
                for (Enemy other : otherEnemies) {
                    if (other == this || other.isDead()) continue;
                    if (other.currentState == EnemyState.FORMING_GROUP || other.currentState == EnemyState.CHASING) {
                        if (position.dst(other.position) < ALLY_SCAN_RADIUS) alliesNearby++;
                    }
                }

                if (alliesNearby >= GROUP_SIZE_THRESHOLD) {
                    currentState = EnemyState.CHASING;
                    stateTimer = MathUtils.random(0.5f * timeToNextWeave, 1.2f * timeToNextWeave);
                } else {
                    if (position.dst2(rallyPoint) > GROUPING_RALLY_RADIUS * GROUPING_RALLY_RADIUS) {
                        targetDirection.set(rallyPoint).sub(position).nor();
                    } else {
                        targetDirection.set(0, 0);
                    }
                }
                break;

            case CHASING:
                if (isCommittingAttack) {
                    currentSpeed = baseSpeed * LUNGE_SPEED_MULTIPLIER;
                    targetDirection.set(playerPosition).sub(position).nor();
                    lungeTimer -= delta;
                    if (lungeTimer <= 0) {
                        isCommittingAttack = false;
                        currentState = EnemyState.RETREATING;
                        stateTimer = RETREAT_DURATION;
                        postRetreatCooldownTimer = POST_RETREAT_COOLDOWN;
                    }
                } else {
                    currentSpeed = baseSpeed * 1.15f;
                    if (postRetreatCooldownTimer > 0) {
                        postRetreatCooldownTimer -= delta;
                        currentSpeed = baseSpeed * 0.9f;
                        if (postRetreatCooldownTimer <= 0 && distanceToPlayer > aggroRadius) {
                            currentState = EnemyState.WANDERING;
                            setNewWanderTarget();
                            stateTimer = MathUtils.random(minWanderTime, maxWanderTime);
                            break;
                        }
                    }

                    if (postRetreatCooldownTimer <= 0 && distanceToPlayer < attackRange) {
                        if (MathUtils.random() < COMMIT_ATTACK_CHANCE) {
                            isCommittingAttack = true;
                            lungeTimer = LUNGE_DURATION;
                            weaveFactor = 0f;
                        } else {
                            currentState = EnemyState.RETREATING;
                            stateTimer = RETREAT_DURATION;
                            postRetreatCooldownTimer = POST_RETREAT_COOLDOWN;
                            break;
                        }
                    }

                    if (!isCommittingAttack && distanceToPlayer > deAggroRadius && distanceToPlayer > contactAggroRadius) {
                        currentState = EnemyState.WANDERING;
                        setNewWanderTarget();
                        stateTimer = MathUtils.random(minWanderTime, maxWanderTime);
                        break;
                    }

                    if (!isCommittingAttack && postRetreatCooldownTimer <= 0) {
                        if (stateTimer <= 0) {
                            weaveFactor = MathUtils.random(-maxWeaveFactor, maxWeaveFactor);
                            stateTimer = MathUtils.random(0.5f * timeToNextWeave, 1.2f * timeToNextWeave);
                        }
                        Vector2 directionToPlayer = new Vector2(playerPosition).sub(position);
                        if (directionToPlayer.len2() > 0.001f) {
                            Vector2 perpendicularToPlayer = new Vector2(-directionToPlayer.y, directionToPlayer.x).nor();
                            targetDirection.set(directionToPlayer.nor())
                                .mulAdd(perpendicularToPlayer, weaveFactor * weaveInfluence)
                                .nor();
                            if (distanceToPlayer < contactAggroRadius * 1.5f) {
                                targetDirection.set(directionToPlayer.nor());
                            }
                        } else {
                            targetDirection.set(0,0);
                        }
                    } else if (!isCommittingAttack) {
                        targetDirection.set(playerPosition).sub(position).nor();
                    }
                }
                break;

            case RETREATING:
                currentSpeed = baseSpeed * 1.0f;
                targetDirection.set(position).sub(playerPosition).nor();

                if (stateTimer <= 0 || distanceToPlayer > deAggroRadius * 0.8f) {
                    currentState = EnemyState.CHASING;
                    stateTimer = MathUtils.random(0.5f * timeToNextWeave, 1.2f * timeToNextWeave);
                }
                break;
        }

        if (targetDirection.len2() > 0.001f) {
            currentVelocity.set(targetDirection).scl(currentSpeed * delta);
        } else {
            currentVelocity.set(0, 0);
        }

        applyMovement(delta, worldObjects, otherEnemies);

        if(currentVelocity.len2() > 0.001f){
            if (currentVelocity.x > 0.001f) facingRight = true;
            else if (currentVelocity.x < -0.001f) facingRight = false;
        } else if (targetDirection.len2() > 0.001f && !isCommittingAttack) {
            if (targetDirection.x > 0.01f) facingRight = true;
            else if (targetDirection.x < -0.01f) facingRight = false;
        } else if (isCommittingAttack) {
            Vector2 dirToPlayer = new Vector2(playerPosition).sub(position);
            if (dirToPlayer.x > 0.001f) facingRight = true;
            else if (dirToPlayer.x < -0.001f) facingRight = false;
        }
    }

    private void setNewWanderTarget() {
        float angle = MathUtils.random(360f);
        float distance = MathUtils.random(wanderRadius * 0.3f, wanderRadius);
        wanderTargetPosition.set(position.x + MathUtils.cosDeg(angle) * distance,
            position.y + MathUtils.sinDeg(angle) * distance);
    }

    private void applyMovement(float delta, List<Object> worldObjects, ArrayList<Enemy> otherEnemies) {
        float moveX = currentVelocity.x;
        float moveY = currentVelocity.y;

        Rectangle tempHitbox = new Rectangle(hitbox);

        tempHitbox.x += moveX;
        tempHitbox.y = hitbox.y;
        if (worldObjects != null) {
            for (Object obj : worldObjects) {
                if (obj.getHitbox().overlaps(tempHitbox)) {
                    moveX = 0;
                    break;
                }
            }
        }
        position.x += moveX;

        tempHitbox.x = position.x - hitbox.width / 2;
        tempHitbox.y = (position.y - hitbox.height / 2) + moveY;
        if (worldObjects != null) {
            for (Object obj : worldObjects) {
                if (obj.getHitbox().overlaps(tempHitbox)) {
                    moveY = 0;
                    break;
                }
            }
        }
        position.y += moveY;

        hitbox.setCenter(position.x, position.y);

        handleCollisionWithOthers(delta, otherEnemies);
    }


    private void handleCollisionWithOthers(float delta, ArrayList<Enemy> enemies) {
        for (Enemy other : enemies) {
            if (other == this || other.isDead()) continue;
            if (this.hitbox.overlaps(other.getHitbox())) {
                Vector2 repulsion = new Vector2(position).sub(other.position).nor().scl(baseSpeed * delta * 0.5f);
                position.add(repulsion);

                Vector2 otherRepulsion = new Vector2(other.position).sub(position).nor().scl(other.baseSpeed * delta * 0.5f);
                other.position.add(otherRepulsion);
            }
        }
        hitbox.setCenter(position.x, position.y);
    }

    @Override
    public void render(SpriteBatch batch) {
        if (dead) return;
        float visualWidth = size;
        float visualHeight = size;
        TextureRegion currentFrame = walkAnimation.getKeyFrame(animationTimer, true);
        if (!facingRight && !currentFrame.isFlipX()) {
            currentFrame.flip(true, false);
        } else if (facingRight && currentFrame.isFlipX()) {
            currentFrame.flip(true, false);
        }
        batch.draw(currentFrame, position.x - visualWidth / 2, position.y - visualHeight / 2, visualWidth, visualHeight);
    }

    public void renderBoxes(ShapeRenderer shape) {
        if (dead) return;
        shape.setColor(75/255f, 60/255f, 255/255f, 1);
        shape.rect(hitbox.x, hitbox.y, hitbox.width, hitbox.height);
    }

    public boolean collidesWith(Bullet bullet) {
        return !dead && Intersector.overlaps(bullet.getHitbox(), hitbox);
    }

    public boolean collidesWith(Player player) {
        return !dead && hitbox.overlaps(player.getHitbox());
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

    public Rectangle getHitbox() {
        return hitbox;
    }

    public int getChasing() {
        if (isCommittingAttack) return 4;
        if (currentState == EnemyState.CHASING) return 1;
        if (currentState == EnemyState.FORMING_GROUP) return 2;
        if (currentState == EnemyState.RETREATING) return 3;
        return 0;
    }
}
