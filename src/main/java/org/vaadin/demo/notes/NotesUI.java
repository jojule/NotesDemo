package org.vaadin.demo.notes;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnHeaderMode;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class NotesUI extends UI {
	Table notes = new Table();
	NoteEditor editor = new NoteEditor();
	Button saveButton = new Button("Save");

	protected void init(VaadinRequest request) {
		buildLayout();
		wireUp();
		connectToBackend();
	}

	private void connectToBackend() {
		notes.setContainerDataSource(Backend.getNotesContainer());
		notes.setVisibleColumns(new String[] { Backend.titleId });
		notes.setColumnHeaderMode(ColumnHeaderMode.HIDDEN);
	}

	private void wireUp() {
		notes.setImmediate(true);
		notes.setSelectable(true);

		notes.addValueChangeListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				editor.setNote(notes.getItem(notes.getValue()));
			}
		});

		saveButton.addClickListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				Backend.commitChanges(notes.getContainerDataSource());
			}
		});
	}

	private void buildLayout() {
		HorizontalSplitPanel split = new HorizontalSplitPanel();
		VerticalLayout right = new VerticalLayout();

		setContent(split);
		split.addComponent(notes);
		split.addComponent(right);

		right.addComponent(editor);
		right.setSizeFull();
		right.setExpandRatio(editor, 1.0f);
		right.setMargin(true);
		right.setSpacing(true);
		right.addComponent(saveButton);
		
		notes.setSizeFull();
	}

}
