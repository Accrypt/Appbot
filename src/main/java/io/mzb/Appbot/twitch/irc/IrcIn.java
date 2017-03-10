package io.mzb.Appbot.twitch.irc;

import io.mzb.Appbot.Appbot;
import io.mzb.Appbot.events.events.*;
import io.mzb.Appbot.twitch.*;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

class IrcIn {

    // Reads the actual incoming data
    private BufferedReader in;

    /**
     * Inits the irc input handler
     *
     * @param inputStream The stream of data from the irc socket
     */
    IrcIn(InputStream inputStream) {
        this.in = new BufferedReader(new InputStreamReader(inputStream));
        startReading();
    }

    /**
     * Starts reading inputs from the buffered reader
     * Should only be called once
     */
    private void startReading() {
        Appbot.getTaskManager().runTask(() -> {
            try {
                String msg;
                while ((msg = in.readLine()) != null) {
                    printOut(msg);
                    final String msgFinal = msg;
                    Appbot.getTaskManager().runOnMain(() -> handleInput(msgFinal));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Handles an input from the irc
     *
     * @param input the incoming line from the socket
     */
    private void handleInput(String input) {
        // Send every now and again to make sure the application is still alive, calls an event
        if (input.startsWith("PING :tmi.twitch.tv")) {
            handlePing(input);
        } else if (input.startsWith(":jtv MODE")) {
            handleMode(input);
        } else if (input.contains(".tmi.twitch.tv JOIN") && !input.contains("PRIVMSG")) {
            handleJoin(input);
        } else if (input.contains(".tmi.twitch.tv PART") && !input.contains("PRIVMSG")) {
            handlePart(input);
        } else if (input.startsWith("@")) {
            if (input.contains("PRIVMSG")) {
                handlePrivmsg(input);
            } else if (input.contains("GLOBALUSERSTATE")) {
                // TODO:
            } else if (input.contains("USERSTATE")) {
                handleUserstate(input);
            } else if (input.contains("USERNOTICE")) {
                // TODO:
            } else if (input.contains("NOTICE")) {
                handleNotice(input);
            } else if (input.contains("CLEARCHAT")) {
                handleClearchat(input);
            } else if (input.contains("ROOMSTATE")) {
                handleRoomstate(input);
            } else if (input.contains("WHISPER")) {
                handleWhisper(input);
            }
        }
    }

    /**
     * Handles a ping input
     *
     * @param input The original input from the socket
     */
    private void handlePing(String input) {
        Appbot.getIrcHandler().send(Appbot.getDefaultChannel().getName(), "PONG :tmi.twitch.tv");
        Appbot.getEventManager().callEvent(new TwitchPingEvent());
    }

    /**
     * Handles a mode input
     *
     * @param input The original input from the socket
     */
    private void handleMode(String input) {
        String endChar = (input.contains("+o") ? "+o" : "-o");
        String channel = input.substring(11, input.indexOf(endChar) - 1);
        String user = input.substring(input.indexOf(endChar) + 3, input.length());
        Channel chan = Channel.getConnectedChannel(channel);
        if (chan != null) {
            chan.onUserMode(user, endChar.equals("+o"));
        }
    }

    /**
     * Handles a join input from the socket
     *
     * @param input The original input from the socket
     */
    private void handleJoin(String input) {
        String name = input.substring(1, input.indexOf("!"));
        String channel = input.substring(input.indexOf("#") + 1, input.length());
        Channel chan = Channel.getConnectedChannel(channel);
        if (chan != null)
            chan.onUserJoin(name);
    }

    /**
     * Handles a part input from the socket
     *
     * @param input The original input from the socket
     */
    private void handlePart(String input) {
        String name = input.substring(1, input.indexOf("!"));
        String channel = input.substring(input.indexOf("#") + 1, input.length());
        Channel chan = Channel.getConnectedChannel(channel);
        if (chan != null) {
            chan.onUserQuit(name);
        }
    }

    /**
     * @param input The original input from the socket
     * Handles a private message from the socket (Any chat message)
     */
    private void handlePrivmsg(String input) {
        String[] infoSplit = input.split(" :");
        String info = infoSplit[0];

        ArrayList<Badges> badges = new ArrayList<>();
        int color = 0;
        boolean mod = false;
        boolean subscriber = false;
        boolean turbo = false;
        long roomId = 0;
        long userId = 0;
        GlobalRank rank = null;
        String emotes = "";
        int bits = 0;

        String[] infoBreaks = info.split(";");
        for (String infoSelect : infoBreaks) {
            if (infoSelect.startsWith("@")) {
                infoSelect = infoSelect.substring(1, infoSelect.length());
            }
            if (infoSelect.startsWith("badges=")) {
                badges.addAll(getBadges(infoSelect));
            }
            if (infoSelect.startsWith("bits=")) {
                bits = Integer.parseInt(infoSelect.substring(6, infoSelect.length()));
            }
            if (infoSelect.startsWith("color=")) {
                color = getColor(infoSelect);
            }
            if (infoSelect.startsWith("emotes=")) {
                if (!infoSelect.equalsIgnoreCase("emotes="))
                    emotes = infoSelect.substring(7, infoSelect.length());
            }
            if (infoSelect.equalsIgnoreCase("mod=1")) {
                mod = true;
            }
            if (infoSelect.equalsIgnoreCase("subscriber=1")) {
                subscriber = true;
            }
            if (infoSelect.equalsIgnoreCase("turbo=1")) {
                turbo = true;
            }
            if (infoSelect.startsWith("user-id=")) {
                userId = Long.parseLong(infoSelect.substring(infoSelect.indexOf("=") + 1, infoSelect.length()));
            }
            if (infoSelect.startsWith("room-id=")) {
                roomId = Long.parseLong(infoSelect.substring(infoSelect.indexOf("=") + 1, infoSelect.length()));
            }
            if (infoSelect.startsWith("user-type=")) {
                if (infoSelect.equalsIgnoreCase("user-type=global_mod")) {
                    rank = GlobalRank.GLOBAL_MOD;
                } else if (infoSelect.equalsIgnoreCase("user-type=admin")) {
                    rank = GlobalRank.ADMIN;
                } else if (infoSelect.equalsIgnoreCase("user-type=staff")) {
                    rank = GlobalRank.TWITCH_STAFF;
                }
            }
        }
        String msgPart = infoSplit[1];
        String name = msgPart.substring(0, msgPart.indexOf("!"));
        String channelName = msgPart.substring(msgPart.lastIndexOf("#") + 1, msgPart.length());
        String msg = infoSplit[2];

        System.out.println("Channel: " + channelName);
        Channel chan = Channel.getConnectedChannel(channelName);
        System.out.println("USER NAME: " + name);
        User user = chan.getChatter(name);

        user.setBadges(badges);

        ArrayList<GlobalRank> globalRanks = new ArrayList<>();
        if (rank != null) globalRanks.add(rank);
        if (turbo) globalRanks.add(GlobalRank.TURBO);
        if (badges.contains(Badges.PREMIUM)) globalRanks.add(GlobalRank.PRIME);
        user.setGlobalRanks(globalRanks);

        user.setSubscriber(subscriber);
        user.setLocalRank(mod ? LocalRank.MOD : LocalRank.USER);
        if (user.getName().equalsIgnoreCase(chan.getName())) user.setLocalRank(LocalRank.OWNER);
        user.setColor(color);
        user.setUserId(userId);
        user.setRoomId(roomId);

        if (chan != null)
            chan.onMessage(user, msg, emotes, bits);
    }

    private ArrayList<Badges> getBadges(String info) {
        ArrayList<Badges> badges = new ArrayList<>();
        if (info.equalsIgnoreCase("badges=")) {
            return badges;
        }
        info = info.substring(7, info.length());
        String[] splitBadges = info.split(",");
        for (String badge : splitBadges) {
            String[] badgeSplit = badge.split("/");
            if (badgeSplit[0].equalsIgnoreCase("broadcaster") && badgeSplit[1].equalsIgnoreCase("1")) {
                badges.add(Badges.BROADCASTER);
            }
            if (badgeSplit[0].equalsIgnoreCase("moderator") && badgeSplit[1].equalsIgnoreCase("1")) {
                badges.add(Badges.MODERATOR);
            }
            if (badgeSplit[0].equalsIgnoreCase("global_mod") && badgeSplit[1].equalsIgnoreCase("1")) {
                badges.add(Badges.GLOBAL_MOD);
            }
            if (badgeSplit[0].equalsIgnoreCase("admin") && badgeSplit[1].equalsIgnoreCase("1")) {
                badges.add(Badges.ADMIN);
            }
            if (badgeSplit[0].equalsIgnoreCase("staff") && badgeSplit[1].equalsIgnoreCase("1")) {
                badges.add(Badges.STAFF);
            }
            if (badgeSplit[0].equalsIgnoreCase("subscriber")) {
                if (badgeSplit[1].equalsIgnoreCase("0"))
                    badges.add(Badges.SUBSCRIBER_0);
                if (badgeSplit[1].equalsIgnoreCase("1"))
                    badges.add(Badges.SUBSCRIBER_1);
                if (badgeSplit[1].equalsIgnoreCase("3"))
                    badges.add(Badges.SUBSCRIBER_3);
                if (badgeSplit[1].equalsIgnoreCase("6"))
                    badges.add(Badges.SUBSCRIBER_6);
                if (badgeSplit[1].equalsIgnoreCase("12"))
                    badges.add(Badges.SUBSCRIBER_12);
                if (badgeSplit[1].equalsIgnoreCase("24"))
                    badges.add(Badges.SUBSCRIBER_24);
            }
            if (badgeSplit[0].equalsIgnoreCase("turbo") && badgeSplit[1].equalsIgnoreCase("1")) {
                badges.add(Badges.TURBO);
            }
            if (badgeSplit[0].equalsIgnoreCase("premium") && badgeSplit[1].equalsIgnoreCase("1")) {
                badges.add(Badges.PREMIUM);
            }
            if (badgeSplit[0].equalsIgnoreCase("bits")) {
                if (badgeSplit[1].equalsIgnoreCase("1")) {
                    badges.add(Badges.BITS_1);
                } else if (badgeSplit[1].equalsIgnoreCase("100")) {
                    badges.add(Badges.BITS_100);
                } else if (badgeSplit[1].equalsIgnoreCase("1000")) {
                    badges.add(Badges.BITS_1000);
                } else if (badgeSplit[1].equalsIgnoreCase("5000")) {
                    badges.add(Badges.BITS_5000);
                } else if (badgeSplit[1].equalsIgnoreCase("10000")) {
                    badges.add(Badges.BITS_10000);
                } else if (badgeSplit[1].equalsIgnoreCase("100000")) {
                    badges.add(Badges.BITS_100000);
                }
            }
        }
        return badges;
    }

    private int getColor(String info) {
        info = info.substring(6, info.length());
        if (info.length() == 0) {
            return 0x0000AA;
        } else {
            return Integer.parseInt(info.replaceFirst("#", ""), 16);
        }
    }

    /**
     * @param input The input from the socket
     * Handles a userstate message
     */
    private void handleUserstate(String input) {
        String name = Appbot.getName().toLowerCase();
        input = input.replace(" :tmi.twitch.tv USERSTATE ", " :" + name + "!" + name + "@" + name + ".tmi.twitch.tv PRIVMSG ");
        input = input + " : ";
        handlePrivmsg(input);
    }

    private void handleNotice(String input) {
        String msgId = input.substring(8, input.indexOf(" :tmi.twitch.tv")).toUpperCase();
        String noticeSplit = input.substring(input.indexOf(" #"), input.length());
        String channel = noticeSplit.substring(2, noticeSplit.indexOf(" :"));
        String msg = noticeSplit.substring(noticeSplit.indexOf(" :") + 2, noticeSplit.length());

        NoticeEvent event = new NoticeEvent(NoticeEvent.NoticeType.valueOf(msgId), channel, msg);
        Appbot.getEventManager().callEvent(event);
    }

    private void handleClearchat(String input) {
        if (input.startsWith("@room-id=")) {
            // Clear chat
            long roomId = Long.parseLong(input.substring(9, input.indexOf(" :")));
            String channel = input.substring(input.indexOf("#") + 1, input.length());

            ClearchatEvent event = new ClearchatEvent(roomId, channel);
            Appbot.getEventManager().callEvent(event);
        } else {
            // Timeout / Ban
            String[] tagSplit = input.split(" :tmi.twitch.tv CLEARCHAT #");
            String tags = tagSplit[0];
            int banDuration = 0;
            String banReason = "";
            long roomId = 0L;
            long targetUserId = 0L;
            String targetName;
            String channel;
            for (String tag : tags.split(";")) {
                if (tag.startsWith("@")) {
                    tag = tag.substring(1, tag.length());
                }
                if (tag.startsWith("ban-duration=")) {
                    banDuration = Integer.parseInt(tag.substring(13, tag.length()));
                }
                if (tag.startsWith("ban-reason=")) {
                    banReason = tag.substring(11, tag.length()).replace("\\s", " ");
                }
                if (tag.startsWith("room-id=")) {
                    roomId = Long.parseLong(tag.substring(8, tag.length()));
                }
                if (tag.startsWith("target-user-id=")) {
                    targetUserId = Long.parseLong(tag.substring(15, tag.length()));
                }
            }
            String[] channelNameSplit = tagSplit[1].split(" :");
            channel = channelNameSplit[0];
            targetName = channelNameSplit[1];

            System.out.println("Ban event: Duration: [" + banDuration + "] Reason: [" + banReason + "] Room: [" + roomId + "] Target ID: [" + targetUserId + "] Channel: [" + channel + "] Target: [" + targetName + "]");

            UserBanEvent event = new UserBanEvent(banDuration, banReason, roomId, targetUserId, channel, targetName);
            Appbot.getEventManager().callEvent(event);
        }
    }

    private void handleRoomstate(String input) {
        String[] tagSplit = input.split(" :tmi.twitch.tv ROOMSTATE #");
        String channel = tagSplit[1];
        String tags = tagSplit[0];
        String broadcasterLang = "";
        int emoteOnly = 0;
        int followerOnly = 0;
        int r9k = 0;
        int slow = 0;
        int subOnly = 0;
        for (String tag : tags.split(";")) {
            if (tag.startsWith("@")) {
                tag = tag.substring(1, tag.length());
            }
            if (tag.startsWith("broadcaster-lang=")) {
                broadcasterLang = tag.substring(17, tag.length());
            }
            if (tag.startsWith("emote-only=")) {
                emoteOnly = Integer.parseInt(tag.substring(11, tag.length()));
            }
            if (tag.startsWith("followers-only=")) {
                followerOnly = Integer.parseInt(tag.substring(15, tag.length()));
            }
            if (tag.startsWith("r9k=")) {
                r9k = Integer.parseInt(tag.substring(4, tag.length()));
            }
            if (tag.startsWith("slow=")) {
                slow = Integer.parseInt(tag.substring(5, tag.length()));
            }
            if (tag.startsWith("subs-only=")) {
                subOnly = Integer.parseInt(tag.substring(10, tag.length()));
            }

            Channel chan = Channel.getConnectedChannel(channel);
            chan.setBroadcastLang(broadcasterLang);
            chan.setEmoteOnly(emoteOnly == 1);
            chan.setFollowerOnlyExpireTime((followerOnly > 0) ? System.currentTimeMillis() + 1000 * followerOnly : Long.MAX_VALUE);
            chan.setR9k(r9k == 1);
            chan.setSlow(slow);
            chan.setSubOnly(subOnly == 1);
        }
    }

    private void handleWhisper(String input) {
        String[] tagSplit = input.split(" :");
        String name = tagSplit[1].substring(0, tagSplit[1].indexOf("!"));
        String msg = tagSplit[2];
        String tags = tagSplit[0];

        ArrayList<Badges> badges = new ArrayList<>();
        int color = 0;
        String emotes = "";
        long userId = 0L;

        for(String tag : tags.split(";")) {
            if(tag.startsWith("@")) tag = tag.substring(1, tag.length());
            if(tag.startsWith("badges=")) {
                badges.addAll(getBadges(tag));
            }
            if(tag.startsWith("color=")) {
                color = getColor(tag);
            }
            if(tag.startsWith("emotes=")) {
                emotes = tag.substring(7, tag.length());
            }
            if(tag.startsWith("user-id=")) {
                userId = Long.parseLong(tag.substring(8, tag.length()));
            }
        }
        final int finalColor = color;
        final long finalUserId = userId;
        final String finalEmotes = emotes;
        User user = new User(name);
        user.reload(() -> {
            user.setBadges(badges);
            user.setColor(finalColor);
            user.setUserId(finalUserId);
            WhisperEvent event = new WhisperEvent(user, msg, finalEmotes);
            Appbot.getEventManager().callEvent(event);
        });
    }

    /**
     * Prints the irc input to the console
     * Also sets thread name to "IRC-I" (IRC Input)
     *
     * @param x The object to be printed, converts to a string
     */
    private void printOut(Object x) {
        Thread.currentThread().setName("IRC-I");
        System.out.println("[IRC] <- " + x.toString());
    }

}
