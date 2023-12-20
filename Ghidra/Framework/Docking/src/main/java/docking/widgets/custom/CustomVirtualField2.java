package docking.widgets.custom;

import java.awt.*;
import java.awt.event.FocusListener;
import java.text.BreakIterator;
import java.util.Locale;

import javax.accessibility.*;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.text.AttributeSet;

import ghidra.util.Msg;

public class CustomVirtualField2 extends JPanel {
	private static Font font = new Font("Monospaced", Font.PLAIN, 15);
	private static final int X_START = 10;
	private static final int Y_START = 10;
	private static int msgCount = 0;
	private String text;
	private AccessibleContext accessibleContext;
	private int indexInParent;
	private JComponent parent;
	private Locale locale;
	private int charWidth;
	private int charHeight;
	private int cursorPos;
	private int x;
	private int y;
	private int w;

	CustomVirtualField2(JComponent parent, String text, int indexInParent, int x, int y, int w) {
		this.parent = parent;
		this.text = text;
		this.indexInParent = indexInParent;
		this.x = x;
		this.y = y;
		this.w = w;
		accessibleContext = new AccessibleCustomVirtualField(parent);
		locale = parent.getLocale();
		FontMetrics metrics = parent.getFontMetrics(font);
		charWidth = metrics.charWidth('A');
		charHeight = metrics.getHeight();

	}

	@Override
	public AccessibleContext getAccessibleContext() {
		return accessibleContext;
	}

	protected class AccessibleCustomVirtualField extends AccessibleJComponent
			implements AccessibleText, AccessibleExtendedText, AccessibleEditableText {

		public AccessibleCustomVirtualField(JComponent parent) {
			setAccessibleParent((Accessible) parent);
			setAccessibleName("Joe");
			setAccessibleDescription("Joe's description");
		}

		@Override
		public AccessibleText getAccessibleText() {
			Msg.debug(this, getOutputLine() + "getAccessible Text");
			return this;
		}

		@Override
		public AccessibleEditableText getAccessibleEditableText() {
			return this;
		}

		@Override
		public AccessibleRole getAccessibleRole() {
			return AccessibleRole.TEXT;
		}

		@Override
		public AccessibleStateSet getAccessibleStateSet() {
			Msg.debug(this, getOutputLine() + "getAccessibleStateSet");
			AccessibleStateSet states = new AccessibleStateSet();
			states.add(AccessibleState.ENABLED);
			states.add(AccessibleState.FOCUSABLE);
			states.add(AccessibleState.VISIBLE);
			states.add(AccessibleState.SHOWING);
			states.add(AccessibleState.FOCUSED);
			states.add(AccessibleState.OPAQUE);
			states.add(AccessibleState.EDITABLE);
			states.add(AccessibleState.SINGLE_LINE);
			return states;

		}

		@Override
		public int getAccessibleIndexInParent() {
			Msg.debug(this, "getAccessibleIndexInParent: " + indexInParent);
			return indexInParent;
		}

		@Override
		public int getAccessibleChildrenCount() {
			return 0;
		}

		@Override
		public Accessible getAccessibleParent() {
			Msg.debug(this, "getAccessiblerParent");
			return super.getAccessibleParent();
		}

		@Override
		public Accessible getAccessibleChild(int i) {
			return null;
		}

		@Override
		public Locale getLocale() throws IllegalComponentStateException {
			return parent.getLocale();
		}

		@Override
		public void setTextContents(String s) {
			// TODO Auto-generated method stub

		}

		@Override
		public void insertTextAtIndex(int index, String s) {
			// TODO Auto-generated method stub

		}

		@Override
		public void delete(int startIndex, int endIndex) {
			// TODO Auto-generated method stub

		}

		@Override
		public void cut(int startIndex, int endIndex) {
			// TODO Auto-generated method stub

		}

		@Override
		public void paste(int startIndex) {
			// TODO Auto-generated method stub

		}

		@Override
		public void replaceText(int startIndex, int endIndex, String s) {
			// TODO Auto-generated method stub

		}

		@Override
		public void selectText(int startIndex, int endIndex) {
			// TODO Auto-generated method stub

		}

		@Override
		public void setAttributes(int startIndex, int endIndex, AttributeSet as) {
			// TODO Auto-generated method stub

		}

		@Override
		public String getTextRange(int startIndex, int endIndex) {
			Msg.debug(this, getOutputLine() + "getTextRange: " + text);
			int p0 = Math.min(startIndex, endIndex);
			int p1 = Math.max(startIndex, endIndex);
			if (p0 == p1) {
				return null;
			}
			return text.substring(startIndex, endIndex);
		}

		@Override
		public AccessibleTextSequence getTextSequenceAt(int part, int index) {
			Msg.debug(this, getOutputLine() + "getTextSequenceAt");
			if (index < 0 || index >= text.length()) {
				return null;
			}

			switch (part) {
				case AccessibleText.CHARACTER:
					return new AccessibleTextSequence(index, index + 1, text);
				case AccessibleText.WORD:
					BreakIterator words = BreakIterator.getWordInstance(locale);
					words.setText(text);
					int end = words.following(index);
					return new AccessibleTextSequence(words.previous(), end, text);
				case AccessibleText.SENTENCE:
					BreakIterator sentences = BreakIterator.getSentenceInstance(locale);
					sentences.setText(text);
					end = sentences.following(index);
					return new AccessibleTextSequence(sentences.previous(), end, text);
				default:
					return null;
			}
		}

		@Override
		public AccessibleTextSequence getTextSequenceAfter(int part, int index) {
			Msg.debug(this, getOutputLine() + "getTextSeaquenceAfter");
			if (index < 0 || index >= text.length() - 1) {
				return null;
			}

			switch (part) {
				case AccessibleText.CHARACTER:
					return new AccessibleTextSequence(index + 1, index + 2, text);
				case AccessibleText.WORD:
					BreakIterator words = BreakIterator.getWordInstance(locale);
					words.setText(text);
					int start = words.following(index);
					if (start == BreakIterator.DONE || start >= text.length()) {
						return null;
					}
					int end = words.following(start);
					if (end == BreakIterator.DONE || end > text.length()) {
						return null;
					}
					return new AccessibleTextSequence(start, end, text);
				case AccessibleText.SENTENCE:
					BreakIterator sentences = BreakIterator.getSentenceInstance(locale);
					sentences.setText(text);
					start = sentences.following(index);
					if (start == BreakIterator.DONE || start > text.length()) {
						return null;
					}
					end = sentences.following(start);
					if (end == BreakIterator.DONE || end > text.length()) {
						return null;
					}
					return new AccessibleTextSequence(start, end, text);
				default:
					return null;
			}
		}

		@Override
		public AccessibleTextSequence getTextSequenceBefore(int part, int index) {
			Msg.debug(this, getOutputLine() + "getTextSeaquenceBefore");
			if (index < 1 || index > text.length()) {
				return null;
			}

			switch (part) {
				case AccessibleText.CHARACTER:
					return new AccessibleTextSequence(index - 1, index, text);
				case AccessibleText.WORD:
					BreakIterator words = BreakIterator.getWordInstance(locale);
					words.setText(text);

					// move to the beginning of the current word so the algorithm
					// gives us the previous word and not the word we are on. Note: this is needed
					// because the preceding() method behaves differently if in the middle of a
					// word than if at the beginning of the word.
					if (!words.isBoundary(index)) {
						words.preceding(index);
					}
					int start = words.previous();
					int end = words.next();
					if (start == BreakIterator.DONE) {
						return null;
					}
					return new AccessibleTextSequence(start, end, text);
				case AccessibleText.SENTENCE:
					BreakIterator sentences = BreakIterator.getSentenceInstance(locale);
					sentences.setText(text);
					if (!sentences.isBoundary(index)) {
						sentences.preceding(index);
					}
					start = sentences.previous();
					end = sentences.next();
					if (start == BreakIterator.DONE) {
						return null;
					}
					return new AccessibleTextSequence(start, end, text);
				default:
					return null;
			}
		}

		@Override
		public Rectangle getTextBounds(int startIndex, int endIndex) {
			Msg.debug(this, getOutputLine() + "getTextBounds");
			return new Rectangle(X_START + startIndex * charWidth, Y_START,
				(endIndex - startIndex) * charWidth, charHeight);
		}

		@Override
		public int getIndexAtPoint(Point p) {
			Msg.debug(this, getOutputLine() + "getIndexAtPoint");

			if (p.y < Y_START || p.y > Y_START + charHeight) {
				return -1;
			}
			int index = (p.x - X_START) / charWidth;
			if (index < 0 || index > text.length()) {
				return -1;
			}
			return index;
		}

		@Override
		public Rectangle getCharacterBounds(int i) {
			Msg.debug(this, getOutputLine() + "getCharBounds");
			return new Rectangle(X_START + i * charWidth, Y_START, charWidth, charHeight);
		}

		@Override
		public int getCharCount() {
			Msg.debug(this, getOutputLine() + "getCharCount = " + text.length());
			return text.length();
		}

		@Override
		public int getCaretPosition() {
			Msg.debug(this, getOutputLine() + "getCaretPosition");
			return cursorPos;
		}

		@Override
		public String getAtIndex(int part, int index) {
			Msg.debug(this, getOutputLine() + "getAtIndex");

			AccessibleTextSequence sequence = getTextSequenceAt(part, index);
			if (sequence == null) {
				return null;
			}
			return text.substring(sequence.startIndex, sequence.endIndex);
		}

		@Override
		public String getAfterIndex(int part, int index) {
			Msg.debug(this, getOutputLine() + "getAfterIndex");
			AccessibleTextSequence sequence = getTextSequenceAfter(part, index);
			if (sequence == null) {
				return null;
			}
			return text.substring(sequence.startIndex, sequence.endIndex);
		}

		@Override
		public String getBeforeIndex(int part, int index) {
			Msg.debug(this, getOutputLine() + "getBeforeIndex");
			AccessibleTextSequence sequence = getTextSequenceBefore(part, index);
			if (sequence == null) {
				return null;
			}
			return text.substring(sequence.startIndex, sequence.endIndex);
		}

		@Override
		public AttributeSet getCharacterAttribute(int i) {
			Msg.debug(this, getOutputLine() + "getCharAttribute");

			return null;
		}

		@Override
		public int getSelectionStart() {
			Msg.debug(this, getOutputLine() + "getSelectionStart");

			return cursorPos;
		}

		@Override
		public int getSelectionEnd() {
			Msg.debug(this, getOutputLine() + "getSelectionEnd");
			return cursorPos;
		}

		@Override
		public String getSelectedText() {
			Msg.debug(this, getOutputLine() + "getSelectedText");

			return "foo and bar";
		}

		String getOutputLine() {
			return "CVF " + (msgCount++) + ": ";
		}

		@Override
		public Accessible getAccessibleAt(Point p) {
			Msg.debug(this, getOutputLine() + "getAccessibleAt: " + p);
			return null;
		}

		@Override
		public Color getBackground() {
			Msg.debug(this, getOutputLine() + "getBackground: ");
			return super.getBackground();
		}

		@Override
		public void setBackground(Color c) {
			Msg.debug(this, getOutputLine() + "setBackground: ");
			super.setBackground(c);
		}

		@Override
		public Color getForeground() {
			Msg.debug(this, getOutputLine() + "getForegound: ");
			return super.getForeground();

		}

		@Override
		public void setForeground(Color c) {
			Msg.debug(this, getOutputLine() + "setForeground: ");
			super.setForeground(c);
		}

		@Override
		public Cursor getCursor() {
			Msg.debug(this, getOutputLine() + "getCursor");
			return super.getCursor();
		}

		@Override
		public void setCursor(Cursor cursor) {
			Msg.debug(this, getOutputLine() + "setCursor");
			super.setCursor(cursor);
		}

		@Override
		public Font getFont() {
			Font f = super.getFont();
			Msg.debug(this, getOutputLine() + "getFont: " + f);
			return f;
		}

		@Override
		public void setFont(Font f) {
			Msg.debug(this, getOutputLine() + "setFont: " + f);
			super.setFont(f);
		}

		@Override
		public FontMetrics getFontMetrics(Font f) {
			FontMetrics fontMetrics = super.getFontMetrics(f);
			Msg.debug(this, getOutputLine() + "getFontMetrics: " + fontMetrics);
			return fontMetrics;
		}

		@Override
		public boolean isEnabled() {
			Msg.debug(this, getOutputLine() + "isEnabled");
			return true;
		}

		@Override
		public void setEnabled(boolean b) {
			Msg.debug(this, getOutputLine() + "setEnabled");

		}

		@Override
		public boolean isVisible() {
			Msg.debug(this, getOutputLine() + "isVisible");
			return super.isVisible();
		}

		@Override
		public void setVisible(boolean b) {
			Msg.debug(this, getOutputLine() + "setVisible");
			super.setVisible(b);
		}

		@Override
		public boolean isShowing() {
			Msg.debug(this, getOutputLine() + "isShowing");
			return super.isShowing();
		}

		@Override
		public boolean contains(Point p) {
			Msg.debug(this, getOutputLine() + "contains " + p + ", result = " + super.contains(p));
			return super.contains(p);
		}

		@Override
		public Point getLocationOnScreen() {
			Point parentP = parent.getLocationOnScreen();
			Point p = new Point(parentP.x + x, parentP.y + y);
			Msg.debug(this, getOutputLine() + "getLocationOnScreenn: " + p);
			return p;
		}

		@Override
		public Point getLocation() {
			Point loc = super.getLocation();
			Msg.debug(this, getOutputLine() + "getLocation: " + loc);
			return loc;
		}

		@Override
		public void setLocation(Point p) {
			Msg.debug(this, getOutputLine() + "setLocation " + p);
			super.setLocation(p);
		}

		@Override
		public Rectangle getBounds() {
			Rectangle bounds = super.getBounds();
			Msg.debug(this, getOutputLine() + "getBounds: " + bounds);
			return bounds;
		}

		@Override
		public void setBounds(Rectangle r) {
			Msg.debug(this, getOutputLine() + "setBounds: " + r);

		}

		@Override
		public Dimension getSize() {
			Dimension size = new Dimension(w, charHeight + 10);
			Msg.debug(this, getOutputLine() + "getSize: " + size);
			return size;
		}

		@Override
		public void setSize(Dimension d) {
			Msg.debug(this, getOutputLine() + "setSize: " + d);
			super.setSize(d);
		}

		@Override
		public boolean isFocusTraversable() {
			boolean focusTraversable = super.isFocusTraversable();
			Msg.debug(this, getOutputLine() + "isFocusTraversable: " + focusTraversable);
			return focusTraversable;
		}

		@Override
		public void requestFocus() {
			Msg.debug(this, getOutputLine() + "request focus");
			super.requestFocus();
		}

		@Override
		public void addFocusListener(FocusListener l) {
			Msg.debug(this, getOutputLine() + "addFocusListener");
			super.addFocusListener(l);
		}

		@Override
		public void removeFocusListener(FocusListener l) {
			Msg.debug(this, getOutputLine() + "removeFocusListener");
			super.removeFocusListener(l);
		}

		@Override
		public String getToolTipText() {
			String toolTipText = super.getToolTipText();
			Msg.debug(this, getOutputLine() + "getToolTipText: " + toolTipText);
			return toolTipText;
		}

		@Override
		public String getTitledBorderText() {
			String text = super.getTitledBorderText();
			Msg.debug(this, getOutputLine() + "getTitledBorderText: " + text);
			return text;

		}

		@Override
		public AccessibleKeyBinding getAccessibleKeyBinding() {
			AccessibleKeyBinding binding = super.getAccessibleKeyBinding();
			Msg.debug(this, getOutputLine() + "getAccessibleKeyBinding: " + binding);
			return binding;
		}

	}

	public void paint(Graphics g, int currentFieldIndex) {
		g.translate(x, y);
		g.setFont(font);
		g.drawString(text, 0, charHeight);
		if (currentFieldIndex == indexInParent) {
			Color saved = g.getColor();
			g.setColor(Color.RED);
			Graphics2D g2d = (Graphics2D) g;
			g2d.setStroke(new BasicStroke(2));
			int cx = cursorPos * charWidth;
			g.drawLine(cx, 0, cx, charHeight);
			g.setColor(saved);
		}
		g.translate(-x, -y);
	}

	public boolean contains(Point p) {
		return p.x >= x && p.x <= x + w && p.y >= y && p.y <= y + charHeight + 10;
	}

	public boolean cursorRight() {
		if (cursorPos < text.length()) {
			cursorPos++;
			return true;
		}
		return false;
	}

	public void setCursor(int i) {
		if (i < 0) {
			cursorPos = text.length();
		}
		else {
			cursorPos = i;
		}
	}

	public boolean cursorLeft() {
		if (cursorPos > 0) {
			cursorPos--;
			return true;
		}
		return false;
	}

	public String getText() {
		return text;
	}
}
