package net.walksanator.spectralsync

import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level

class SyncBook(properties: Properties) : Item(properties) {
    override fun use(
        level: Level,
        player: Player,
        interactionHand: InteractionHand
    ): InteractionResultHolder<ItemStack> {
        val stack = player.getItemInHand(interactionHand)
        if (level.isClientSide()) {
            return super.use(level,player,interactionHand)
        }
        level as ServerLevel
        player as ServerPlayer

        val adv = player.advancements
        val srvAdv  = level.server.advancements
        val comp = stack.get(SpectralSync.SYNC_COMPONENT)
        if ( (comp?.size?: 0) == 0 || player.isShiftKeyDown) {
            val advs = srvAdv.allAdvancements.filter {
                val prog = adv.getOrStartProgress(it)
                prog.isDone &&
                        // I should probally cache a set of every advancment ID so it doesn't iterate EVERY advancement on right-click
                        // but I am lazy so servers suffer.
                        (it.id.namespace == "spectrum" || it.id.namespace == "pastel")
            }.map {it.id}
            stack.set(SpectralSync.SYNC_COMPONENT, advs.toMutableList())
        } else {
            comp!!
                .mapNotNull { srvAdv.get(it) }
                .forEach {
                    val prog = adv.getOrStartProgress(it)
                    prog.remainingCriteria.forEach { crit ->
                        adv.award(it,crit)
                    }
                }
        }

        return super.use(level, player, interactionHand)
    }

    override fun isFoil(itemStack: ItemStack): Boolean {
        val tag = itemStack.get<MutableList<*>>(SpectralSync.SYNC_COMPONENT)?.size?: 0
        return tag > 0
    }
}