package net.anvian.chiseledbookshelfvisualizer.client.render;

import com.github.fracpete.romannumerals4j.RomanNumeralFormat;
import net.anvian.chiseledbookshelfvisualizer.ChiseledBookshelfVisualizerClient;
import net.anvian.chiseledbookshelfvisualizer.client.data.BookInfo;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
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

        float scale = (float) ChiseledBookshelfVisualizerClient.CONFIG.scale();

        MutableText name = itemStack.getName().copy();
        Style styleName = itemStack.getRarity().getFormatting() == Formatting.WHITE
                ? Style.EMPTY.withColor(DEFAULT_COLOR)
                : Style.EMPTY.withColor(itemStack.getRarity().getFormatting());

        Texts.setStyleIfAbsent(name, styleName);
        drawScaledText(context, name, x, y + (int) (NAME_Y_OFFSET * scale), client.textRenderer);

        ItemEnchantmentsComponent storedComponents = itemStack.getComponents().get(DataComponentTypes.STORED_ENCHANTMENTS);
        if (storedComponents != null) {
            int i = (int) ((NAME_Y_OFFSET + ENCHANTMENT_Y_OFFSET) * scale);
            for (RegistryEntry<Enchantment> enchantment : storedComponents.getEnchantments()) {
                String lvl = "";
                final int level = storedComponents.getLevel(enchantment);
                if (level != 1) lvl = String.valueOf(level);

                final MutableText enchantmentText;
                if (!ChiseledBookshelfVisualizerClient.CONFIG.useRoman() || level == -1)
                    enchantmentText = enchantment.value().description().copy().append(" " + lvl);
                else if (level != 1)
                    enchantmentText = enchantment.value().description().copy().append(" " + new RomanNumeralFormat().format(level));
                else
                    enchantmentText = enchantment.value().description().copy();

                Style style = enchantment.isIn(EnchantmentTags.CURSE)
                        ? Style.EMPTY.withColor(Formatting.RED)
                        : Style.EMPTY.withColor(Formatting.GRAY);
                Texts.setStyleIfAbsent(enchantmentText, style);
                drawScaledText(context, enchantmentText, x, y + i, client.textRenderer);
                i += (int) (ENCHANTMENT_Y_OFFSET * scale);
            }
        }

        var writtenBookContentComponent = itemStack.getComponents().get(DataComponentTypes.WRITTEN_BOOK_CONTENT);
        if (writtenBookContentComponent != null) {
            drawScaledText(context, Text.translatable("book.byAuthor", writtenBookContentComponent.author()), x, y + (int) (ENCHANTMENT_START_Y * scale), client.textRenderer);
        }
    }

    private static void drawScaledText(DrawContext context, Text text, int centerX, int y, TextRenderer textRenderer) {
        Matrix3x2fStack stack = context.getMatrices();
        stack.pushMatrix();
        stack.translate(centerX, y);
        float scale = (float) ChiseledBookshelfVisualizerClient.CONFIG.scale();
        stack.scale(scale, scale);
        stack.translate(-centerX, -y);
        context.drawCenteredTextWithShadow(textRenderer, text, centerX, y, BookInfoRenderer.DEFAULT_COLOR);
        stack.popMatrix();
    }
}