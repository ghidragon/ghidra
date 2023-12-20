package docking.widgets.custom;

import java.awt.*;
import java.awt.event.*;
import java.awt.im.InputMethodRequests;
import java.text.BreakIterator;
import java.util.Locale;

import javax.accessibility.*;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.text.AttributeSet;

import ghidra.util.Msg;
import ghidra.util.Swing;

//@JavaBean(defaultProperty = "UIClassID", description = "A component which allows for the editing of a single line of text.")
public class CustomTextComponent extends JPanel {
	/**
	* @see #getUIClassID
	* @see #readObject
	*/
	private static final String uiClassID = "TextFieldUI";

	int msgCount = 0;
	private static final int X_START = 10;
	private static final int Y_START = 10;
	private String text = "This is a test";
	private int maxCursor = text.length();
	private int cursorPos = 3;
	private Font font = new Font("Monospaced", Font.PLAIN, 15);
	private int charWidth;
	private int charHeight;
	private Locale locale;

	private InputMethodRequests inputMethodRequestsHandler;

	public CustomTextComponent() {
		setBorder(BorderFactory.createLineBorder(Color.black));
		setName("Custom Text Component");
		setFocusable(true);
		accessibleContext = new AccessibleCustomTextField();
		setFont(font);
		charWidth = getFontMetrics(font).charWidth('A');
		charHeight = getFontMetrics(font).getHeight();
		locale = getLocale();
		setEnabled(true);
		setOpaque(true);
//		setText("What the hell");
		addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) {
				setBackground(Color.LIGHT_GRAY);
			}

			@Override
			public void focusGained(FocusEvent e) {
				setBackground(Color.WHITE);
			}
		});
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				requestFocus();
			}
		});

		addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {
				int keyCode = e.getKeyCode();
				switch (keyCode) {
					case KeyEvent.VK_LEFT:
						cursorPos = Math.max(cursorPos - 1, 0);
						accessibleContext.firePropertyChange(
							AccessibleContext.ACCESSIBLE_CARET_PROPERTY, cursorPos + 1, cursorPos);
						break;
					case KeyEvent.VK_RIGHT:
						cursorPos = Math.min(cursorPos + 1, maxCursor);
						accessibleContext.firePropertyChange(
							AccessibleContext.ACCESSIBLE_CARET_PROPERTY, cursorPos - 1, cursorPos);
						break;
				}
				repaint();
				Swing.runLater(() -> firePropertyChange(AccessibleContext.ACCESSIBLE_TEXT_PROPERTY,
					null, cursorPos));
			}
		});
		firePropertyChange(AccessibleContext.ACCESSIBLE_TEXT_PROPERTY, null, 1);

	}

	@Override
	public AccessibleContext getAccessibleContext() {
		return accessibleContext;
	}

	@Override
	public synchronized void addInputMethodListener(InputMethodListener l) {
		// TODO Auto-generated method stub
		super.addInputMethodListener(l);
	}

	@Override
	protected void processInputMethodEvent(InputMethodEvent e) {
		// TODO Auto-generated method stub
		super.processInputMethodEvent(e);
	}

	@Override
	public String getToolTipText() {
		return "My custom tooltiop";
	}

//	/**
//	* Gets the class ID for a UI.
//	*
//	* @return the string "TextFieldUI"
//	* @see JComponent#getUIClassID
//	* @see UIDefaults#getUI
//	*/
//	@BeanProperty(bound = false)
//	public String getUIClassID() {
//		return uiClassID;
//	}

	@Override
	protected void paintComponent(Graphics g) {
		g.drawString(text, X_START, Y_START + charHeight);
		g.setFont(font);
		int x = X_START + charWidth * cursorPos;
		g.setColor(Color.RED);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setStroke(new BasicStroke(2));
		g.drawLine(x, Y_START, x, Y_START + charHeight);
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(100, 300);
	}

	protected class AccessibleCustomTextField extends AccessibleJComponent
			implements AccessibleText,
			AccessibleExtendedText, AccessibleEditableText {
		Point oldLocationOnScreen;

		/**
		 * Constructs an {@code AccessibleJTextField}.
		 */
		protected AccessibleCustomTextField() {
			try {
				oldLocationOnScreen = getLocationOnScreen();
			}
			catch (IllegalComponentStateException iae) {
			}

			// Fire a ACCESSIBLE_VISIBLE_DATA_PROPERTY PropertyChangeEvent
			// when the text component moves (e.g., when scrolling).
			// Using an anonymous class since making AccessibleJTextComponent
			// implement ComponentListener would be an API change.
			CustomTextComponent.this.addComponentListener(new ComponentAdapter() {

				public void componentMoved(ComponentEvent e) {
					try {
						Point newLocationOnScreen = getLocationOnScreen();
						firePropertyChange(ACCESSIBLE_VISIBLE_DATA_PROPERTY,
							oldLocationOnScreen,
							newLocationOnScreen);

						oldLocationOnScreen = newLocationOnScreen;
					}
					catch (IllegalComponentStateException iae) {
					}
				}
			});
			setAccessibleName("Bob");
			setAccessibleDescription("Bob's House");
		}

		/**
		 * Gets the state set of this object.
		 *
		 * @return an instance of AccessibleStateSet describing the states
		 * of the object
		 * @see AccessibleState
		 */
		public AccessibleStateSet getAccessibleStateSet() {
			Msg.debug(this, getOutputLine() + getOutputLine() + "getAccessibleStateSet");
			AccessibleStateSet states = super.getAccessibleStateSet();
			states.add(AccessibleState.EDITABLE);
			states.add(AccessibleState.SINGLE_LINE);
			return states;
		}

		public AccessibleRole getAccessibleRole() {
			Msg.debug(this, getOutputLine() + "getAccessibleRole");
			return AccessibleRole.TEXT;
		}

		@Override
		public AccessibleText getAccessibleText() {
			Msg.debug(this, getOutputLine() + "getAccessibleText");
			return this;
		}

		@Override
		public AccessibleEditableText getAccessibleEditableText() {
			return this;
		}

		@Override
		public int getIndexAtPoint(Point p) {
			Msg.debug(this, getOutputLine() + "getIndexAtPoint");

			if (p.y < Y_START || p.y > Y_START + charHeight) {
				return -1;
			}
			int index = (p.x - X_START) / charWidth;
			if (index < 0 || index > maxCursor) {
				return -1;
			}
			return index;
		}

		/**
		 * Gets the editor's drawing rectangle.  Stolen
		 * from the unfortunately named
		 * BasicTextUI.getVisibleEditorRect()
		 *
		 * @return the bounding box for the root view
		 */
		Rectangle getRootEditorRect() {
			Rectangle alloc = CustomTextComponent.this.getBounds();
			if ((alloc.width > 0) && (alloc.height > 0)) {
				alloc.x = alloc.y = 0;
				Insets insets = CustomTextComponent.this.getInsets();
				alloc.x += insets.left;
				alloc.y += insets.top;
				alloc.width -= insets.left + insets.right;
				alloc.height -= insets.top + insets.bottom;
				return alloc;
			}
			return null;
		}

		@Override
		public Rectangle getCharacterBounds(int i) {
			Msg.debug(this, getOutputLine() + "getCharBounds");
			return new Rectangle(X_START + i * charWidth, Y_START, charWidth, charHeight);

		}

		@Override
		public String getTextRange(int startIndex, int endIndex) {
			Msg.debug(this, getOutputLine() + "getTextRange");
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

		@Override
		public void setTextContents(String s) {
			Msg.debug(this, getOutputLine() + "\t\t****setTextContents");
			text = s;
			Swing.runLater(() -> firePropertyChange(AccessibleContext.ACCESSIBLE_TEXT_PROPERTY,
				null, cursorPos));

		}

		@Override
		public void insertTextAtIndex(int index, String s) {
			Msg.debug(this, getOutputLine() + getOutputLine() + "\t\t****insertTextAtIndex");

		}

		@Override
		public void delete(int startIndex, int endIndex) {
			Msg.debug(this, getOutputLine() + getOutputLine() + "\t\t****delete");
		}

		@Override
		public void cut(int startIndex, int endIndex) {
			Msg.debug(this, getOutputLine() + getOutputLine() + "\t\t****cut");

		}

		@Override
		public void paste(int startIndex) {
			Msg.debug(this, getOutputLine() + getOutputLine() + "\t\t****paste");
		}

		@Override
		public void replaceText(int startIndex, int endIndex, String s) {
			Msg.debug(this, getOutputLine() + getOutputLine() + "\t\t****replace text");
		}

		@Override
		public void selectText(int startIndex, int endIndex) {
			Msg.debug(this, getOutputLine() + getOutputLine() + "\t\t****selectText");
		}

		@Override
		public void setAttributes(int startIndex, int endIndex, AttributeSet as) {
			Msg.debug(this, getOutputLine() + getOutputLine() + "\t\t****setAttributes");
		}
	}

	String getOutputLine() {
		return "" + (msgCount++) + ": ";
	}

}
