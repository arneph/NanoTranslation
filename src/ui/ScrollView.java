package ui;

import java.awt.*;

import javax.swing.*;
import javax.swing.plaf.metal.*;

@SuppressWarnings("serial")
public class ScrollView extends JScrollPane {

	public JScrollBar createVerticalScrollBar() {
		JScrollBar scrollBar = new JScrollBar(JScrollBar.VERTICAL);
		
		scrollBar.setUI(new ScrollbarUI());
		
		return scrollBar;
	}
	
	public JScrollBar createHorizontalScrollBar() {
		JScrollBar scrollBar = new JScrollBar(JScrollBar.HORIZONTAL);
		
		scrollBar.setUI(new ScrollbarUI());
		
		return scrollBar;
	}
	
	private class ScrollbarUI extends MetalScrollBarUI {
		
		public ScrollbarUI() {
			super();
		}
		
		public Dimension getPreferredSize(JComponent c) {
	        return (scrollbar.getOrientation() == JScrollBar.VERTICAL)
	            ? new Dimension(16, 48)
	            : new Dimension(48, 16);
	    }
		
        protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
    		g.setColor(Color.lightGray);
    		g.fillRoundRect(r.x, r.y, r.width, r.height, 0, 0);
        }
        
        protected void paintTrack(Graphics g, JComponent c, Rectangle r) {
    		g.setColor(new Color(223, 223, 223));
    		g.fillRect(r.x, r.y, r.width, r.height);
        }

        protected JButton createDecreaseButton(int orientation) {
            JButton button = new JButton();
            
            button.setPreferredSize(new Dimension(0, 0));
            
            return button;
        }

        protected JButton createIncreaseButton(int orientation) {
        	JButton button = new JButton();
            
            button.setPreferredSize(new Dimension(0, 0));
            
            return button;
        }
	}
	
}
