package net.dustley.divinity.content.defense.catena;

import dev.dominion.ecs.api.Entity;
import net.dustley.divinity.content.defense.catena.network.ConnectCatenaS2CPacketPayload;
import net.dustley.divinity.content.defense.catena.render.CatenaSpawnOverlay;
import net.dustley.divinity.registry.ModEntities;
import net.dustley.lemon.modules.camera_effects.freeze_frames.FreezeFrameManager;
import net.dustley.lemon.modules.camera_effects.screen_shake.ScreenShake;
import net.dustley.lemon.modules.camera_effects.screen_shake.ScreenShakeManager;
import net.dustley.lemon.modules.citrus_physics.PhysicsWorld;
import net.dustley.lemon.modules.citrus_physics.component.ActorComponent;
import net.dustley.lemon.modules.citrus_physics.component.constraint.multi.FixedDistanceConstraint;
import net.dustley.lemon.modules.citrus_physics.component.constraint.single.GravityConstraint;
import net.dustley.lemon.modules.citrus_physics.component.constraint.single.StaticConstraint;
import net.dustley.lemon.modules.math.conversion.JomlToMC;
import net.dustley.lemon.modules.math.conversion.McToJoml;
import net.dustley.lemon.modules.math.easing.Easing;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import org.joml.Vector3d;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CatenaEntity extends LivingEntity {

    // Settings
    public static int CHAIN_LENGTH = 10;
    public static double CHAIN_SIZE = 0.5; // Distance between segments so [ 0.5 = (1/16)*8 ]
    public static int TICKS_BETWEEN_CHAIN_SPAWN = 4;

    // Owner
    private static final TrackedData<Optional<UUID>> OWNER_ID = DataTracker.registerData(CatenaEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);
    public PlayerEntity ownerEntity;
    private ChunkPos lastChunkPos;

    // Chain
    public Entity[] chainSegments = new Entity[CHAIN_LENGTH];
    public int shownSegments = 0;
    public int throwDelayTicks = 0;
    public boolean isConnected = false;

    // Physics
    public StaticConstraint startEntityConstraint;
    public StaticConstraint endEntityConstraint;

    public CatenaEntity(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    public void createChain() {
        var physics = PhysicsWorld.getFromWorld(getWorld());
        PhysicsWorld.TICK_RATE = 60;

        // Cleanup last chain if present
        for (Entity chainSegment : chainSegments) { if(chainSegment != null) physics.ecsWorld.deleteEntity(chainSegment); }
        shownSegments = 0;

        // Create a new one
        isConnected = true;
        for (int i = 0; i < CHAIN_LENGTH; i++) {
            var actor = new ActorComponent(McToJoml.fromVec3d(getChainStartPos()), 0.25);
            chainSegments[i] = physics.createEntity().add(actor);
            actor.positionCache.y += 0.1;
        }

        // Apply constraints
        for (int i = 0; i < CHAIN_LENGTH - 1; i++) {
            var segmentA = chainSegments[i];
            var segmentB = chainSegments[i+1];

            physics.addConstraint(segmentA,
                    new FixedDistanceConstraint(segmentB, CHAIN_SIZE), new GravityConstraint(new Vector3d(0.0, 0.25, 0.0))
            );
        }

        startEntityConstraint = new StaticConstraint(getChainStartPos());
        endEntityConstraint = new StaticConstraint(getChainEndPos());

        physics.addConstraint(chainSegments[0], startEntityConstraint);
        physics.addConstraint(chainSegments[CHAIN_LENGTH - 1], endEntityConstraint);
    }

    public void createChainSpawnAnim() {
        // Apply effects
        TICKS_BETWEEN_CHAIN_SPAWN = 2;
        ScreenShakeManager.createScreenShake(new ScreenShake((TICKS_BETWEEN_CHAIN_SPAWN * CHAIN_LENGTH) * 2, 0.2f, Easing.LINEAR, Easing.LINEAR_INV));
    }

    public void updateChain() {
        if(!isConnected) return;
        if(getOwnerID().isEmpty()) return;
        if(ownerEntity == null) return;

        startEntityConstraint.position = getChainStartPos();
        endEntityConstraint.position = getChainEndPos();

        setNoDrag(!isOnGround() && !isInsideWall() && !isSubmergedInWater());

        // Move player to chain
        double distance = getChainStartPos().distance(getChainEndPos());
        if (distance >= getMaxDist() && distance < getMaxDist() * 3 && !ownerEntity.isSpectator()) {
            double remaining = getMaxDist() - distance;
            Vector3d move = getChainStartPos().sub(getChainEndPos()).normalize(remaining * 0.5);

            ownerEntity.move(MovementType.PLAYER, JomlToMC.fromVector3d(move));
            ownerEntity.addVelocity(move.x, move.y, move.z);

            move = move.mul(-1.0);
            move(MovementType.PLAYER, JomlToMC.fromVector3d(move));
            addVelocity(move.x, move.y, move.z);
        }
    }

    public void spawnNewChainSegment() {
        shownSegments++;
        var actor = chainSegments[shownSegments - 1].get(ActorComponent.class);

        getWorld().addParticle(ParticleTypes.POOF, actor.position.x, actor.position.y, actor.position.z, 0.0, 0.0, 0.0);

        if(shownSegments == chainSegments.length - 1) {
            // Last one specific
            playSound(SoundEvents.ENTITY_WITHER_DEATH, 1f, 4f);
            var pos = getChainEndPos();
            getWorld().addParticle(ParticleTypes.LARGE_SMOKE, pos.x, pos.y, pos.z, 0.0, 0.0, 0.0);
            FreezeFrameManager.triggerFreeze(5, new CatenaSpawnOverlay());

        } else {
            // Non-last one specific
            playSound(SoundEvents.BLOCK_ANVIL_LAND, 0.25f, 1f);
        }
    }

    public Vector3d getChainStartPos() {
        var pos = ownerEntity.getPos().add(0.0,ownerEntity.getHeight() * 0.5,0.0);
        return new Vector3d(pos.x, pos.y, pos.z);
    }

    public Vector3d getChainEndPos() {
        var pos = getPos().add(0.0,getHeight(),0.0);
        return new Vector3d(pos.x, pos.y, pos.z);
    }

    public Vector3d getChainStartLerpedPos(float delta) {
        var pos = ownerEntity.getLerpedPos(delta).add(0.0,ownerEntity.getHeight() * 0.5,0.0);
        return new Vector3d(pos.x, pos.y, pos.z);
    }

    public Vector3d getChainEndLerpedPos(float delta) {
        var pos = getLerpedPos(delta).add(0.0,getHeight(),0.0);
        return new Vector3d(pos.x, pos.y, pos.z);
    }

    public double getMaxDist() { return ((CHAIN_LENGTH-1) * CHAIN_SIZE) + 0.05; }

    @Override
    public void tick() {
        super.tick();

        if(ownerEntity != null && ownerEntity.isDead()) {
            ownerEntity = null;
            isConnected = false;
            setVelocity(0,0,0);
        }

        if(ownerEntity != null) {

            setOwnerID(Optional.ofNullable(ownerEntity.getUuid()));

            updateChain();

            // Run animation
            if (age % TICKS_BETWEEN_CHAIN_SPAWN == 0 && shownSegments < chainSegments.length) { spawnNewChainSegment(); }

            // Throw ability
            var holder = getVehicle();
            if(holder instanceof PlayerEntity player) {
                if(throwDelayTicks > 0) throwDelayTicks--;
                if(player.handSwinging && throwDelayTicks <= 0) {
                    var dir = holder.getRotationVector().multiply(0.35);
                    if(player.isSneaking()) dir = dir.multiply(2);
                    stopRiding();
                    setVelocity(dir);
                }
            }

            // Tp if too far
            double distance = getChainStartPos().distance(getChainEndPos());
            if (distance >= getMaxDist() * 3) {
                teleport(ownerEntity.getX(), ownerEntity.getY(), ownerEntity.getZ(), true);
                setVelocity(0,0,0);
            }
        }

        if(ownerEntity == null) { isConnected = false; }

        if(getOwnerID().isPresent()) {
            // Handle tp
            if(getServer() != null) {
                var playerManager = getServer().getPlayerManager();
                var player = playerManager.getPlayer(getOwnerID().get());

                if(player != null) {
                    keepChunkLoaded(player.getServerWorld());

                    if (player.getWorld().getRegistryKey() != getWorld().getRegistryKey()) {
                        var entity = new CatenaEntity(ModEntities.CATENA, player.getWorld());
                        entity.connectToPlayer(player, false);

                        entity.setPosition(player.getPos());
                        entity.setPos(player.getX(), player.getY(), player.getZ());
                        entity.teleport(player.getX(), player.getY(), player.getZ(), false);

                        player.getWorld().spawnEntity(entity);

                        entity.setPosition(player.getPos());
                        entity.setPos(player.getX(), player.getY(), player.getZ());
                        entity.teleport(player.getX(), player.getY(), player.getZ(), false);

                        discard();
                    }
                }
            }

            // Links if in the same world
            if(ownerEntity == null) ownerEntity = getWorld().getPlayerByUuid(getOwnerID().get());

            if(!isConnected && ownerEntity != null) { connectToPlayer(ownerEntity, false); } // Ensure that we have a chain
        }
    }

    public void connectToPlayer(PlayerEntity player, boolean showAnimation) {
        // Linking
        ownerEntity = player;
        setOwnerID(Optional.ofNullable(player.getUuid()));
        createChain();

        // Animation
        if(showAnimation) createChainSpawnAnim();
        shownSegments = showAnimation ? 0 : CHAIN_LENGTH - 1;
    }

    private void keepChunkLoaded(ServerWorld world) {
        if(lastChunkPos != null) world.setChunkForced(lastChunkPos.x, lastChunkPos.z, false);
        ChunkPos chunkPos = new ChunkPos(getBlockPos());
        lastChunkPos = chunkPos;
        world.setChunkForced(chunkPos.x, chunkPos.z, true);
    }

    // Handle picking catena up
    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        // Create link
        if(player.isSneaking() && !player.getWorld().isClient) {
            for (PlayerEntity playerEntity : player.getWorld().getPlayers()) {
                var packet = new ConnectCatenaS2CPacketPayload(getId(), player.getUuid());
                ServerPlayNetworking.send((ServerPlayerEntity) playerEntity, packet);
            }
            connectToPlayer(player, true);
        }

        if( ownerEntity != null ) {
            startRiding(player, true);
            player.handSwinging = false;
        }
        return super.interact(player, hand);
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        var entity = source.getAttacker();
        if(entity instanceof PlayerEntity player && player.isSneaking() && !player.getWorld().isClient) {
            for (PlayerEntity playerEntity : player.getWorld().getPlayers()) {
                var packet = new ConnectCatenaS2CPacketPayload(getId(), player.getUuid());
                ServerPlayNetworking.send((ServerPlayerEntity) playerEntity, packet);
            }
            connectToPlayer(player, true);
        }

        return source.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY) || source.isIn(DamageTypeTags.BYPASSES_COOLDOWN) || source.isOf(DamageTypes.GENERIC_KILL) || source.isOf(DamageTypes.OUT_OF_WORLD);
    }

    // Data
    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        builder.add(OWNER_ID, Optional.empty());
        super.initDataTracker(builder);
    }

    public Optional<UUID> getOwnerID() { return dataTracker.get(OWNER_ID); }
    public void setOwnerID(Optional<UUID> ownerID) { dataTracker.set(OWNER_ID, ownerID);}

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        if (getOwnerID().isPresent()) nbt.putUuid("OwnerID", getOwnerID().get());
        return super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        if (nbt.contains("OwnerID")) setOwnerID(Optional.ofNullable(nbt.getUuid("OwnerID")));
        super.readNbt(nbt);
    }

    // Make entity not move around
    @Override protected boolean shouldSwimInFluids() { return false; }
    @Override protected boolean isImmobile() { return true; }
    @Override public boolean isFallFlying() { return false; }
    @Override public boolean canUsePortals(boolean allowVehicles) { return false; }

    // Remove inventory
    @Override public Iterable<ItemStack> getArmorItems() { return List.of(); }
    @Override public ItemStack getEquippedStack(EquipmentSlot slot) { return ItemStack.EMPTY; }
    @Override public void equipStack(EquipmentSlot slot, ItemStack stack) { }
    @Override public Arm getMainArm() { return Arm.RIGHT; }


}
