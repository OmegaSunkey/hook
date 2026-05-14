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
        /*MutableText signature;
        MutableText reply;
        MutableText content;*/
        String reply;
        String signature;
        String content;
        MutableComponent msg;

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

        //msg = reply + signature + content;
        msg = MutableComponent.create(PlainTextContents.create(reply + signature + content));

        return msg.withStyle(ChatFormatting.BLUE);
    }
}
