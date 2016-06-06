package gtlp.prettyniceores.events;

import gtlp.prettyniceores.PrettyNiceOres;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Optional;

/**
 * Created by Marv1 on 06.06.2016.
 */
public class OnPlayerLoginEvent {

    boolean notified = false;

    @SubscribeEvent
    public void versionCheck(EntityJoinWorldEvent event) {
        if (!notified && event.getEntity() instanceof EntityPlayer && event.getWorld().isRemote) {
            EntityPlayer player = (EntityPlayer) event.getEntity();
            Optional<ModContainer> modContainer = Loader.instance().getModList().stream().filter(container -> container.getModId().equals(PrettyNiceOres.MOD_ID)).findFirst();
            ForgeVersion.CheckResult checkResult = ForgeVersion.getResult(modContainer.get());
            if (checkResult.status == ForgeVersion.Status.OUTDATED) {
                ITextComponent updateMsg = new TextComponentString(TextFormatting.GOLD.toString() + "A new version of " + TextFormatting.DARK_GREEN.toString() + "PrettyNiceOres" + TextFormatting.GOLD.toString() + " is out! Go grab it " + TextFormatting.BLUE.toString() + "here!");
                ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.OPEN_URL, checkResult.url);
                updateMsg.getStyle().setClickEvent(clickEvent);
                player.addChatComponentMessage(updateMsg);
                notified = true;
            }
        }
    }
}
