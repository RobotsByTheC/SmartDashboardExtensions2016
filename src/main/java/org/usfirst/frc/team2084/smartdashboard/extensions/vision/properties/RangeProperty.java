/* 
 * Copyright (c) 2014 RobotsByTheC. All rights reserved.
 *
 * Open Source Software - may be modified and shared by FRC teams. The code must
 * be accompanied by the BSD license file in the root directory of the project.
 */
package org.usfirst.frc.team2084.smartdashboard.extensions.vision.properties;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import org.usfirst.frc.team2084.CMonster2016.vision.Range;

import edu.wpi.first.smartdashboard.properties.GenericProperty;
import edu.wpi.first.smartdashboard.properties.PropertyHolder;

/**
 *
 * @author Ben Wolsieffer
 */
public class RangeProperty extends GenericProperty<Range> {

    private final Range maxRange;

    public RangeProperty(PropertyHolder holder, String name, Range range) {
        this(holder, name, range, range);
    }

    public RangeProperty(PropertyHolder holder, String name, Range range, Range defaultValue) {
        super(Range.class, holder, name, defaultValue);
        this.maxRange = range;
        slider = new RangeSlider(maxRange.getMin(), maxRange.getMax());
    }

    private final RangeSlider slider;

    private final RangeEditor editor = new RangeEditor();

    @SuppressWarnings("serial")
    private class RangeEditor extends AbstractCellEditor implements TableCellEditor {

        @Override
        public Object getCellEditorValue() {
            return slider.getRange();
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            if (value instanceof Range) {
                slider.setRange((Range) value);
            }
            return slider;
        }

        @Override
        public boolean isCellEditable(EventObject e) {
            if (e instanceof MouseEvent) {
                return ((MouseEvent) e).getClickCount() >= 2;
            }
            return super.isCellEditable(e);
        }

    };

    @Override
    public TableCellRenderer getRenderer() {
        return null;
    }

    @Override
    public TableCellEditor getEditor(Component c) {
        return editor;
    }

    @Override
    protected Range transformValue(Object value) {
        if (value instanceof String) {
            String strVal = (String) value;
            String[] vals = strVal.split(",");
            return new Range(Integer.parseInt(vals[0]), Integer.parseInt(vals[1]));
        } else {
            return super.transformValue(value);
        }
    }

}
