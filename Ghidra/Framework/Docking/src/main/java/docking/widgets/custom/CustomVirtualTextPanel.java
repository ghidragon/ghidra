package docking.widgets.custom;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.accessibility.*;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

import ghidra.util.Msg;

public class CustomVirtualTextPanel extends JPanel {
	private List<CustomVirtualField> fields = new ArrayList<>();
	int currentFieldIndex = 0;

	public CustomVirtualTextPanel() {
		fields.add(new CustomVirtualField(this, "This is line 1", 0, 10, 10, 100));
		fields.add(new CustomVirtualField(this, "This is line 2", 1, 40, 50, 200));
		fields.add(new CustomVirtualField(this, "This is line 3", 2, 40, 100, 300));
		setBorder(BorderFactory.createLineBorder(Color.black));
		setFocusable(true);
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				requestFocus();
			}
		});
		addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) {
				setBackground(Color.LIGHT_GRAY);
			}

			@Override
			public void focusGained(FocusEvent e) {
				if (accessibleContext != null) {
					accessibleContext.firePropertyChange(
						AccessibleContext.ACCESSIBLE_ACTIVE_DESCENDANT_PROPERTY, -1, fields.get(0));
					accessibleContext
							.firePropertyChange(AccessibleContext.ACCESSIBLE_CARET_PROPERTY, 0, 1);
				}
				setBackground(Color.WHITE);

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
						if (!fields.get(currentFieldIndex).cursorLeft()) {
							if (currentFieldIndex > 0) {
								setCurrentField(currentFieldIndex - 1);
								fields.get(currentFieldIndex).setCursor(-1);
							}
						}
						break;
					case KeyEvent.VK_RIGHT:
						if (!fields.get(currentFieldIndex).cursorRight()) {
							if (currentFieldIndex < fields.size() - 1) {
								setCurrentField(currentFieldIndex + 1);
								fields.get(currentFieldIndex).setCursor(0);
							}
						}
						break;
					case KeyEvent.VK_UP:
						if (currentFieldIndex > 0) {
							setCurrentField(currentFieldIndex - 1);
							fields.get(currentFieldIndex).setCursor(-1);
						}
						break;
					case KeyEvent.VK_DOWN:
						if (currentFieldIndex < 2) {
							setCurrentField(currentFieldIndex + 1);
							fields.get(currentFieldIndex).setCursor(0);
						}
						break;
				}
				repaint();
			}
		});
	}

	@Override
	protected void paintChildren(Graphics g) {
		fields.get(0).paint(g, currentFieldIndex);
		fields.get(1).paint(g, currentFieldIndex);
		fields.get(2).paint(g, currentFieldIndex);
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(100, 300);
	}

	@Override
	public AccessibleContext getAccessibleContext() {
		if (accessibleContext == null) {
			accessibleContext = new AccessibleField();
		}
		return accessibleContext;
	}

	private void setCurrentField(int n) {
		Msg.debug(this, "set current Field " + n);
		if (currentFieldIndex == n) {
			return;
		}
		CustomVirtualField oldField = currentFieldIndex < 0 ? null : fields.get(currentFieldIndex);
		currentFieldIndex = n;
		CustomVirtualField newField = fields.get(currentFieldIndex);
		accessibleContext.firePropertyChange(
			AccessibleContext.ACCESSIBLE_ACTIVE_DESCENDANT_PROPERTY,
			oldField, newField);
		accessibleContext.firePropertyChange(AccessibleContext.ACCESSIBLE_VISIBLE_DATA_PROPERTY,
			Boolean.valueOf(false), Boolean.valueOf(true));
		accessibleContext.firePropertyChange(AccessibleContext.ACCESSIBLE_TEXT_PROPERTY,
			"", newField.getText());
		accessibleContext.firePropertyChange(AccessibleContext.ACCESSIBLE_CARET_PROPERTY, 0, 1);
	}

	protected class AccessibleField extends AccessibleJComponent {
		@Override
		public AccessibleRole getAccessibleRole() {
			return AccessibleRole.SWING_COMPONENT;
		}

		@Override
		public int getAccessibleChildrenCount() {
			Msg.debug(this, "getChildCount = 3");
			return 3;
		}

		@Override
		public Accessible getAccessibleChild(int i) {
			Msg.debug(this, "getChildAtIndex: " + i);
			return fields.get(i);
		}

		@Override
		public Accessible getAccessibleAt(Point p) {
			Msg.debug(this, "getAccessibleAt: " + p);
			for (int i = 0; i < 3; i++) {
				CustomVirtualField customVirtualField = fields.get(i);
				if (customVirtualField.contains(p)) {
					return customVirtualField;
				}
			}
			return null;
		}

		@Override
		public Color getBackground() {
			Msg.debug(this, "getBackground: ");
			return super.getBackground();
		}

		@Override
		public void setBackground(Color c) {
			Msg.debug(this, "setBackground: ");
			super.setBackground(c);
		}

		@Override
		public Color getForeground() {
			Msg.debug(this, "getForegound: ");
			return super.getForeground();

		}

		@Override
		public void setForeground(Color c) {
			Msg.debug(this, "setForeground: ");
			super.setForeground(c);
		}

		@Override
		public Cursor getCursor() {
			Msg.debug(this, "getCursor");
			return super.getCursor();
		}

		@Override
		public void setCursor(Cursor cursor) {
			Msg.debug(this, "setCursor");
			super.setCursor(cursor);
		}

		@Override
		public Font getFont() {
			Font f = super.getFont();
			Msg.debug(this, "getFont: " + f);
			return f;
		}

		@Override
		public void setFont(Font f) {
			Msg.debug(this, "setFont: " + f);
			super.setFont(f);
		}

		@Override
		public FontMetrics getFontMetrics(Font f) {
			FontMetrics fontMetrics = super.getFontMetrics(f);
			Msg.debug(this, "getFontMetrics: " + fontMetrics);
			return fontMetrics;
		}

		@Override
		public boolean isEnabled() {
			Msg.debug(this, "isEnabled");
			return true;
		}

		@Override
		public void setEnabled(boolean b) {
			Msg.debug(this, "setEnabled");

		}

		@Override
		public boolean isVisible() {
			Msg.debug(this, "isVisible");
			return super.isVisible();
		}

		@Override
		public void setVisible(boolean b) {
			Msg.debug(this, "setVisible");
			super.setVisible(b);
		}

		@Override
		public boolean isShowing() {
			Msg.debug(this, "isShowing");
			return super.isShowing();
		}

		@Override
		public boolean contains(Point p) {
			Msg.debug(this, "contains " + p + ", result = " + super.contains(p));
			return super.contains(p);
		}

		@Override
		public Point getLocationOnScreen() {
			Point locationOnScreen = super.getLocationOnScreen();
			Msg.debug(this, "getLocationOnScreenn: " + locationOnScreen);
			return locationOnScreen;
		}

		@Override
		public Point getLocation() {
			Point loc = super.getLocation();
			Msg.debug(this, "getLocation: " + loc);
			return loc;
		}

		@Override
		public void setLocation(Point p) {
			Msg.debug(this, "setLocation " + p);
			super.setLocation(p);
		}

		@Override
		public Rectangle getBounds() {
			Rectangle bounds = super.getBounds();
			Msg.debug(this, "getBounds: " + bounds);
			return bounds;
		}

		@Override
		public void setBounds(Rectangle r) {
			Msg.debug(this, "setBounds: " + r);

		}

		@Override
		public Dimension getSize() {
			Dimension size = super.getSize();
			Msg.debug(this, "getSize: " + size);
			return size;
		}

		@Override
		public void setSize(Dimension d) {
			Msg.debug(this, "setSize: " + d);
			super.setSize(d);
		}

		@Override
		public boolean isFocusTraversable() {
			boolean focusTraversable = super.isFocusTraversable();
			Msg.debug(this, "isFocusTraversable: " + focusTraversable);
			return focusTraversable;
		}

		@Override
		public void requestFocus() {
			Msg.debug(this, "request focus");
			super.requestFocus();
		}

		@Override
		public void addFocusListener(FocusListener l) {
			Msg.debug(this, "addFocusListener");
			super.addFocusListener(l);
		}

		@Override
		public void removeFocusListener(FocusListener l) {
			Msg.debug(this, "removeFocusListener");
			super.removeFocusListener(l);
		}

		@Override
		public AccessibleStateSet getAccessibleStateSet() {
			AccessibleStateSet accessibleStateSet = super.getAccessibleStateSet();
			Msg.debug(this, "getAccissibleStateSet: " + accessibleStateSet);
			return accessibleStateSet;
		}

		@Override
		public int getAccessibleIndexInParent() {
			int index = super.getAccessibleIndexInParent();
			Msg.debug(this, "getAccessibleIndexInParent: " + index);
			return index;
		}

		@Override
		public Locale getLocale() throws IllegalComponentStateException {
			Msg.debug(this, "getLocale");
			return super.getLocale();
		}

		@Override
		public String getToolTipText() {
			String toolTipText = super.getToolTipText();
			Msg.debug(this, "getToolTipText: " + toolTipText);
			return toolTipText;
		}

		@Override
		public String getTitledBorderText() {
			String text = super.getTitledBorderText();
			Msg.debug(this, "getTitledBorderText: " + text);
			return text;

		}

		@Override
		public AccessibleKeyBinding getAccessibleKeyBinding() {
			AccessibleKeyBinding binding = super.getAccessibleKeyBinding();
			Msg.debug(this, "getAccessibleKeyBinding: " + binding);
			return binding;
		}
	}
}
