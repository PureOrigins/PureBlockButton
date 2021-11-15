package it.pureorigins.purehub

import net.fabricmc.api.ModInitializer
import net.minecraft.util.math.BlockPos
import org.apache.logging.log4j.LogManager

object PureHub : ModInitializer {
    override fun onInitialize() {
        println("PureHub has been initialized!")
        val btn = CuboidRegion(BlockPos(0,0,0), BlockPos(10,10,10))
        btn.onClick { _, _ ->
            LogManager.getLogger().info("Clicked!")
        }
    }
}
