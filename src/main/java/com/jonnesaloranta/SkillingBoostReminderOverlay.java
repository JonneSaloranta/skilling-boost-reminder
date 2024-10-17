package com.jonnesaloranta;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.widgets.Widget;
import net.runelite.client.ui.overlay.Overlay;

import javax.inject.Inject;
import java.awt.*;

@Slf4j
public class SkillingBoostReminderOverlay extends Overlay {

    @Getter
    public boolean isHighlighted = false;
    private final Client client;
    private final SkillingBoostReminderConfig config;


    private final int specWidgetID = 10485794;

    @Inject
    private SkillingBoostReminderOverlay(Client client, SkillingBoostReminderConfig config) {
        this.client = client;
        this.config = config;
    }

    public void setHighlighted(boolean b) {
        isHighlighted = b;
    }

    @Override
    public Dimension render(Graphics2D graphics2D) {
        Widget specWidget = client.getWidget(specWidgetID);
        Color color = config.highlightColor();

        if (specWidget == null) {
            return null;
        }

        Rectangle bounds = specWidget.getBounds();

        if (isHighlighted && config.highlightSpecWidget()) {


            Polygon arrow = new Polygon();
            int arrowWidth = bounds.width / 4;
            int arrowHeight = bounds.height / 2;  // Adjust arrow height

            int offset = config.offsetY();

            // Set the points for the arrow (upward pointing triangle)
            arrow.addPoint(bounds.x + bounds.width / 2, bounds.y + offset + 25); // Top point
            arrow.addPoint(bounds.x + (bounds.width / 2) - arrowWidth, bounds.y + arrowHeight + offset + 25); // Left
            // point
            arrow.addPoint(bounds.x + (bounds.width / 2) + arrowWidth, bounds.y + arrowHeight + offset + 25); // Right
            // point

            graphics2D.setColor(color);
            graphics2D.fill(arrow);
        }

        return null;
    }

}
