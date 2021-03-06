package zone.nora.stack.util;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class DelayedTask {
    private int counter;
    private final Runnable run;

    public DelayedTask(Runnable run, int ticks){
        counter = ticks;
        this.run = run;
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            if (counter <= 0) {
                MinecraftForge.EVENT_BUS.unregister(this);
                try {
                    run.run();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            counter--;
        }
    }
}
