package com.doublecheck.bstworkbench.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

@SuppressWarnings("serial")
public class LineNumbers extends JPanel {
    // for this simple experiment, we keep the pane + scrollpane as members.
    JTextPane pane;
    JScrollPane scrollPane;
    JFrame parent;

    public LineNumbers(JScrollPane scrollPane, JTextPane pane) {
        super();
        setMinimumSize(new Dimension(10, 30));
        setPreferredSize(new Dimension(10, 30));
        setMinimumSize(new Dimension(10, 30));
        this.pane = pane;
        this.scrollPane = scrollPane;// new JScrollPane(pane);

    }

    public void paint(Graphics g) {
        super.paint(g);

        // We need to properly convert the points to match the viewport
        // Read docs for viewport
        int start = pane
                .viewToModel(scrollPane.getViewport().getViewPosition()); // starting
                                                                        // pos
                                                                        // in
                                                                        // document
        int end = pane.viewToModel(new Point(scrollPane.getViewport()
                .getViewPosition().x
                + pane.getWidth(), scrollPane.getViewport().getViewPosition().y
                + pane.getHeight()));
        // end pos in doc

        // translate offsets to lines
        Document doc = pane.getDocument();
        int startline = doc.getDefaultRootElement().getElementIndex(start) + 1;
        int endline = doc.getDefaultRootElement().getElementIndex(end) + 1;
        int fontHeight = g.getFontMetrics(pane.getFont()).getHeight();
        int fontDesc = g.getFontMetrics(pane.getFont()).getDescent();
        int starting_y = -1;

        try {
            starting_y = pane.modelToView(start).y
                    - scrollPane.getViewport().getViewPosition().y + fontHeight
                    - fontDesc;
        } catch (BadLocationException e1) {
            e1.printStackTrace();
        }

        for (int line = startline, y = starting_y; line <= endline; y += fontHeight, line++) {
            g.drawString(Integer.toString(line), 0, y);
        }

        // Trying to update the panels size.
        setMinimumSize(new Dimension(
                Integer.toString(endline + 1).length() * 10, 30));
        setPreferredSize(new Dimension(
                Integer.toString(endline + 1).length() * 10, 30));
        setMinimumSize(new Dimension(
                Integer.toString(endline + 1).length() * 10, 30));

    }

}
