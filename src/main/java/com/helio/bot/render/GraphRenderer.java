package com.helio.bot.render;

import com.helio.bot.minecraft.StatsRepository;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/** Рендер линейного графика онлайна во времени (PNG), поддерживает несколько серий. */
public final class GraphRenderer {
    private GraphRenderer() {
    }

    private static final Color[] COLORS = {
            new Color(80, 180, 255), new Color(255, 180, 80),
            new Color(120, 220, 120), new Color(255, 120, 160),
            new Color(200, 160, 255), new Color(255, 220, 100)
    };

    public static byte[] render(String title, Map<String, List<StatsRepository.Sample>> series,
                                long fromTs, long toTs) throws Exception {
        int w = 900, h = 500, padL = 60, padR = 200, padT = 60, padB = 60;
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(new Color(24, 26, 32));
        g.fillRect(0, 0, w, h);
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 20));
        g.drawString(title, padL, 35);

        int maxY = 1;
        for (List<StatsRepository.Sample> s : series.values()) {
            for (StatsRepository.Sample p : s) {
                maxY = Math.max(maxY, p.online);
            }
        }
        maxY = (int) (maxY * 1.15) + 1;

        int plotW = w - padL - padR;
        int plotH = h - padT - padB;
        g.setColor(new Color(70, 74, 84));
        g.drawLine(padL, padT, padL, padT + plotH);
        g.drawLine(padL, padT + plotH, padL + plotW, padT + plotH);

        g.setFont(new Font("SansSerif", Font.PLAIN, 12));
        for (int i = 0; i <= 4; i++) {
            int y = padT + plotH - (plotH * i / 4);
            int val = maxY * i / 4;
            g.setColor(new Color(45, 48, 56));
            g.drawLine(padL, y, padL + plotW, y);
            g.setColor(new Color(160, 160, 160));
            g.drawString(String.valueOf(val), padL - 45, y + 4);
        }

        long span = Math.max(1, toTs - fromTs);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd.MM HH:mm").withZone(ZoneId.systemDefault());
        g.setColor(new Color(160, 160, 160));
        g.drawString(fmt.format(Instant.ofEpochSecond(fromTs)), padL, padT + plotH + 20);
        g.drawString(fmt.format(Instant.ofEpochSecond(toTs)), padL + plotW - 90, padT + plotH + 20);

        int ci = 0, legendY = padT;
        for (Map.Entry<String, List<StatsRepository.Sample>> e : series.entrySet()) {
            Color col = COLORS[ci % COLORS.length];
            ci++;
            g.setColor(col);
            int prevX = -1, prevY = -1;
            for (StatsRepository.Sample p : e.getValue()) {
                if (p.ts < fromTs || p.ts > toTs) {
                    continue;
                }
                int x = padL + (int) (plotW * (p.ts - fromTs) / span);
                int y = padT + plotH - (plotH * Math.min(p.online, maxY) / maxY);
                if (prevX >= 0) {
                    g.drawLine(prevX, prevY, x, y);
                }
                g.fillOval(x - 2, y - 2, 4, 4);
                prevX = x;
                prevY = y;
            }
            g.fillRect(padL + plotW + 20, legendY, 12, 12);
            g.setColor(Color.WHITE);
            String label = e.getKey();
            if (label.length() > 22) {
                label = label.substring(0, 21) + "…";
            }
            g.drawString(label, padL + plotW + 38, legendY + 11);
            legendY += 22;
        }
        g.dispose();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(img, "png", bos);
        return bos.toByteArray();
    }
}
