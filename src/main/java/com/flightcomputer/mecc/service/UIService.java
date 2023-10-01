package com.flightcomputer.mecc.service;

import krpc.client.Connection;
import krpc.client.RPCException;
import krpc.client.services.UI;
import krpc.client.services.UI.Canvas;
import org.javatuples.Pair;
import org.javatuples.Triplet;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UIService {

    private final int fontSize = 14;

    private UI ui;
    private Canvas canvas;
    private Pair<Double, Double> screenSize;

    private Map<Integer, UI.Text> texts = new HashMap<>();

    public void createMainWindow(Connection connection) {
        this.createMainWindow(connection, 400.0, 400.0);
    }

    public void createMainWindow(Connection connection, Double height, Double width) {
        try {
            this.ui = UI.newInstance(connection);
            this.canvas = ui.getStockCanvas();
            this.screenSize = canvas.getRectTransform().getSize();

            this.addText("m.e.c.c.", 0);
        } catch (RPCException e) {
            System.out.println(e.getMessage());
        }
    }

    public void addText(String text, int pos) throws RPCException {
        if (this.texts.containsKey(pos)) {
            this.texts.get(pos).setContent(text);
        } else {
            UI.Text uiText = this.canvas.addText(text, true);
            uiText.getRectTransform().setPosition(new Pair<Double, Double>(-this.screenSize.getValue0() / 3, this.screenSize.getValue1() / 3 - (fontSize * pos)));
            uiText.setColor(new Triplet<Double, Double, Double>(1.0, 1.0, 1.0));
            uiText.setSize(fontSize);
            uiText.setAlignment(UI.TextAnchor.UPPER_LEFT);
            this.texts.put(pos, uiText);
        }
    }

}
