package org.vaadin.demo.notes;

import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.PropertyId;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class NoteEditor extends CustomComponent {

	@PropertyId(Backend.titleId)
	TextField title = new TextField();
	@PropertyId(Backend.textId)
	RichTextArea text = new RichTextArea();

	public NoteEditor() {
		VerticalLayout layout = new VerticalLayout();
		setCompositionRoot(layout);

		layout.addComponent(title);
		layout.addComponent(text);
		setSizeFull();
		layout.setSizeFull();		
		title.setWidth("100%");
		text.setSizeFull();
		layout.setExpandRatio(text, 1.0f);
	}

	public void setNote(Item note) {
		FieldGroup g = new FieldGroup(note);
		g.bindMemberFields(this);
		g.setBuffered(false);
	}
}
