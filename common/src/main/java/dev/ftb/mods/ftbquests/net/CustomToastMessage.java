package dev.ftb.mods.ftbquests.net;

import dev.architectury.networking.NetworkManager;
import dev.ftb.mods.ftbquests.api.FTBQuestsAPI;
import dev.ftb.mods.ftbquests.client.ClientQuestFile;
import dev.ftb.mods.ftbquests.client.FTBQuestsNetClient;
import dev.ftb.mods.ftbquests.quest.reward.ToastReward;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record CustomToastMessage(long id) implements CustomPacketPayload {
	public static final Type<CustomToastMessage> TYPE = new Type<>(FTBQuestsAPI.rl("custom_toast_message"));

	public static final StreamCodec<RegistryFriendlyByteBuf, CustomToastMessage> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_LONG, CustomToastMessage::id,
			CustomToastMessage::new
	);

	@Override
	public Type<CustomToastMessage> type() {
		return TYPE;
	}

	public static void handle(CustomToastMessage message, NetworkManager.PacketContext context) {
		context.queue(() -> {
			if (ClientQuestFile.exists() && ClientQuestFile.INSTANCE.getBase(message.id) instanceof ToastReward toastReward) {
				FTBQuestsNetClient.displayCustomToast(toastReward);
			}
		});
	}
}