package ch.sysout.pixelatedbackgroundimagegenerator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.text.ParseException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.JTextComponent;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

import ch.sysout.pixelatedbackgroundimagegenerator.util.ColorUtil;

public class MainFrame extends JFrame implements ActionListener, FocusListener {
	private static final long serialVersionUID = 1L;

	private PixelatedBackgroundPanel pnlMain;

	private JPanel pnlImageOptions;
	private JSpinner spnImageWidth;
	private JSpinner spnImageHeight;
	private JSpinner spnPixelSizeW;
	private JSpinner spnPixelSizeH;
	private JSpinner spnMaxLoops;
	private JButton btnColor;
	private JButton btnStartSimulation;
	private JCheckBox chkComplementary;

	private JCheckBox chkRandomColors;
	private JCheckBox chkAutoDecideBrighterDarker;

	private JPanel pnlSaveOptions;
	private JButton btnSaveImageAs;
	private JButton btnCopyImageToClipboard;

	private CustomColor baseColor = new CustomColor(200, 50, 85);

	private JDialog dlg;

	private JColorChooser colorChooser;

	private JCheckBox chkFillWidth = new JCheckBox("Fill Width");
	private JCheckBox chkFillHeight = new JCheckBox("Fill Height");

	public MainFrame() {
		super("Pixelated Background Image Generator");
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		initComponents();
		addListeners();
		createUI();
		pack();
	}

	private void initComponents() {
		pnlImageOptions = new JPanel();
		spnImageWidth = new JSpinner();
		spnImageHeight = new JSpinner();
		spnPixelSizeW = new JSpinner();
		spnPixelSizeH = new JSpinner();
		spnMaxLoops = new JSpinner();
		btnColor = new JButton("Color");
		btnColor.setBackground(baseColor);
		btnStartSimulation = new JButton("Start Simulation");
		chkComplementary = new JCheckBox("Complementary");
		chkRandomColors = new JCheckBox("Random Colors");
		chkAutoDecideBrighterDarker = new JCheckBox("Auto Decide Brighter/Darker");

		pnlMain = new PixelatedBackgroundPanel(new BorderLayout());
		pnlMain.setOpaque(true);
		pnlMain.setBaseColor(baseColor);
		pnlMain.setMaxLoops(20);
		pnlMain.setFactor(0.99);

		pnlSaveOptions = new JPanel();
		btnSaveImageAs = new JButton("Save image as...");
		btnCopyImageToClipboard = new JButton("Copy image to clipboard");
	}

	private void addListeners() {
		chkRandomColors.addActionListener(this);
		chkAutoDecideBrighterDarker.addActionListener(this);
		btnColor.addActionListener(this);
		btnStartSimulation.addActionListener(this);
		chkComplementary.addActionListener(this);
		chkFillWidth.addActionListener(this);
		chkFillHeight.addActionListener(this);
		spnImageWidth.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				try {
					spnImageWidth.commitEdit();
				} catch (ParseException e1) {
					e1.printStackTrace();
				} finally {
					pnlMain.setImageWidth((int) spnImageWidth.getValue());
				}
			}
		});

		JSpinner[] spinners = { spnImageWidth, spnImageHeight, spnPixelSizeW, spnPixelSizeH, spnMaxLoops };
		for (JSpinner spinner : spinners) {
			JTextField txt = ((JSpinner.DefaultEditor) spinner.getEditor()).getTextField();
			txt.addFocusListener(this);
		}

		spnImageHeight.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				try {
					spnImageHeight.commitEdit();
				} catch (ParseException e1) {
					e1.printStackTrace();
				} finally {
					pnlMain.setImageHeight((int) spnImageHeight.getValue());
				}
			}
		});

		spnPixelSizeW.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				try {
					spnPixelSizeW.commitEdit();
				} catch (ParseException e1) {
					e1.printStackTrace();
				} finally {
					pnlMain.setPixelSizeW((int) spnPixelSizeW.getValue());
				}
			}
		});

		spnPixelSizeH.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				try {
					spnPixelSizeH.commitEdit();
				} catch (ParseException e1) {
					e1.printStackTrace();
				} finally {
					pnlMain.setPixelSizeH((int) spnPixelSizeH.getValue());
				}
			}
		});

		spnMaxLoops.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				try {
					spnMaxLoops.commitEdit();
				} catch (ParseException e1) {
					e1.printStackTrace();
				} finally {
					pnlMain.setMaxLoops((int) spnMaxLoops.getValue());
					pnlMain.repaint();
				}
			}
		});

		btnCopyImageToClipboard.addActionListener(this);

		pnlMain.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					doPop(e);
					pnlMain.repaint();
				}
			}

			private void doPop(MouseEvent e) {
				PopUpDemo menu = new PopUpDemo();
				menu.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}

	private void createUI() {
		FormLayout formLayout = new FormLayout();
		pnlImageOptions.setLayout(formLayout);
		formLayout.appendRow(RowSpec.decode("fill:min"));
		formLayout.appendRow(RowSpec.decode("$lgap"));
		formLayout.appendRow(RowSpec.decode("fill:min"));

		formLayout.appendColumn(ColumnSpec.decode("min"));
		pnlImageOptions.add(btnStartSimulation, CC.xywh(1, 1, 1, formLayout.getRowCount()));

		formLayout.appendColumn(ColumnSpec.decode("$rgap"));
		formLayout.appendColumn(ColumnSpec.decode("min"));
		pnlImageOptions.add(chkFillWidth, CC.xy(3, 1));
		pnlImageOptions.add(chkFillHeight, CC.xy(3, 3));

		formLayout.appendColumn(ColumnSpec.decode("$rgap"));
		formLayout.appendColumn(ColumnSpec.decode("min"));
		pnlImageOptions.add(spnImageWidth, CC.xy(5, 1));
		pnlImageOptions.add(spnImageHeight, CC.xy(5, 3));

		formLayout.appendColumn(ColumnSpec.decode("$rgap"));
		formLayout.appendColumn(ColumnSpec.decode("min"));
		pnlImageOptions.add(spnPixelSizeW, CC.xy(7, 1));
		pnlImageOptions.add(spnPixelSizeH, CC.xy(7, 3));

		formLayout.appendColumn(ColumnSpec.decode("$rgap"));
		formLayout.appendColumn(ColumnSpec.decode("min"));
		pnlImageOptions.add(btnColor, CC.xywh(9, 1, 1, formLayout.getRowCount()));

		formLayout.appendColumn(ColumnSpec.decode("$rgap"));
		formLayout.appendColumn(ColumnSpec.decode("min"));
		pnlImageOptions.add(chkComplementary, CC.xy(11, 1));
		pnlImageOptions.add(chkRandomColors, CC.xy(11, 3));

		formLayout.appendColumn(ColumnSpec.decode("$rgap"));
		formLayout.appendColumn(ColumnSpec.decode("min"));
		pnlImageOptions.add(spnMaxLoops, CC.xy(13, 1));
		pnlImageOptions.add(chkAutoDecideBrighterDarker, CC.xy(13, 3));
		add(pnlImageOptions, BorderLayout.NORTH);
		add(pnlMain);

		pnlSaveOptions.add(btnSaveImageAs);
		pnlSaveOptions.add(btnCopyImageToClipboard);
		add(pnlSaveOptions, BorderLayout.SOUTH);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source.equals(btnColor)) {
			colorChooser = new JColorChooser();
			dlg = JColorChooser.createDialog(MainFrame.this, "Color Chooser", false, colorChooser, new OKListener(),
					null);
			dlg.setSize(300, 300);
			dlg.pack();
			dlg.setVisible(true);
		} else if (source.equals(btnStartSimulation)) {
			System.out.println("starting simulation...");
		} else if (source.equals(chkComplementary)) {
			Color color = ColorUtil.getComplementaryAsColor(pnlMain.getBaseColor());
			btnColor.setBackground(color);
			pnlMain.setBaseColor(color);
			pnlMain.repaint();
		} else if (source.equals(chkRandomColors)) {
			pnlMain.setRandomColors(chkRandomColors.isSelected());
			btnColor.setEnabled(!chkRandomColors.isSelected());
		} else if (source.equals(chkAutoDecideBrighterDarker)) {
			pnlMain.setAutoDecideBrighterDarker(chkAutoDecideBrighterDarker.isSelected());
		} else if (source.equals(chkFillWidth)) {
			pnlMain.setFillWidthEnabled(chkFillWidth.isSelected());
		} else if (source.equals(chkFillHeight)) {
			pnlMain.setFillHeightEnabled(chkFillHeight.isSelected());
		} else if (source.equals(btnCopyImageToClipboard)) {
			copyImageToClipboard(pnlMain.getBackgroundImage());
		} else {
			System.out.println(source);
		}
		pnlMain.repaint();
	}

	private void copyImageToClipboard(Image image) {
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new ImageSelection(image), null);
	}

	// This class is used to hold an image while on the clipboard.
	static class ImageSelection implements Transferable {
		private Image image;

		public ImageSelection(Image image) {
			this.image = image;
		}

		// Returns supported flavors
		@Override
		public DataFlavor[] getTransferDataFlavors() {
			return new DataFlavor[] { DataFlavor.imageFlavor };
		}

		// Returns true if flavor is supported
		@Override
		public boolean isDataFlavorSupported(DataFlavor flavor) {
			return DataFlavor.imageFlavor.equals(flavor);
		}

		// Returns image
		@Override
		public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
			if (!DataFlavor.imageFlavor.equals(flavor)) {
				throw new UnsupportedFlavorException(flavor);
			}
			return image;
		}
	}

	public class OKListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			Color color = colorChooser.getColor();
			btnColor.setBackground(color);
			pnlMain.setBaseColor(color);
			pnlMain.repaint();
		}
	}

	class PopUpDemo extends JPopupMenu {
		private static final long serialVersionUID = 1L;

		private JMenuItem itmCopy;
		private JMenuItem itmOpenInExplorer;

		public PopUpDemo() {
			itmCopy = new JMenuItem("Copy (Ctrl+C)");
			itmOpenInExplorer = new JMenuItem("Open File Path");
			add(itmCopy);
			add(itmOpenInExplorer);

			itmCopy.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					// Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
					// TransferableImage trans = new TransferableImage(currentImage);
					// clipboard.setContents(trans, null);
					// txtLog.setText("copied image");
				}
			});

			itmOpenInExplorer.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					// try {
					// Runtime.getRuntime().exec("explorer.exe /select," + currentFilePath);
					// } catch (IOException e1) {
					// e1.printStackTrace();
					// }
				}
			});
		}
	}

	private class TransferableImage implements Transferable {
		private Image image;

		public TransferableImage(Image image) {
			this.image = image;
		}

		@Override
		public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
			if (flavor.equals(DataFlavor.imageFlavor) && image != null) {
				return image;
			} else {
				throw new UnsupportedFlavorException(flavor);
			}
		}

		@Override
		public DataFlavor[] getTransferDataFlavors() {
			DataFlavor[] flavors = new DataFlavor[1];
			flavors[0] = DataFlavor.imageFlavor;
			return flavors;
		}

		@Override
		public boolean isDataFlavorSupported(DataFlavor flavor) {
			DataFlavor[] flavors = getTransferDataFlavors();
			for (int i = 0; i < flavors.length; i++) {
				if (flavor.equals(flavors[i])) {
					return true;
				}
			}
			return false;
		}
	}

	@Override
	public void focusGained(FocusEvent e) {
		SwingUtilities.invokeLater(() -> selectAll((JTextField) e.getSource()));
	}

	private void selectAll(JTextComponent txt) {
		txt.selectAll();
	}

	@Override
	public void focusLost(FocusEvent e) {

	}
}