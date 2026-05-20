package com.github.pinmacaroon.dchook.bot.event;

import com.github.pinmacaroon.dchook.Hook;
import com.github.pinmacaroon.dchook.bot.Bot;
import com.github.pinmacaroon.dchook.conf.ModConfigs;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReference;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.PlainTextContents;
import org.jetbrains.annotations.NotNull;

import java.util.List;
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
        String reply;
        String signature;
        String content;
        MutableComponent msg;
        MutableComponent user;

        List<Role> roles = Objects.requireNonNull(message.getMember()).getRoles();
        int user_color = !roles.isEmpty() && roles.getFirst().getColorRaw() != 0x1FFFFFFF ? roles.getFirst().getColorRaw() : 16748981;

        MessageReference r = message.getMessageReference();
        if (r != null) {
            reply = "<@%s -> ".formatted(
                    r.getMessage().getAuthor().getName()
            );
        } else {
            reply = "<";
        }

        signature = "@%s> ".formatted(
                message.getAuthor().getName()
        );

        content = (raw_message.isBlank())
                ? "[embed]"
                : raw_message;

        user = MutableComponent.create(PlainTextContents.create(reply + signature)).withColor(user_color);
        msg = MutableComponent.create(PlainTextContents.create(content)).withStyle(ChatFormatting.WHITE);

        return user.append(msg);
    }
}
