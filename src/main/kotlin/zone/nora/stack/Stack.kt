package zone.nora.stack

import net.minecraftforge.client.ClientCommandHandler
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.Mod.EventHandler
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import zone.nora.stack.command.Command

@Mod(modid = "stack", name = "Stack!", version = "1.0")
class Stack {
    @EventHandler
    fun onInit(event: FMLInitializationEvent) {
        ClientCommandHandler.instance.registerCommand(Command())
    }
}