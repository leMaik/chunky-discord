package de.lemaik.chunky.discord;

import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;
import se.llbit.chunky.Plugin;
import se.llbit.chunky.main.Chunky;
import se.llbit.chunky.main.ChunkyOptions;
import se.llbit.chunky.renderer.RenderMode;
import se.llbit.chunky.renderer.RenderStatusListener;
import se.llbit.chunky.ui.ChunkyFx;

import java.text.NumberFormat;
import java.time.Duration;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public class DiscordPlugin implements Plugin {
    static boolean ready = false;
    AtomicInteger spp = new AtomicInteger(0);
    AtomicInteger totalSpp = new AtomicInteger(0);
    AtomicInteger sps = new AtomicInteger(0);
    AtomicReference<String> renderMode = new AtomicReference<>("PREVIEW");
    AtomicLong rendertime = new AtomicLong(0);

    @Override
    public void attach(Chunky chunky) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Closing Discord hook");
            DiscordRPC.discordShutdown();
        }));

        initDiscord();
        System.out.println("DISCORD REGISTERED");
        new Thread(() -> {
            while (true) {
                DiscordRPC.discordRunCallbacks();
                if (!ready)
                    continue;

                try {
                    Thread.sleep(10000);
                    updateDiscordStatus(chunky.getSceneManager().getScene().name());
                } catch (InterruptedException e) {
                    return;
                }
            }
        }).start();

        chunky.getRenderController().getRenderManager().addRenderListener(new RenderStatusListener() {
            @Override
            public void setRenderTime(long l) {
                rendertime.set(l);
            }

            @Override
            public void setSamplesPerSecond(int i) {
                sps.set(i);
            }

            @Override
            public void setSpp(int i) {
                spp.set(i);
                totalSpp.set(chunky.getRenderController().getSceneManager().getScene().getTargetSpp());
            }

            @Override
            public void renderStateChanged(RenderMode renderMode) {
                DiscordPlugin.this.renderMode.set(renderMode.name());
                if (renderMode == RenderMode.RENDERING) {
                    rendertime.set(0);
                }
            }
        });
    }

    private void updateDiscordStatus(String name) {
        if (ready) {
            if (renderMode.get().equals("RENDERING")) {
                Duration renderTime = Duration.ofMillis(rendertime.get());
                NumberFormat numberFormat = NumberFormat.getInstance(Locale.US);
                String state = String.format("%s/%s SPP (%s SPS)",
                        numberFormat.format(spp.get()),
                        numberFormat.format(totalSpp.get()),
                        numberFormat.format(sps.get())
                );
                String details = String.format("Rendering " + name + " (%dh %dm)",
                        renderTime.toHours(),
                        renderTime.toMinutes() - renderTime.toHours() * 60
                );
                DiscordRPC.discordUpdatePresence(new DiscordRichPresence.Builder(state).setDetails(details).build());
            } else {
                DiscordRichPresence.Builder presence = new DiscordRichPresence.Builder("");
                DiscordRPC.discordUpdatePresence(presence.build());
            }
        }
    }

    public static void main(String[] args) throws Exception {
        // Start Chunky normally with this plugin attached.
        Chunky.loadDefaultTextures();
        Chunky chunky = new Chunky(ChunkyOptions.getDefaults());
        new DiscordPlugin().attach(chunky);
        ChunkyFx.startChunkyUI(chunky);
    }

    private static void initDiscord() {
        DiscordEventHandlers handlers = new DiscordEventHandlers.Builder().setReadyEventHandler((user) -> {
            ready = true;
            System.out.println("[Discord] Connected to " + user.username + "#" + user.discriminator);
            DiscordRichPresence.Builder presence = new DiscordRichPresence.Builder("");
            DiscordRPC.discordUpdatePresence(presence.build());
        }).build();
        DiscordRPC.discordInitialize(BuildConfig.DISCORD_CLIENT_ID, handlers, true);
    }
}
