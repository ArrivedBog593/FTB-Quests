package dev.ftb.mods.ftbquests.quest.task.forge;

import dev.ftb.mods.ftblibrary.util.StringUtils;
import dev.ftb.mods.ftbquests.FTBQuests;
import dev.ftb.mods.ftbquests.client.EnergyTaskClientData;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.task.EnergyTask;
import dev.ftb.mods.ftbquests.quest.task.TaskType;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author LatvianModder
 */
public class ForgeEnergyTask extends EnergyTask {
	public static TaskType TYPE;
	public static final ResourceLocation EMPTY_TEXTURE = new ResourceLocation(FTBQuests.MOD_ID, "textures/tasks/fe_empty.png");
	public static final ResourceLocation FULL_TEXTURE = new ResourceLocation(FTBQuests.MOD_ID, "textures/tasks/fe_full.png");

	public ForgeEnergyTask(Quest quest) {
		super(quest);
	}

	@Override
	public TaskType getType() {
		return TYPE;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public MutableComponent getAltTitle() {
		return new TranslatableComponent("ftbquests.task.ftbquests.forge_energy.text", StringUtils.formatDouble(value, true));
	}

	@Override
	public EnergyTaskClientData getClientData() {
		return ForgeEnergyTaskClientData.INSTANCE;
	}

//	public int receiveEnergy(TeamData teamData, int maxReceive, boolean simulate) {
//		if (maxReceive > 0 && !teamData.isCompleted(this)) {
//			long add = Math.min(maxReceive, value - teamData.getProgress(this));
//
//			if (maxInput > 0) {
//				add = Math.min(add, maxInput);
//			}
//
//			if (add > 0L) {
//				if (!simulate) {
//					teamData.addProgress(this, add);
//				}
//
//				return (int) add;
//			}
//		}
//
//		return 0;
//	}
}