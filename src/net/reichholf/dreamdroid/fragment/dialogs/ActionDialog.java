/*
 * Copyright © 2013. Stephan Reichholf
 *
 * Unless stated otherwise in a files head all java and xml-code of this Project is:
 *
 * Licensed under the Create-Commons Attribution-Noncommercial-Share Alike 3.0 Unported
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 *
 * All grahpics, except the dreamdroid icon, can be used for any other non-commercial purposes.
 * The dreamdroid icon may not be used in any other projects than dreamdroid itself.
 */

package net.reichholf.dreamdroid.fragment.dialogs;

/**
 * @author sre
 * 
 */
public abstract class ActionDialog extends AbstractDialog {
	protected void finishDialog(int action, Object details) {
		DialogActionListener listener = (DialogActionListener) getActivity();
		if(listener != null)
			listener.onDialogAction(action, details, getTag());
		dismiss();
	}

	public interface DialogActionListener {
		public void onDialogAction(int action, Object details, String dialogTag);
	}
}
