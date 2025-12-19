package net.walksanator.spectralsync

import net.fabricmc.api.ModInitializer
import net.minecraft.core.Registry
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.item.Rarity
import org.slf4j.LoggerFactory
import java.util.function.UnaryOperator


object SpectralSync : ModInitializer {
    private val logger = LoggerFactory.getLogger("spectral-sync")

	val SYNC_COMPONENT: DataComponentType<MutableList<ResourceLocation>> =
		Registry.register(
			BuiltInRegistries.DATA_COMPONENT_TYPE,
			ResourceLocation.fromNamespaceAndPath("spectral-sync", "sync_comp"),
			DataComponentType.builder<MutableList<ResourceLocation>>().persistent(ResourceLocation.CODEC.listOf()).build()
		)

	val SYNC_BOOK = SyncBook(
		Item.Properties()
		.rarity(Rarity.UNCOMMON).stacksTo(1)
		.component(SYNC_COMPONENT, mutableListOf())
	)

	override fun onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		logger.info("Hello Fabric world!")
		Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath("spectral-sync", "syncbook"), SYNC_BOOK);

	}
}