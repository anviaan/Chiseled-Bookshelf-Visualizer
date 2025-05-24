package net.anvian.chiseledbookshelfvisualizer.client.render;

import com.github.fracpete.romannumerals4j.RomanNumeralFormat;
import net.anvian.chiseledbookshelfvisualizer.ChiseledBookshelfVisualizerClient;
import net.anvian.chiseledbookshelfvisualizer.client.data.BookInfo;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.EnchantmentTags;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Formatting;

@Environment(EnvType.CLIENT)
public class BookInfoRenderer {
    private static boolean renderCrosshair = true;

    public static void toggleCrosshair() {
        renderCrosshair = !renderCrosshair;
    }

    public static boolean shouldRenderCrosshair() {
        return renderCrosshair;
    }

    public static void hudRender(DrawContext context, MinecraftClient client) {
        if (!shouldRenderCrosshair() || !ChiseledBookshelfVisualizerClient.isModAvailable() || client.options.hudHidden)
            return;

        if (!ChiseledBookshelfVisualizerClient.getBookshelfState().isCurrentBookDataToggled) return;

        final BookInfo currentBookInfo = ChiseledBookshelfVisualizerClient.getCurrentBookInfo();
        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();
        int x = screenWidth / 2;
        int y = screenHeight / 2;
        final ItemStack itemStack = currentBookInfo.itemStack;
        int color = 0xFFFFFFFF;
        final Integer colorValue = itemStack.getRarity().getFormatting().getColorValue();
        if (colorValue != null) color = colorValue;

        float scale = (float) ChiseledBookshelfVisualizerClient.CONFIG.scale();
        drawScaledText(context, itemStack.getName(), x, y + ((int) (10 * scale)), color, client.textRenderer);

        ItemEnchantmentsComponent storedComponents = itemStack.getComponents().get(DataComponentTypes.STORED_ENCHANTMENTS);
        if (storedComponents != null) {
            int i = ((int) (20 * scale));
            for (RegistryEntry<Enchantment> enchantment : storedComponents.getEnchantments()) {
                int level = storedComponents.getLevel(enchantment);
                MutableText enchantmentText;
                if (!ChiseledBookshelfVisualizerClient.CONFIG.useRoman() || level == -1) {
                    enchantmentText = enchantment.value().description().copy();
                    if (level != 1) enchantmentText.append(" " + level);
                } else if (level != 1) {
                    enchantmentText = enchantment.value().description().copy().append(" " + new RomanNumeralFormat().format(level));
                } else {
                    enchantmentText = enchantment.value().description().copy();
                }
                Style style = enchantment.isIn(EnchantmentTags.CURSE) ? Style.EMPTY.withColor(Formatting.RED) : Style.EMPTY.withColor(Formatting.GRAY);
                Texts.setStyleIfAbsent(enchantmentText, style);
                drawScaledText(context, enchantmentText, x, y + i, 0xFFFFFFFF, client.textRenderer);
                i += (int) (10 * scale);
            }
        }

        var writtenBookContentComponent = itemStack.getComponents().get(DataComponentTypes.WRITTEN_BOOK_CONTENT);
        if (writtenBookContentComponent != null) {
            drawScaledText(context, Text.translatable("book.byAuthor", writtenBookContentComponent.author()), x, y + (int) (20 * scale), 0xFFFFFFFF, client.textRenderer);
        }
    }

    private static void drawScaledText(DrawContext context, Text text, int centerX, int y, int color, TextRenderer textRenderer) {
        MatrixStack stack = context.getMatrices();
        stack.push();
        stack.translate(centerX, y, 0);
        final float scale = (float) ChiseledBookshelfVisualizerClient.CONFIG.scale();
        stack.scale(scale, scale, scale);
        stack.translate(-centerX, -y, 0);
        context.drawCenteredTextWithShadow(textRenderer, text, centerX, y, color);
        stack.pop();
    }
}