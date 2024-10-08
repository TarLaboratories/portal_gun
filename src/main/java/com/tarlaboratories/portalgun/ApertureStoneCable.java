package com.tarlaboratories.portalgun;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.RedstoneSide;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.server.level.ServerLevel;

public class ApertureStoneCable extends Block {
    @SuppressWarnings("unused")
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final EnumProperty<RedstoneSide> IS_POWERED = EnumProperty.create("is_powered", RedstoneSide.class);
    public static final BooleanProperty NORTH = BooleanProperty.create("north");
    public static final BooleanProperty SOUTH = BooleanProperty.create("south");
    public static final BooleanProperty EAST = BooleanProperty.create("east");
    public static final BooleanProperty WEST = BooleanProperty.create("west");
    public static final BooleanProperty DOWN = BooleanProperty.create("down");
    public static final BooleanProperty UP = BooleanProperty.create("up");
    public static final VoxelShape CENTER = box(6.0D, 6.0D, 6.0D, 10.0D, 10.0D, 10.0D);
    public static final VoxelShape NORTH_CONNECTOR = box(6.0D, 6.0D, 0.0D, 10.0D, 10.0D, 6.0D);
    public static final VoxelShape SOUTH_CONNECTOR = box(6.0D, 6.0D, 10.0D, 10.0D, 10.0D, 16.0D);
    public static final VoxelShape EAST_CONNECTOR = box(10.0D, 6.0D, 6.0D, 16.0D, 10.0D, 10.0D);
    public static final VoxelShape WEST_CONNECTOR = box(0.0D, 6.0D, 6.0D, 6.0D, 10.0D, 10.0D);
    public static final VoxelShape DOWN_CONNECTOR = box(6.0D, 0.0D, 6.0D, 10.0D, 6.0D, 10.0D);
    public static final VoxelShape UP_CONNECTOR = box(6.0D, 10.0D, 6.0D, 10.0D, 16.0D, 10.0D);

    public ApertureStoneCable(Properties properties) {
        super(properties);
        this.registerDefaultState(
            defaultBlockState()
                .setValue(IS_POWERED, RedstoneSide.NONE)
                .setValue(NORTH, false)
                .setValue(SOUTH, false)
                .setValue(EAST, false)
                .setValue(WEST, false)
                .setValue(DOWN, false)
                .setValue(UP, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(IS_POWERED, NORTH, SOUTH, EAST, WEST, DOWN, UP));
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState state2, LevelAccessor level, BlockPos pos, BlockPos pos2) {
        state = state.setValue(NORTH, canConnectApertureStone(level.getBlockState(pos.north())));
        state = state.setValue(SOUTH, canConnectApertureStone(level.getBlockState(pos.south())));
        state = state.setValue(EAST, canConnectApertureStone(level.getBlockState(pos.east())));
        state = state.setValue(WEST, canConnectApertureStone(level.getBlockState(pos.west())));
        state = state.setValue(DOWN, canConnectApertureStone(level.getBlockState(pos.below())));
        state = state.setValue(UP, canConnectApertureStone(level.getBlockState(pos.above())));
        return state;
    }

    @Override
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        setSignalStrength(level, pos, 0, null, new HashSet<BlockPos>(), null);
        super.playerWillDestroy(level, pos, state, player);
    }

    public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
        VoxelShape shape = CENTER;
        if (state.getValue(NORTH)) shape = Shapes.join(shape, NORTH_CONNECTOR, BooleanOp.OR);
        if (state.getValue(SOUTH)) shape = Shapes.join(shape, SOUTH_CONNECTOR, BooleanOp.OR);
        if (state.getValue(EAST)) shape = Shapes.join(shape, EAST_CONNECTOR, BooleanOp.OR);
        if (state.getValue(WEST)) shape = Shapes.join(shape, WEST_CONNECTOR, BooleanOp.OR);
        if (state.getValue(DOWN)) shape = Shapes.join(shape, DOWN_CONNECTOR, BooleanOp.OR);
        if (state.getValue(UP)) shape = Shapes.join(shape, UP_CONNECTOR, BooleanOp.OR);
        return shape;
    }

    public VoxelShape getInteractionShape(BlockState state, BlockGetter getter, BlockPos pos) {
        return getShape(state, getter, pos, null);
    }

    public static void setSignalStrength(LevelAccessor level, BlockPos pos, int signal, Direction direction, Set<BlockPos> poslist, BlockPos source_pos) {
        Set<BlockPos> pos_list = (Set<BlockPos>) poslist;
        if (pos_list.size() > 1000) return;
        if (pos_list.contains(pos)) return;
        List<Block> canSetSignalStrength = List.of(
            portalgun.APERTURESTONE_CABLE.get(),
            portalgun.APERTURESTONE_CABLE_ENCASED.get(),
            portalgun.EMANCIPATION_GRID_EMITTER.get(),
            portalgun.APERTURESTONE_REDSTONE_SWITCH.get(),
            portalgun.APERTURESTONE_LOGIC_GATE.get(),
            portalgun.APERTURESTONE_INDICATOR.get(),
            portalgun.HARD_LIGHT_BRIDGE_EMITTER.get(),
            portalgun.WEIGHTED_CUBE_DROPPER.get(),
            portalgun.COMPANION_CUBE_DROPPER.get(),
            portalgun.APERTURESTONE_SOURCE.get(),
            portalgun.LASER_EMITTER.get(),
            portalgun.LASER_CATCHER.get()
        );
        List<Block> consumePower = List.of(
            portalgun.EMANCIPATION_GRID_EMITTER.get(),
            portalgun.HARD_LIGHT_BRIDGE_EMITTER.get(),
            portalgun.WEIGHTED_CUBE_DROPPER.get(),
            portalgun.COMPANION_CUBE_DROPPER.get(),
            portalgun.LASER_EMITTER.get()
        );
        if (!canSetSignalStrength.contains(level.getBlockState(pos).getBlock())) return;
        if (consumePower.contains(level.getBlockState(pos).getBlock())) {
            if (source_pos != null && level.getBlockState(source_pos).is(portalgun.APERTURESTONE_SOURCE.get()) && level.getBlockEntity(source_pos) != null) {
                ((ApertureStoneSourceBlockEntity) level.getBlockEntity(source_pos)).connected_devices.add(pos);
            } else if (source_pos != null && level.getBlockState(source_pos).is(portalgun.LASER_CATCHER.get()) && level.getBlockEntity(source_pos) != null) {
                ((LaserCatcherBlockEntity) level.getBlockEntity(source_pos)).connected_devices.add(pos);
            }
        }
        if (direction == null) {}
        else if (level.getBlockState(pos).is(portalgun.EMANCIPATION_GRID_EMITTER.get())) {
            if (signal >= 100) EmancipationGridEmitter.tryActivate(level.getBlockState(pos), level, pos);
            else EmancipationGridEmitter.deactivate(level.getBlockState(pos), level, pos);
            return;
        } else if (level.getBlockState(pos).is(portalgun.APERTURESTONE_REDSTONE_SWITCH.get())) {
            Boolean flag = false;
            for (Direction i : Direction.values()) {
                if (signal <= 50) {flag = true; break;}
                if (level.getSignal(pos.relative(i), i.getOpposite()) != 0) {
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                setSignalStrength(level, pos, Math.min(signal, 50), direction, poslist, source_pos);
                return;
            }
        } else if (level.getBlockState(pos).is(portalgun.APERTURESTONE_LOGIC_GATE.get())) {
            List<Boolean> input = new ArrayList<Boolean>();
            BlockState state = level.getBlockState(pos);
            ApertureStoneLogicGateBlockEntity blockentity = (ApertureStoneLogicGateBlockEntity) level.getBlockEntity(pos);
            switch (direction) {
                case NORTH: blockentity.NORTH_POWER = signal; break;
                case SOUTH: blockentity.SOUTH_POWER = signal; break;
                case EAST: blockentity.EAST_POWER = signal; break;
                case WEST: blockentity.WEST_POWER = signal; break;
                case DOWN: blockentity.DOWN_POWER = signal; break;
                case UP: blockentity.UP_POWER = signal; break;
            }
            int direction_power = 0;
            for (Direction i : Direction.values()) {
                if (i == state.getValue(ApertureStoneLogicGate.OUTPUT_FACE)) continue;
                switch (i) {
                    case NORTH: if (!state.getValue(ApertureStoneCable.NORTH)) continue; direction_power = blockentity.NORTH_POWER; break;
                    case SOUTH: if (!state.getValue(ApertureStoneCable.SOUTH)) continue; direction_power = blockentity.SOUTH_POWER; break;
                    case EAST: if (!state.getValue(ApertureStoneCable.EAST)) continue; direction_power = blockentity.EAST_POWER; break;
                    case WEST: if (!state.getValue(ApertureStoneCable.WEST)) continue; direction_power = blockentity.WEST_POWER; break;
                    case DOWN: if (!state.getValue(ApertureStoneCable.DOWN)) continue; direction_power = blockentity.DOWN_POWER; break;
                    case UP: if (!state.getValue(ApertureStoneCable.UP)) continue; direction_power = blockentity.UP_POWER; break;
                }
                if (direction_power == 0) {
                    setSignalStrength(level, pos.relative(state.getValue(ApertureStoneLogicGate.OUTPUT_FACE)), 0, state.getValue(ApertureStoneLogicGate.OUTPUT_FACE).getOpposite(), poslist, source_pos);
                    level.setBlock(pos, state.setValue(IS_POWERED, RedstoneSide.NONE), 15);
                    return;
                }
                if (direction_power < 100) input.add(false);
                if (direction_power >= 100) input.add(true);
            }
            if (state.getValue(ApertureStoneLogicGate.LOGIC_GATE_TYPE).applyFunction(input)) {
                state = state.setValue(IS_POWERED, RedstoneSide.UP);
                setSignalStrength(level, pos.relative(state.getValue(ApertureStoneLogicGate.OUTPUT_FACE)), Math.max(signal, 100), state.getValue(ApertureStoneLogicGate.OUTPUT_FACE).getOpposite(), poslist, source_pos);
            } else {
                state = state.setValue(IS_POWERED, RedstoneSide.SIDE);
                setSignalStrength(level, pos.relative(state.getValue(ApertureStoneLogicGate.OUTPUT_FACE)), Math.min(signal, 50), state.getValue(ApertureStoneLogicGate.OUTPUT_FACE).getOpposite(), poslist, source_pos);
            }
            level.setBlock(pos, state, 15);
            return;
        } else if (level.getBlockState(pos).is(portalgun.HARD_LIGHT_BRIDGE_EMITTER.get()) || level.getBlockState(pos).is(portalgun.LASER_EMITTER.get())) {
            if (signal >= 100) {
                ((AnyBlockEmitter) level.getBlockState(pos).getBlock()).emmit(level, pos);
                return;
            } else {
                ((AnyBlockEmitter) level.getBlockState(pos).getBlock()).deactivate(level, pos);
            }
            return;
        } else if (level.getBlockState(pos).is(portalgun.WEIGHTED_CUBE_DROPPER.get()) || level.getBlockState(pos).is(portalgun.COMPANION_CUBE_DROPPER.get())) {
            BlockState state = level.getBlockState(pos);
            if (signal == 0) {
                state = state.setValue(WeightedCubeDropper.POWERED, RedstoneSide.NONE);
                state = state.setValue(WeightedCubeDropper.ACTIVE, false);
            } else if (signal < 100) {
                state = state.setValue(WeightedCubeDropper.POWERED, RedstoneSide.SIDE);
                state = state.setValue(WeightedCubeDropper.ACTIVE, false);
            } else {
                if (!state.getValue(WeightedCubeDropper.ACTIVE)) {
                    EntityType<?> entitytype = ((WeightedCubeDropper) state.getBlock()).getDroppedEntityType();
                    if (!level.isClientSide()) {
                        for (Entity entity : ((ServerLevel) level).getAllEntities()) {
                            if (entity == null) continue;
                            if (entity.isRemoved()) continue;
                            if (entity.getType() == entitytype) {
                                //LOGGER.info("found cube: {}", entity);
                                //LOGGER.info("It has spawn dropper position: {}", entity.getEntityData().get(WeightedCube.SPAWN_DROPPER_POS));
                                //LOGGER.info("This dropper's position is: {}", pos);
                                if (entity.getEntityData().get(WeightedCube.SPAWN_DROPPER_POS).getX() == pos.getX() &
                                    entity.getEntityData().get(WeightedCube.SPAWN_DROPPER_POS).getY() == pos.getY() &
                                    entity.getEntityData().get(WeightedCube.SPAWN_DROPPER_POS).getZ() == pos.getZ()) {
                                        //LOGGER.info("removing it :)");
                                        entity.remove(RemovalReason.KILLED);
                                }
                            }
                        }
                    }
                    state = state.setValue(WeightedCubeDropper.ACTIVE, true);
                    state = state.setValue(WeightedCubeDropper.POWERED, RedstoneSide.UP);
                    Entity entity = entitytype.create((Level) level);
                    entity.setPos(new Vec3(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5));
                    entity.getEntityData().set(WeightedCube.SPAWN_DROPPER_POS, pos);
                    level.addFreshEntity(entity);
                    //LOGGER.info("created new cube");
                }
            }
            level.setBlock(pos, state, 15);
            return;
        } else if (level.getBlockState(pos).is(portalgun.APERTURESTONE_SOURCE.get())) {
            if (level.getBlockEntity(pos) != null) {
                ((ApertureStoneSourceBlockEntity) level.getBlockEntity(pos)).connected_devices.clear();
            }
            return;
        } else if (level.getBlockState(pos).is(portalgun.LASER_CATCHER.get())) {
            if (level.getBlockEntity(pos) != null) {
                ((LaserCatcherBlockEntity) level.getBlockEntity(pos)).connected_devices.clear();
            }
            return;
        }
        pos_list.add(pos);
        BlockState state = level.getBlockState(pos);
        //LOGGER.info("Setting signal strength {}", signal);
        //LOGGER.info("At block {}", pos);
        //LOGGER.info("Already set at {}", pos_list);
        if (signal == 0) state = state.setValue(IS_POWERED, RedstoneSide.NONE);
        else if (signal < 100) state = state.setValue(IS_POWERED, RedstoneSide.SIDE);
        else state = state.setValue(IS_POWERED, RedstoneSide.UP);
        if (state.getValue(NORTH) && direction != Direction.NORTH) setSignalStrength(level, pos.north(), signal, Direction.SOUTH, pos_list, source_pos);
        if (state.getValue(SOUTH) && direction != Direction.SOUTH) setSignalStrength(level, pos.south(), signal, Direction.NORTH, pos_list, source_pos);
        if (state.getValue(EAST) && direction != Direction.EAST) setSignalStrength(level, pos.east(), signal, Direction.WEST, pos_list, source_pos);
        if (state.getValue(WEST) && direction != Direction.WEST) setSignalStrength(level, pos.west(), signal, Direction.EAST, pos_list, source_pos);
        if (state.getValue(DOWN) && direction != Direction.DOWN) setSignalStrength(level, pos.below(), signal, Direction.UP, pos_list, source_pos);
        if (state.getValue(UP) && direction != Direction.UP) setSignalStrength(level, pos.above(), signal, Direction.DOWN, pos_list, source_pos);
        level.setBlock(pos, state, 15);
    }

    public static Boolean canConnectApertureStone(BlockState state) {
        List<Block> can_connect = List.of(
            portalgun.APERTURESTONE_CABLE.get(),
            portalgun.APERTURESTONE_CABLE_ENCASED.get(),
            portalgun.EMANCIPATION_GRID_EMITTER.get(),
            portalgun.CREATIVE_APERTURESTONE_SOURCE.get(),
            portalgun.APERTURESTONE_REDSTONE_SWITCH.get(),
            portalgun.APERTURESTONE_LOGIC_GATE.get(),
            portalgun.APERTURESTONE_INDICATOR.get(),
            portalgun.HARD_LIGHT_BRIDGE_EMITTER.get(),
            portalgun.WEIGHTED_CUBE_DROPPER.get(),
            portalgun.COMPANION_CUBE_DROPPER.get(),
            portalgun.APERTURESTONE_SOURCE.get(),
            portalgun.LASER_EMITTER.get(),
            portalgun.LASER_CATCHER.get()
        );
        return can_connect.contains(state.getBlock());
    }
}
