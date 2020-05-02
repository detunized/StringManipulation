package com.github.krasa.stringmanipulation.intellij.increment;

import com.github.krasa.stringmanipulation.commons.util.DuplicatUtils;
import com.github.krasa.stringmanipulation.commons.util.StringUtil;
import com.github.krasa.stringmanipulation.intellij.MyApplicationService;
import com.github.krasa.stringmanipulation.intellij.MyEditorAction;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.editor.actionSystem.EditorWriteActionHandler;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Olivier Smedile
 * @author Vojtech Krasa
 */
public class DecrementAction extends MyEditorAction {

	public DecrementAction() {
		super(null);
		this.setupHandler(new EditorWriteActionHandler(true) {
			@Override
			public void executeWriteAction(Editor editor, DataContext dataContext) {
				MyApplicationService.setAction(getActionClass());

				// Column mode not supported
				if (editor.isColumnMode()) {
					return;
				}
				final CaretModel caretModel = editor.getCaretModel();

				final int line = caretModel.getLogicalPosition().line;
				final int column = caretModel.getLogicalPosition().column;
				int caretOffset = caretModel.getOffset();

				final SelectionModel selectionModel = editor.getSelectionModel();
				boolean hasSelection = selectionModel.hasSelection();
				if (hasSelection == false) {
					selectionModel.selectLineAtCaret();
				}
				final String selectedText = selectionModel.getSelectedText();

				if (selectedText != null) {
					String[] textParts = StringUtil.splitPreserveAllTokens(selectedText,
						DuplicatUtils.SIMPLE_NUMBER_REGEX);
					for (int i = 0; i < textParts.length; i++) {
						textParts[i] = DuplicatUtils.simpleDec(textParts[i]);
					}

					final String newText = StringUtils.join(textParts);
					applyChanges(editor, caretModel, line, column, selectionModel, hasSelection, newText, caretOffset);
				}
			}
		});
	}

	protected void applyChanges(Editor editor, CaretModel caretModel, int line, int column,
								SelectionModel selectionModel, boolean hasSelection, String newText, int caretOffset) {
		editor.getDocument().replaceString(selectionModel.getSelectionStart(), selectionModel.getSelectionEnd(),
			newText);
	}
}