package net.anvian.chiseledbookshelfvisualizer.client.render;

import com.github.fracpete.romannumerals4j.RomanNumeralFormat;
import net.anvian.chiseledbookshelfvisualizer.ChiseledBookshelfVisualizerClient;
import net.anvian.chiseledbookshelfvisualizer.client.data.BookInfo;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.joml.Matrix3x2fStack;

@Environment(EnvType.CLIENT)
public class BookInfoRenderer {
    private static final int DEFAULT_COLOR = 0xFFFFFFFF;
    private static final int ENCHANTMENT_Y_OFFSET = 10;
    private static final int NAME_Y_OFFSET = 10;
    private static final int ENCHANTMENT_START_Y = 20;
    private static boolean renderCrosshair = true;

    public static void toggleCrosshair() {
        renderCrosshair = !renderCrosshair;
    }

    public static boolean shouldRenderCrosshair() {
        return renderCrosshair;
    }

    public static void hudRender(GuiGraphicsExtractor context, Minecraft client) {
        if (!shouldRenderCrosshair() || !ChiseledBookshelfVisualizerClient.isModAvailable() || client.options.hideGui) {
            return;
        }
        if (!ChiseledBookshelfVisualizerClient.getBookshelfState().isCurrentBookDataToggled) {
            return;
        }

        BookInfo currentBookInfo = ChiseledBookshelfVisualizerClient.getCurrentBookInfo();
        ItemStack itemStack = currentBookInfo.itemStack;
        if (itemStack == null) {
            return;
        }

        int x = context.guiWidth() / 2;
        int y = context.guiHeight() / 2;
        float scale = (float) ChiseledBookshelfVisualizerClient.CONFIG.scale();

        MutableComponent name = itemStack.getHoverName().copy();
        Style styleName = itemStack.getRarity().color() == ChatFormatting.WHITE
                ? Style.EMPTY.withColor(DEFAULT_COLOR)
                : Style.EMPTY.withColor(itemStack.getRarity().color());
        name.withStyle(styleName);
        drawScaledText(context, name, x, y + (int) (NAME_Y_OFFSET * scale), client.font);

        ItemEnchantments storedEnchantments = itemStack.getComponents().get(DataComponents.STORED_ENCHANTMENTS);
        if (storedEnchantments != null) {
            int offset = (int) ((NAME_Y_OFFSET + ENCHANTMENT_Y_OFFSET) * scale);
            for (Holder<Enchantment> enchantment : storedEnchantments.keySet()) {
                int level = storedEnchantments.getLevel(enchantment);
                MutableComponent enchantmentText;
                if (!ChiseledBookshelfVisualizerClient.CONFIG.useRoman() || level == -1) {
                    String suffix = level == 1 ? "" : " " + level;
                    enchantmentText = enchantment.value().description().copy().append(suffix);
                } else if (level != 1) {
                    enchantmentText = enchantment.value().description().copy().append(" " + new RomanNumeralFormat().format(level));
                } else {
                    enchantmentText = enchantment.value().description().copy();
                }

                Style style = enchantment.is(EnchantmentTags.CURSE)
                        ? Style.EMPTY.withColor(ChatFormatting.RED)
                        : Style.EMPTY.withColor(ChatFormatting.GRAY);
                enchantmentText.withStyle(style);
                drawScaledText(context, enchantmentText, x, y + offset, client.font);
                offset += (int) (ENCHANTMENT_Y_OFFSET * scale);
            }
        }

        var writtenBookContent = itemStack.getComponents().get(DataComponents.WRITTEN_BOOK_CONTENT);
        if (writtenBookContent != null) {
            drawScaledText(
                    context,
                    Component.translatable("book.byAuthor", writtenBookContent.author()),
                    x,
                    y + (int) (ENCHANTMENT_START_Y * scale),
                    client.font
            );
        }
    }

    private static void drawScaledText(GuiGraphicsExtractor context, Component text, int centerX, int y, Font font) {
        Matrix3x2fStack stack = context.pose();
        stack.pushMatrix();
        stack.translate(centerX, y);
        float scale = (float) ChiseledBookshelfVisualizerClient.CONFIG.scale();
        stack.scale(scale, scale);
        stack.translate(-centerX, -y);
        context.centeredText(font, text, centerX, y, DEFAULT_COLOR);
        stack.popMatrix();
    }
}