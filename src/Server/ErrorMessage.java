package Server;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
/**
 * Klasa wyswietlajaca okno z komunikatem bledow
 * @author Mateusz
 *
 */
public class ErrorMessage{
	public static void show(String exMsg)
		{
			JFrame errFrame = new JFrame("ERROR!");
			errFrame.setLayout(new BorderLayout());
			errFrame.add(new Label(exMsg, Label.CENTER), BorderLayout.CENTER);
			JButton errOkButton = new JButton("OK");
			errFrame.add(errOkButton, BorderLayout.SOUTH);
			errFrame.pack();
			errOkButton.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent ae) {errFrame.dispose();}});
			errFrame.addWindowListener(new WindowAdapter() {public void windowClosing(WindowEvent we) {errFrame.dispose();}});
			EventQueue.invokeLater(new Runnable() {public void run() {errFrame.setVisible(true);}});
		}
	}