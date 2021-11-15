package it.pureorigins.faketitlebuttons

import it.pureorigins.framework.configuration.configFile
import it.pureorigins.framework.configuration.json
import it.pureorigins.framework.configuration.readFileAs
import kotlinx.serialization.Serializable
import net.fabricmc.api.ModInitializer
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.math.BlockPos
import org.apache.logging.log4j.LogManager
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

object FakeTitleButtons : ModInitializer {
    val clickListeners: MutableList<(ServerPlayerEntity, BlockPos) -> Unit> = mutableListOf()
    val lookAtListeners: MutableList<(ServerPlayerEntity, BlockPos) -> Unit> = mutableListOf()
    private lateinit var scheduler: ScheduledExecutorService
    lateinit var config: Config

    override fun onInitialize() {
        config = json.readFileAs(configFile("fancyparticles.json"), Config())
        scheduler = Executors.newScheduledThreadPool(4)
        println("FakeTitleButtons has been initialized!")
        //Test
        val btn = CuboidRegion(BlockPos(0, 0, 0), BlockPos(10, 10, 10))
        btn.onClick ({ LogManager.getLogger().info("Clicked!") })
        btn.onHover ({ LogManager.getLogger().info("Hovered!") })
    }

    fun registerClickListener(listener: (playerEntity: ServerPlayerEntity, pos: BlockPos) -> Unit) {
        clickListeners.add(listener)
    }

    fun registerLookAtListener(listener: (playerEntity: ServerPlayerEntity, pos: BlockPos) -> Unit) {
        lookAtListeners.add(listener)
    }

    fun scheduleRelease(delay: Long, runnable: Runnable) {
        scheduler.schedule(runnable, delay, TimeUnit.SECONDS)
    }

    @Serializable
    data class Config(
        val defaultDelay: Long = 1,
        val includeFluids: Boolean = false
    )
}
