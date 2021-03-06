package uk.co.mysterymayhem.gravitymod;

import com.google.common.collect.Lists;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.registry.GameRegistry;
import uk.co.mysterymayhem.gravitymod.common.capabilities.gravitydirection.GravityDirectionCapability;
import uk.co.mysterymayhem.gravitymod.common.config.ConfigHandler;
import uk.co.mysterymayhem.gravitymod.common.entities.EntityFloatingItem;
import uk.co.mysterymayhem.gravitymod.common.items.materials.ItemGravityDust;
import uk.co.mysterymayhem.gravitymod.common.listeners.FallOutOfWorldUpwardsListenerCommon;
import uk.co.mysterymayhem.gravitymod.common.listeners.GravityManagerCommon;
import uk.co.mysterymayhem.gravitymod.common.listeners.ItemStackUseListener;
import uk.co.mysterymayhem.gravitymod.common.listeners.LootTableListener;
import uk.co.mysterymayhem.gravitymod.common.packets.PacketHandler;
import uk.co.mysterymayhem.gravitymod.common.registries.*;
import uk.co.mysterymayhem.gravitymod.common.world.generation.ore.Generator;
import uk.co.mysterymayhem.mystlib.setup.IFMLStaged;
import uk.co.mysterymayhem.mystlib.setup.registries.AbstractIFMLStagedRegistry;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Mysteryem on 2016-08-04.
 */
public class CommonProxy extends AbstractIFMLStagedRegistry<IFMLStaged, ArrayList<IFMLStaged>> {

    public GravityManagerCommon gravityManagerCommon;

    public CommonProxy() {
        super(new ArrayList<>());
    }

    @Override
    public void preInit() {
        GravityDirectionCapability.registerCapability();
        this.registerGravityManager();
        PacketHandler.registerMessages();
        super.preInit();
    }

    public void registerGravityManager() {
        this.gravityManagerCommon = new GravityManagerCommon();
    }

    @Override
    public void init() {
        super.init();
        this.registerListeners();
        GameRegistry.registerWorldGenerator(Generator.INSTANCE, 0);
    }

    public void registerListeners() {
        MinecraftForge.EVENT_BUS.register(this.getGravityManager());
        MinecraftForge.EVENT_BUS.register(ItemStackUseListener.class);
        MinecraftForge.EVENT_BUS.register(ItemGravityDust.BlockBreakListener.class);
        MinecraftForge.EVENT_BUS.register(EntityFloatingItem.class);
        MinecraftForge.EVENT_BUS.register(LootTableListener.class);
        MinecraftForge.EVENT_BUS.register(Generator.class);
        MinecraftForge.EVENT_BUS.register(ConfigHandler.class);
        this.createSidedEventListeners().forEach(MinecraftForge.EVENT_BUS::register);
//        MinecraftForge.EVENT_BUS.register(new DebugHelperListener());
    }

    public GravityManagerCommon getGravityManager() {
        return this.gravityManagerCommon;
    }

    public Collection<?> createSidedEventListeners() {
        return Lists.newArrayList(new FallOutOfWorldUpwardsListenerCommon());
    }

    @Override
    protected void addToCollection(ArrayList<IFMLStaged> modObjects) {
        modObjects.add(new ModItems());
        modObjects.add(new ModBlocks());
        modObjects.add(new ModEntities());
        modObjects.add(new ModTileEntities());
        modObjects.add(new ModGUIs());
        modObjects.add(new ModPotions());
    }
}
