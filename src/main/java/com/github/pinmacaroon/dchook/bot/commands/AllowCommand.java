package com.github.pinmacaroon.dchook.bot.commands;

import com.github.pinmacaroon.dchook.Hook;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.minecraft.world.level.GameType;

public class AllowCommand {
    public static void run(SlashCommandInteractionEvent event) {
        String username = event.getOption("username", null, OptionMapping::getAsString);
        try {
            Hook.getGameServer().getPlayerList().getPlayer(username).setGameMode(GameType.SURVIVAL);
            event.reply("Changed " + username + "'s gamemode to survival.").queue();
        } catch (NullPointerException e) {
            event.reply("Couldn't change " + username + "'s gamemode; maybe they haven't joined?").queue();
        }
    }
}
