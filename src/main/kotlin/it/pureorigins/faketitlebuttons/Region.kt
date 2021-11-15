package it.pureorigins.faketitlebuttons

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.ActionResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import org.apache.logging.log4j.LogManager

interface Region {
    operator fun contains(pos: BlockPos): Boolean
}

fun Region.onClick(listener: (ServerPlayerEntity) -> Unit) {
    FakeTitleButtons.registerClickListener { playerEntity, blockPos ->
        if (blockPos in this) listener(playerEntity)
        LogManager.getLogger().info("Click: $blockPos.toString()")
        ActionResult.PASS
    }
}

fun Region.onRelease(listener: (ServerPlayerEntity, World) -> Unit) {
    TODO("Not yet implemented")
}

fun Region.onHover(listener: (ServerPlayerEntity) -> Unit) {
    FakeTitleButtons.registerLookAtListener { playerEntity, blockPos ->
        if (blockPos in this) listener(playerEntity)
        LogManager.getLogger().info("Hover: $blockPos.toString()")
        ActionResult.PASS
    }
}

fun Region.onHoverOff(listener: (ServerPlayerEntity, World) -> Unit) {
    TODO("Not yet implemented")
}