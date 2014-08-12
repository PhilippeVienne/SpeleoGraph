/*
 * Copyright (c) 2013 Philippe VIENNE
 *
 * This file is a part of SpeleoGraph
 *
 * SpeleoGraph is free software: you can redistribute
 * it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * SpeleoGraph is distributed in the hope that it will
 * be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU General Public License
 * for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with SpeleoGraph.
 * If not, see <http://www.gnu.org/licenses/>.
 */

package org.cds06.speleograph.actions.data;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.FormLayout;
import org.cds06.speleograph.I18nSupport;
import org.cds06.speleograph.data.Series;
import org.cds06.speleograph.utils.DateSelector;
import org.cds06.speleograph.utils.FormDialog;
import org.jfree.data.time.DateRange;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

/**
 * Cette action sert à tronquer les données temporellement.
 */
public class LimitDateRangeAction extends AbstractAction {

    private final Series series;

    public LimitDateRangeAction(Series series) {
        super();
        putValue(NAME, I18nSupport.translate("actions.limit"));
        this.series = series;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        PromptDialog dialog = new PromptDialog();
        DateRange range = series.getRange();
        dialog.startDateSelector.setDate(range.getLowerDate());
        dialog.endDateSelector.setDate(range.getUpperDate());
        dialog.setVisible(true);
    }

    private class PromptDialog extends FormDialog {

        private DateSelector startDateSelector = new DateSelector();
        private DateSelector endDateSelector = new DateSelector();
        private JCheckBox applyToAllSeriesInTheSameFile = new JCheckBox(I18nSupport.translate("actions.limit.applyAllInFile"));
        private JCheckBox applyToAllOpenedSeries = new JCheckBox(I18nSupport.translate("actions.limit.applyAllOpened"));
        private FormLayout layout = new FormLayout("p:grow", "p,4dlu,p,4dlu,p,4dlu,p,4dlu,p,4dlu,p,6dlu,p");

        public PromptDialog() {
            super();
            construct();
            setTitle(I18nSupport.translate("actions.limit"));
        }

        @Override
        protected void setup() {
            PanelBuilder builder = new PanelBuilder(layout, getPanel());
            builder.addLabel(I18nSupport.translate("actions.limit.label1"));
            builder.nextLine(2);
            builder.add(startDateSelector);
            builder.nextLine(2);
            builder.addLabel(I18nSupport.translate("actions.limit.label2"));
            builder.nextLine(2);
            builder.add(endDateSelector);
            builder.nextLine(2);
            builder.add(applyToAllSeriesInTheSameFile);
            builder.nextLine(2);
            builder.add(applyToAllOpenedSeries);
            builder.nextLine(2);
            builder.add(new JButton(new AbstractAction() {

                {
                    putValue(NAME, I18nSupport.translate("actions.limit.button"));
                }

                @Override
                public void actionPerformed(ActionEvent e) {
                    validateForm();
                }
            }));

            Dimension dim = getPanel().getPreferredSize();
            getPanel().setPreferredSize(new Dimension(dim.width + 100, dim.height));
        }

        @Override
        protected void validateForm() {
            ArrayList<Series> seriesToApply = new ArrayList<>();
            boolean applyToAll = false;
            if (applyToAllOpenedSeries.isSelected()) {
                applyToAll = true;
                for (Series s : Series.getInstances())
                    seriesToApply.add(s);
            } else if (applyToAllSeriesInTheSameFile.isSelected()) {
                applyToAll = true;
                for (Series s : Series.getInstances()) {
                    if (s.getOrigin().equals(series.getOrigin())) seriesToApply.add(s);
                }
            } else {
                seriesToApply.add(series);
            }
            for (Series s : seriesToApply) {
                s.subSeries(startDateSelector.getDate(), endDateSelector.getDate(), applyToAll);
            }
            setVisible(false);
        }

        @Override
        protected FormLayout getFormLayout() {
            return null;
        }
    }
}
