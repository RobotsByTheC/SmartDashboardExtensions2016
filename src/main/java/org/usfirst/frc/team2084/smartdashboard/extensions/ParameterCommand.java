/* 
 * Copyright (c) 2016 RobotsByTheC. All rights reserved.
 *
 * Open Source Software - may be modified and shared by FRC teams. The code must
 * be accompanied by the BSD license file in the root directory of the project.
 */
package org.usfirst.frc.team2084.smartdashboard.extensions;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.util.HashSet;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import edu.wpi.first.smartdashboard.gui.elements.bindings.AbstractTableWidget;
import edu.wpi.first.smartdashboard.properties.ColorProperty;
import edu.wpi.first.smartdashboard.properties.Property;
import edu.wpi.first.smartdashboard.types.DataType;
import edu.wpi.first.wpilibj.tables.ITable;

/**
 * Command that can have a set of parameters that can be changed on the
 * SmartDashboard.
 *
 * @author Ben Wolsieffer
 * @author Jeff Copeland
 */
@SuppressWarnings("serial")
public class ParameterCommand extends AbstractTableWidget {

    public static final DataType[] TYPES = { ParameterCommandType.get() };
    private static final String START_CARD = "Start";
    private static final String CANCEL_CARD = "Cancel";

    public final ColorProperty startBackground = new ColorProperty(this, "Start Button Color", new Color(32, 134, 32));
    public final ColorProperty cancelBackground =
            new ColorProperty(this, "Cancel Button Color", new Color(243, 32, 32));

    private JPanel commandPanel;
    private JLabel name;
    private JPanel buttonPanel;
    private CardLayout buttonPanelLayout;
    private JButton start;
    private JButton cancel;

    private JPanel fieldPanel;

    private Set<String> parameters = new HashSet<>();

    /**
     * 
     */
    public ParameterCommand() {
        super(true);
    }

    @Override
    public void init() {
        SwingUtilities.invokeLater(() -> {
            commandPanel = new JPanel();
            name = new JLabel();
            buttonPanel = new JPanel(buttonPanelLayout = new CardLayout());
            start = new JButton("start");
            cancel = new JButton("cancel");

            fieldPanel = new JPanel();

            name.setText(getFieldName());

            // setResizable(false);
            setLayout(new BorderLayout());

            buttonPanel.setOpaque(false);

            start.setOpaque(false);
            start.addActionListener((ae) -> {
                table.putBoolean("running", true);
            });
            start.setForeground(startBackground.getValue());

            buttonPanel.add(start, START_CARD);

            cancel.setOpaque(false);
            cancel.addActionListener((ae) -> {
                table.putBoolean("running", false);
            });
            cancel.setForeground(cancelBackground.getValue());

            buttonPanel.add(cancel, CANCEL_CARD);

            commandPanel.add(name);
            commandPanel.add(buttonPanel);
            add(commandPanel, BorderLayout.CENTER);

            fieldPanel.setLayout(new BoxLayout(fieldPanel, BoxLayout.PAGE_AXIS));
            add(fieldPanel, BorderLayout.SOUTH);
        });
    }

    @Override
    public void booleanChanged(ITable source, String key, final boolean value, boolean isNew) {
        if (key.equals("running")) {
            SwingUtilities.invokeLater(() -> {
                if (value) {
                    buttonPanelLayout.show(buttonPanel, CANCEL_CARD);
                } else {
                    buttonPanelLayout.show(buttonPanel, START_CARD);
                }
                revalidate();
                repaint();
            });
        }
    }

    private void addParameter(ITable table, String key, DataType type) {
        SwingUtilities.invokeLater(() -> {
            JComponent widget;

            if (type == DataType.BOOLEAN) {
                widget = createCheckBox(table, key);
            } else {
                widget = createTextBox(table, key, type);
            }

            fieldPanel.add(widget);
            revalidate();
            repaint();
        });
    }

    private boolean addedParameterListener = false;

    @Override
    public void tableChanged(ITable source, String key, ITable value, boolean isNew) {
        super.tableChanged(source, key, value, isNew);

        if (key.equals("Parameters") && !addedParameterListener) {
            value.addTableListener((ITable s, String k, Object v, boolean n) -> {
                if (!parameters.contains(k)) {
                    parameters.add(k);
                    addParameter(s, k, DataType.getType(v));
                }
            }, true);
            addedParameterListener = true;
        }
    }

    private JComponent createTextBox(ITable table, String field, DataType type) {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel(field);
        panel.add(label, BorderLayout.WEST);

        EditorTextField text;
        if (type == DataType.NUMBER) {
            BindableNumberField bnf;
            text = bnf = new BindableNumberField(new BindableTableEntry(table, field));
            table.addTableListener(field, (source, key, value, isNew) -> {
                bnf.setBindableValue((double) value);
            }, true);
        } else {
            BindableStringField bsf;
            text = bsf = new BindableStringField(new BindableTableEntry(table, field));
            table.addTableListener(field, (source, key, value, isNew) -> {
                bsf.setBindableValue((String) value);
            }, true);
        }
        panel.add(text, BorderLayout.CENTER);

        return panel;
    }

    private JComponent createCheckBox(ITable table, String field) {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel(field);
        BindableBooleanCheckBox check = new BindableBooleanCheckBox(new BindableTableEntry(table, field));

        table.addTableListener(field, (source, key, value, isNew) -> {
            check.setBindableValue((boolean) value);
        }, true);

        panel.add(label, BorderLayout.WEST);
        panel.add(check, BorderLayout.EAST);

        return panel;
    }

    @Override
    public void propertyChanged(Property property) {
        if (property == startBackground) {
            start.setBackground(startBackground.getValue());
        } else if (property == cancelBackground) {
            cancel.setBackground(cancelBackground.getValue());
        }
    }
}
