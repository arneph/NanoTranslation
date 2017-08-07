package ui;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;

public class ListView extends JPanel implements ActionListener, 
												FocusListener, 
												KeyListener, 
												MouseListener {
	public ListViewDataSource dataSource;
	public ListViewDelegate delegate;
	
	private int selectedRow;
	
	private ScrollView scrollView;
	private JPanel contentView;
	private JPanel[] listRows;
	private JCheckBox[] listCheckBoxes;
	private JTextField[] listCells;
	
	public ListView() {
		dataSource = null;
		delegate = null;
		
		selectedRow = -1;
		
		setLayout(null);
		
		scrollView = new ScrollView();
		scrollView.setBackground(Color.white);
		scrollView.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollView.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollView.setBorder(BorderFactory.createLineBorder(Color.lightGray, 1));
		
		contentView = new JPanel();
		contentView.setOpaque(false);
		contentView.setLayout(null);
		contentView.setPreferredSize(new Dimension(0, 0));
		contentView.addMouseListener(this);
		
		scrollView.setViewportView(contentView);
		scrollView.getViewport().setOpaque(false);
		
		add(scrollView);
		
		listRows = new JPanel[0];
		listCheckBoxes = new JCheckBox[0];
		listCells = new JTextField[0];
	}
	
	public ListViewDataSource getDataSource() {
		return dataSource;
	}
	
	public void setDataSource(ListViewDataSource dataSource) {
		this.dataSource = dataSource;
		
		reloadData();
	}
	
	public ListViewDelegate getDelegate() {
		return delegate;
	}
	
	public void setDelegate(ListViewDelegate delegate) {
		this.delegate = delegate;
		
		reloadData();
	}
	
	public int getSelectedRow() {
		return selectedRow;
	}
	
	public void setSelectedRow(int index) {
		boolean allowSelection = (getDelegate() != null) ? getDelegate().shouldAllowSelection(this) : true;
		
		if (!allowSelection) index = -1;
		
		int r = listRows.length;
		
		if (index < -1 || index >= r) {
			throw new IllegalArgumentException();
		}
		
		if (selectedRow != -1) {
			JPanel row = listRows[selectedRow];
			
			row.setBackground(Color.white);
		}
		
		selectedRow = index;
		
		if (selectedRow != -1) {
			JPanel row = listRows[selectedRow];
			
			row.setBackground(new Color(239, 239, 239));
		}
		
		if (getDelegate() != null) {
			getDelegate().selectionChanged(this);
		}
	}
	
	public void reloadData() {
		int r = (getDataSource() != null) ? getDataSource().getNumberOfRows(this) : 0;
		boolean showCheckBoxes = (getDelegate() != null) ? getDelegate().shouldShowCheckBoxes(this) : false;
		boolean allowEditing = (getDelegate() != null) ? getDelegate().shouldAllowEditing(this) : true;
		
		if (r < 0) r = 0;
		
		if (selectedRow >= r) {
			selectedRow = -1;
		}
		
		int oldR = listRows.length;
		
		if (r < oldR) {
			removeRows(r);
		}else if (r > oldR) {
			addRows(r);
		}
		
		for (int i = 0; i < r; i++) {
			boolean checked = getDataSource().isRowChecked(this, i);
			String cellValue = getDataSource().getRowValue(this, i);
			JPanel row = listRows[i];
			JCheckBox checkBox = listCheckBoxes[i];
			JTextField cell = listCells[i];
			
			if (selectedRow != i) {
				row.setBackground(Color.white);
			}else{
				row.setBackground(new Color(239, 239, 239));
			}
			
			checkBox.setSelected(checked);
			checkBox.setVisible(showCheckBoxes);
			cell.setText(cellValue);
			cell.setEditable(allowEditing);
		}
		
		repositionContents();
		revalidate();
		repaint();
	}
	
	private void addRows(int r) {
		int oldR = listRows.length;
		
		listRows = Arrays.copyOf(listRows, r);
		listCheckBoxes = Arrays.copyOf(listCheckBoxes, r);
		listCells = Arrays.copyOf(listCells, r);
		
		for (int i = oldR; i < r; i++) {
			JPanel row = new JPanel();
			
			row.setBackground(Color.white);
			row.setBorder(null);
			row.setLayout(null);
			row.addMouseListener(this);
			
			JCheckBox checkBox = new JCheckBox();
			
			checkBox.setIcon(Icons.uncheckedIcon());
			checkBox.setSelectedIcon(Icons.checkecIcon());
			checkBox.setBorder(null);
			checkBox.addActionListener(this);
			
			JTextField cell = new JTextField();
			
			cell.setOpaque(false);
			cell.setBackground(null);
			cell.setForeground(Color.black);
			cell.setBorder(null);
			cell.addFocusListener(this);
			cell.addKeyListener(this);
			
			row.add(checkBox);
			row.add(cell);
			
			contentView.add(row);
			
			listRows[i] = row;
			listCheckBoxes[i] = checkBox;
			listCells[i] = cell;
		}
	}
	
	private void removeRows(int r) {
		int oldR = listRows.length;
		
		for (int i = r; i < oldR; i++) {
			JPanel row = listRows[i];
			
			contentView.remove(row);
			row.removeAll();
		}

		listRows = Arrays.copyOf(listRows, r);
		listCheckBoxes = Arrays.copyOf(listCheckBoxes, r);
		listCells = Arrays.copyOf(listCells, r);
	}
	
	public void setSize(Dimension d) {
		super.setSize(d);
		
		repositionContents();
	}
	
	public void setSize(int width, int height) {
		super.setSize(width, height);
		
		repositionContents();
	}
	
	private void repositionContents() {
		int w = getWidth();
		int h = getHeight();
		
		int r = listRows.length;
		
		scrollView.setLocation(0, 0);
		scrollView.setSize(w, h);
		
		contentView.setPreferredSize(new Dimension(w - 18, r * 22));
		
		for (int i = 0; i < r; i++) {
			JPanel row = listRows[i];
			JCheckBox checkBox = listCheckBoxes[i];
			JTextField cell = listCells[i];
			
			row.setLocation(0, i * 23);
			row.setSize(w - 18, 22);
			
			if (checkBox.isVisible()) {
				checkBox.setLocation(1, 1);
				checkBox.setSize(20, 20);
				
				if (!cell.isFocusOwner()) {
					cell.setLocation(22, 0);
					cell.setSize(Math.min(w - 41, cell.getPreferredSize().width), 22);
				}else{
					cell.setLocation(22, 0);
					cell.setSize(w - 41, 22);
				}
				
			}else{
				if (!cell.isFocusOwner()) {
					cell.setLocation(1, 0);
					cell.setSize(Math.min(w - 20, cell.getPreferredSize().width), 22);
				}else{
					cell.setLocation(1, 0);
					cell.setSize(w - 20, 22);
				}
			}
		}
	}
	
	public void actionPerformed(ActionEvent e) {
		int r = listRows.length;
		
		for (int i = 0; i < r; i++) {
			JCheckBox checkBox = listCheckBoxes[i];
			
			if (checkBox != e.getSource()) continue;
			
			if (getDataSource() == null) {
				reloadData();
			}else{
				getDataSource().setRowChecked(this, i, checkBox.isSelected());
			}
			
			setSelectedRow(i);
		}
	}
	
	public void focusGained(FocusEvent e) {
		int r = listRows.length;
		
		for (int i = 0; i < r; i++) {
			JTextField cell = listCells[i];
			
			if (cell != e.getSource()) continue;
			
			setSelectedRow(i);
			repositionContents();
		}
	}
	
	public void focusLost(FocusEvent e) {
		int r = listRows.length;
		
		for (int i = 0; i < r; i++) {
			JTextField cell = listCells[i];
			
			if (cell != e.getSource()) continue;
			
			if (getDataSource() == null) {
				reloadData();
			}else{
				getDataSource().setRowValue(this, i, cell.getText());
			}
			
			repositionContents();
		}
	}
	
	public void keyTyped(KeyEvent e) {}
	
	public void keyPressed(KeyEvent e) {
		if ((e.getKeyCode() == KeyEvent.VK_ESCAPE || 
			 e.getKeyCode() == KeyEvent.VK_ENTER) && 
			e.getSource() instanceof JTextField) {
			JFrame frame = (JFrame) SwingUtilities.getRoot(this);
			
			if (frame != null) frame.requestFocusInWindow();
		}
	}
	
	public void keyReleased(KeyEvent e) {}
	
	public void mouseClicked(MouseEvent e) {
		if (e.getSource() == contentView) {
			setSelectedRow(-1);
			
		}else{
			for (int i = 0; i < listRows.length; i++) {
				JPanel row = listRows[i];
				
				if (row != e.getSource()) continue;
				
				setSelectedRow(i);
			}
		}
	}
	
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	
	//Interfaces:
	public interface ListViewDataSource {
		
		public int getNumberOfRows(ListView listView);
		public boolean isRowChecked(ListView listView, int index);
		public void setRowChecked(ListView listView, int index, boolean checked);
		public String getRowValue(ListView listView, int index);
		public void setRowValue(ListView listView, int index, String value);
		
	}
	
	public interface ListViewDelegate {
		
		public boolean shouldShowCheckBoxes(ListView listView);
		public boolean shouldAllowSelection(ListView listView);
		public boolean shouldAllowEditing(ListView listView);
		
		public void selectionChanged(ListView listView);
		
	}
	
}
