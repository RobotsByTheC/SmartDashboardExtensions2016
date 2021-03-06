/* 
 * Copyright (c) 2014 RobotsByTheC. All rights reserved.
 *
 * Open Source Software - may be modified and shared by FRC teams. The code must
 * be accompanied by the BSD license file in the root directory of the project.
 */
package org.usfirst.frc.team2084.smartdashboard.extensions.vision.properties;

import edu.wpi.first.smartdashboard.properties.IntegerProperty;
import edu.wpi.first.smartdashboard.properties.PropertyHolder;
import java.awt.Component;
import javax.swing.AbstractCellEditor;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

import org.usfirst.frc.team2084.CMonster2016.vision.Range;

/**
 *
 * @author Ben Wolsieffer
 */
public class SliderProperty extends IntegerProperty {

    private final JSlider slider;

    public SliderProperty(PropertyHolder element, String name, Range range, int defaultValue) {
        super(element, name, defaultValue);
        slider = new JSlider(range.getMin(), range.getMax());
        slider.setUI(new CircleSliderUI(slider));
    }

    public SliderProperty(PropertyHolder element, String name, Range range) {
        this(element, name, range, 0);
    }

    private final SliderEditor editor = new SliderEditor();

    @SuppressWarnings("serial")
    private class SliderEditor extends AbstractCellEditor implements TableCellEditor {

        @Override
        public Object getCellEditorValue() {
            return slider.getValue();
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            if (value instanceof Integer) {
                slider.setValue((Integer) value);
            }
            return slider;
        }
    }

    @Override
    public TableCellEditor getEditor(Component c) {
        return editor;
    }

}
