import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Date;
import java.util.Stack;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.html.*;

//Courier skeleton
public class Courier {
	private static Stack backStack = new Stack();
	private static JLabel status = new JLabel(" ");

	private static void runCourier() {
		// Create main frame
		JFrame f = new JFrame("Shak's Courier");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setMinimumSize(new Dimension(1100, 800));
		f.setPreferredSize(new Dimension(1300, 800));

		// Create tabbed panes for left hand side of Courier
		JTabbedPane tp = new JTabbedPane();

		// Browser
		JLabel urlLabel = new JLabel("URL");
		String initUrl = "http://www.google.com";
		final JTextField url = new JTextField(initUrl, 20);
		JButton go = new JButton("GO!");
		JButton back = new JButton("Back");

		JPanel view = new JPanel();
		view.add(urlLabel);
		view.add(url);
		view.add(go);
		view.add(back);
		final JEditorPane wb;

		try {
			backStack.push(initUrl);
			wb = new JEditorPane(initUrl);
			wb.setEditable(false);
			wb.addHyperlinkListener(new HyperlinkListener() {

				public void hyperlinkUpdate(HyperlinkEvent e) {
					if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
						JEditorPane pane = (JEditorPane) e.getSource();
						if (e instanceof HTMLFrameHyperlinkEvent) {
							HTMLFrameHyperlinkEvent evt = (HTMLFrameHyperlinkEvent) e;
							HTMLDocument doc = (HTMLDocument) pane.getDocument();
							doc.processHTMLFrameHyperlinkEvent(evt);
						} else {
							try {
								pane.setPage(e.getURL());
							} catch (Throwable t) {
								t.printStackTrace();
							}
						}
					}
				}
			});

			// browse button setup
			go.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					try {
						backStack.push(url.getText().trim());
						wb.setPage(url.getText().trim());
					} catch (IOException ex) {
						ex.printStackTrace();
					}
				}
			});

			// back button setup
			back.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					if (backStack.size() <= 1)
						return;
					try { // get URL from back button
						backStack.pop();
						// show URL in text field
						String prevUrl = (String) backStack.peek();
						url.setText(prevUrl);

						wb.setPage(prevUrl);
					} catch (IOException e) {
						wb.setText("Error: " + e);
					}
				}
			});
			JScrollPane browser = new JScrollPane(wb);
			view.add(browser);

		} catch (IOException ex) {
			ex.printStackTrace();
		}

		// Address Book
		JPanel ab = new JPanel(new BorderLayout());
		Object[][] contacts = { { "Shakeel", "Bhamani", "803-207-4289", "shakeel.bhamani@gatech.edu" },
				{ "Morgan", "Freeman", "555-0123", "bruceAlmightyJokes@lol.com" },
				{ "Noopur", "Tanna", "123-456-7890", "gooshy@squishy.com" }, };
		String[] columns = { "First Name", "Last Name", "Phone #", "Email" };
		final JTable table = new JTable(contacts, columns);
		final JEditorPane info = new JEditorPane();
		info.setContentType("text/html");
		info.setFont(new Font("Serif", Font.PLAIN, 16));
		info.setEditable(false);
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent event) {
				String firstName = table.getValueAt(table.getSelectedRow(), 0).toString();
				String lastName = table.getValueAt(table.getSelectedRow(), 1).toString();
				String phone = table.getValueAt(table.getSelectedRow(), 2).toString();
				String email = table.getValueAt(table.getSelectedRow(), 3).toString();
				info.setText("<strong>Name: </strong>" + firstName + " " + lastName + "<br />"
						+ "<strong>Phone Number: </strong>" + phone + "<br />" + "<strong>Email Address: </strong>"
						+ email);
			}
		});
		JScrollPane addressBook = new JScrollPane(table);
		JScrollPane contact = new JScrollPane(info);
		ab.add(addressBook, BorderLayout.NORTH);
		ab.add(contact, BorderLayout.CENTER);

		// Clock
		JPanel c = new JPanel(new BorderLayout());
		final JLabel time = new JLabel();
		time.setHorizontalAlignment(SwingConstants.CENTER);
		time.setFont(new Font("Serif", Font.ROMAN_BASELINE, 40));
		ActionListener timeUpdate = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				time.setText(new Date().toString());
			}
		};
		Timer timer = new Timer(100, timeUpdate);
		timer.start();
		System.out.println(time.getText());
		c.add(time, BorderLayout.CENTER);

		tp.addTab("Web Browser", null, view, "Go Online!");
		tp.setMnemonicAt(0, KeyEvent.VK_1);
		tp.addTab("Address Book", null, ab, "See Contacts!");
		tp.setMnemonicAt(1, KeyEvent.VK_2);
		tp.addTab("Clock", null, c, "What time is it?");

		//Right pane
		JPanel rightPane = new JPanel(new BorderLayout());
		final DrawPad canvas = new DrawPad();

		// Buttons Organizer
		JPanel buttonManager = new JPanel();
		Box buttons = Box.createHorizontalBox();
		JButton np = new JButton("New Page");
		JButton dp = new JButton("Delete Page");
		JButton pf = new JButton("Page Forward");
		JButton pb = new JButton("Page Backward");
		JButton clearButton = new JButton("Clear");
		clearButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				canvas.clear();
			}
		});
		JRadioButton ffi = new JRadioButton("Free-form Ink");
		JRadioButton r = new JRadioButton("Rectangle");
		JRadioButton o = new JRadioButton("Oval");
		JRadioButton t = new JRadioButton("Text");
		ButtonGroup group = new ButtonGroup();
		group.add(ffi);
		group.add(r);
		group.add(o);
		group.add(t);
		np.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				status.setText("New Page Selected");
			}
		});
		dp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				status.setText("Delete Page Selected");
			}
		});
		pf.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				status.setText("Page Forward Selected");
			}
		});
		pb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				status.setText("Page Backward Selected");
			}
		});
		ffi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				status.setText("Free-form Ink Selected");
			}
		});
		r.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				status.setText("Rectangle Selected");
			}
		});
		o.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				status.setText("Oval Selected");
			}
		});
		t.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				status.setText("Text Selected");
			}
		});
		buttons.add(np);
		buttons.add(dp);
		buttons.add(pf);
		buttons.add(pb);
		buttons.add(clearButton);
		buttons.add(ffi);
		buttons.add(r);
		buttons.add(o);
		buttons.add(t);
		buttonManager.add(buttons);


		// rightPane.add(canvas, BorderLayout.PAGE_START);
		rightPane.add(canvas, BorderLayout.CENTER);
		rightPane.add(buttonManager, BorderLayout.SOUTH);

		// Create split pane
		JSplitPane sp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tp, rightPane);
		sp.setOneTouchExpandable(true);
		sp.setDividerLocation(f.getWidth() / 2);
		sp.setResizeWeight(1);

		f.setLayout(new BorderLayout());
		f.add(sp, BorderLayout.CENTER);
		f.add(status, BorderLayout.SOUTH);
		f.pack();
		f.setVisible(true);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				runCourier();
			}
		});
	}

}
