package com.helio.bot.commands.media;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.helio.bot.core.Category;
import com.helio.bot.core.Command;
import com.helio.bot.core.CommandContext;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class QrCodeCommand implements Command {

    @Override
    public String name() {
        return "qrcode";
    }

    @Override
    public String description() {
        return "Генерация QR-кода из текста.";
    }

    @Override
    public Category category() {
        return Category.MEDIA;
    }

    @Override
    public void execute(CommandContext ctx) throws Exception {
        String text = ctx.argsTrimmed();
        if (text.isBlank()) {
            ctx.reply("Использование: /qrcode <текст>");
            return;
        }
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.MARGIN, 2);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        BitMatrix matrix = new QRCodeWriter().encode(text, BarcodeFormat.QR_CODE, 512, 512, hints);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(MatrixToImageWriter.toBufferedImage(matrix), "png", bos);
        String cap = text.length() > 50 ? text.substring(0, 50) + "…" : text;
        ctx.replyPhoto(bos.toByteArray(), "QR: " + cap);
    }
}
