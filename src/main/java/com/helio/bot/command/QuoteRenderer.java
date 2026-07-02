package com.helio.bot.command;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public final class QuoteRenderer {
    private QuoteRenderer() {
    }

    public static byte[] render(String text, String author) throws Exception {
        int width = 800;
        Font font = new Font("SansSerif", Font.PLAIN, 32);

        BufferedImage tmp = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        Graphics2D g0 = tmp.createGraphics();
        g0.setFont(font);
        FontMetrics fm = g0.getFontMetrics();
        List<String> lines = wrap(text, fm, width - 80);
        int lineHeight = fm.getHeight();
        g0.dispose();

        int height = Math.max(200, 80 + lines.size() * lineHeight + (author != null ? 50 : 0));
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(new Color(24, 26, 32));
        g.fillRect(0, 0, width, height);
        g.setColor(new Color(255, 215, 0));
        g.fillRect(0, 0, 8, height);
        g.setFont(font);
        g.setColor(Color.WHITE);
        int y = 60;
        for (String line : lines) {
            g.drawString(line, 40, y);
            y += lineHeight;
        }
        if (author != null) {
            g.setFont(new Font("SansSerif", Font.ITALIC, 26));
            g.setColor(new Color(180, 180, 180));
            g.drawString("— " + author, 40, y + 20);
        }
        g.dispose();

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(img, "png", bos);
        return bos.toByteArray();
    }

    private static List<String> wrap(String text, FontMetrics fm, int maxWidth) {
        List<String> lines = new ArrayList<>();
        for (String para : text.split("\n")) {
            StringBuilder cur = new StringBuilder();
            for (String word : para.split(" ")) {
                String test = cur.length() == 0 ? word : cur + " " + word;
                if (fm.stringWidth(test) > maxWidth && cur.length() > 0) {
                    lines.add(cur.toString());
                    cur = new StringBuilder(word);
                } else {
                    cur = new StringBuilder(test);
                }
            }
            lines.add(cur.toString());
        }
        return lines;
    }
}
