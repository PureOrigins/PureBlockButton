package it.pureorigins.faketitlebuttons

import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.ActionResult
import net.minecraft.util.math.BlockPos

abstract class Region {
    val hovering: HashSet<ServerPlayerEntity> = HashSet()
    abstract operator fun contains(pos: BlockPos): Boolean
}

fun Region.onClick(
    click: (ServerPlayerEntity) -> Unit,
    release: (ServerPlayerEntity) -> Unit = {},
    delay: Long = FakeTitleButtons.config.defaultDelay
) {
    FakeTitleButtons.registerClickListener { playerEntity, blockPos ->
        if (blockPos in this) {
            click(playerEntity)
            FakeTitleButtons.scheduleRelease(delay) { release(playerEntity) }
        }
        ActionResult.PASS
    }
}

fun Region.onHover(hover: (ServerPlayerEntity) -> Unit, hoverOff: (ServerPlayerEntity) -> Unit = {}) {
    FakeTitleButtons.registerLookAtListener { player, blockPos ->
        if (blockPos in this && hovering.add(player)) hover(player)
        else if (blockPos !in this && hovering.remove(player)) hoverOff(player)
        ActionResult.PASS
    }
}
