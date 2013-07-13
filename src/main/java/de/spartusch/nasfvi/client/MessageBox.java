/*
 * Copyright 2011 Stefan Partusch
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

package de.spartusch.nasfvi.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A popup with a caption and a Widget as the content.
 * Each MessageBox features an "Ok" button to hide the box.
 * @author Stefan Partusch
 * @see {@link com.google.gwt.user.client.ui.DialogBox DialogBox}
 *
 */
public class MessageBox implements ClickHandler {
	/** The box itself. */
	private final DialogBox box;
	/** Button to hide the box. */
	private final Button ok;

	/**
	 * @param caption Caption of the MessageBox
	 * @param content Widget to set as the content of the MessageBox
	 */
	public MessageBox(final String caption, final Widget content) {
		box = new DialogBox(true, false);
		box.setAnimationEnabled(true);
		box.setGlassEnabled(true);
		ok = new Button("Ok");
		ok.addClickHandler(this);
		set(caption, content);
	}

	/**
	 * Sets the content and a caption of this MessageBox.
	 * @param caption New caption of the box
	 * @param content New content of the box
	 */
	public final void set(final String caption, final Widget content) {
		box.setText(caption);

		HorizontalPanel hori = new HorizontalPanel();
		VerticalPanel verti = new VerticalPanel();
		
		hori.setHorizontalAlignment(Label.ALIGN_CENTER);
		hori.add(ok);
		hori.setWidth("100%");

		verti.add(content);
		verti.add(hori);
		verti.setWidth("100%");
		verti.setSpacing(10);
		
		box.add(verti);
		box.center();
	}

	@Override
	public final void onClick(final ClickEvent event) {
		Object sender = event.getSource();
		if (sender == ok) {
			box.hide();
		}
	}
}
