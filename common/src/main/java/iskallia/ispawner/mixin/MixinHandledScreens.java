package iskallia.ispawner.mixin;

import iskallia.ispawner.init.ModScreens;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HandledScreens.class)
public class MixinHandledScreens {

    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void staticInit(CallbackInfo ci) {
        ModScreens.register();
    }

}
