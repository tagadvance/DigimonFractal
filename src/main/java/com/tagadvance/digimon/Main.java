package com.tagadvance.digimon;

import static java.awt.Color.BLACK;
import static java.awt.image.BufferedImage.TYPE_INT_RGB;
import static javax.swing.JOptionPane.ERROR_MESSAGE;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;

import javax.imageio.ImageIO;
import javax.swing.GroupLayout;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.KeyStroke;
import javax.swing.LayoutStyle;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkEvent.EventType;
import javax.swing.event.HyperlinkListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * 
 * @author Tag <tagadvance@gmail.com>
 *
 */
public class Main implements Runnable {
	
	static {
		try {
			String systemLookAndFeel = UIManager.getSystemLookAndFeelClassName();
			UIManager.setLookAndFeel(systemLookAndFeel);
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| javax.swing.UnsupportedLookAndFeelException e) {
			e.printStackTrace(System.err);
		}
	}

	private final ResourceBundle resourceBundle;
	private final FractalPainter painter;

	public Main(ResourceBundle resourceBundle, FractalPainter painter) {
		super();
		this.resourceBundle = resourceBundle;
		this.painter = painter;
	}

	public static void main(String[] args) {
		Locale locale = Locale.getDefault();
		ResourceBundle resourceBundle = ResourceBundle.getBundle("interface", locale);
		double seed = 1;
		FractalPainter painter = new FractalPainter(seed);
		Main main = new Main(resourceBundle, painter);
		SwingUtilities.invokeLater(main);
	}

	public void run() {
		Window window = createMainWindow();
		window.pack();
		window.setLocationRelativeTo(null);
		window.setVisible(true);
	}
	
	public Window createMainWindow() {
		String title = resourceBundle.getString("mainTitle");
		final JFrame frame = new JFrame(title);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		final BufferedImage image = createCanvasImage();
		final CanvasComponent canvas = new CanvasComponent(image);
		canvas.setBackground(BLACK);
		updateCanvasImage(canvas, image);

		JSlider slider = new JSlider();
		slider.setMajorTickSpacing(100);
		slider.setMaximum(2000);
		slider.setMinimum(1000);
		slider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				Object source = e.getSource();
				if (source instanceof JSlider) {
					JSlider slider = (JSlider) source;
					int value = slider.getValue();
					double seed = value / 1000d;
					painter.setSeed(seed);
					updateCanvasImage(canvas, image);
				}
			}
		});

		String file = resourceBundle.getString("file");
		JMenu menuFile = new JMenu(file);
		String saveAs = resourceBundle.getString("saveAs");
		JMenuItem menuItemSaveAs = new JMenuItem(saveAs, KeyEvent.VK_S);
		KeyStroke controlSKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK);
		menuItemSaveAs.setAccelerator(controlSKeyStroke);
		menuItemSaveAs.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				String description = resourceBundle.getString("imageFiles");
				FileFilter imageFilter = new FileNameExtensionFilter(description, ImageIO.getReaderFileSuffixes());
				chooser.setFileFilter(imageFilter);
				File selectedFile = new File("coast.png");
				chooser.setSelectedFile(selectedFile);
				int option = chooser.showSaveDialog(frame);
				switch (option) {
				case JFileChooser.APPROVE_OPTION:
					File file = chooser.getSelectedFile();
					saveAs(image, file);
				}
			}

			private void saveAs(final BufferedImage image, final File file) {
				new SwingWorker<Boolean, Void>() {

					@Override
					protected Boolean doInBackground() throws Exception {
						String name = file.getName();
						String extension = "png";
						int index = name.indexOf('.');
						if (index >= 0) {
							extension = name.substring(index + 1);
						}
						return ImageIO.write(image, extension, file);
					}

					@Override
					protected void done() {
						super.done();
						try {
							@SuppressWarnings("unused")
							boolean b = get();
							// TODO: if (b) show saved dialog
						} catch (InterruptedException e) {
							Thread.currentThread().interrupt();
						} catch (ExecutionException e) {
							e.printStackTrace(System.err);
							String message = e.getMessage();
							String title = resourceBundle.getString("error");
							JOptionPane.showMessageDialog(frame, message, title, ERROR_MESSAGE);
						}
					}

				}.execute();
			}

		});
		menuFile.add(menuItemSaveAs);

		String refresh = resourceBundle.getString("refresh");
		JMenuItem menuItemRefresh = new JMenuItem(refresh, KeyEvent.VK_R);
		KeyStroke f5KeyStroke = KeyStroke.getKeyStroke("F5");
		menuItemRefresh.setAccelerator(f5KeyStroke);
		menuItemRefresh.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateCanvasImage(canvas, image);
			}
		});
		menuFile.add(menuItemRefresh);

		String exit = resourceBundle.getString("exit");
		JMenuItem menuItemExit = new JMenuItem(exit);
		int modifiers = 0;
		KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, modifiers);
		menuItemExit.setAccelerator(escapeKeyStroke);
		menuItemExit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		menuFile.add(menuItemExit);

		String help = resourceBundle.getString("help");
		JMenu menuHelp = new JMenu(help);
		JMenuItem menuItemAbout = new JMenuItem("About", KeyEvent.VK_A);
		menuItemAbout.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String title = resourceBundle.getString("aboutTitle");
				JFrame dialog = new JFrame(title);
				int width = 540, height = 240;
				Dimension preferredSize = new Dimension(width, height);
				String text = resourceBundle.getString("aboutText");
				Container about = createAbout(text, preferredSize);
				dialog.setContentPane(about);
				dialog.pack();
				dialog.setLocationRelativeTo(frame);
				dialog.setVisible(true);
			}
		});
		menuHelp.add(menuItemAbout);
		
		JMenuBar menuBar = new JMenuBar();
		menuBar.add(menuFile);
		menuBar.add(menuHelp);
		frame.setJMenuBar(menuBar);
		
		JPanel contentPane = new JPanel();
		GroupLayout layout = new GroupLayout(contentPane);
		// the layout was generated in NetBeans
		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.LEADING)
			.addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
				.addContainerGap()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
					.addComponent(canvas, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addComponent(slider, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
				.addContainerGap())
		);
		layout.setVerticalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.LEADING)
			.addGroup(layout.createSequentialGroup()
				.addContainerGap()
				.addComponent(slider, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				.addComponent(canvas, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addContainerGap())
		);
		contentPane.setLayout(layout);
		
		frame.setContentPane(contentPane);
		return frame;
	}
	
	protected BufferedImage createCanvasImage() {
		int width = 512, height = 200;
		BufferedImage image = new BufferedImage(width, height, TYPE_INT_RGB);
		return image;
	}
	
	protected void updateCanvasImage(CanvasComponent canvas, BufferedImage image) {
		Graphics g = image.getGraphics();
		Color background = canvas.getBackground();
		g.setColor(background);
		int x = 0, y = 0, width = image.getWidth(), height = image.getHeight();
		g.fillRect(x, y, width, height);
		painter.paintCustom(g);
		g.dispose();
		canvas.repaint();
	}
	
	public Container createAbout(String text, Dimension preferredSize) {
		String mimeType = "text/html";
		JEditorPane editor = new JEditorPane(mimeType, text);
		editor.setOpaque(false);
		editor.setEditable(false);
		editor.addHyperlinkListener(new SimpleHyperlinkListener(editor));
		editor.setFocusable(true);
		JPanel panel = new JPanel();
		panel.setPreferredSize(preferredSize);
		GroupLayout layout = new GroupLayout(panel);
		panel.setLayout(layout);
		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.LEADING)
			.addGroup(layout.createSequentialGroup()
				.addContainerGap()
				.addComponent(editor)
				.addContainerGap())
		);
		layout.setVerticalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.LEADING)
			.addGroup(layout.createSequentialGroup()
				.addContainerGap()
				.addComponent(editor)
				.addContainerGap())
		);
		return panel;
	}
	
	public static class SimpleHyperlinkListener implements HyperlinkListener {

		private final Component sourceComponent;

		public SimpleHyperlinkListener(Component sourceComponent) {
			super();
			this.sourceComponent = sourceComponent;
		}

		@Override
		public void hyperlinkUpdate(HyperlinkEvent e) {
			EventType eventType = e.getEventType();
			if (eventType == EventType.ENTERED) {
				Cursor cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
				sourceComponent.setCursor(cursor);
			} else if (eventType == EventType.ACTIVATED) {
				URL url = e.getURL();
				String externalForm = url.toExternalForm();
				Desktop desktop = Desktop.getDesktop();
				if (externalForm.startsWith("mailto:") && desktop.isSupported(Desktop.Action.MAIL)) {
					try {
						URI uri = url.toURI();
						desktop.mail(uri);
						return;
					} catch (URISyntaxException ex) {
						ex.printStackTrace(System.err);
					} catch (IOException ex) {
						ex.printStackTrace(System.err);
					}
				}
				if (desktop.isSupported(Desktop.Action.BROWSE)) {
					try {
						URI uri = url.toURI();
						desktop.browse(uri);
					} catch (URISyntaxException ex) {
						ex.printStackTrace(System.err);
					} catch (IOException ex) {
						ex.printStackTrace(System.err);
					}
				}
			} else if (eventType == EventType.EXITED) {
				Cursor cursor = Cursor.getDefaultCursor();
				sourceComponent.setCursor(cursor);
			}
		}

	}

}
