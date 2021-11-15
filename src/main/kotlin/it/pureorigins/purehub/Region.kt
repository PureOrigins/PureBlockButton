package it.pureorigins.purehub

import net.fabricmc.fabric.api.event.player.AttackBlockCallback
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.ActionResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import org.apache.logging.log4j.LogManager

interface Region {
    operator fun contains(pos: BlockPos): Boolean
}

fun Region.onClick(listener: (PlayerEntity, World) -> Unit) {
    AttackBlockCallback.EVENT.register { playerEntity, world, _, blockPos, _ ->
        if (this.contains(blockPos)) listener(playerEntity, world)
        LogManager.getLogger().info(blockPos.toString())
        ActionResult.PASS
    }
}

fun Region.onRelease(listener: (PlayerEntity, World) -> Unit) {
    TODO("Not yet implemented")
}

fun Region.onHover(listener: (PlayerEntity, World) -> Unit) {
    TODO("Not yet implemented")
}

fun Region.onHoverOff(listener: (PlayerEntity, World) -> Unit) {
    TODO("Not yet implemented")
}