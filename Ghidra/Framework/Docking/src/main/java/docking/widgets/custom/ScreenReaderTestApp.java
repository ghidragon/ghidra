package docking.widgets.custom;

import java.awt.*;
import java.io.FileNotFoundException;

import javax.swing.*;

import docking.framework.DockingApplicationConfiguration;
import generic.application.GenericApplicationLayout;
import ghidra.framework.Application;
import utility.application.ApplicationLayout;

public class ScreenReaderTestApp {
	JComponent mainPanel;

	ScreenReaderTestApp() throws FileNotFoundException {
		ApplicationLayout layout = new GenericApplicationLayout("Splash Screen Main", "1.0");
		DockingApplicationConfiguration config = new DockingApplicationConfiguration();

		config.setShowSplashScreen(false);
		Application.initializeApplication(layout, config);
		mainPanel = new JPanel(new BorderLayout());
		JTextArea textArea = new JTextArea("Apple", 20, 100);
		mainPanel.add(textArea, BorderLayout.NORTH);
//		mainPanel.add(new CustomTextComponent(), BorderLayout.CENTER);
		mainPanel.add(new CustomVirtualTextPanel(), BorderLayout.CENTER);
		mainPanel.add(buildList(), BorderLayout.EAST);

		mainPanel.add(buildExitButton(), BorderLayout.SOUTH);
	}

	private Component buildList() {

		JList<String> jList = new JList<>(new ExampleListModel());
		jList.setCellRenderer(new MyListCellRenderer());

		return jList;
	}

	private Component buildExitButton() {
		JPanel panel = new JPanel();
		JButton button = new JButton("Exit");
		button.addActionListener(e -> System.exit(0));
		panel.add(button);
		return panel;
	}

	private Component getMainPanel() {
		return mainPanel;
	}

	public static void main(String[] args) throws FileNotFoundException {
		JFrame frame = new JFrame("Screen Reader Test");
		ScreenReaderTestApp app = new ScreenReaderTestApp();
		frame.getContentPane().add(app.getMainPanel());
		frame.pack();
		frame.setVisible(true);

	}

	class ExampleListModel extends AbstractListModel<String> {
		String[] data = { "first", "second", "third", "forth", "fifth" };

		@Override
		public String getElementAt(int index) {
			return data[index];
		}

		@Override
		public int getSize() {
			return data.length;
		}
	}

	class MyListCellRenderer implements ListCellRenderer<String> {
		JTextField field = new JTextField("Initial", 20);

		@Override
		public Component getListCellRendererComponent(JList<? extends String> list, String value,
				int index, boolean isSelected, boolean cellHasFocus) {
			field.setText(value);
			if (isSelected) {
				field.setBackground(Color.GREEN);
			}
			else {
				field.setBackground(Color.WHITE);
			}
			return field;
		}

	}
}
