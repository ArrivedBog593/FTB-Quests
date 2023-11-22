package dev.ftb.mods.ftbquests.integration.kubejs;

import dev.ftb.mods.ftbquests.FTBQuests;
import dev.ftb.mods.ftbquests.events.ObjectCompletedEvent;
import dev.ftb.mods.ftbquests.quest.QuestObject;
import dev.ftb.mods.ftbquests.quest.ServerQuestFile;
import dev.latvian.mods.kubejs.core.PlayerSelector;
import dev.latvian.mods.kubejs.player.EntityArrayList;
import dev.latvian.mods.kubejs.player.ServerPlayerJS;
import dev.latvian.mods.kubejs.server.ServerEventJS;
import dev.latvian.mods.kubejs.server.ServerJS;
import net.fabricmc.loader.impl.util.ExceptionUtil;
import org.jetbrains.annotations.Nullable;

/**
 * @author LatvianModder
 */
public class QuestObjectCompletedEventJS extends ServerEventJS {
	public final ObjectCompletedEvent<?> event;
	private final FTBQuestsKubeJSTeamDataWrapper wrapper;

	@Override
	public ServerJS getServer() {
		return ServerJS.instance;
	}

	public QuestObjectCompletedEventJS(ObjectCompletedEvent<?> e) {
		event = e;
		wrapper = new FTBQuestsKubeJSTeamDataWrapper(event.getData());
	}

	public FTBQuestsKubeJSTeamDataWrapper getData() {
		return wrapper;
	}

	public QuestObject getObject() {
		return event.getObject();
	}

	public EntityArrayList getNotifiedPlayers() {
		return ServerJS.instance.getOverworld().createEntityList(event.getNotifiedPlayers());
	}

	public EntityArrayList getOnlineMembers() {
		return getData().getOnlineMembers();
	}

	@Nullable
	public ServerPlayerJS getPlayer() {
		if (!(event.getData().file instanceof ServerQuestFile sqf)) {
			return null;
		}

		if (sqf.getCurrentPlayer() == null) {
			FTBQuests.LOGGER.error("player is unexpectedly null in server quest file!");
			try {
				throw new Exception();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return ServerJS.instance.getPlayer(PlayerSelector.mc(sqf.getCurrentPlayer()));
	}
}
