package com.simibubi.create.modules.logistics.block.diodes;

import static com.simibubi.create.modules.logistics.block.diodes.FlexpeaterBlock.POWERING;
import static net.minecraft.block.RedstoneDiodeBlock.POWERED;

import com.simibubi.create.AllPackets;
import com.simibubi.create.AllTileEntities;
import com.simibubi.create.foundation.block.SyncedTileEntity;
import com.simibubi.create.foundation.utility.Lang;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.math.MathHelper;

public class FlexpeaterTileEntity extends SyncedTileEntity implements ITickableTileEntity {

	public int state;
	public int maxState;
	public int newMaxState;
	public int lastModified;
	public boolean charging;

	public FlexpeaterTileEntity() {
		super(AllTileEntities.FLEXPEATER.type);
		lastModified = -1;
		maxState = 1;
	}
	
	@Override
	public void read(CompoundNBT compound) {
		state = compound.getInt("State");
		charging = compound.getBoolean("Charging");
		newMaxState = maxState = compound.getInt("MaxState");
		lastModified = -1;
		super.read(compound);
	}
	
	@Override
	public CompoundNBT write(CompoundNBT compound) {
		compound.putInt("State", state);
		compound.putInt("MaxState", maxState);
		compound.putBoolean("Charging", charging);
		return super.write(compound);
	}

	public void increment(int amount) {

		if (amount > 0) {
			if (newMaxState < 20) {
				newMaxState += amount;
			} else if (newMaxState < 20 * 60) {
				newMaxState += amount * 20;
			} else {
				newMaxState += amount * 20 * 60;
			}
			lastModified = 0;
		}
		
		if (amount < 0) {
			if (newMaxState <= 20) {
				newMaxState += amount;
			} else if (newMaxState <= 20 * 60) {
				newMaxState += amount * 20;
			} else {
				newMaxState += amount * 20 * 60;
			}
			lastModified = 0;
		}

		newMaxState = MathHelper.clamp(newMaxState, 1, 60 * 20 * 30);
	}
	
	@Override
	public boolean hasFastRenderer() {
		return true;
	}

	public int getDisplayValue() {
		if (newMaxState < 20)
			return newMaxState;
		if (newMaxState < 20 * 60)
			return newMaxState / 20;
		return newMaxState / 20 / 60;
	}

	public String getUnit() {
		if (newMaxState < 20)
			return Lang.translate("generic.unit.ticks");
		if (newMaxState < 20 * 60)
			return Lang.translate("generic.unit.seconds");
		return Lang.translate("generic.unit.minutes");
	}
	
	@Override
	public void tick() {
		updateConfigurableValue();
		boolean powered = getBlockState().get(POWERED);
		boolean powering = getBlockState().get(POWERING);
		boolean atMax = state == maxState;
		boolean atMin = state == 0;
		
		if (!charging && powered)
			charging = true;
		
		if (charging && atMax) {
			if (!powering && !world.isRemote)
				world.setBlockState(pos, getBlockState().with(POWERING, true));
			if (!powered)
				charging = false;
			return;
		}
		
		if (!charging && atMin) {
			if (powering && !world.isRemote)
				world.setBlockState(pos, getBlockState().with(POWERING, false));
			return;
		}
		
		state += charging ? 1 : -1;
	}

	public void updateConfigurableValue() {
		if (!world.isRemote)
			return;
		if (lastModified == -1)
			return;
		if (lastModified++ > 10) {
			lastModified = -1;
			AllPackets.channel.sendToServer(new ConfigureFlexpeaterPacket(pos, newMaxState));
		}
	}

}