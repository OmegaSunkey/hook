package com.github.pinmacaroon.dchook.bot.event;

import com.github.pinmacaroon.dchook.Hook;
import com.github.pinmacaroon.dchook.bot.Bot;
import com.github.pinmacaroon.dchook.conf.ModConfigs;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReference;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.PlainTextContents;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class MessageReceivedListener extends ListenerAdapter {
    private final Bot BOT;

    public MessageReceivedListener(Bot bot) {
        this.BOT = bot;
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getGuild().getIdLong() != this.BOT.getGUILD_ID()) return;
        if (event.getMessage().getAuthor().isBot()) return;
        if (event.getChannel().getIdLong() == this.BOT.getCHANNEL_ID() && Hook.getGameServer() != null) {
            if (event.getMessage().getContentStripped().endsWith("//") && ModConfigs.FUNCTIONS_ALLOWOOCMESSAGES) return;
            Hook.getGameServer().getPlayerList().broadcastSystemMessage(renderMessage(event.getMessage()), false);
        }
    }

    private static Component renderMessage(Message message) {
        final String raw_message = message.getContentDisplay();
        MutableComponent reply;
        MutableComponent signature;
        MutableComponent content;

        int user_color = Objects.requireNonNull(message.getMember()).getColorRaw() != 0x1FFFFFFF
                ? message.getMember().getColorRaw()
                : 16748981;

        MessageReference r = message.getMessageReference();
        if (r != null) {
            reply = createMessage("<").withStyle(ChatFormatting.WHITE)
                    .append(
                            createMessage(
                                    "@%s".formatted(
                                            r.getMessage().getAuthor().getName()
                                    )
                            ).withColor(16748981)
                    ).append(
                            createMessage(" -> ").withColor(user_color)
                    );
        } else {
            reply = createMessage("<").withStyle(ChatFormatting.WHITE);
        }

        signature = createMessage(
                "@%s".formatted(
                        message.getAuthor().getName()
                )).withColor(user_color)
                .append(createMessage("> ").withStyle(ChatFormatting.WHITE));

        if (!message.getAttachments().isEmpty()) {
            content = raw_message.isBlank()
                    ? createMessage("[embed]").withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY)
                    : createMessage(raw_message).append(createMessage(" [embed]").withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY));
        } else content = createMessage(raw_message);

        return reply.append(signature.append(content));
    }

    private static MutableComponent createMessage(String message) {
        return MutableComponent.create(PlainTextContents.create(message));
    }
}
