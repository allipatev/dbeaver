/*
 * DBeaver - Universal Database Manager
 * Copyright (C) 2010-2023 DBeaver Corp and others
 * Copyright (C) 2011-2012 Eugene Fradkin (eugene.fradkin@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jkiss.dbeaver.tasks.ui.nativetool;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.jkiss.code.NotNull;
import org.jkiss.dbeaver.tasks.ui.nativetool.internal.TaskNativeUIMessages;
import org.jkiss.dbeaver.ui.UIUtils;
import org.jkiss.dbeaver.ui.contentassist.ContentAssistUtils;
import org.jkiss.dbeaver.ui.contentassist.SmartTextContentAdapter;
import org.jkiss.dbeaver.ui.contentassist.StringContentProposalProvider;
import org.jkiss.dbeaver.ui.dialogs.ActiveWizardPage;

public abstract class AbstractNativeToolWizardPage<WIZARD extends AbstractNativeToolWizard> extends ActiveWizardPage {

    protected final WIZARD wizard;

    protected Text extraCommandArgsText;

    protected AbstractNativeToolWizardPage(WIZARD wizard, String pageName)
    {
        super(pageName);
        setPageComplete(false);
        this.wizard = wizard;
    }

    @Override
    protected boolean determinePageCompletion() {
        if (wizard.getSettings().getClientHome() == null) {
            setErrorMessage(TaskNativeUIMessages.tools_wizard_message_no_client_home);
            return false;
        }
        return super.determinePageCompletion();
    }

    protected void createCheckButtons(Composite buttonsPanel, final Table table) {
        UIUtils.createDialogButton(buttonsPanel, TaskNativeUIMessages.tools_wizard_page_dialog_button_all, new CheckListener(table, true));
        UIUtils.createDialogButton(buttonsPanel, TaskNativeUIMessages.tools_wizard_page_dialog_button_none, new CheckListener(table, false));
    }

    protected void createExtraArgsInput(Composite outputGroup) {
        extraCommandArgsText = UIUtils.createLabelText(outputGroup, TaskNativeUIMessages.tools_wizard_page_dialog_label_extra_command_args, wizard.getSettings().getExtraCommandArgs());
        extraCommandArgsText.setToolTipText(TaskNativeUIMessages.tools_wizard_page_dialog_tooltip_extra_command_args);
        ContentAssistUtils.installContentProposal(
            extraCommandArgsText,
            new SmartTextContentAdapter(),
            new StringContentProposalProvider(new String[]{}));
        extraCommandArgsText.addModifyListener(e -> wizard.getSettings().setExtraCommandArgs(extraCommandArgsText.getText()));

    }

    public void saveState() {
        if (extraCommandArgsText != null) {
            wizard.getSettings().setExtraCommandArgs(extraCommandArgsText.getText());
        }
    }

    protected void updateState() {
        saveState();

        setPageComplete(true);
    }

    protected void updateTableCheckedStatus(@NotNull Table table, boolean check) {
        // Handling "All" and "None" buttons
    }

    private class CheckListener extends SelectionAdapter {
        private final Table table;
        private final boolean check;

        public CheckListener(Table table, boolean check) {
            this.table = table;
            this.check = check;
        }

        @Override
        public void widgetSelected(SelectionEvent e) {
            for (TableItem item : table.getItems()) {
                item.setChecked(check);
            }
            updateTableCheckedStatus(table, check);
            updateState();
        }
    }

}
