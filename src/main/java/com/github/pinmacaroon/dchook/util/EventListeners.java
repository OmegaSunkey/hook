package com.github.pinmacaroon.dchook.util;

import com.github.pinmacaroon.dchook.Hook;
import com.github.pinmacaroon.dchook.conf.ModConfigs;
import net.dv8tion.jda.api.entities.emoji.RichCustomEmoji;
import net.dv8tion.jda.api.utils.MarkdownSanitizer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.minecraft.network.chat.Component;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.github.pinmacaroon.dchook.Hook.LOGGER;

public class EventListeners {

    public static void registerEventListeners(){

        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            Hook.setMinecraftServer(server);
            if (!ModConfigs.MESSAGES_SERVER_STARTING_ALLOWED) return;

            HashMap<String, String> request_body = new HashMap<>();
            request_body.put("content", "**"+ModConfigs.MESSAGES_SERVER_STARTING+"**");
            request_body.put("username", "server");
            request_body.put("avatar_url", "https://cdn.discordapp.com/attachments/1503920155925942412/1503925778138533908/image.png?ex=6a051f87&is=6a03ce07&hm=277eb16e8ed6083fa3e87c9791b28b67b3f8a85edc76158b1b6d34313720c685");

            HttpRequest post = HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(Hook.GSON.toJson(request_body)))
                    .uri(Hook.WEBHOOK_URI)
                    .header("Content-Type", "application/json")
                    .build();

            try {
                Hook.HTTPCLIENT.sendAsync(post, HttpResponse.BodyHandlers.ofString()).get().body();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        });

        if (ModConfigs.MESSAGES_SERVER_STARTED_ALLOWED)
            ServerLifecycleEvents.SERVER_STARTED.register(server -> {
                if (ModConfigs.MESSAGES_SERVER_STARTED_ALLOWED) {
                    HashMap<String, String> request_body = new HashMap<>();
                    request_body.put("content", "**"+ModConfigs.MESSAGES_SERVER_STARTED+"**");
                    request_body.put("username", "server");
                    request_body.put("avatar_url", "https://cdn.discordapp.com/attachments/1503920155925942412/1503925778138533908/image.png?ex=6a051f87&is=6a03ce07&hm=277eb16e8ed6083fa3e87c9791b28b67b3f8a85edc76158b1b6d34313720c685");

                    HttpRequest post = HttpRequest.newBuilder()
                            .POST(HttpRequest.BodyPublishers.ofString(Hook.GSON.toJson(request_body)))
                            .uri(Hook.WEBHOOK_URI)
                            .header("Content-Type", "application/json")
                            .build();

                    try {
                        Hook.HTTPCLIENT.sendAsync(post, HttpResponse.BodyHandlers.ofString()).get().body();
                    } catch (InterruptedException | ExecutionException e) {
                        throw new RuntimeException(e);
                    }
                }

                if (ModConfigs.FUNCTIONS_PROMOTIONS_ENABLED) {
                    PromotionProvider.sendPromotion(Hook.WEBHOOK_URI);
                }
            });

        ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
            if (ModConfigs.MESSAGES_SERVER_STOPPED_ALLOWED) {
                HashMap<String, String> request_body = new HashMap<>();
                request_body.put("content", "**" + ModConfigs.MESSAGES_SERVER_STOPPED + "**");
                request_body.put("username", "server");
                request_body.put("avatar_url", "https://cdn.discordapp.com/attachments/1503920155925942412/1503925778138533908/image.png?ex=6a051f87&is=6a03ce07&hm=277eb16e8ed6083fa3e87c9791b28b67b3f8a85edc76158b1b6d34313720c685");

                HttpRequest post = HttpRequest.newBuilder()
                        .POST(HttpRequest.BodyPublishers.ofString(Hook.GSON.toJson(request_body)))
                        .uri(Hook.WEBHOOK_URI)
                        .header("Content-Type", "application/json")
                        .build();

                try {
                    Hook.HTTPCLIENT.sendAsync(post, HttpResponse.BodyHandlers.ofString()).get().body();
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }

            if(Hook.BOT != null) Hook.BOT.stop();
        });

        if (ModConfigs.MESSAGES_SERVER_STOPPING_ALLOWED) ServerLifecycleEvents.SERVER_STOPPING.register(server -> {

            HashMap<String, String> request_body = new HashMap<>();
            request_body.put("content", "**"+ModConfigs.MESSAGES_SERVER_STOPPING+"**");
            request_body.put("username", "server");
            request_body.put("avatar_url", "https://cdn.discordapp.com/attachments/1503920155925942412/1503925778138533908/image.png?ex=6a051f87&is=6a03ce07&hm=277eb16e8ed6083fa3e87c9791b28b67b3f8a85edc76158b1b6d34313720c685");

            HttpRequest post = HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(Hook.GSON.toJson(request_body)))
                    .uri(Hook.WEBHOOK_URI)
                    .header("Content-Type", "application/json")
                    .build();

            try {
                Hook.HTTPCLIENT.sendAsync(post, HttpResponse.BodyHandlers.ofString()).get().body();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        });

        ServerMessageEvents.CHAT_MESSAGE.register((message, sender, parameters) -> {
            HashMap<String, Object> request_body = new HashMap<>();

            if(XaeoWaypoint.parse(message.signedContent())!=null){
                XaeoWaypoint point = XaeoWaypoint.parse(message.signedContent());
                request_body.put("content", MessageFormat.format(
                        "*"+ModConfigs.MESSAGES_SERVER_WAYPOINT+"*",
                        point.name,
                        point.marker,
                        point.x, point.y, point.z,
                        point.getDimension()
                ));
            } else request_body.put("content", MarkdownSanitizer.escape(message.signedContent()));

            request_body.put("username", sender.getName().getString());
            request_body.put("avatar_url", "https://crafthead.net/helm/" + sender.getName().getString());
            
            HashMap<String, Object> allowedMentions = new HashMap<>();
	        allowedMentions.put("parse", new ArrayList<>());
	        request_body.put("allowed_mentions", allowedMentions);
            
            HttpRequest post;
            HttpRequest repost = null;
            //admin stuff
            if(message.signedContent().strip().endsWith("//") && ModConfigs.FUNCTIONS_ALLOWOOCMESSAGES) {
                post = HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(Hook.GSON.toJson(request_body)))
                    .uri(URI.create(ModConfigs.FUNCTIONS_MOD_BACKLOG))
                    .header("Content-Type", "application/json")
                    .build();

            } else {
                post = HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(Hook.GSON.toJson(request_body)))
                    .uri(Hook.WEBHOOK_URI)
                    .header("Content-Type", "application/json")
                    .build();
                repost = HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(Hook.GSON.toJson(request_body)))
                    .uri(URI.create(ModConfigs.FUNCTIONS_MOD_BACKLOG))
                    .header("Content-Type", "application/json")
                    .build();
            }
            try {
                Hook.HTTPCLIENT.sendAsync(post, HttpResponse.BodyHandlers.ofString()).get().body();
                if(repost != null) Hook.HTTPCLIENT.sendAsync(repost, HttpResponse.BodyHandlers.ofString()).get().body();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        });

        ServerMessageEvents.GAME_MESSAGE.register((server, text, b) -> {
            if(Component.translatable(text.getString()).getString().startsWith("<")) return;

            HashMap<String, String> request_body = new HashMap<>();
            request_body.put("content", "**"+Component.translatable(text.getString()).getString()+"**");
            request_body.put("username", "game");
            request_body.put("avatar_url", "https://cdn.discordapp.com/attachments/1503920155925942412/1503925778138533908/image.png?ex=6a051f87&is=6a03ce07&hm=277eb16e8ed6083fa3e87c9791b28b67b3f8a85edc76158b1b6d34313720c685&animated=true");

            HttpRequest post = HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(Hook.GSON.toJson(request_body)))
                    .uri(Hook.WEBHOOK_URI)
                    .header("Content-Type", "application/json")
                    .build();

            try {
                Hook.HTTPCLIENT.sendAsync(post, HttpResponse.BodyHandlers.ofString()).get().body();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
