package org.eurocarbdb.application.glycanbuilder.util;

import java.awt.Frame;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;

import org.eurocarbdb.application.glycanbuilder.CustomFocusTraversalPolicy;
import org.eurocarbdb.application.glycanbuilder.converter.GlycanParserFactory;
import org.eurocarbdb.application.glycanbuilder.renderutil.SVGUtils;
import org.jdesktop.layout.GroupLayout;
import org.jdesktop.layout.LayoutStyle;

public class OutputImageDialog extends EscapeDialog implements ActionListener{

	public OutputImageDialog(Frame _frame) {
		super(_frame, true);
		
		this.initComponents();
		this.fillComponents();
		this.setSelections();
		this.setTraversal();
		this.setActions();
		this.enableItems();

		// set location
		setLocationRelativeTo(_frame);
	}

	private void setTraversal() {
		CustomFocusTraversalPolicy tp = new CustomFocusTraversalPolicy();
		tp.addComponent(this.field_string_format);
		tp.addComponent(this.button_ok);
		tp.addComponent(this.button_cancel);
		this.setFocusTraversalPolicy(tp);
		getRootPane().setDefaultButton(this.button_ok);
	}

	private void enableItems() {
	}

	private void setActions() {
	}

	private void setSelections() {
		this.button_ok.addActionListener(this);
		this.button_cancel.addActionListener(this);
	}

	private void fillComponents() {
		List<String> exportOptions = new ArrayList<String>();
		for(java.util.Map.Entry<String,String> e : SVGUtils.getExportFormats().entrySet() )
			exportOptions.add("Export to "+e.getKey());
		
		this.field_string_format.setModel(new DefaultComboBoxModel(exportOptions.toArray()));
		
//		Map<String,String> formats = GlycanParserFactory.getExportFormats();
//		this.field_string_format.setModel(new DefaultComboBoxModel(formats.keySet().toArray()));
	}

	private void closeDialog() {
		setVisible(false);
		dispose();
	}

	private void initComponents() {
		this.jLabel1 = new JLabel();
		this.button_ok = new JButton();
		this.button_cancel = new JButton();
		this.field_string_format = new JComboBox();

		addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent evt) {
				closeDialog();
			}
		});

		this.jLabel1.setText("Graphical output format");
		this.button_ok.setText("Export");
		this.button_cancel.setText("Cancel");
		
		this.field_string_format.setModel(new DefaultComboBoxModel(new String[] {}));

		GroupLayout layout = new GroupLayout(this.getContentPane());
		this.getContentPane().setLayout(layout);
		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.LEADING)
			.add(layout.createSequentialGroup()
				.add(layout.createParallelGroup(GroupLayout.LEADING)
					.add(layout.createSequentialGroup()
						.addContainerGap()
						.add(layout.createParallelGroup(GroupLayout.LEADING)
							.add(layout.createSequentialGroup()
								.add(this.jLabel1)
								.addPreferredGap(LayoutStyle.RELATED, 200, Short.MAX_VALUE)
								.add(this.field_string_format, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
							)
						)
					.add(layout.createSequentialGroup()
						.add(127, 127, 127)
						.add(this.button_ok)
						.addPreferredGap(LayoutStyle.RELATED)
						.add(this.button_cancel)
						)
					)
					.addContainerGap()
				)
		);

		layout.linkSize(new Component[] {this.button_cancel, this.button_ok}, GroupLayout.HORIZONTAL);

		layout.setVerticalGroup(
			layout.createParallelGroup(GroupLayout.LEADING)
			.add(layout.createSequentialGroup()
				.addContainerGap()
				.add(layout.createParallelGroup(GroupLayout.BASELINE)
					.add(this.jLabel1)
					.add(this.field_string_format, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					)
				.addPreferredGap(LayoutStyle.RELATED)
				.add(17, 17, 17)
				.add(layout.createParallelGroup(GroupLayout.BASELINE)
					.add(this.button_cancel)
					.add(this.button_ok))
					.addContainerGap()
				)
		);
		pack();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String action = e.getActionCommand();
		if (action == "Export") {
			this.return_status = "Export";
			this.closeDialog();
		}
		else if (action == "Cancel"){
			this.return_status = "Cancel";
			this.closeDialog();
		}
	}
	
	public boolean isCanceled() {
		return this.return_status.equals("Cancel");
	}
	
	public String getFormat() {
		String rawString = (String) this.field_string_format.getSelectedItem();
		String key = rawString.substring(("Export to ").length(), rawString.length());
		return key;
	}

	private JButton button_cancel;
	private JButton button_ok;	
	private JComboBox field_string_format;
	private JLabel jLabel1;
}
