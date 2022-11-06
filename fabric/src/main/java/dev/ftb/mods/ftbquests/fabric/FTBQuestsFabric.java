package dev.ftb.mods.ftbquests.fabric;

import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftbquests.FTBQuests;
import dev.ftb.mods.ftbquests.block.entity.FTBQuestsBlockEntities;
import dev.ftb.mods.ftbquests.block.fabric.FabricLootCrateOpenerBlockEntity;
import dev.ftb.mods.ftbquests.block.fabric.FabricTaskScreenAuxBlockEntity;
import dev.ftb.mods.ftbquests.block.fabric.FabricTaskScreenBlockEntity;
import dev.ftb.mods.ftbquests.quest.task.TaskTypes;
import dev.ftb.mods.ftbquests.quest.task.TechRebornEnergyTask;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.minecraft.resources.ResourceLocation;
import team.reborn.energy.api.EnergyStorage;

public class FTBQuestsFabric implements ModInitializer {
	@SuppressWarnings("UnstableApiUsage")
	@Override
	public void onInitialize() {
		new FTBQuests().setup();

		TechRebornEnergyTask.TYPE = TaskTypes.register(new ResourceLocation(FTBQuests.MOD_ID, "tech_reborn_energy"), TechRebornEnergyTask::new, () -> Icon.getIcon(TechRebornEnergyTask.EMPTY_TEXTURE.toString()).combineWith(Icon.getIcon(TechRebornEnergyTask.FULL_TEXTURE.toString())));

		ItemStorage.SIDED.registerForBlockEntity(
				((blockEntity, direction) -> ((FabricTaskScreenBlockEntity) blockEntity).getItemStorage()), FTBQuestsBlockEntities.CORE_TASK_SCREEN.get()
		);
		ItemStorage.SIDED.registerForBlockEntity(
				((blockEntity, direction) -> ((FabricTaskScreenAuxBlockEntity) blockEntity).getItemStorage()), FTBQuestsBlockEntities.AUX_TASK_SCREEN.get()
		);

		FluidStorage.SIDED.registerForBlockEntity(
				((blockEntity, direction) -> ((FabricTaskScreenBlockEntity) blockEntity).getFluidStorage()), FTBQuestsBlockEntities.CORE_TASK_SCREEN.get()
		);
		FluidStorage.SIDED.registerForBlockEntity(
				((blockEntity, direction) -> ((FabricTaskScreenAuxBlockEntity) blockEntity).getFluidStorage()), FTBQuestsBlockEntities.AUX_TASK_SCREEN.get()
		);

		EnergyStorage.SIDED.registerForBlockEntity(
				((blockEntity, direction) -> ((FabricTaskScreenBlockEntity) blockEntity).getEnergyStorage()), FTBQuestsBlockEntities.CORE_TASK_SCREEN.get()
		);
		EnergyStorage.SIDED.registerForBlockEntity(
				((blockEntity, direction) -> ((FabricTaskScreenAuxBlockEntity) blockEntity).getEnergyStorage()), FTBQuestsBlockEntities.AUX_TASK_SCREEN.get()
		);

		ItemStorage.SIDED.registerForBlockEntity(
				((blockEntity, direction) -> ((FabricLootCrateOpenerBlockEntity) blockEntity).getItemStorage()), FTBQuestsBlockEntities.LOOT_CRATE_OPENER.get()
		);
	}
}
