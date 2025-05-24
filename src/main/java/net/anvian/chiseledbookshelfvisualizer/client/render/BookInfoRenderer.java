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

    public static void hudRender(DrawContext context, MinecraftClient client) {
        if (!shouldRenderCrosshair() || !ChiseledBookshelfVisualizerClient.isModAvailable() || client.options.hudHidden)
            return;

        if (!ChiseledBookshelfVisualizerClient.getBookshelfState().isCurrentBookDataToggled) return;

        final BookInfo currentBookInfo = ChiseledBookshelfVisualizerClient.getCurrentBookInfo();
        final ItemStack itemStack = currentBookInfo.itemStack;
        if (itemStack == null) return;

        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();
        int x = screenWidth / 2;
        int y = screenHeight / 2;
        int color = itemStack.getRarity().getFormatting().getColorValue() != null
                ? itemStack.getRarity().getFormatting().getColorValue()
                : DEFAULT_COLOR;

        float scale = (float) ChiseledBookshelfVisualizerClient.CONFIG.scale();
        drawScaledText(context, itemStack.getName(), x, y + (int) (NAME_Y_OFFSET * scale), color, client.textRenderer);

        ItemEnchantmentsComponent storedComponents = itemStack.getComponents().get(DataComponentTypes.STORED_ENCHANTMENTS);
        if (storedComponents != null) {
            int i = (int) (ENCHANTMENT_START_Y * scale);
            for (RegistryEntry<Enchantment> enchantment : storedComponents.getEnchantments()) {
                int level = storedComponents.getLevel(enchantment);
                MutableText enchantmentText = enchantment.value().description().copy();
                if (level > 1) {
                    String levelStr = ChiseledBookshelfVisualizerClient.CONFIG.useRoman()
                            ? " " + new RomanNumeralFormat().format(level)
                            : " " + level;
                    enchantmentText.append(levelStr);
                }
                Style style = enchantment.isIn(EnchantmentTags.CURSE)
                        ? Style.EMPTY.withColor(Formatting.RED)
                        : Style.EMPTY.withColor(Formatting.GRAY);
                Texts.setStyleIfAbsent(enchantmentText, style);
                drawScaledText(context, enchantmentText, x, y + i, DEFAULT_COLOR, client.textRenderer);
                i += (int) (ENCHANTMENT_Y_OFFSET * scale);
            }
        }

        var writtenBookContentComponent = itemStack.getComponents().get(DataComponentTypes.WRITTEN_BOOK_CONTENT);
        if (writtenBookContentComponent != null) {
            drawScaledText(context, Text.translatable("book.byAuthor", writtenBookContentComponent.author()), x, y + (int) (ENCHANTMENT_START_Y * scale), DEFAULT_COLOR, client.textRenderer);
        }
    }

    private static void drawScaledText(DrawContext context, Text text, int centerX, int y, int color, TextRenderer textRenderer) {
        MatrixStack stack = context.getMatrices();
        stack.push();
        stack.translate(centerX, y, 0);
        float scale = (float) ChiseledBookshelfVisualizerClient.CONFIG.scale();
        stack.scale(scale, scale, scale);
        stack.translate(-centerX, -y, 0);
        context.drawCenteredTextWithShadow(textRenderer, text, centerX, y, color);
        stack.pop();
    }
}