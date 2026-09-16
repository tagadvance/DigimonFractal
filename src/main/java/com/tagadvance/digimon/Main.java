package com.tagadvance.digimon;

import static java.awt.Color.BLACK;
import static java.awt.image.BufferedImage.TYPE_INT_RGB;
import static javax.swing.JOptionPane.ERROR_MESSAGE;

import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
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
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkEvent.EventType;
import javax.swing.event.HyperlinkListener;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * The Swing front end: a canvas, a slider for the seed, and a menu to save the image. Construct it
 * on any thread, but hand it to {@link SwingUtilities#invokeLater(Runnable)} rather than calling
 * {@link #run()} directly.
 */
public final class Main implements Runnable {

  static {
    try {
      final String systemLookAndFeel = UIManager.getSystemLookAndFeelClassName();
      UIManager.setLookAndFeel(systemLookAndFeel);
    } catch (final ClassNotFoundException
        | InstantiationException
        | IllegalAccessException
        | UnsupportedLookAndFeelException e) {
      e.printStackTrace(System.err);
    }
  }

  private final ResourceBundle resourceBundle;
  private final FractalPainter painter;

  public Main(final ResourceBundle resourceBundle, final FractalPainter painter) {
    super();
    this.resourceBundle = resourceBundle;
    this.painter = painter;
  }

  static void main() {
    final var locale = Locale.getDefault();
    final var resourceBundle = ResourceBundle.getBundle("interface", locale);
    final double seed = 1;
    final var painter = new FractalPainter(seed);
    final var main = new Main(resourceBundle, painter);
    SwingUtilities.invokeLater(main);
  }

  @Override
  public void run() {
    final var window = createMainWindow();
    window.pack();
    window.setLocationRelativeTo(null);
    window.setVisible(true);
  }

  public Window createMainWindow() {
    final String title = resourceBundle.getString("mainTitle");
    final JFrame frame = new JFrame(title);
    frame.setResizable(false);
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

    final BufferedImage image = createCanvasImage();
    final CanvasComponent canvas = new CanvasComponent(image);
    canvas.setBackground(BLACK);
    updateCanvasImage(canvas, image);

    final var slider = new JSlider();
    slider.setMajorTickSpacing(100);
    slider.setMaximum(2000);
    slider.setMinimum(1000);
    slider.addChangeListener(
        new ChangeListener() {

          // Ignore the final event, when the mouse is released. One could also use
          // slider.getValueIsAdjusting(); however, I like watching the fractal change.
          private int previousValue;

          @Override
          public void stateChanged(final ChangeEvent e) {
            if (e.getSource() instanceof final JSlider slider) {
              final int value = slider.getValue();
              if (value != previousValue) {
                final double seed = value / 1000d;
                painter.setSeed(seed);
                updateCanvasImage(canvas, image);
                previousValue = value;
              }
            }
          }
        });

    final String file = resourceBundle.getString("file");
    final var menuFile = new JMenu(file);
    final String saveAs = resourceBundle.getString("saveAs");
    final var menuItemSaveAs = new JMenuItem(saveAs, KeyEvent.VK_S);
    final var controlSKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK);
    menuItemSaveAs.setAccelerator(controlSKeyStroke);
    menuItemSaveAs.addActionListener(
        _ -> {
          final var chooser = new JFileChooser();
          final String description = resourceBundle.getString("imageFiles");
          final var imageFilter =
              new FileNameExtensionFilter(description, ImageIO.getReaderFileSuffixes());
          chooser.setFileFilter(imageFilter);
          final var selectedFile = new File("coast.png");
          chooser.setSelectedFile(selectedFile);
          final int option = chooser.showSaveDialog(frame);
          if (option == JFileChooser.APPROVE_OPTION) {
            saveAs(frame, image, chooser.getSelectedFile());
          }
        });
    menuFile.add(menuItemSaveAs);

    final String refresh = resourceBundle.getString("refresh");
    final var menuItemRefresh = new JMenuItem(refresh, KeyEvent.VK_R);
    final var f5KeyStroke = KeyStroke.getKeyStroke("F5");
    menuItemRefresh.setAccelerator(f5KeyStroke);
    menuItemRefresh.addActionListener(_ -> updateCanvasImage(canvas, image));
    menuFile.add(menuItemRefresh);

    final String exit = resourceBundle.getString("exit");
    final var menuItemExit = new JMenuItem(exit);
    final int modifiers = 0;
    final var escapeKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, modifiers);
    menuItemExit.setAccelerator(escapeKeyStroke);
    menuItemExit.addActionListener(_ -> System.exit(0));
    menuFile.add(menuItemExit);

    final String help = resourceBundle.getString("help");
    final var menuHelp = createMenuHelp(help, frame);

    final var menuBar = new JMenuBar();
    menuBar.add(menuFile);
    menuBar.add(menuHelp);
    frame.setJMenuBar(menuBar);

    final var contentPane = new JPanel();
    final var layout = new GroupLayout(contentPane);
    // the layout was generated in NetBeans
    layout.setHorizontalGroup(
        layout
            .createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(
                GroupLayout.Alignment.TRAILING,
                layout
                    .createSequentialGroup()
                    .addContainerGap()
                    .addGroup(
                        layout
                            .createParallelGroup(GroupLayout.Alignment.TRAILING)
                            .addComponent(
                                canvas,
                                GroupLayout.DEFAULT_SIZE,
                                GroupLayout.DEFAULT_SIZE,
                                Short.MAX_VALUE)
                            .addComponent(
                                slider,
                                GroupLayout.DEFAULT_SIZE,
                                GroupLayout.DEFAULT_SIZE,
                                Short.MAX_VALUE))
                    .addContainerGap()));
    layout.setVerticalGroup(
        layout
            .createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(
                layout
                    .createSequentialGroup()
                    .addContainerGap()
                    .addComponent(
                        slider,
                        GroupLayout.PREFERRED_SIZE,
                        GroupLayout.DEFAULT_SIZE,
                        GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(
                        canvas, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()));
    contentPane.setLayout(layout);

    frame.setContentPane(contentPane);
    return frame;
  }

  private JMenu createMenuHelp(final String help, final JFrame frame) {
    final var menuHelp = new JMenu(help);
    final var menuItemAbout = new JMenuItem("About", KeyEvent.VK_A);
    menuItemAbout.addActionListener(
        _ -> {
          final String aboutTitle = resourceBundle.getString("aboutTitle");
          final var dialog = new JFrame(aboutTitle);
          final int width = 540, height = 240;
          final var preferredSize = new Dimension(width, height);
          final String text = resourceBundle.getString("aboutText");
          final var about = createAbout(text, preferredSize);
          dialog.setContentPane(about);
          dialog.pack();
          dialog.setLocationRelativeTo(frame);
          dialog.setVisible(true);
        });
    menuHelp.add(menuItemAbout);

    return menuHelp;
  }

  /**
   * Writes {@code image} to {@code file} off the event thread, in the format named by the file's
   * extension (PNG when it has none). A failure is shown in a dialog over {@code parent}.
   */
  private void saveAs(final JFrame parent, final BufferedImage image, final File file) {
    new SwingWorker<Boolean, Void>() {

      @Override
      protected Boolean doInBackground() throws Exception {
        final String name = file.getName();
        String extension = "png";
        final int index = name.lastIndexOf('.');
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
          final boolean b = get();
          // TODO: if (b) show saved dialog
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
          e.printStackTrace(System.err);
          final String message = e.getMessage();
          final String title = resourceBundle.getString("error");
          JOptionPane.showMessageDialog(parent, message, title, ERROR_MESSAGE);
        }
      }
    }.execute();
  }

  /** The backing image is shared with the canvas, so repainting it repaints the canvas. */
  private BufferedImage createCanvasImage() {
    final int width = 512, height = 200;

    return new BufferedImage(width, height, TYPE_INT_RGB);
  }

  /** Clears {@code image} to the canvas background, paints a fresh coastline, and repaints. */
  private void updateCanvasImage(final CanvasComponent canvas, final BufferedImage image) {
    final var g = image.getGraphics();
    final var background = canvas.getBackground();
    g.setColor(background);
    final int x = 0, y = 0, width = image.getWidth(), height = image.getHeight();
    g.fillRect(x, y, width, height);
    painter.paintCustom(g);
    g.dispose();
    canvas.repaint();
  }

  /** Builds the About panel from an HTML string whose links open in the desktop browser. */
  public Container createAbout(final String text, final Dimension preferredSize) {
    final String mimeType = "text/html";
    final var editor = new JEditorPane(mimeType, text);
    editor.setOpaque(false);
    editor.setEditable(false);
    editor.addHyperlinkListener(new SimpleHyperlinkListener(editor));
    editor.setFocusable(true);
    final var panel = new JPanel();
    panel.setPreferredSize(preferredSize);
    final var layout = new GroupLayout(panel);
    panel.setLayout(layout);
    layout.setHorizontalGroup(
        layout
            .createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(
                layout
                    .createSequentialGroup()
                    .addContainerGap()
                    .addComponent(editor)
                    .addContainerGap()));
    layout.setVerticalGroup(
        layout
            .createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(
                layout
                    .createSequentialGroup()
                    .addContainerGap()
                    .addComponent(editor)
                    .addContainerGap()));
    return panel;
  }

  /**
   * Opens activated links with the desktop browser, or the mail client for {@code mailto:}, and
   * shows a hand cursor while hovering. A desktop that supports neither silently does nothing.
   */
  public static final class SimpleHyperlinkListener implements HyperlinkListener {

    private final Component sourceComponent;

    public SimpleHyperlinkListener(final Component sourceComponent) {
      super();
      this.sourceComponent = sourceComponent;
    }

    @Override
    public void hyperlinkUpdate(final HyperlinkEvent e) {
      final var eventType = e.getEventType();
      if (eventType == EventType.ENTERED) {
        final var cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
        sourceComponent.setCursor(cursor);
      } else if (eventType == EventType.ACTIVATED) {
        final var url = e.getURL();
        open(url);
      } else if (eventType == EventType.EXITED) {
        final var cursor = Cursor.getDefaultCursor();
        sourceComponent.setCursor(cursor);
      }
    }

    public void open(final URL url) {
      final String externalForm = url.toExternalForm();
      final var desktop = Desktop.getDesktop();
      try {
        final var uri = url.toURI();
        if (externalForm.startsWith("mailto:")) {
          if (desktop.isSupported(Desktop.Action.MAIL)) {
            desktop.mail(uri);
          }
        } else if (desktop.isSupported(Desktop.Action.BROWSE)) {
          desktop.browse(uri);
        }
      } catch (URISyntaxException | IOException ex) {
        ex.printStackTrace(System.err);
      }
    }
  }

}
