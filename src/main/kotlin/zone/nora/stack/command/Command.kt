package zone.nora.stack.command

import net.minecraft.client.Minecraft
import net.minecraft.command.CommandBase
import net.minecraft.command.ICommandSender
import zone.nora.stack.gui.StackGUI
import zone.nora.stack.util.DelayedTask

class Command : CommandBase() {
    override fun getCommandName(): String = "stack"

    override fun getCommandUsage(sender: ICommandSender?): String = "/stack"

    override fun processCommand(sender: ICommandSender?, args: Array<out String>?) {
        DelayedTask({ Minecraft.getMinecraft().displayGuiScreen(StackGUI()) }, 1)
    }

    override fun canCommandSenderUseCommand(sender: ICommandSender?): Boolean = true
}