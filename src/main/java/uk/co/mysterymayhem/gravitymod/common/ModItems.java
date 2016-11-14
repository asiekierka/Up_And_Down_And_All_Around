package uk.co.mysterymayhem.gravitymod.common;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.ShapedOreRecipe;
import uk.co.mysterymayhem.gravitymod.GravityMod;
import uk.co.mysterymayhem.gravitymod.common.items.ItemAntiGravityBoots;
import uk.co.mysterymayhem.gravitymod.common.items.ItemCreativeTabIcon;
import uk.co.mysterymayhem.gravitymod.common.items.ItemPersonalGravityController;

/**
 * Created by Mysteryem on 2016-08-08.
 */
public class ModItems {
    private static final ItemCreativeTabIcon CREATIVE_TAB_ICON = new ItemCreativeTabIcon();
    public static final CreativeTabs UP_AND_DOWN_CREATIVE_TAB = new CreativeTabs(GravityMod.MOD_ID) {
        @Override
        public Item getTabIconItem() {
            return CREATIVE_TAB_ICON;
        }
    };
    public static ItemAntiGravityBoots antiGravityBoots;
    public static ItemPersonalGravityController personalGravityController;

    public static void initItems() {
//        antiGravityBoots = new ItemAntiGravityBoots();
        CREATIVE_TAB_ICON.init();
        personalGravityController = new ItemPersonalGravityController();
    }

    @SideOnly(Side.CLIENT)
    public static void initModels() {
//        antiGravityBoots.initModel();
        CREATIVE_TAB_ICON.initModel();
        personalGravityController.initModel();
    }

    public static void initRecipes() {
//        GameRegistry.addRecipe(new ShapedOreRecipe(
//                antiGravityBoots,
//
//                "IGI",
//                "IPI",
//                "YGY",
//
//                'I', "ingotIron",
//                // Grey wool
//                'G', new ItemStack(Blocks.WOOL, 1, 7),
//                'P', Items.ENDER_PEARL,
//                // Yellow wool
//                'Y', new ItemStack(Blocks.WOOL, 1, 4)));
//        GameRegistry.addRecipe(new ShapedOreRecipe(
//                antiGravityBoots,
//
//                "YGY",
//                "IPI",
//                "IGI",
//
//                'I', "ingotIron",
//                // Grey wool
//                'G', new ItemStack(Blocks.WOOL, 1, 7),
//                'P', Items.ENDER_PEARL,
//                // Yellow wool
//                'Y', new ItemStack(Blocks.WOOL, 1, 4)));
        GameRegistry.addRecipe(new ShapedOreRecipe(
                new ItemStack(personalGravityController, 1, ItemPersonalGravityController.DEFAULT_META),

                "IDI",
                "DPD",
                "IDI",

                'I', "ingotIron",
                'D', Blocks.DIRT,
                'P', Items.ENDER_PEARL
        ));
//        GameRegistry.addRecipe(new ShapedOreRecipe(
//                personalGravityController,
//
//                "IDI",
//                "DSD",
//                "IDI",
//
//                'S', "ingotIron",
//                'D', Blocks.DIRT,
//                'P', Items.ENDER_PEARL
//        ));
    }
}