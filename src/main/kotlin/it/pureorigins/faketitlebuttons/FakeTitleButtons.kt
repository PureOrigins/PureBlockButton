package it.pureorigins.faketitlebuttons

import net.fabricmc.api.ModInitializer
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import org.apache.logging.log4j.LogManager

object FakeTitleButtons : ModInitializer {
    val clickListeners: MutableList<(ServerPlayerEntity, BlockPos) -> Unit> = mutableListOf()
    val lookAtListeners: MutableList<(ServerPlayerEntity, BlockPos) -> Unit> = mutableListOf()

    override fun onInitialize() {
        println("FakeTitleButtons has been initialized!")
        val btn = CuboidRegion(BlockPos(0, 0, 0), BlockPos(10, 10, 10))
        btn.onClick { LogManager.getLogger().info("Clicked!") }
    }

    fun registerClickListener(listener: (playerEntity: ServerPlayerEntity, pos: BlockPos) -> Unit) {
        clickListeners.add(listener)
    }

    fun registerLookAtListener(listener: (playerEntity: ServerPlayerEntity, pos: BlockPos) -> Unit) {
        lookAtListeners.add(listener)
    }
}
