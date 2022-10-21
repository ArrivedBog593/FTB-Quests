package dev.ftb.mods.ftbquests.quest.task;

import dev.ftb.mods.ftblibrary.util.StringUtils;
import dev.ftb.mods.ftbquests.FTBQuests;
import dev.ftb.mods.ftbquests.client.EnergyTaskClientData;
import dev.ftb.mods.ftbquests.quest.Quest;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;

public class TechRebornEnergyTask extends EnergyTask {
    public static TaskType TYPE;
    public static final ResourceLocation EMPTY_TEXTURE = new ResourceLocation(FTBQuests.MOD_ID, "textures/tasks/ic2_empty.png");
    public static final ResourceLocation FULL_TEXTURE = new ResourceLocation(FTBQuests.MOD_ID, "textures/tasks/ic2_full.png");

    public TechRebornEnergyTask(Quest quest) {
        super(quest);
    }

    @Override
    public EnergyTaskClientData getClientData() {
        return TREnergyTaskClientData.INSTANCE;
    }

    @Override
    public TaskType getType() {
        return TYPE;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public MutableComponent getAltTitle() {
        return new TranslatableComponent("ftbquests.task.ftbquests.tech_reborn_energy.text", StringUtils.formatDouble(value, true));
    }
}
